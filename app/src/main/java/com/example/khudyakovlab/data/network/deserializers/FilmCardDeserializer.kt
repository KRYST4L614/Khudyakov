package com.example.khudyakovlab.data.network.deserializers

import androidx.core.net.toUri
import com.example.khudyakovlab.data.network.models.FilmCard
import com.example.khudyakovlab.data.network.models.FilmsResponse
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import java.lang.reflect.Type

class FilmCardDeserializer : JsonDeserializer<FilmsResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): FilmsResponse {
        val jsonObject: JsonArray = json?.asJsonObject?.get("films")?.asJsonArray!!
        val jsonArray: JsonArray = jsonObject.asJsonArray!!
        val films: MutableList<FilmCard> = mutableListOf()
        jsonArray.forEach { it ->
            val countriesJson: JsonArray = it.asJsonObject.get("countries").asJsonArray
            val countries: ArrayList<String> = ArrayList()
            countriesJson.forEach {
                countries.add(it.asJsonObject.get("country").asString)
            }
            val genresJson: JsonArray = it.asJsonObject.get("genres").asJsonArray
            val genres: ArrayList<String> = ArrayList()
            genresJson.forEach {
                genres.add(it.asJsonObject.get("genre").asString)
            }
            films.add(
                FilmCard(
                    if (it.asJsonObject.get("filmId") !is JsonNull) it.asJsonObject.get("filmId").asString else "",
                    if (it.asJsonObject.get("nameRu") !is JsonNull) it.asJsonObject.get("nameRu").asString else "",
                    if (it.asJsonObject.get("year") !is JsonNull) it.asJsonObject.get("year").asString else "",
                    countries,
                    genres,
                    if (it.asJsonObject.get("posterUrlPreview") !is JsonNull) it.asJsonObject.get("posterUrlPreview").asString.toUri() else "".toUri()
                )
            )
        }
        return FilmsResponse(films)
    }
}