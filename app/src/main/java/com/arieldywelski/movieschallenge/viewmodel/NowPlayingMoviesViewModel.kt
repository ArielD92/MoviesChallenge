package com.arieldywelski.movieschallenge.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.arieldywelski.movieschallenge.data.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NowPlayingMoviesViewModel @Inject constructor(
  private val repository: MoviesRepository,
  private val savedStateHandle: SavedStateHandle
) : ViewModel() {
}

