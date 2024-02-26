package com.example.khudyakovlab.data.models.network

import com.example.khudyakovlab.data.models.FilmCard

data class FilmsResponse(
    var filmsItems: List<FilmCard>
)