package com.example.wewatch.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey
    val imdbID: String,
    val title: String,
    val year: String,
    val posterUrl: String,
    val genre: String? = null,
    val plot: String? = null,
    val director: String? = null,
    val actors: String? = null,
    val rating: String? = null,
    val runtime: String? = null,  // Длительность
    val released: String? = null, // Дата выхода
    val writer: String? = null,   // Сценарист
    val awards: String? = null,   // Награды
    val country: String? = null,  // Страна
    var isSelected: Boolean = false
) : Parcelable