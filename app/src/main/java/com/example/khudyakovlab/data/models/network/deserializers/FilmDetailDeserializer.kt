package com.example.khudyakovlab.data.models.network.deserializers

import androidx.core.net.toUri
import com.example.khudyakovlab.Utils
import com.example.khudyakovlab.data.models.network.FilmResponse
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import java.lang.reflect.Type

class FilmDetailDeserializer : JsonDeserializer<FilmResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): FilmResponse {
        val id: String = json?.asJsonObject?.get("kinopoiskId")?.toString() ?: ""
        val name: String = json?.asJsonObject?.get("nameRu")?.toString() ?: ""
        val description: String = json?.asJsonObject?.get("description")?.toString() ?: ""
        val year: String = json?.asJsonObject?.get("year")?.toString() ?: ""
        val countriesJson: JsonArray =
            json?.asJsonObject?.get("countries")?.asJsonArray ?: JsonArray()
        val posterUrl = if (json?.asJsonObject?.get("posterUrl") !is JsonNull)
            json?.asJsonObject?.get("posterUrl")?.asString?.toUri() ?: "".toUri() else "".toUri()
        val countries: ArrayList<String> = ArrayList()
        countriesJson.forEach {
            countries.add(it.asJsonObject.get("country").asString)
        }
        val genresJson: JsonArray = json?.asJsonObject?.get("genres")?.asJsonArray ?: JsonArray()
        val genres: ArrayList<String> = ArrayList()
        genresJson.forEach {
            genres.add(it.asJsonObject.get("genre").asString)
        }
        return FilmResponse(
            id,
            Utils.removeDoubleQuotes(name),
            Utils.removeDoubleQuotes(description),
            year,
            countries,
            genres,
            posterUrl
        )
    }
}