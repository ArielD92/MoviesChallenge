package com.arieldywelski.movieschallenge.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.arieldywelski.movieschallenge.api.MovieAPIService
import com.arieldywelski.movieschallenge.db.MovieDao
import com.arieldywelski.movieschallenge.db.MovieDatabase
import com.arieldywelski.movieschallenge.db.MovieModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class MoviesRepository @Inject constructor(
  private val service: MovieAPIService,
  private val movieDao: MovieDao
) {
  fun getMoviesStream(query: String?): Flow<PagingData<CombineMovieModel>> {
    return Pager(
      config = PagingConfig(
        enablePlaceholders = false,
        pageSize = NETWORK_PAGE_SIZE
      ),
      pagingSourceFactory = {
        MoviesPagingSource(service, query)
      }
    ).flow
  }

  fun getLikedMovies() = movieDao.getLikedMovies()
  suspend fun getMovieDetails(movieId: Long) = service.getMovieDetails(movieId = movieId.toInt())
  suspend fun insertLikedMovie(movieId: Long) = movieDao.insertLikedMovie(movieModel = MovieModel(movieId = movieId))
  suspend fun removeLikedMovie(movieId: Long) = movieDao.removeLikedMovie(movieModel = MovieModel(movieId = movieId))

  companion object {
    private const val NETWORK_PAGE_SIZE = 30
  }
}