package com.arieldywelski.movieschallenge.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.arieldywelski.movieschallenge.R
import com.arieldywelski.movieschallenge.databinding.FragmentMovieDetailsBinding
import com.arieldywelski.movieschallenge.utils.Constants
import com.arieldywelski.movieschallenge.viewmodel.MovieDetailViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {

  private lateinit var binding: FragmentMovieDetailsBinding
  private val viewModel: MovieDetailViewModel by viewModels()

  private var movieId: Long? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    arguments?.let {
      movieId = it.getLong(Constants.MOVIE_ID)
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ) : View {
    binding = FragmentMovieDetailsBinding.inflate(inflater, container,false)

    movieId?.let {
      viewModel.getMovieDetails(movieId = it.toInt())
    }

    viewModel.item.observe(viewLifecycleOwner) {
      bindMovieDetails(it)
    }

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    with(binding) {
      toolbar.inflateMenu(R.menu.toolbar_menu)
      toolbar.title = "Movie Details"

      toolbar.setOnMenuItemClickListener { menuItem ->
        when(menuItem.itemId) {
          R.id.action_liked_movie -> {
            true
          }
          else -> false
        }
      }
    }
  }
  private fun bindMovieDetails(movie: MovieDetailViewModel.Movie) {
    with(binding) {
      movieTitle.text = movie.movieName
      if (movie.movieDescription.isEmpty()) {
        movieDescription.text = root.context.getText(R.string.unknown)
      } else {
        movieDescription.text = movie.movieDescription
      }
      movieReleaseDate.text = movie.movieReleaseDate

      Glide.with(binding.root)
        .load(Constants.MOVIE_IMAGE_BASE_PATH + movie.moviePosterPath)
        .into(moviePoster)
    }
  }

}