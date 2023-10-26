package com.arieldywelski.movieschallenge.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.arieldywelski.movieschallenge.data.CombineMovieModel
import com.arieldywelski.movieschallenge.data.Movie
import com.arieldywelski.movieschallenge.data.MoviesRepository
import com.arieldywelski.movieschallenge.db.MovieModel
import com.arieldywelski.movieschallenge.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
  private val repository: MoviesRepository,
  private val savedStateHandle: SavedStateHandle
) : ViewModel() {

  val state: StateFlow<UiState>
  val pagingDataFlow: Flow<PagingData<CombineMovieModel>>
  val accept: (UiAction) -> Unit

  private lateinit var likedMovies: Flow<List<MovieModel>>

  init {
    val initialQuery = savedStateHandle[LAST_SEARCH_QUERY] ?: Constants.EMPTY_STRING
    val lastQueryScrolled = savedStateHandle[LAST_QUERY_SCROLLED] ?: Constants.EMPTY_STRING
    val actionStateFlow = MutableSharedFlow<UiAction>()
    val searches = actionStateFlow
      .filterIsInstance<UiAction.Search>()
      .distinctUntilChanged()
      .onStart {
        emit(UiAction.Search(query = initialQuery))
      }

    val queriesScrolled = actionStateFlow
      .filterIsInstance<UiAction.Scroll>()
      .distinctUntilChanged()
      .shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000)
      )
      .onStart {
        emit(UiAction.Scroll(currentQuery = lastQueryScrolled))
      }

    viewModelScope.launch {
      likedMovies = repository.getLikedMovies()
    }

    pagingDataFlow = searches
      .flatMapLatest {
        getMovies(query = it.query)
      }
      .cachedIn(viewModelScope)
      .combine(likedMovies) { movies, likes ->
        movies.map { item ->
          item.copy(isLiked = likes.any { it.movieId == item.movieId })
        }
      }

    state = combine(
      searches,
      queriesScrolled,
      ::Pair
    ).map { (search, scroll) ->
      UiState(
        query = search.query,
        lastQueryScrolled = scroll.currentQuery,
        hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
      )
    }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
      initialValue = UiState()
    )

    accept = { action ->
      viewModelScope.launch {
        actionStateFlow.emit(action)
      }
    }
  }

  fun onMovieLiked(movieId: Long, isLiked: Boolean) {
    viewModelScope.launch {
      if (isLiked) {
        repository.insertLikedMovie(movieId)
      } else {
        repository.removeLikedMovie(movieId)
      }
    }
  }
  override fun onCleared() {
    savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
    savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
    super.onCleared()
  }

  private fun getMovies(query: String): Flow<PagingData<CombineMovieModel>> = repository.getMoviesStream(query = query)
}

sealed class UiAction {
  data class Search(val query: String) : UiAction()
  data class Scroll(
    val currentQuery: String
  ) : UiAction()
}

data class UiState(
  val query: String = Constants.EMPTY_STRING,
  val lastQueryScrolled: String = Constants.EMPTY_STRING,
  val hasNotScrolledForCurrentSearch: Boolean = false
)

private const val LAST_QUERY_SCROLLED = "last_query_scrolled"
private const val LAST_SEARCH_QUERY = "last_search_query"
