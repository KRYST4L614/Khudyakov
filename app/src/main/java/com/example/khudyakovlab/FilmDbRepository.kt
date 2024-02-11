package com.example.khudyakovlab

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.khudyakovlab.database.FilmDAO
import com.example.khudyakovlab.database.FilmDatabase
import com.example.khudyakovlab.models.FilmDetail
import java.util.concurrent.Executors

private const val DATABASE_NAME = "film-database"

class FilmDbRepository private constructor(context: Context) {

    private val database: FilmDatabase = Room.databaseBuilder(
        context.applicationContext,
        FilmDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val executor = Executors.newSingleThreadExecutor()

    private val dao: FilmDAO = database.filmDAO()

    fun getFilms(): LiveData< List<FilmDetail> > = dao.getFilms()

    fun getFilm(id: String): LiveData< FilmDetail? > = dao.getFilm(id)

    fun deleteFilm(id: String): Unit {
        executor.execute {
            dao.deleteFilm(id)
        }
    }

    fun addFilm(film: FilmDetail) {
        executor.execute {
            dao.addFilm(film)
        }
    }

    companion object {
        private var INSTANCE: FilmDbRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = FilmDbRepository(context)
            }
        }

        fun get(): FilmDbRepository {
            return INSTANCE ?: throw IllegalStateException("${this::class::java.name} must be initialized")
        }
    }
}