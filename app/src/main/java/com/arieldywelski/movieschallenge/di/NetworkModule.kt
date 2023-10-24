package com.arieldywelski.movieschallenge.di

import com.arieldywelski.movieschallenge.api.MovieAPIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

  @Singleton
  @Provides
  fun provideMovieAPIService(): MovieAPIService {
    return MovieAPIService.create()
  }
}