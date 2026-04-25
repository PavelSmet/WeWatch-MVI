package com.example.wewatch.controllers

import android.content.Context
import com.example.wewatch.models.Movie
import com.example.wewatch.views.MainView
import com.example.wewatch.api.OmdbApiService
import com.example.wewatch.data.MovieDatabase
import com.example.wewatch.repository.MovieRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class MovieController(
    context: Context, // Убрали 'private val', так как используется только при инициализации
    private val view: MainView
) {
    private val database = MovieDatabase.getInstance(context)
    private val repository = MovieRepository(database.movieDao())
    private val apiService = OmdbApiService()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    fun loadMovies() {
        view.showLoading()
        coroutineScope.launch {
            repository.getAllMovies().collectLatest { movies ->
                view.hideLoading()
                if (movies.isEmpty()) {
                    view.showEmptyState()
                } else {
                    view.showMovieList(movies)
                    
                    val selectedCount = movies.count { it.isSelected }
                    view.updateSelectionMode(selectedCount > 0)
                }
            }
        }
    }

    fun addMovie(movie: Movie) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                // При добавлении сразу подтягиваем полные данные
                val fullMovie = apiService.getMovieDetails(movie.imdbID)
                repository.addMovie(fullMovie)
            } catch (_: Exception) {
                // Если не вышло получить детали, сохраняем базовую инфо
                repository.addMovie(movie)
            }
        }
    }

    fun deleteSelectedMovies() {
        coroutineScope.launch(Dispatchers.IO) {
            val selectedMovies = repository.getSelectedMovies()
            if (selectedMovies.isNotEmpty()) {
                repository.deleteMovies(selectedMovies)
                repository.clearAllSelections()
            }
        }
    }

    fun updateMovieSelection(movie: Movie, isSelected: Boolean) {
        coroutineScope.launch(Dispatchers.IO) {
            repository.updateMovieSelection(movie.imdbID, isSelected)
        }
    }

    fun clearSelection() {
        coroutineScope.launch(Dispatchers.IO) {
            repository.clearAllSelections()
        }
    }

    fun onDestroy() {
        coroutineScope.cancel()
    }
}