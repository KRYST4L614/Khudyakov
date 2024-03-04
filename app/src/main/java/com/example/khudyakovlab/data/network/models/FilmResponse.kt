package com.example.khudyakovlab.data.network.models

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class FilmResponse(
    @SerializedName("kinopoiskId") var id: String = "",
    @SerializedName("nameRu") var name: String = "",
    var description: String = "",
    var year: String = "",
    var countries: List<String> = ArrayList(),
    var genres: List<String> = ArrayList(),
    @SerializedName("posterUrl") var poster: Uri,
)