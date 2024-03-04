package com.example.khudyakovlab.di

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.example.khudyakovlab.LabApplication
import com.example.khudyakovlab.data.network.models.FilmCard
import com.example.khudyakovlab.data.database.FilmDatabase
import com.example.khudyakovlab.data.network.models.FilmResponse
import com.example.khudyakovlab.data.network.models.FilmsResponse
import com.example.khudyakovlab.data.network.api.KinopoiskApi
import com.example.khudyakovlab.data.network.deserializers.FilmCardDeserializer
import com.example.khudyakovlab.data.network.deserializers.FilmDetailDeserializer
import com.example.khudyakovlab.data.paging.FilmCardPagingSource
import com.example.khudyakovlab.data.repositories.db.RepositoryFavoriteDb
import com.example.khudyakovlab.data.repositories.db.RepositoryFavoriteDbImpl
import com.example.khudyakovlab.data.repositories.kinopoiskapi.RepositoryKinopoiskApi
import com.example.khudyakovlab.data.repositories.kinopoiskapi.RepositoryKinopoiskApiImpl
import com.example.khudyakovlab.ui.FilmFragment
import com.example.khudyakovlab.ui.MainFragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val DATABASE_NAME = "film-database"
private const val BASE_URL = "https://kinopoiskapiunofficial.tech/"

@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder
        fun build(): AppComponent
    }

    fun inject(labApplication: LabApplication)
    fun inject(labApplication: MainFragment)
    fun inject(filmFragment: FilmFragment)

    val pager: Pager<Int, FilmCard>

    val kinopoiskApi: KinopoiskApi
}

@Module(includes = [NetworkModule::class, DataBaseModule::class])
class AppModule

@Module
class DataBaseModule {
    @Provides
    fun provideRepositoryFavoriteDb(): RepositoryFavoriteDb {
        return RepositoryFavoriteDbImpl.get()
    }

    @Provides
    fun providesFilmDatabase(context: Context): FilmDatabase {
        return Room.databaseBuilder(
            context,
            FilmDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
}

@Module
class NetworkModule {
    @Provides
    fun providesPager(): Pager<Int, FilmCard> {
        return Pager(
            PagingConfig(
                pageSize = 100,
                enablePlaceholders = true,
                prefetchDistance = 15
            ), 1
        ) {
            FilmCardPagingSource()
        }
    }

    @Provides
    fun providesRetrofit(converter: GsonConverterFactory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(converter)
            .build()
    }

    @Provides
    fun providesFilmConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    fun providesGson(
        filmCardDeserializer: FilmCardDeserializer,
        filmDetailDeserializer: FilmDetailDeserializer
    ): Gson {
        return GsonBuilder().registerTypeAdapter(FilmsResponse::class.java, filmCardDeserializer)
            .registerTypeAdapter(FilmResponse::class.java, filmDetailDeserializer).create()
    }

    @Provides
    fun provideFilmCardDeserializer(): FilmCardDeserializer = FilmCardDeserializer()

    @Provides
    fun provideFilmDetailDeserializer(): FilmDetailDeserializer = FilmDetailDeserializer()

    @Provides
    fun provideKinopoiskApi(retrofit: Retrofit): KinopoiskApi {
        return retrofit.create(KinopoiskApi::class.java)
    }

    @Provides
    fun provideRepositoryKinopoiskApi(): RepositoryKinopoiskApi {
        return RepositoryKinopoiskApiImpl.get()
    }
}