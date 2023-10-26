package com.arieldywelski.movieschallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arieldywelski.movieschallenge.data.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
  private val repository: MoviesRepository
) : ViewModel() {

  val item = MutableLiveData<Movie>()
  private var movieId: Long = 0

  fun getMovieDetails(movieId: Long) {
    this.movieId = movieId
    viewModelScope.launch {
      val response = repository.getMovieDetails(movieId = movieId)
      val likedMovies = repository.getLikedMovies()
      val isLiked = likedMovies.first().any { it.movieId == movieId }
      if (response.isSuccessful) {
        response.body()?.let {
          val movie = Movie(
            movieName = it.movieTitle,
            moviePosterPath = it.moviePosterPath,
            movieDescription = it.movieOverview,
            movieReleaseDate = it.movieReleaseDate,
            isLiked = isLiked
          )
          item.postValue(movie)
        }
      }
    }
  }

  fun onMovieLiked(isLiked: Boolean) {
    viewModelScope.launch {
      if (isLiked) {
        repository.insertLikedMovie(movieId)
      } else {
        repository.removeLikedMovie(movieId)
      }
      item.value = item.value?.copy(isLiked = isLiked)
    }
  }
  data class Movie(
    val movieName: String,
    val moviePosterPath: String,
    val movieDescription: String,
    val movieReleaseDate: String,
    val isLiked: Boolean
  )
}