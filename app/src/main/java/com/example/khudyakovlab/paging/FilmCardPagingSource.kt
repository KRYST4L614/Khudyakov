package com.example.khudyakovlab.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.khudyakovlab.models.FilmCard
import com.example.khudyakovlab.KinopoiskFetchR

class FilmCardPagingSource : PagingSource<Int, FilmCard>() {
    override fun getRefreshKey(state: PagingState<Int, FilmCard>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val closePage = state.closestPageToPosition(anchorPosition) ?: return null
        return closePage.prevKey?.minus(1) ?: closePage.nextKey?.plus(1)
    }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmCard> {
            val page = params.key ?: 1
            val pageSize = params.loadSize.coerceAtMost(20)
            try {
                val response = KinopoiskFetchR.fetchTop100Popular(page)
                val newPrevKey = if (page == 1) null else page - 1;
                val newNextKey = if ((response.body()?.filmsItems?.size ?: 0) < pageSize) null else page + 1
                return LoadResult.Page(
                    response.body()?.filmsItems ?: emptyList(), newPrevKey,
                    newNextKey
                )
            } catch (t: Throwable) {
                return LoadResult.Error(t)
            }
        }
}