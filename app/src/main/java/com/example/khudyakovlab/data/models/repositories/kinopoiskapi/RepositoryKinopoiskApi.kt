package com.example.khudyakovlab.data.models.repositories.kinopoiskapi

import androidx.lifecycle.LiveData
import com.example.khudyakovlab.data.models.FilmDetail
import com.example.khudyakovlab.data.models.network.FilmsResponse
import retrofit2.Response

interface RepositoryKinopoiskApi {
    suspend fun fetchTop100Popular(page: Int): Response<FilmsResponse>
    fun fetchFilm(filmId: String): LiveData<FilmDetail?>
}