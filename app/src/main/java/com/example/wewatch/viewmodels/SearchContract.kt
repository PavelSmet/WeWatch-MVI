package com.example.wewatch.viewmodels

import com.example.wewatch.models.Movie

class SearchContract {
    // Состояние экрана поиска
    data class State(
        val searchResults: List<Movie> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    // Намерения пользователя
    sealed class Intent {
        data class SearchMovies(val query: String) : Intent()
    }

    // Побочные эффекты (например, если нужно показать Toast не через состояние)
    sealed class Effect {
        data class ShowToast(val message: String) : Effect()
    }
}
