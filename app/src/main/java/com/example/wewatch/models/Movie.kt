package com.example.wewatch.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    val imdbID: String,
    val title: String,
    val year: String,
    val posterUrl: String,
    var isSelected: Boolean = false
) : Parcelable