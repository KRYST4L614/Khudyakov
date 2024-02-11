package com.example.khudyakovlab.views

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.khudyakovlab.FilmDbRepository
import com.example.khudyakovlab.KinopoiskFetchR
import com.example.khudyakovlab.Utils
import com.example.khudyakovlab.models.FilmCard
import com.example.khudyakovlab.models.FilmDetail
import com.example.khudyakovlab.viewModels.FilmCardViewModel
import com.example.photogallery.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception

private const val KEY_IS_POPULAR = "isPopular"

class MainFragment : Fragment() {
    private lateinit var recyclerViewFilms: RecyclerView
    private lateinit var filmCardViewModel: FilmCardViewModel
    private lateinit var pagindAdapter: FilmPagingAdapter
    private lateinit var dbAdapter: FilmDbAdapter
    private lateinit var popularButton: Button
    private lateinit var featuredButton: Button
    private lateinit var appBar: androidx.appcompat.widget.Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var cloudImageView: ImageView
    private lateinit var retryButton: Button
    private var isPopular = true
    private var callbacks: Callbacks? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filmCardViewModel = ViewModelProvider(this)[FilmCardViewModel::class.java]
        isPopular = savedInstanceState?.getBoolean(KEY_IS_POPULAR, true) ?: true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        recyclerViewFilms = view.findViewById(R.id.recycler_view_films)
        recyclerViewFilms.layoutManager = LinearLayoutManager(context)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        popularButton.setOnClickListener {
            popularBtnHandler()
        }
        featuredButton.setOnClickListener {
            featuredBtnHandler()
        }

        pagindAdapter = FilmPagingAdapter(FilmCardDiffUtilCallback())
        pagindAdapter.addLoadStateListener {state ->
            when (state.refresh) {
                is LoadState.Loading -> {
                    recyclerViewFilms.isVisible = false
                    progressBar.isVisible = true
                    cloudImageView.isVisible = false
                    retryButton.isVisible = false
                    retryButton.isClickable = false
                }
                is LoadState.Error -> {
                    recyclerViewFilms.isVisible = false
                    progressBar.isVisible = false
                    cloudImageView.isVisible = true
                    retryButton.isVisible = true
                    retryButton.isClickable = true
                }
                else -> {
                    recyclerViewFilms.isVisible = true
                    progressBar.isVisible = false
                    cloudImageView.isVisible = false
                    retryButton.isVisible = false
                    retryButton.isClickable = false
                }
            }

        }

        dbAdapter = FilmDbAdapter()
        val dbResponse = FilmDbRepository.get().getFilms()
        dbResponse.observe(
            viewLifecycleOwner,
            Observer {
                dbAdapter.submitList(it.toMutableList())
            }
        )

