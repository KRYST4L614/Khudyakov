package com.example.khudyakovlab.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.khudyakovlab.data.models.FilmCard
import com.example.khudyakovlab.data.models.FilmDetail
import com.example.khudyakovlab.data.models.repositories.db.RepositoryFavoriteDb
import com.example.khudyakovlab.data.models.repositories.kinopoiskapi.RepositoryKinopoiskApi
import javax.inject.Inject

class FilmViewModel @Inject constructor(
    private val repositoryApi: RepositoryKinopoiskApi,
    private val repositoryDb: RepositoryFavoriteDb,
    pager: Pager<Int, FilmCard>
) : ViewModel() {
    private val filmIdLiveData: MutableLiveData<String> = MutableLiveData()
    val pagerLiveData = pager.liveData.cachedIn(this.viewModelScope)

    val filmLiveData: LiveData<FilmDetail?> = filmIdLiveData.switchMap { filmId: String ->
        repositoryDb.getFilm(filmId)
    }


    fun loadFilm(filmId: String) {
        filmIdLiveData.value = filmId
    }

    fun getFilms(): LiveData<List<FilmDetail>> = repositoryDb.getFilms()

    fun getFilm(filmId: String): LiveData<FilmDetail?> = repositoryDb.getFilm(filmId)

    fun deleteFilm(filmId: String): Unit = repositoryDb.deleteFilm(filmId)

    fun addFilm(film: FilmDetail) = repositoryDb.addFilm(film)

    fun fetchFilm(filmId: String): LiveData<FilmDetail?> = repositoryApi.fetchFilm(filmId)

    class Factory @Inject constructor(
        private val repositoryApi: RepositoryKinopoiskApi,
        private val repositoryDb: RepositoryFavoriteDb,
        private val pager: Pager<Int, FilmCard>
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FilmViewModel(repositoryApi, repositoryDb, pager) as T
        }
    }

}