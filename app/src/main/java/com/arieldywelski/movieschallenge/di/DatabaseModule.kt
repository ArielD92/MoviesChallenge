package com.arieldywelski.movieschallenge.di

import android.content.Context
import androidx.room.Room
import com.arieldywelski.movieschallenge.db.MovieDao
import com.arieldywelski.movieschallenge.db.MovieDatabase
import com.arieldywelski.movieschallenge.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

  @Provides
  fun provideMovieDao(movieDatabase: MovieDatabase): MovieDao {
    return movieDatabase.movieDao()
  }

  @Singleton
  @Provides
  fun provideMovieDatabase(@ApplicationContext context: Context): MovieDatabase {
    return Room.databaseBuilder(
      context = context,
      klass = MovieDatabase::class.java,
      name = Constants.MOVIE_DATABASE_NAME
    ).build()
  }
}