package com.arieldywelski.movieschallenge.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_data")
data class MovieModel(
  @ColumnInfo(name = "movie_id") @PrimaryKey val movieId: Long,
  @ColumnInfo(name = "is_liked_movie") val isLikedMovie: Boolean = false
)
