package com.example.khudyakovlab.ui.adapters.diffutils

import androidx.recyclerview.widget.DiffUtil
import com.example.khudyakovlab.data.network.models.FilmCard

class FilmCardDiffUtilCallback : DiffUtil.ItemCallback<FilmCard>() {
    override fun areItemsTheSame(oldItem: FilmCard, newItem: FilmCard): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FilmCard, newItem: FilmCard): Boolean {
        return oldItem.name == newItem.name && oldItem.poster == newItem.poster
    }

}