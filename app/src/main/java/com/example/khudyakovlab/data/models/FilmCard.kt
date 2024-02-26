package com.example.khudyakovlab.data.models

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class FilmCard(
    @SerializedName("filmId") var id: String = "",
    @SerializedName("nameRu") var name: String = "",
    var year: String = "",
    var countries: List<String> = ArrayList(),
    var genres: List<String> = ArrayList(),
    @SerializedName("posterUrlPreview") var poster: Uri,
)