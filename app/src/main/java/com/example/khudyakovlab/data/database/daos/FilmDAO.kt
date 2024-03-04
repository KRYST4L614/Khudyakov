package com.example.khudyakovlab.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.khudyakovlab.data.database.models.FilmDetail

@Dao
interface FilmDAO {
    @Query("SELECT * FROM FilmDetail")
    fun getFilms(): LiveData<List<FilmDetail>>

    @Query("SELECT * FROM FilmDetail WHERE id=(:id)")
    fun getFilm(id: String): LiveData<FilmDetail?>

    @Query("SELECT * FROM FilmDetail")
    fun getIdFilms(): LiveData<List<FilmDetail>>

    @Insert
    fun addFilm(film: FilmDetail)

    @Query("DELETE FROM FilmDetail WHERE id=(:id)")
    fun deleteFilm(id: String)

    @Query("SELECT EXISTS(SELECT * FROM FilmDetail WHERE id=(:id))")
    fun containsFilm(id: String): Boolean
}