package com.arieldywelski.movieschallenge.utils

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.arieldywelski.movieschallenge.view.MoviesLoadStateViewHolder

class MoviesLoadStateAdapter(
  private val retry: () -> Unit
) : LoadStateAdapter<MoviesLoadStateViewHolder>() {
  override fun onBindViewHolder(holder: MoviesLoadStateViewHolder, loadState: LoadState) {
    holder.bind(loadState)
  }

  override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MoviesLoadStateViewHolder {
    return MoviesLoadStateViewHolder.create(parent, retry)
  }
}