package com.example.khudyakovlab.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.khudyakovlab.data.network.models.FilmCard
import com.example.khudyakovlab.data.repositories.kinopoiskapi.RepositoryKinopoiskApiImpl

internal class FilmCardPagingSource : PagingSource<Int, FilmCard>() {
    override fun getRefreshKey(state: PagingState<Int, FilmCard>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val closePage = state.closestPageToPosition(anchorPosition) ?: return null
        return closePage.prevKey?.minus(1) ?: closePage.nextKey?.plus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmCard> {
        val page = params.key ?: 1
        val pageSize = params.loadSize.coerceAtMost(20)
        return try {
            val response = RepositoryKinopoiskApiImpl.get().fetchTop100Popular(page)
            val newPrevKey = if (page == 1) null else page - 1
            val newNextKey =
                if ((response.body()?.filmsItems?.size ?: 0) < pageSize) null else page + 1
            LoadResult.Page(
                response.body()?.filmsItems ?: emptyList(), newPrevKey,
                newNextKey
            )
        } catch (t: Throwable) {
            LoadResult.Error(t)
        }
    }
}