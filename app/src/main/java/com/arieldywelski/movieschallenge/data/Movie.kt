package com.arieldywelski.movieschallenge.data

import com.google.gson.annotations.SerializedName

data class Movie(
  @field:SerializedName("adult") val isMovieAdult: Boolean,
  @field:SerializedName("id") val movieId: Long,
  @field:SerializedName("title") val movieTitle: String,
  @field:SerializedName("original_title") val movieOriginalTitle: String,
  @field:SerializedName("original_language") val movieOriginalLanguage:String,
  @field:SerializedName("backdrop_path") val movieBackdropPath: String,
  @field:SerializedName("poster_path") val moviePosterPath: String,
  @field:SerializedName("release_date") val movieReleaseDate: String,
  @field:SerializedName("overview") val movieOverview: String,
  @field:SerializedName("vote_average") val movieVoteAverage: Double,
  @field:SerializedName("vote_count") val movieVoteCount: Long,
  @field:SerializedName("genre_ids") val movieGenreIds: List<Int>,
  @field:SerializedName("popularity") val moviePopularity: Double
)
