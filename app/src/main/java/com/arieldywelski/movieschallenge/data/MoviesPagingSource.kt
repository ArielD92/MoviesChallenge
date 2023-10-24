package com.arieldywelski.movieschallenge.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arieldywelski.movieschallenge.api.MovieAPIService

private const val STARTING_PAGE_INDEX = 1

class MoviesPagingSource(
  private val service: MovieAPIService,
) : PagingSource<Int, Movie>() {
  override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      state.closestPageToPosition(anchorPosition)?.prevKey
    }
  }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
    val page = params.key ?: STARTING_PAGE_INDEX
    return try {
      val response = service.getNowPlayingMovies()
      val movies = response.moviesList
      val nextKey = if (movies.isEmpty()) {
        null
      } else {
        page + (params.loadSize / NETWORK_PAGE_SIZE)
      }
      LoadResult.Page(
        data = movies,
        prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
        nextKey = nextKey
      )
    } catch (exception: Exception) {
      return LoadResult.Error(exception)
    }
  }

  companion object {
    const val NETWORK_PAGE_SIZE = 30
  }
}