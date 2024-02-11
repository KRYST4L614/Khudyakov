package com.example.khudyakovlab.network.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface KinopoiskApi {
    @Headers("x-api-key: e30ffed0-76ab-4dd6-b41f-4c9da2b2735b")
    @GET(
        "api/v2.2/films/top?type=TOP_100_POPULAR_FILMS"
    )
    suspend fun fetchTop100PopularFilms(@Query("page") page: Int): Response<FilmsResponse>

    @Headers("x-api-key: e30ffed0-76ab-4dd6-b41f-4c9da2b2735b")
    @GET(
        "api/v2.2/films/{id}"
    )
    fun fetchFilm(@Path("id") id: String): Call<FilmResponse>

}