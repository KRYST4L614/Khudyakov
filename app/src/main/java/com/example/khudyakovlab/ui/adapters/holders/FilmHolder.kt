package com.example.khudyakovlab.ui.adapters.holders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.khudyakovlab.data.network.models.FilmCard
import com.example.khudyakovlab.data.database.models.FilmDetail

abstract class FilmHolder(
    view: View
) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
    abstract fun bind(filmItem: FilmCard?, featuredFilmsId: Collection<String>)
    abstract fun bindFromDb(filmDetail: FilmDetail)
}