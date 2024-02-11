package com.example.khudyakovlab.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.khudyakovlab.models.FilmDetail

@Database(entities = [FilmDetail::class], version = 1, exportSchema = false)
@TypeConverters(FilmTypeConverters::class)
abstract class FilmDatabase : RoomDatabase() {
    abstract fun filmDAO(): FilmDAO
}
