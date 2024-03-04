package com.example.khudyakovlab

import android.app.Application
import android.content.Context
import com.example.khudyakovlab.data.database.FilmDatabase
import com.example.khudyakovlab.data.network.api.KinopoiskApi
import com.example.khudyakovlab.data.repositories.db.RepositoryFavoriteDbImpl
import com.example.khudyakovlab.data.repositories.kinopoiskapi.RepositoryKinopoiskApiImpl
import com.example.khudyakovlab.di.AppComponent
import com.example.khudyakovlab.di.DaggerAppComponent
import javax.inject.Inject

class LabApplication : Application() {

    lateinit var appComponent: AppComponent

    @Inject
    lateinit var kinopoiskApi: KinopoiskApi

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().context(this).build()
        appComponent.inject(this)
    }

    @Inject
    fun initializeRepositories(
        kinopoiskApi: KinopoiskApi,
        database: FilmDatabase
    ) {
        RepositoryKinopoiskApiImpl.initialize(kinopoiskApi)
        RepositoryFavoriteDbImpl.initialize(database.dao())
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is LabApplication -> this.appComponent
        else -> this.applicationContext.appComponent
    }