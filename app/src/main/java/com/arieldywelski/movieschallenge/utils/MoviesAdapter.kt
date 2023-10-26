package com.arieldywelski.movieschallenge.utils

import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arieldywelski.movieschallenge.R
import com.arieldywelski.movieschallenge.view.MovieViewHolder
import com.arieldywelski.movieschallenge.view.MoviesFragment
import com.arieldywelski.movieschallenge.view.MoviesFragmentDirections
import com.arieldywelski.movieschallenge.viewmodel.UiModel

class MoviesAdapter : PagingDataAdapter<UiModel, ViewHolder>(UI_MODEL_COMPARATOR) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return MovieViewHolder.create(parent)
  }
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val uiModel: UiModel.MovieItem = getItem(position) as UiModel.MovieItem
    (holder as MovieViewHolder).bind(uiModel.movie)
  }

  override fun getItemViewType(position: Int): Int {
    return R.layout.list_movie_item
  }

  companion object {
    private val UI_MODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
      override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
        return (oldItem is UiModel.MovieItem && newItem is UiModel.MovieItem && oldItem.movie.movieId == newItem.movie.movieId)
      }

      override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
        oldItem == newItem
    }
  }
}