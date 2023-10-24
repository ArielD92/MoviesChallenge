package com.arieldywelski.movieschallenge.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.arieldywelski.movieschallenge.api.MovieAPIService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MoviesRepository @Inject constructor(
  private val service: MovieAPIService
) {

  fun getNowPlayingMMoviesStream(): Flow<PagingData<Movie>> {
    return Pager(
      config = PagingConfig(
        enablePlaceholders = false,
        pageSize = NETWORK_PAGE_SIZE
      ),
      pagingSourceFactory = {
        MoviesPagingSource(service)
      }
    ).flow
  }

  companion object {
    private const val NETWORK_PAGE_SIZE = 30
  }
}