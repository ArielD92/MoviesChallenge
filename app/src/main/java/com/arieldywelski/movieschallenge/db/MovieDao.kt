package com.arieldywelski.movieschallenge.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertLikedMovie(movieModel: MovieModel)

  @Query("SELECT * FROM movie_data WHERE is_liked_movie= :isLikedMovie")
  suspend fun getLikedMovies(isLikedMovie: Boolean)
}