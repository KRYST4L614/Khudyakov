package com.example.khudyakovlab.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.example.khudyakovlab.Utils
import com.example.khudyakovlab.data.database.models.FilmDetail
import com.example.khudyakovlab.di.AppComponent
import com.example.khudyakovlab.di.DaggerAppComponent
import com.example.khudyakovlab.ui.viewModels.FilmViewModel
import com.example.photogallery.R
import com.example.photogallery.databinding.FragmentDetailFilmBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import javax.inject.Inject

private const val ARG_FILM_ID = "film_id"

class FilmFragment : Fragment() {
    lateinit var appComponent: AppComponent

    @Inject
    lateinit var factory: FilmViewModel.Factory
    private var _binding: FragmentDetailFilmBinding? = null
    private val binding get() = _binding!!
    private lateinit var filmId: String
    private lateinit var filmDetail: FilmDetail
    private val viewModel: FilmViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            factory
        )[FilmViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent = DaggerAppComponent
            .builder()
            .context(requireContext())
            .build()
        appComponent.inject(this)
        filmId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(ARG_FILM_ID, String::class.java)!!
        } else {
            arguments?.getSerializable(ARG_FILM_ID) as String
        }
        viewModel.loadFilm(filmId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailFilmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.retryButton.setOnClickListener {
            fetchFilmDetail()
        }
        fetchFilmDetail()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchFilmDetail() {
        val responseDb = viewModel.filmLiveData
        responseDb.observe(
            viewLifecycleOwner
        ) { film ->
            if (film != null) {
                filmDetail = film
                updateUI(filmDetail)
            } else {
                val stream: LiveData<FilmDetail?> = viewModel.fetchFilm(filmId)
                stream.observe(viewLifecycleOwner) { it: FilmDetail? ->
                    if (it != null) {
                        hideErrorUi()
                        filmDetail = FilmDetail(
                            it.posterUrl,
                            it.name,
                            it.description,
                            it.id,
                            Utils.removeSquareBrackets(it.genre),
                            Utils.removeSquareBrackets(it.country),
                            it.year
                        )
                        updateUI(filmDetail)
                    } else {
                        showErrorUi()
                    }
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.decorView.windowInsetsController?.hide(android.view.WindowInsets.Type.statusBars())
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requireActivity().window.decorView.windowInsetsController?.show(android.view.WindowInsets.Type.statusBars())
        }
    }

    private fun updateUI(filmDetail: FilmDetail) {
        binding.name.text = filmDetail.name
        binding.description.text = filmDetail.description
        binding.genre.text = context?.getString(R.string.genre)?.format(filmDetail.genre)
        binding.country.text = context?.getString(R.string.country)?.format(filmDetail.country)
        binding.year.text = context?.getString(R.string.year)?.format(filmDetail.year)
        Picasso.get()
            .load(filmDetail.posterUrl)
            .into(binding.poster, object : Callback {
                override fun onSuccess() {
                    binding.posterProgressBar.isVisible = false
                }

                override fun onError(e: Exception?) {
                    return
                }
            })
    }

    private fun hideErrorUi() {
        binding.cloudImage.isVisible = false
        binding.error.isVisible = false
        binding.retryButton.apply {
            isClickable = false
            isVisible = false
        }
        binding.posterProgressBar.isVisible = true
    }

    private fun showErrorUi() {
        binding.posterProgressBar.isVisible = false
        binding.cloudImage.isVisible = true
        binding.error.isVisible = true
        binding.retryButton.apply {
            isClickable = true
            isVisible = true
        }
    }

    companion object {
        fun newInstance(id: String): FilmFragment {
            val args = Bundle().apply {
                putSerializable(ARG_FILM_ID, id)
            }
            return FilmFragment().apply {
                arguments = args
            }
        }
    }


}