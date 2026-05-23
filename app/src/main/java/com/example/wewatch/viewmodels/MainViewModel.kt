package com.example.wewatch.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wewatch.api.OmdbApiService
import com.example.wewatch.data.MovieDatabase
import com.example.wewatch.models.Movie
import com.example.wewatch.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MovieRepository
    private val apiService = OmdbApiService()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        val database = MovieDatabase.getInstance(application)
        repository = MovieRepository(database.movieDao())
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllMovies().collect { movieList ->
                _movies.value = movieList
                _isLoading.value = false
            }
        }
    }

    fun addMovie(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fullMovie = apiService.getMovieDetails(movie.imdbID)
                repository.addMovie(fullMovie)
            } catch (e: Exception) {
                repository.addMovie(movie)
            }
        }
    }

    fun deleteSelectedMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedMovies = repository.getSelectedMovies()
            if (selectedMovies.isNotEmpty()) {
                repository.deleteMovies(selectedMovies)
                repository.clearAllSelections()
            }
        }
    }

    fun updateMovieSelection(movie: Movie, isSelected: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateMovieSelection(movie.imdbID, isSelected)
        }
    }

    fun clearSelection() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAllSelections()
        }
    }
}
