package com.arieldywelski.movieschallenge.data

import com.google.gson.annotations.SerializedName

data class MoviesResponse(
  @field:SerializedName("total_pages") val totalPages: Int,
  @field:SerializedName("results") val moviesList: List<Movie> = emptyList(),
  val nextPage: Int? = null
)