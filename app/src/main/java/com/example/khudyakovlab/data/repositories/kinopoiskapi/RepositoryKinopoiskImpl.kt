package com.example.khudyakovlab.data.repositories.kinopoiskapi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.khudyakovlab.data.database.models.FilmDetail
import com.example.khudyakovlab.data.network.models.FilmResponse
import com.example.khudyakovlab.data.network.models.FilmsResponse
import com.example.khudyakovlab.data.network.api.KinopoiskApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal class RepositoryKinopoiskApiImpl private constructor(
    private val api: KinopoiskApi,
) : RepositoryKinopoiskApi {

    override suspend fun fetchTop100Popular(page: Int): Response<FilmsResponse> {
        return api.fetchTop100PopularFilms(page)
    }

    override fun fetchFilm(filmId: String): LiveData<FilmDetail?> {
        return fetchKinopoiskFilm(filmId)
    }

    private fun fetchKinopoiskFilm(filmId: String): LiveData<FilmDetail?> {
        val call: Call<FilmResponse> = api.fetchFilm(filmId)
        val responseLiveData: MutableLiveData<FilmDetail?> = MutableLiveData()
        call.enqueue(object : Callback<FilmResponse> {
            override fun onFailure(call: Call<FilmResponse>, t: Throwable) {
                responseLiveData.value = null
            }

            override fun onResponse(call: Call<FilmResponse>, response: Response<FilmResponse>) {
                responseLiveData.value = FilmDetail(
                    response.body()!!.poster,
                    response.body()!!.name,
                    response.body()!!.description,
                    response.body()!!.id,
                    response.body()!!.genres.toString(),
                    response.body()!!.countries.toString(),
                    response.body()!!.year
                )
            }
        })
        return responseLiveData
    }

    companion object {
        private var INSTANCE: RepositoryKinopoiskApiImpl? = null

        fun initialize(
            api: KinopoiskApi
        ) {
            if (INSTANCE == null) {
                INSTANCE = RepositoryKinopoiskApiImpl(api)
            }
        }

        fun get(): RepositoryKinopoiskApiImpl {
            return INSTANCE
                ?: throw IllegalStateException("${this::class::java.name} must be initialized")
        }
    }
}