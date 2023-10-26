package com.arieldywelski.movieschallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arieldywelski.movieschallenge.data.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
  private val repository: MoviesRepository
) : ViewModel() {

  val item = MutableLiveData<Movie>()

  fun getMovieDetails(movieId: Int) {
    viewModelScope.launch {
      val response = repository.getMovieDetails(movieId = movieId)
      if (response.isSuccessful) {
        response.body()?.let {
          val movie = Movie(
            movieName = it.movieTitle,
            moviePosterPath = it.moviePosterPath,
            movieDescription = it.movieOverview,
            movieReleaseDate = it.movieReleaseDate
          )
          item.postValue(movie)
        }
      }
    }
  }

  class Movie(
    val movieName: String,
    val moviePosterPath: String,
    val movieDescription: String,
    val movieReleaseDate: String,
  )
}