package com.example.khudyakovlab.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.khudyakovlab.ui.adapters.holders.FilmHolder
import com.example.khudyakovlab.ui.adapters.holders.FilmHolderImpl
import com.example.khudyakovlab.data.network.models.FilmCard
import com.example.khudyakovlab.ui.MainFragment
import com.example.khudyakovlab.ui.viewModels.FilmViewModel
import com.example.photogallery.databinding.FragmentFilmCardBinding

class FilmPagingAdapter(
    utilCallback: DiffUtil.ItemCallback<FilmCard>,
    private val viewModel: FilmViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val callback: MainFragment.Callbacks?,
) : PagingDataAdapter<FilmCard, FilmHolder>(utilCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmHolderImpl {
        val binding =
            FragmentFilmCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmHolderImpl(binding, lifecycleOwner, viewModel, callback)
    }

    override fun onBindViewHolder(holder: FilmHolder, position: Int) {
        holder.bind(getItem(position), viewModel.featuredFilms)
    }
}