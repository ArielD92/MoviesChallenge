package com.arieldywelski.movieschallenge.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.arieldywelski.movieschallenge.data.Movie
import com.arieldywelski.movieschallenge.data.MoviesRepository
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
  val pagingDataFlow: Flow<PagingData<UiModel>>
  val accept: (UiAction) -> Unit

  init {
    val initialQuery = savedStateHandle[LAST_SEARCH_QUERY] ?: DEFAULT_QUERY
    val lastQueryScrolled = savedStateHandle[LAST_QUERY_SCROLLED] ?: DEFAULT_QUERY
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

    pagingDataFlow = searches
      .flatMapLatest {
        getMovies(query = it.query)
      }.cachedIn(viewModelScope)

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

  override fun onCleared() {
    savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
    savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
    super.onCleared()
  }

  private fun getMovies(query: String): Flow<PagingData<UiModel>> =
    repository.getMoviesStream(query = query)
      .map { pagingData -> pagingData.map { UiModel.MovieItem(it) } }
}

sealed class UiAction {
  data class Search(val query: String) : UiAction()
  data class Scroll(
    val currentQuery: String
  ) : UiAction()
}

data class UiState(
  val query: String = DEFAULT_QUERY,
  val lastQueryScrolled: String = DEFAULT_QUERY,
  val hasNotScrolledForCurrentSearch: Boolean = false
)

sealed class UiModel {
  data class MovieItem(val movie: Movie) : UiModel()
}

private const val DEFAULT_QUERY = ""
private const val LAST_QUERY_SCROLLED = "last_query_scrolled"
private const val LAST_SEARCH_QUERY = "last_search_query"
