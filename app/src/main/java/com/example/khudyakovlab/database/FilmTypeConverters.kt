package com.example.khudyakovlab.database

import android.net.Uri
import androidx.core.net.toUri
import androidx.room.TypeConverter


class FilmTypeConverters {

    @TypeConverter
    fun fromPosterUri(posterUri: Uri?): String? {
        return posterUri?.toString()
    }

    @TypeConverter
    fun toPosterUri(posterUri: String?): Uri? {
        return posterUri?.let {
            posterUri.toUri()
        }
    }
}