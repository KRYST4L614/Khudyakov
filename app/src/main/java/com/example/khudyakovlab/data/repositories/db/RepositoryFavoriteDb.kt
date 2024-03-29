package com.example.khudyakovlab.data.repositories.db

import androidx.lifecycle.LiveData
import com.example.khudyakovlab.data.database.models.FilmDetail

interface RepositoryFavoriteDb {
    fun getFilms(): LiveData<List<FilmDetail>>

    fun getFilm(id: String): LiveData<FilmDetail?>

    fun deleteFilm(id: String)

    fun addFilm(film: FilmDetail)

}