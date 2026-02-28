package com.example.wewatch.models

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MovieModel(private val context: Context) {
    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences("movie_watchlist", Context.MODE_PRIVATE)
    private val gson = Gson()

    private var movies: MutableList<Movie> = mutableListOf()

    init {
        loadMovies()
    }

    fun getMovies(): List<Movie> = movies

    fun addMovie(movie: Movie) {
        movies.add(movie)
        saveMovies()
    }

    fun removeMovies(moviesToRemove: List<Movie>) {
        movies.removeAll(moviesToRemove)
        saveMovies()
    }

    fun updateMovieSelection(imdbId: String, selected: Boolean) {
        movies.find { it.imdbID == imdbId }?.isSelected = selected
        saveMovies()
    }

    fun clearSelection() {
        movies.forEach { it.isSelected = false }
        saveMovies()
    }

    fun getSelectedMovies(): List<Movie> = movies.filter { it.isSelected }

    private fun saveMovies() {
        val json = gson.toJson(movies)
        sharedPrefs.edit().putString("movies_list", json).apply()
    }

    private fun loadMovies() {
        val json = sharedPrefs.getString("movies_list", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Movie>>() {}.type
            movies = gson.fromJson(json, type)
        }
    }

    fun isEmpty(): Boolean = movies.isEmpty()
}