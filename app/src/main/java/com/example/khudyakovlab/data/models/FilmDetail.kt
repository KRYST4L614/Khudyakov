package com.example.khudyakovlab.data.models

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FilmDetail(
    var posterUrl: Uri,
    var name: String,
    var description: String,
    @PrimaryKey val id: String,
    var genre: String,
    var country: String,
    var year: String,
)