package com.example.khudyakovlab.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.khudyakovlab.paging.FilmCardPagingSource

class FilmCardViewModel : ViewModel() {
    val galleryLiveData = Pager(
        PagingConfig(
            pageSize = 100,
            enablePlaceholders = true,
            prefetchDistance = 15), 1
    ) {
        FilmCardPagingSource()
    }.flow.cachedIn(this.viewModelScope)
}