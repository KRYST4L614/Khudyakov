package com.example.khudyakovlab.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photogallery.R


class MainActivity : AppCompatActivity(), MainFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)


        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.FragmentContainer, MainFragment.newInstance())
                .commit()
        }
    }
    override fun onFilmSelected(filmId: String) {
        val fragment = FilmFragment.newInstance(filmId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.FragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}