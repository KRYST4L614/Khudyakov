package com.example.khudyakovlab.data.models.repositories.db

import androidx.lifecycle.LiveData
import com.example.khudyakovlab.data.models.FilmDetail
import com.example.khudyakovlab.data.models.database.daos.FilmDAO
import java.util.concurrent.Executors

internal class RepositoryFavoriteDbImpl(
    private val dao: FilmDAO,
) : RepositoryFavoriteDb {
    private val executor = Executors.newSingleThreadExecutor()
    override fun getFilms(): LiveData<List<FilmDetail>> = dao.getFilms()

    override fun getFilm(id: String): LiveData<FilmDetail?> = dao.getFilm(id)

    override fun deleteFilm(id: String) {
        executor.execute {
            dao.deleteFilm(id)
        }
    }

    override fun addFilm(film: FilmDetail) {
        executor.execute {
            dao.addFilm(film)
        }
    }

    companion object {
        private var INSTANCE: RepositoryFavoriteDbImpl? = null

        fun initialize(
            dao: FilmDAO
        ) {
            if (INSTANCE == null) {
                INSTANCE = RepositoryFavoriteDbImpl(dao)
            }
        }

        fun get(): RepositoryFavoriteDbImpl {
            return INSTANCE
                ?: throw IllegalStateException("${this::class::java.name} must be initialized")
        }
    }
}