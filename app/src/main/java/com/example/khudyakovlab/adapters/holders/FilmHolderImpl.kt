package com.example.khudyakovlab.adapters.holders

import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import com.example.khudyakovlab.Utils
import com.example.khudyakovlab.data.models.FilmCard
import com.example.khudyakovlab.data.models.FilmDetail
import com.example.khudyakovlab.ui.MainFragment
import com.example.khudyakovlab.viewModels.FilmViewModel
import com.example.photogallery.databinding.FragmentFilmCardBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class FilmHolderImpl(
    private val binding: FragmentFilmCardBinding,
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: FilmViewModel,
    private val callback: MainFragment.Callbacks?,
) : FilmHolder(binding.root) {
    private lateinit var filmId: String

    init {
        binding.root.also {
            it.setOnClickListener(this)
            it.setOnLongClickListener(this)
        }
        binding.starImage.apply {
            isVisible = false
        }
    }

    override fun bind(filmItem: FilmCard?, featuredFilmsId: Collection<String>) {
        filmItem?.let {
            binding.name.text = filmItem.name
            binding.genre.text =
                Utils.removeSquareBrackets(filmItem.genres.first().toString()).replaceFirstChar {
                    it.uppercase()
                }.plus(" (${filmItem.year})")
            filmId = filmItem.id
            binding.starImage.isVisible = featuredFilmsId.contains(filmId)
            Picasso.get().load(filmItem.poster).into(binding.poster, object : Callback {
                    override fun onSuccess() {
                        binding.posterProgressBar.isVisible = false
                    }

                    override fun onError(e: Exception?) {
                        return
                    }
                })
            binding.shimmer.hideShimmer()
        }
    }

    override fun bindFromDb(filmDetail: FilmDetail) {
        binding.name.text = filmDetail.name
        binding.genre.text = filmDetail.genre.split(',').first().replaceFirstChar {
            it.uppercase()
        }.plus(" (${filmDetail.year})")
        binding.starImage.isVisible = true
        Picasso.get().load(filmDetail.posterUrl).into(binding.poster, object : Callback {
                override fun onSuccess() {
                    binding.posterProgressBar.isVisible = false
                    binding.shimmer.hideShimmer()
                }

                override fun onError(e: Exception?) {
                    return
                }
            })
        filmId = filmDetail.id
    }

    override fun onLongClick(v: View?): Boolean {
        if (!binding.starImage.isVisible) {
            val response = viewModel.fetchFilm(filmId)
            response.observe(lifecycleOwner) {
                it?.let {
                    viewModel.addFilm(
                        FilmDetail(
                            it.posterUrl,
                            it.name,
                            it.description,
                            it.id,
                            Utils.removeSquareBrackets(it.genre),
                            Utils.removeSquareBrackets(it.country),
                            it.year
                        )
                    )
                    binding.starImage.isVisible = true
                }
            }
        } else {
            viewModel.deleteFilm(filmId)
            binding.starImage.isVisible = false
        }
        return true
    }

    override fun onClick(v: View?) {
        callback?.onFilmSelected(filmId)
    }
}