package com.example.wewatch

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.wewatch.databinding.ActivityAddMovieBinding
import com.example.wewatch.models.Movie

class AddMovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddMovieBinding
    private var selectedMovie: Movie? = null

    // Современный способ получения результата (Controller логика)
    private val searchLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            
            // Безопасное получение Parcelable (Model)
            selectedMovie = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                data?.getParcelableExtra("selected_movie", Movie::class.java)
            } else {
                @Suppress("DEPRECATION")
                data?.getParcelableExtra("selected_movie")
            }

            // Обновление View
            selectedMovie?.let { movie ->
                binding.etTitle.setText(movie.title)
                binding.etYear.setText(movie.year)

                Glide.with(this)
                    .load(movie.posterUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(binding.ivPoster)
                
                binding.ivPoster.visibility = android.view.View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // Запуск поиска через новый Launcher
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            searchLauncher.launch(intent)
        }

        binding.btnAdd.setOnClickListener {
            selectedMovie?.let { movie ->
                val intent = Intent()
                intent.putExtra("selected_movie", movie)
                setResult(RESULT_OK, intent)
                finish()
            } ?: run {
                Toast.makeText(this, "Сначала выберите фильм", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Современный способ обработки кнопки "Назад"
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}