        recyclerViewFilms.adapter = if (isPopular) pagindAdapter else dbAdapter
        lifecycleScope.launch {
            filmCardViewModel.galleryLiveData.collectLatest {
                pagindAdapter.submitData(it)
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
    }

    private fun setupUI(view: View) {
        appBar = view.findViewById(R.id.tool_bar)
        appBar.title = if (isPopular) context?.getString(R.string.popular) else context?.getString(
            R.string.featured
        )
        popularButton = view.findViewById(R.id.popular_button)
        popularButton.setTextColor(if (isPopular) Color.WHITE else context?.getColor(R.color.button_pressed)!!)
        popularButton.setBackgroundColor(if (isPopular) context?.getColor(R.color.button_pressed)!! else context?.getColor(
            R.color.button_normal
        )!!)
        featuredButton = view.findViewById(R.id.featured_button)
        featuredButton.setTextColor(if (!isPopular) Color.WHITE else context?.getColor(R.color.button_pressed)!!)
        featuredButton.setBackgroundColor(if (!isPopular) context?.getColor(R.color.button_pressed)!! else context?.getColor(
            R.color.button_normal
        )!!)
        progressBar = view.findViewById(R.id.progress_bar)
        errorTextView = view.findViewById(R.id.error_text_view)
        errorTextView.text = context?.getString(R.string.error_message)
        cloudImageView = view.findViewById(R.id.cloud_image_view)
        retryButton = view.findViewById<Button?>(R.id.retry_button).also {
            it.setOnClickListener {
                pagindAdapter.retry()
            }
        }
    }

    private fun popularBtnHandler() {
        if (isPopular) {
            return
        }
        popularButton.setBackgroundColor(context?.getColor(R.color.button_normal)!!)
        popularButton.setTextColor(Color.WHITE)
        recyclerViewFilms.adapter = pagindAdapter
        featuredButton.setBackgroundColor(context?.getColor(R.color.button_pressed)!!)
        featuredButton.setTextColor(context?.getColor(R.color.button_normal)!!)
        isPopular = true
        appBar.title = context?.getString(R.string.popular)
    }

    private fun featuredBtnHandler() {
        if (!isPopular) {
            return
        }
        featuredButton.setBackgroundColor(context?.getColor(R.color.button_pressed)!!)
        featuredButton.setTextColor(Color.WHITE)
        recyclerViewFilms.adapter = dbAdapter
        popularButton.setBackgroundColor(context?.getColor(R.color.button_normal)!!)
        popularButton.setTextColor(context?.getColor(R.color.button_pressed)!!)
        isPopular = false
        appBar.title = context?.getString(R.string.featured)
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }

    inner class FilmHolder(view: View): RecyclerView.ViewHolder(view), View.OnLongClickListener,
        View.OnClickListener {
        private lateinit var filmId: String
        var nameTextView: TextView = view.findViewById(R.id.name)
        var genreTextView: TextView = view.findViewById(R.id.genre)
        var banner: ImageView = view.findViewById(R.id.poster_preview)
        var progressBar: ProgressBar = view.findViewById(R.id.poster_preview_progress_bar)
        var star: ImageView = view.findViewById<ImageView?>(R.id.star_image_view).also {
            it.isVisible = false
        }

        init {
            itemView.also {
                it.setOnClickListener(this)
                it.setOnLongClickListener(this)
            }
        }

        fun bind(film_item: FilmCard?,) {
            film_item?.let {
                val response = FilmDbRepository.get().getFilm(film_item.id)
                response.observe(
                    viewLifecycleOwner,
                    Observer {
                        star.isVisible = it != null
                    }
                )
                nameTextView.text = film_item.name
                genreTextView.text = Utils.removeSquareBrackets(film_item.genres.first().toString()).replaceFirstChar {
                    it.uppercase()
                }.plus(" (${film_item.year})")
                filmId = film_item.id
                Picasso.get()
                    .load(film_item.poster)
                    .into(banner, object: Callback {
                        override fun onSuccess() {
                            progressBar.isVisible = false
                        }

                        override fun onError(e: Exception?) {
                            return
                        }
                    })
            }
        }

        fun bindFromDb(filmDetail: FilmDetail) {
            nameTextView.text = filmDetail.name
            genreTextView.text = filmDetail.genre
            star.isVisible = true
            Picasso.get()
                .load(filmDetail.posterUrl)
                .into(banner, object: Callback {
                    override fun onSuccess() {
                        progressBar.isVisible = false
                    }

                    override fun onError(e: Exception?) {
                        return
                    }
                })
            filmId = filmDetail.id
        }

        override fun onLongClick(v: View?): Boolean {
            if (!star.isVisible) {
                val stream = KinopoiskFetchR.fetchFilm(filmId)
                stream.observe(viewLifecycleOwner, Observer {
                    it?.let {
                        FilmDbRepository.get().addFilm(
                            FilmDetail(
                                it.poster,
                                it.name,
                                it.description,
                                it.id,
                                Utils.removeSquareBrackets(it.genres.toString()),
                                Utils.removeSquareBrackets(it.countries.toString()),
                                it.year
                            )
                        )
                        star.isVisible = true
                        stream.removeObservers(viewLifecycleOwner)
                    }
                })
            }
            else {
                FilmDbRepository.get().deleteFilm(filmId)
                star.isVisible = false
            }
            return true
        }

        override fun onClick(v: View?) {
            callbacks?.onFilmSelected(filmId)
        }
    }

    private inner class FilmPagingAdapter(utillCallback: DiffUtil.ItemCallback<FilmCard>)
        : PagingDataAdapter<FilmCard, FilmHolder>(utillCallback) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmHolder {
            val view = layoutInflater.inflate(R.layout.fragment_film_card, parent, false)
            return FilmHolder(view)
        }

        override fun onBindViewHolder(holder: FilmHolder, position: Int) {
            holder.bind(getItem(position))
        }

    }

    private inner class FilmDbAdapter : ListAdapter<FilmDetail, FilmHolder>(FilmDetailUtilCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmHolder {
            val view = layoutInflater.inflate(R.layout.fragment_film_card, parent, false)
            return FilmHolder(view)
        }

        override fun getItemCount(): Int {
            return currentList.size
        }

        override fun onBindViewHolder(holder: FilmHolder, position: Int) {
            holder.bindFromDb(getItem(position))
        }

    }

    private class FilmCardDiffUtilCallback: DiffUtil.ItemCallback<FilmCard>() {
        override fun areItemsTheSame(oldItem: FilmCard, newItem: FilmCard): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FilmCard, newItem: FilmCard): Boolean {
            return oldItem.name == newItem.name && oldItem.poster==newItem.poster
        }

    }

    private class FilmDetailUtilCallback: DiffUtil.ItemCallback<FilmDetail>() {
        override fun areItemsTheSame(oldItem: FilmDetail, newItem: FilmDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FilmDetail, newItem: FilmDetail): Boolean {
            return oldItem.name == newItem.name && oldItem.posterUrl == newItem.posterUrl
        }

    }

    interface Callbacks {
        fun onFilmSelected(filmId: String)
    }
}