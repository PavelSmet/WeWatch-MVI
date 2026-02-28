package com.example.wewatch.views

import com.example.wewatch.models.Movie

interface MainView {
    fun showEmptyState()
    fun showMovieList(movies: List<Movie>)
    fun showLoading()
    fun hideLoading()
    fun showError(message: String)
    fun updateSelectionMode(isActive: Boolean)
}