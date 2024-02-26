package com.example.khudyakovlab.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import com.example.khudyakovlab.adapters.diffutils.FilmDetailUtilCallback
import com.example.khudyakovlab.adapters.holders.FilmHolder
import com.example.khudyakovlab.adapters.holders.FilmHolderImpl
import com.example.khudyakovlab.data.models.FilmDetail
import com.example.khudyakovlab.ui.MainFragment
import com.example.khudyakovlab.viewModels.FilmViewModel
import com.example.photogallery.databinding.FragmentFilmCardBinding

class FilmDbAdapter(
    private val lifecycleOwner: LifecycleOwner,
    private val viewModel: FilmViewModel,
    private val callbacks: MainFragment.Callbacks?,
) : ListAdapter<FilmDetail, FilmHolder>(FilmDetailUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmHolderImpl {
        val binding =
            FragmentFilmCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FilmHolderImpl(binding, lifecycleOwner, viewModel, callbacks)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    override fun onBindViewHolder(holder: FilmHolder, position: Int) {
        holder.bindFromDb(getItem(position))
    }

}