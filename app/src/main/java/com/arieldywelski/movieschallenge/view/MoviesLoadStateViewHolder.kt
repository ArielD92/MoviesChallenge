package com.arieldywelski.movieschallenge.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.arieldywelski.movieschallenge.R
import com.arieldywelski.movieschallenge.databinding.MovieLoadStateItemViewBinding

class MoviesLoadStateViewHolder(
  private val binding: MovieLoadStateItemViewBinding,
  retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

  init {
    binding.retryButton.setOnClickListener { retry.invoke() }
  }

  fun bind(loadState: LoadState) {
    if (loadState is LoadState.Error) {
      binding.errorMessage.text = loadState.error.localizedMessage
    }
    with(binding) {
      progressBar.isVisible = loadState is LoadState.Loading
      retryButton.isVisible = loadState is LoadState.Error
      errorMessage.isVisible = loadState is LoadState.Error
    }
  }

  companion object {
    fun create(parent: ViewGroup, retry: () -> Unit): MoviesLoadStateViewHolder {
      val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.movie_load_state_item_view, parent, false)
      val binding = MovieLoadStateItemViewBinding.bind(view)
      return MoviesLoadStateViewHolder(binding, retry)
    }
  }
}