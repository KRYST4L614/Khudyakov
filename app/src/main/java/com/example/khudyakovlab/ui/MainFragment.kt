package com.example.khudyakovlab.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.khudyakovlab.ui.adapters.FilmDbAdapter
import com.example.khudyakovlab.ui.adapters.FilmPagingAdapter
import com.example.khudyakovlab.ui.adapters.diffutils.FilmCardDiffUtilCallback
import com.example.khudyakovlab.di.AppComponent
import com.example.khudyakovlab.di.DaggerAppComponent
import com.example.khudyakovlab.ui.viewModels.FilmViewModel
import com.example.photogallery.R
import com.example.photogallery.databinding.FragmentMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val KEY_IS_POPULAR = "isPopular"
private const val KEY_ERROR_NETWORK = "errorNetwork"

class MainFragment : Fragment() {
    lateinit var appComponent: AppComponent

    @Inject
    lateinit var factory: FilmViewModel.Factory
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FilmViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            factory
        )[FilmViewModel::class.java]
    }
    private lateinit var pagingAdapter: FilmPagingAdapter
    private lateinit var dbAdapter: FilmDbAdapter
    private var isPopular = true
    private var callbacks: Callbacks? = null
    private var errorNetwork = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent = DaggerAppComponent
            .builder()
            .context(requireContext())
            .build()
        appComponent.inject(this)
        isPopular = savedInstanceState?.getBoolean(KEY_IS_POPULAR, true) ?: true
        errorNetwork = savedInstanceState?.getBoolean(KEY_ERROR_NETWORK, false) ?: false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        pagingAdapter = FilmPagingAdapter(
            FilmCardDiffUtilCallback(),
            viewModel,
            viewLifecycleOwner,
            callbacks
        )
        pagingAdapter.addLoadStateListener { state ->
            when (state.refresh) {
                is LoadState.Loading -> {
                    errorNetwork = false
                    loadingUi()
                }

                is LoadState.Error -> {
                    errorNetwork = true
                    errorUi()
                }

                else -> {
                    errorNetwork = false
                    notLoadingUi()
                }
            }

        }

        dbAdapter = FilmDbAdapter(viewLifecycleOwner, viewModel, callbacks).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        val dbResponse = viewModel.getFilms()
        dbResponse.observe(
            viewLifecycleOwner
        ) {
            dbAdapter.submitList(it.toMutableList())
        }

        binding.recyclerView.adapter = if (isPopular) pagingAdapter else dbAdapter
        lifecycleScope.launch {
            viewModel.pagerLiveData.asFlow().collectLatest {
                pagingAdapter.submitData(it)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putBoolean(KEY_IS_POPULAR, isPopular)
        savedInstanceState.putBoolean(KEY_ERROR_NETWORK, errorNetwork)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        binding.toolBar.title =
            if (isPopular) context?.getString(R.string.popular) else context?.getString(
                R.string.featured
            )
        binding.popularButton.setTextColor(if (isPopular) Color.WHITE else context?.getColor(R.color.button_pressed)!!)
        binding.popularButton.setBackgroundColor(
            if (isPopular) context?.getColor(R.color.button_pressed)!! else context?.getColor(
                R.color.button_normal
            )!!
        )
        binding.featuredButton.setTextColor(if (!isPopular) Color.WHITE else context?.getColor(R.color.button_pressed)!!)
        binding.featuredButton.setBackgroundColor(
            if (!isPopular) context?.getColor(R.color.button_pressed)!! else context?.getColor(
                R.color.button_normal
            )!!
        )
        binding.retryButton.setOnClickListener {
            pagingAdapter.retry()
        }
        binding.popularButton.setOnClickListener {
            popularBtnHandler()
        }
        binding.featuredButton.setOnClickListener {
            featuredBtnHandler()
        }
        if (errorNetwork) {
            errorUi()
        } else {
            notLoadingUi()
        }
    }

    private fun popularBtnHandler() {
        if (isPopular) {
            return
        }
        binding.popularButton.apply {
            setBackgroundColor(context?.getColor(R.color.button_pressed)!!)
            setTextColor(Color.WHITE)
        }
        binding.recyclerView.adapter = pagingAdapter
        binding.featuredButton.apply {
            setBackgroundColor(context?.getColor(R.color.button_normal)!!)
            setTextColor(context?.getColor(R.color.button_pressed)!!)
        }
        isPopular = true
        binding.toolBar.title = context?.getString(R.string.popular)
        if (errorNetwork) {
            errorUi()
        }
    }

    private fun featuredBtnHandler() {
        if (!isPopular) {
            return
        }
        binding.featuredButton.apply {
            setBackgroundColor(context?.getColor(R.color.button_pressed)!!)
            setTextColor(Color.WHITE)
        }
        binding.recyclerView.adapter = dbAdapter
        notLoadingUi()
        binding.popularButton.apply {
            setBackgroundColor(context?.getColor(R.color.button_normal)!!)
            setTextColor(context?.getColor(R.color.button_pressed)!!)
        }
        isPopular = false
        binding.toolBar.title = context?.getString(R.string.featured)
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    private fun errorUi() {
        if (!isPopular) {
            return
        }
        binding.recyclerView.isVisible = false
        binding.progressBar.isVisible = false
        binding.cloudImage.isVisible = true
        binding.retryButton.apply {
            isVisible = true
            isClickable = true
        }
        binding.error.isVisible = true
    }

    private fun loadingUi() {
        if (!isPopular) {
            return
        }
        binding.recyclerView.isVisible = false
        binding.progressBar.isVisible = true
        binding.cloudImage.isVisible = false
        binding.retryButton.apply {
            isVisible = false
            isClickable = false
        }
        binding.error.isVisible = false
    }

    private fun notLoadingUi() {
        binding.recyclerView.isVisible = true
        binding.progressBar.isVisible = false
        binding.cloudImage.isVisible = false
        binding.retryButton.apply {
            isVisible = false
            isClickable = false
        }
        binding.error.isVisible = false
    }

    interface Callbacks {
        fun onFilmSelected(filmId: String)
    }
}