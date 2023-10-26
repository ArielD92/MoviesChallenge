package com.arieldywelski.movieschallenge.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arieldywelski.movieschallenge.R
import com.arieldywelski.movieschallenge.data.Movie
import com.arieldywelski.movieschallenge.databinding.ListMovieItemBinding
import com.arieldywelski.movieschallenge.utils.Constants
import com.bumptech.glide.Glide

class MovieViewHolder(
  private val binding: ListMovieItemBinding
) : RecyclerView.ViewHolder(binding.root) {

  fun bind(movie: Movie?) {
    if (movie == null) {
      with(binding) {
        movieTitle.text = UNKNOWN_PLACEHOLDER
        movieAverageScore.visibility = View.GONE
      }
    } else {
      showMovieData(movie)
    }
  }

  private fun showMovieData(movie: Movie) {
    with(binding) {
      movieTitle.text = movie.movieTitle
      movieAverageScore.text = movie.movieVoteAverage.toString()

      likedMovie.setOnClickListener {

      }

      Glide.with(this.root)
        .load(Constants.MOVIE_IMAGE_BASE_PATH + movie.moviePosterPath)
        .into(moviePoster)

      root.setOnClickListener {
        val actionToMovieDetails = MoviesFragmentDirections.actionToMovieDetails(movieId = movie.movieId)
        it.findNavController().navigate(actionToMovieDetails)
      }
    }
  }

  companion object {
    private const val UNKNOWN_PLACEHOLDER = "Unknown"
    fun create(parent: ViewGroup): MovieViewHolder {
      val view = LayoutInflater.from(parent.context)
        .inflate(R.layout.list_movie_item, parent, false)
      val binding = ListMovieItemBinding.bind(view)
      return MovieViewHolder((binding))
    }
  }
}