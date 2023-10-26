package com.arieldywelski.movieschallenge.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.arieldywelski.movieschallenge.api.MovieAPIService
import com.arieldywelski.movieschallenge.db.MovieDao

private const val STARTING_PAGE_INDEX = 1

class MoviesPagingSource(
  private val service: MovieAPIService,
  private val query: String?
) : PagingSource<Int, CombineMovieModel>() {
  override fun getRefreshKey(state: PagingState<Int, CombineMovieModel>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      state.closestPageToPosition(anchorPosition)?.prevKey
    }
  }

  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CombineMovieModel> {
    val page = params.key ?: STARTING_PAGE_INDEX
    return try {
      val response = when {
        query.isNullOrEmpty() -> service.getNowPlayingMovies(page = page)
        else -> service.getSearchMovies(query = query, page = page)
      }
      val movies = response.moviesList.sortedByDescending { it.movieVoteAverage }.map {
        CombineMovieModel(
          isMovieAdult = it.isMovieAdult,
          movieId = it.movieId,
          movieTitle = it.movieTitle,
          movieOriginalTitle = it.movieOriginalTitle,
          movieOriginalLanguage = it.movieOriginalLanguage,
          movieBackdropPath = it.movieBackdropPath,
          moviePosterPath = it.moviePosterPath,
          movieReleaseDate = it.movieReleaseDate,
          movieOverview = it.movieOverview,
          movieVoteAverage = it.movieVoteAverage,
          movieVoteCount = it.movieVoteCount,
          movieGenreIds = it.movieGenreIds,
          moviePopularity = it.moviePopularity,
          isLiked = false
        )
      }

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