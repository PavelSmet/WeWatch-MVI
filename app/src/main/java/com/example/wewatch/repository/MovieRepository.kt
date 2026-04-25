package com.example.wewatch.repository

import com.example.wewatch.data.MovieDao
import com.example.wewatch.models.Movie
import kotlinx.coroutines.flow.Flow

// Прослойка между Controller и БД
class MovieRepository(private val movieDao: MovieDao) {

    fun getAllMovies(): Flow<List<Movie>> = movieDao.getAllMovies()

    suspend fun addMovie(movie: Movie) = movieDao.insertMovie(movie)

    suspend fun addMovies(movies: List<Movie>) = movieDao.insertAllMovies(movies)

    suspend fun deleteMovies(movies: List<Movie>) {
        val movieIds = movies.map { it.imdbID }
        movieDao.deleteMoviesByIds(movieIds)
    }

    suspend fun updateMovieSelection(imdbId: String, selected: Boolean) {
        movieDao.updateSelection(imdbId, selected)
    }

    suspend fun clearAllSelections() = movieDao.clearAllSelections()

    suspend fun getSelectedMovies(): List<Movie> = movieDao.getSelectedMovies()

    suspend fun isEmpty(): Boolean = movieDao.getMoviesSync().isEmpty()
}