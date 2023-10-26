package com.arieldywelski.movieschallenge.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.arieldywelski.movieschallenge.data.CombineMovieModel
import com.arieldywelski.movieschallenge.databinding.FragmentMoviesBinding
import com.arieldywelski.movieschallenge.utils.Constants
import com.arieldywelski.movieschallenge.utils.MovieLikedInterface
import com.arieldywelski.movieschallenge.utils.MoviesAdapter
import com.arieldywelski.movieschallenge.utils.MoviesLoadStateAdapter
import com.arieldywelski.movieschallenge.utils.RemotePresentationState
import com.arieldywelski.movieschallenge.utils.asRemotePresentationState
import com.arieldywelski.movieschallenge.viewmodel.MoviesViewModel
import com.arieldywelski.movieschallenge.viewmodel.UiAction
import com.arieldywelski.movieschallenge.viewmodel.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MoviesFragment : Fragment(), MovieLikedInterface {

  private val viewModel: MoviesViewModel by viewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val binding = FragmentMoviesBinding.inflate(inflater, container, false)
    context ?: return binding.root

    val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
    binding.moviesRecycler.addItemDecoration(decoration)

    binding.bindState(
      uiState = viewModel.state,
      pagingData = viewModel.pagingDataFlow,
      uiAction = viewModel.accept
    )

    return binding.root
  }

  private fun FragmentMoviesBinding.bindState(
    uiState: StateFlow<UiState>,
    pagingData: Flow<PagingData<CombineMovieModel>>,
    uiAction: (UiAction) -> Unit
  ) {

    val moviesAdapter = MoviesAdapter(this@MoviesFragment)
    val header = MoviesLoadStateAdapter { moviesAdapter.retry() }

    moviesRecycler.adapter = moviesAdapter.withLoadStateHeaderAndFooter(
      header = header,
      footer = MoviesLoadStateAdapter { moviesAdapter.retry() }
    )

    bindSearch(
      uiState = uiState,
      onQueryChanged = uiAction
    )

    bindList(
      header = header,
      moviesAdapter = moviesAdapter,
      uiState = uiState,
      pagingData = pagingData,
      onScrollChanged = uiAction
    )
  }

  private fun FragmentMoviesBinding.bindSearch(
    uiState: StateFlow<UiState>,
    onQueryChanged: (UiAction.Search) -> Unit
  ) {
    searchMovies.setOnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_GO) {
        updateMoviesListFromInput(onQueryChanged)
        true
      } else {
        false
      }
    }
    searchMovies.setOnKeyListener { _, keyCode, event ->
      if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
        updateMoviesListFromInput(onQueryChanged)
        true
      } else {
        false
      }
    }

    lifecycleScope.launch {
      uiState
        .map { it.query }
        .distinctUntilChanged()
        .collect(searchMovies::setText)
    }
  }

  private fun FragmentMoviesBinding.updateMoviesListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
    searchMovies.text.trim().let {
      if (it.isNotEmpty()) {
        moviesRecycler.scrollToPosition(0)
        onQueryChanged(UiAction.Search(query = it.toString()))
      } else {
        moviesRecycler.scrollToPosition(0)
        onQueryChanged(UiAction.Search(query = Constants.EMPTY_STRING))
      }
    }
  }

  private fun FragmentMoviesBinding.bindList(
    header: MoviesLoadStateAdapter,
    moviesAdapter: MoviesAdapter,
    uiState: StateFlow<UiState>,
    pagingData: Flow<PagingData<CombineMovieModel>>,
    onScrollChanged: (UiAction.Scroll) -> Unit
  ) {
    retryButton.setOnClickListener { moviesAdapter.retry() }

    moviesRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
      }
    })

    val notLoading = moviesAdapter.loadStateFlow
      .asRemotePresentationState()
      .map { it == RemotePresentationState.PRESENTED }

    val hasNotScrollForCurrentSearch = uiState
      .map { it.hasNotScrolledForCurrentSearch }
      .distinctUntilChanged()

    val shouldScrollToTop = combine(
      notLoading,
      hasNotScrollForCurrentSearch,
      Boolean::and
    ).distinctUntilChanged()

    lifecycleScope.launch {
      pagingData.collectLatest(moviesAdapter::submitData)
    }

    lifecycleScope.launch {
      shouldScrollToTop.collect { shouldScroll ->
        if (shouldScroll) moviesRecycler.scrollToPosition(0)
      }
    }

    lifecycleScope.launch {
      moviesAdapter.loadStateFlow.collect { loadState ->
        header.loadState =
          loadState.mediator
            ?.refresh
            ?.takeIf { it is LoadState.Error && moviesAdapter.itemCount > 0 }
            ?: loadState.prepend

        val isListEmpty = loadState.refresh is LoadState.NotLoading && loadState.mediator?.refresh is LoadState.NotLoading
        emptyList.isVisible = isListEmpty
        moviesRecycler.isVisible = loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
        progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
        retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && moviesAdapter.itemCount == 0

        val errorState = loadState.source.append as? LoadState.Error
          ?: loadState.source.prepend as? LoadState.Error
          ?: loadState.append as? LoadState.Error
          ?: loadState.prepend as? LoadState.Error

        errorState?.let {
          Toast.makeText(
            context,
            "\uD83D\uDE28 Wooops ${it.error}",
            Toast.LENGTH_LONG
          ).show()
        }
      }
    }
  }

  override fun onMovieLiked(movieId: Long, isLiked: Boolean) {
    viewModel.onMovieLiked(movieId, isLiked)
  }
}