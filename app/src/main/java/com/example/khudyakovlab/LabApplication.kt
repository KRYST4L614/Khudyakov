package com.example.khudyakovlab
import android.app.Application

class LabApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FilmDbRepository.initialize(this)
    }
}