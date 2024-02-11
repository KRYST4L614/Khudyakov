package com.example.khudyakovlab.views

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.khudyakovlab.KinopoiskFetchR
import com.example.khudyakovlab.Utils
import com.example.khudyakovlab.models.FilmDetail
import com.example.khudyakovlab.viewModels.FilmDetailViewModel
import com.example.photogallery.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

private const val ARG_FILM_ID = "film_id"

class FilmFragment : Fragment() {
    private lateinit var name: TextView
    private lateinit var description: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var year: TextView
    private lateinit var poster: ImageView
    private lateinit var filmId: String
    private lateinit var filmDetail: FilmDetail
    private lateinit var progressBarPoster: ProgressBar
    private lateinit var errorTextView: TextView
    private lateinit var cloudImageView: ImageView
    private lateinit var retryButton: Button
    private val filmDetailViewModel: FilmDetailViewModel by lazy {
        ViewModelProvider(this).get(FilmDetailViewModel::class.java)
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filmId = arguments?.getSerializable(ARG_FILM_ID, String::class.java)!!
        filmDetailViewModel.loadFilm(filmId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_film, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi(view)
        fetchFilmDetail()
    }

    private fun setupUi(view: View) {
        name = view.findViewById(R.id.detail_name_text_view) as TextView
        description = view.findViewById(R.id.detail_description_text_view) as TextView
        description.setMovementMethod(ScrollingMovementMethod());
        genre = view.findViewById(R.id.detail_genre_text_view) as TextView
        country = view.findViewById(R.id.detail_country_text_view) as TextView
        year = view.findViewById(R.id.detail_year_text_view) as TextView
        poster = view.findViewById(R.id.poster_image_view) as ImageView
        progressBarPoster = view.findViewById(R.id.poster_progress_bar) as ProgressBar
        errorTextView = view.findViewById(R.id.film_detail_error_text_view)
        errorTextView.text = context?.getString(R.string.error_message)
        cloudImageView = view.findViewById(R.id.film_detail_cloud_image_view)
        retryButton = view.findViewById(R.id.film_detail_retry_button)
        retryButton.setOnClickListener {
            fetchFilmDetail()
        }
    }

    private fun fetchFilmDetail() {
        val responseDb = filmDetailViewModel.filmLiveData
        responseDb.observe(
            viewLifecycleOwner,
            Observer {film ->
                if (film != null) {
                    filmDetail = film
                    updateUI(filmDetail)
                } else {
                    val stream = KinopoiskFetchR.fetchFilm(filmId)
                    stream.observe(viewLifecycleOwner, Observer {
                        if (it != null) {
                            cloudImageView.isVisible = false
                            errorTextView.isVisible = false
                            retryButton.isClickable = false
                            retryButton.isVisible = false
                            progressBarPoster.isVisible = true
                            filmDetail = FilmDetail(
                                it.poster,
                                it.name,
                                it.description,
                                it.id,
                                Utils.removeSquareBrackets(it.genres.toString()),
                                Utils.removeSquareBrackets(it.countries.toString()),
                                it.year
                            )
                            updateUI(filmDetail)
                            stream.removeObservers(viewLifecycleOwner)
                        } else {
                            progressBarPoster.isVisible = false
                            cloudImageView.isVisible = true
                            errorTextView.isVisible = true
                            retryButton.isClickable = true
                            retryButton.isVisible = true
                        }
                    })
                }
                responseDb.removeObservers(viewLifecycleOwner)
            }
        )
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
        name.text = filmDetail.name
        description.text = filmDetail.description
        genre.text = context?.getString(R.string.genre)?.format(filmDetail.genre)
        country.text = context?.getString(R.string.country)?.format(filmDetail.country)
        year.text = context?.getString(R.string.year)?.format(filmDetail.year)
        Picasso.get()
            .load(filmDetail.posterUrl)
            .into(poster, object: Callback {
                override fun onSuccess() {
                    progressBarPoster.isVisible = false
                }

                override fun onError(e: Exception?) {
                    return
                }
            })
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