package com.example.khudyakovlab.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.photogallery.R
import com.example.photogallery.databinding.ActivityPhotoGalleryBinding


class MainActivity : AppCompatActivity(), MainFragment.Callbacks {
    private lateinit var binding: ActivityPhotoGalleryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.frame_layout, MainFragment.newInstance())
                .commit()
        }
    }

    override fun onFilmSelected(filmId: String) {
        val fragment = FilmFragment.newInstance(filmId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }
}