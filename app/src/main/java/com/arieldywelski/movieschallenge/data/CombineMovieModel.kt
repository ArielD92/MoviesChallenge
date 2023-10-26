package com.arieldywelski.movieschallenge.data

data class CombineMovieModel(
  val isMovieAdult: Boolean,
  val movieId: Long,
  val movieTitle: String,
  val movieOriginalTitle: String,
  val movieOriginalLanguage:String,
  val movieBackdropPath: String?,
  val moviePosterPath: String?,
  val movieReleaseDate: String,
  val movieOverview: String,
  val movieVoteAverage: Double,
  val movieVoteCount: Long,
  val movieGenreIds: List<Int>,
  val moviePopularity: Double,
  var isLiked: Boolean
)
