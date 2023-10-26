package com.arieldywelski.movieschallenge.utils

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arieldywelski.movieschallenge.R
import com.arieldywelski.movieschallenge.data.CombineMovieModel
import com.arieldywelski.movieschallenge.view.MovieViewHolder

class MoviesAdapter(private val movieLikedInterface: MovieLikedInterface) : PagingDataAdapter<CombineMovieModel, ViewHolder>(UI_MODEL_COMPARATOR) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return MovieViewHolder.create(parent)
  }
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val movie: CombineMovieModel = getItem(position) as CombineMovieModel
    (holder as MovieViewHolder).bind(movie, movieLikedInterface)
  }

  override fun getItemViewType(position: Int): Int {
    return R.layout.list_movie_item
  }

  companion object {
    private val UI_MODEL_COMPARATOR = object : DiffUtil.ItemCallback<CombineMovieModel>() {
      override fun areItemsTheSame(oldItem: CombineMovieModel, newItem: CombineMovieModel): Boolean {
        return (oldItem.movieId == newItem.movieId)
      }

      override fun areContentsTheSame(oldItem: CombineMovieModel, newItem: CombineMovieModel): Boolean =
        oldItem == newItem
    }
  }
}

interface MovieLikedInterface {
  fun onMovieLiked(movieId: Long, isLiked: Boolean)
}