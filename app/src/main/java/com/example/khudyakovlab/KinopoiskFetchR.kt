package com.example.khudyakovlab

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.khudyakovlab.network.api.FilmResponse
import com.example.khudyakovlab.network.api.FilmsResponse
import com.example.khudyakovlab.network.api.KinopoiskApi
import com.example.khudyakovlab.network.api.deserializers.FilmCardDeserializer
import com.example.khudyakovlab.network.api.deserializers.FilmDetailDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object KinopoiskFetchR {

    private val kinopoiskApi: KinopoiskApi

    init {
        val gson: Gson = GsonBuilder().registerTypeAdapter(FilmsResponse::class.java, FilmCardDeserializer())
            .registerTypeAdapter(FilmResponse::class.java, FilmDetailDeserializer()).create()
        val converter = GsonConverterFactory.create(gson)
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://kinopoiskapiunofficial.tech/")
            .addConverterFactory(converter)
            .build()
        kinopoiskApi = retrofit.create(KinopoiskApi::class.java)
    }

    suspend fun fetchTop100Popular(page: Int): Response<FilmsResponse> {
        val request: Response<FilmsResponse> = kinopoiskApi.fetchTop100PopularFilms(page)
        return request
    }

    fun fetchFilm(id: String): LiveData<FilmResponse?> {
        val call: Call<FilmResponse> = kinopoiskApi.fetchFilm(id)
        val responseLiveData: MutableLiveData<FilmResponse> = MutableLiveData()
        try {
            call.enqueue(object: Callback<FilmResponse> {
                override fun onFailure(call: Call<FilmResponse>, t: Throwable) {
                    responseLiveData.value = null
                }

                override fun onResponse(call: Call<FilmResponse>, response: Response<FilmResponse>) {
                    responseLiveData.value = response.body()
                }
            })
            return responseLiveData
        }
        catch (t: Throwable) {
            throw t
        }
    }
}