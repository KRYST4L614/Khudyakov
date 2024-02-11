package com.example.khudyakovlab.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.khudyakovlab.models.FilmDetail
import com.example.khudyakovlab.FilmDbRepository

class FilmDetailViewModel() : ViewModel() {
    private val filmRepository = FilmDbRepository.get()
    private val filmIdLiveData: MutableLiveData<String> = MutableLiveData()


    val filmLiveData: LiveData<FilmDetail?> = filmIdLiveData.switchMap { filmId: String ->
        filmRepository.getFilm(filmId)
    }

    fun loadFilm(filmId: String) {
        filmIdLiveData.value = filmId
    }
}