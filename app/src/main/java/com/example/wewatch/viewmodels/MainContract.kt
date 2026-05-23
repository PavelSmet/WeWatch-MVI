package com.example.wewatch.viewmodels

import com.example.wewatch.models.Movie

class MainContract {
    // Состояние экрана
    data class State(
        val movies: List<Movie> = emptyList(),
        val isLoading: Boolean = false,
        val isSelectionMode: Boolean = false,
        val selectedCount: Int = 0
    )

    // Намерения пользователя (UI -> ViewModel)
    sealed class Intent {
        object LoadMovies : Intent()
        data class AddMovie(val movie: Movie) : Intent()
        data class UpdateMovieSelection(val movie: Movie, val isSelected: Boolean) : Intent()
        object DeleteSelectedMovies : Intent()
        object ClearSelection : Intent()
    }

    // Одноразовые эффекты (ViewModel -> UI: Toast, Navigation)
    sealed class Effect {
        data class ShowError(val message: String) : Effect()
        data class ShowToast(val message: String) : Effect()
    }
}
