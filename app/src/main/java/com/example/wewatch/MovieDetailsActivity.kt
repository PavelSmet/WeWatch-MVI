package com.example.wewatch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.wewatch.databinding.ActivityMovieDetailsBinding
import com.example.wewatch.models.Movie

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        val movie = intent.getParcelableExtra<Movie>("movie")
        movie?.let { displayMovieDetails(it) }
    }

    private fun displayMovieDetails(movie: Movie) {
        binding.apply {
            tvTitle.text = movie.title
            tvYear.text = movie.year
            tvRating.text = movie.rating?.let { "IMDb: $it" } ?: "Нет рейтинга"
            tvGenre.text = movie.genre
            tvPlot.text = movie.plot ?: "Описание отсутствует"
            tvActors.text = movie.actors
            tvDirector.text = "Режиссер: ${movie.director}"

            Glide.with(this@MovieDetailsActivity)
                .load(movie.posterUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(ivPoster)

            btnTrailer.setOnClickListener {
                val query = "${movie.title} ${movie.year} official trailer"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$query"))
                startActivity(intent)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}