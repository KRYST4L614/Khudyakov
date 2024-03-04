package com.example.khudyakovlab.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.khudyakovlab.data.database.models.FilmDetail
import com.example.khudyakovlab.data.database.converters.FilmTypeConverters
import com.example.khudyakovlab.data.database.daos.FilmDAO

@Database(entities = [FilmDetail::class], version = 1, exportSchema = false)
@TypeConverters(FilmTypeConverters::class)
abstract class FilmDatabase : RoomDatabase() {
    abstract fun dao(): FilmDAO
}
