package com.example.wewatch.api

import com.example.wewatch.models.Movie
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApi {
    @GET("/")
    suspend fun searchMovies(
        @Query("s") query: String,
        @Query("apikey") apiKey: String,
        @Query("type") type: String = "movie"
    ): OmdbSearchResponse

    @GET("/")
    suspend fun getMovieDetails(
        @Query("i") imdbId: String,
        @Query("apikey") apiKey: String,
        @Query("plot") plot: String = "full"
    ): OmdbMovieResponse
}

data class OmdbSearchResponse(
    val Search: List<OmdbMovieResponse>?,
    val totalResults: String?,
    val Response: String,
    val Error: String?
)

data class OmdbMovieResponse(
    val imdbID: String,
    val Title: String,
    val Year: String,
    val Genre: String?,
    val Plot: String?,
    val Director: String?,
    val Writer: String?,    // Сценарист
    val Actors: String?,
    val imdbRating: String?,
    val Runtime: String?,   // Длительность
    val Released: String?,  // Дата релиза
    val Awards: String?,    // Награды
    val Country: String?,   // Страна
    val Poster: String,
    val Response: String?,
    val Error: String?
)

class OmdbApiService {
    private val apiKey = "a9cca3ad"

    private val api: OmdbApi = Retrofit.Builder()
        .baseUrl("http://www.omdbapi.com")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build())
        .build()
        .create(OmdbApi::class.java)

    suspend fun searchMovies(query: String): List<Movie> {
        val response = api.searchMovies(query, apiKey)
        if (response.Response == "True" && response.Search != null) {
            return response.Search.map { omdbMovie ->
                Movie(
                    imdbID = omdbMovie.imdbID,
                    title = omdbMovie.Title,
                    year = omdbMovie.Year,
                    posterUrl = omdbMovie.Poster,
                    genre = omdbMovie.Genre
                )
            }
        } else {
            throw Exception(response.Error ?: "Unknown error")
        }
    }

    suspend fun getMovieDetails(imdbId: String): Movie {
        val response = api.getMovieDetails(imdbId, apiKey)
        if (response.Response == "True") {
            return Movie(
                imdbID = response.imdbID,
                title = response.Title,
                year = response.Year,
                genre = response.Genre,
                plot = response.Plot,
                director = response.Director,
                writer = response.Writer,
                actors = response.Actors,
                rating = response.imdbRating,
                runtime = response.Runtime,
                released = response.Released,
                awards = response.Awards,
                country = response.Country,
                posterUrl = response.Poster
            )
        } else {
            throw Exception(response.Error ?: "Unknown error")
        }
    }
}