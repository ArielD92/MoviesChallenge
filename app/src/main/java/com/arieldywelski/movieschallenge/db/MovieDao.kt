package com.arieldywelski.movieschallenge.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertLikedMovie(movieModel: MovieModel)

  @Query("SELECT * FROM movie_data")
  fun getLikedMovies(): Flow<List<MovieModel>>

  @Delete
  suspend fun removeLikedMovie(movieModel: MovieModel)
}