package com.example.wewatch

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wewatch.api.OmdbApiService
import com.example.wewatch.databinding.ActivitySearchBinding
import com.example.wewatch.models.Movie
import com.example.wewatch.views.adapters.SearchMovieAdapter
import kotlinx.coroutines.*

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var apiService: OmdbApiService
    private lateinit var adapter: SearchMovieAdapter
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        apiService = OmdbApiService()
        setupRecyclerView()
        setupClickListeners()

        // Если передали название из AddMovieActivity, сразу ищем
        val initialQuery = intent.getStringExtra("query")
        if (!initialQuery.isNullOrEmpty()) {
            binding.etSearch.setText(initialQuery)
            searchMovies(initialQuery)
        }
    }

    private fun setupRecyclerView() {
        adapter = SearchMovieAdapter(
            movies = emptyList(),
            onMovieClick = {
                // Обычный клик - по ТЗ ничего не делает или показывает Toast
                Toast.makeText(this, "Удерживайте для выбора фильма", Toast.LENGTH_SHORT).show()
            },
            onMovieLongClick = { movie ->
                // Выбор по долгому нажатию (ТЗ: правый клик/долгое нажатие)
                val intent = intent
                intent.putExtra("selected_movie", movie)
                setResult(RESULT_OK, intent)
                finish()
            }
        )

        binding.rvSearchResults.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchMovies(query)
            } else {
                Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchMovies(query: String) {
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.tvEmpty.visibility = android.view.View.GONE
        binding.rvSearchResults.visibility = android.view.View.GONE

        coroutineScope.launch {
            try {
                val results = apiService.searchMovies(query)
                binding.progressBar.visibility = android.view.View.GONE

                if (results.isEmpty()) {
                    binding.tvEmpty.text = "Ничего не найдено"
                    binding.tvEmpty.visibility = android.view.View.VISIBLE
                } else {
                    adapter.updateMovies(results)
                    binding.rvSearchResults.visibility = android.view.View.VISIBLE
                }
            } catch (e: Exception) {
                binding.progressBar.visibility = android.view.View.GONE
                binding.tvEmpty.text = "Ошибка: ${e.message}"
                binding.tvEmpty.visibility = android.view.View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }
}