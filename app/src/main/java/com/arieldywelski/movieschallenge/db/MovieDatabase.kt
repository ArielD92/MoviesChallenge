package com.arieldywelski.movieschallenge.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [MovieModel::class], version = 1)
abstract class MovieDatabase : RoomDatabase() {
  abstract fun movieDao(): MovieDao
}