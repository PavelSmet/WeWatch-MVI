package com.example.wewatch.controllers

import android.content.Context
import com.example.wewatch.models.Movie
import com.example.wewatch.models.MovieModel
import com.example.wewatch.views.MainView
import com.example.wewatch.api.OmdbApiService
import kotlinx.coroutines.*

class MovieController(
    private val context: Context,
    private val view: MainView
) {
    private val model = MovieModel(context)
    private val apiService = OmdbApiService()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    fun loadMovies() {
        if (model.isEmpty()) {
            view.showEmptyState()
        } else {
            view.showMovieList(model.getMovies())
        }
    }

    fun searchMovies(query: String) {
        view.showLoading()
        coroutineScope.launch {
            try {
                val result = apiService.searchMovies(query)
            } catch (e: Exception) {
                view.hideLoading()
                view.showError("Ошибка поиска: ${e.message}")
            }
        }
    }

    fun addMovie(movie: Movie) {
        model.addMovie(movie)
        loadMovies()
    }

    fun deleteSelectedMovies() {
        val selectedMovies = model.getSelectedMovies()
        if (selectedMovies.isNotEmpty()) {
            model.removeMovies(selectedMovies)
            model.clearSelection()
            loadMovies()
        }
    }

    fun updateMovieSelection(movie: Movie, isSelected: Boolean) {
        model.updateMovieSelection(movie.imdbID, isSelected)
        // Уведомляем view о необходимости обновить UI
        if (model.getSelectedMovies().isNotEmpty()) {
            view.updateSelectionMode(true)
        } else {
            view.updateSelectionMode(false)
        }
    }

    fun clearSelection() {
        model.clearSelection()
        loadMovies()
        view.updateSelectionMode(false)
    }

    fun onDestroy() {
        coroutineScope.cancel()
    }
}