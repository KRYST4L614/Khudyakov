package com.example.khudyakovlab.ui.adapters.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.example.khudyakovlab.data.database.models.FilmDetail

class FilmDetailUtilCallback : DiffUtil.ItemCallback<FilmDetail>() {
    override fun areItemsTheSame(oldItem: FilmDetail, newItem: FilmDetail): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FilmDetail, newItem: FilmDetail): Boolean {
        return oldItem.name == newItem.name && oldItem.posterUrl == newItem.posterUrl
    }

}