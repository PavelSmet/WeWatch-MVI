package com.example.wewatch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wewatch.databinding.ActivitySearchBinding
import com.example.wewatch.viewmodels.SearchViewModel
import com.example.wewatch.views.adapters.MovieAdapter
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearchButton()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            movies = emptyList(),
            onItemClick = { movie ->
                val resultIntent = Intent().apply {
                    putExtra("selected_movie", movie)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            },
            onSelectionChange = { _, _ -> }
        )
        binding.rvSearchResults.layoutManager = LinearLayoutManager(this)
        binding.rvSearchResults.adapter = adapter
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchMovies(query)
            } else {
                Toast.makeText(this, "Введите текст", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            // Результаты поиска
            launch {
                viewModel.searchResults.collect { movies ->
                    adapter.updateMovies(movies)
                    binding.rvSearchResults.visibility = if (movies.isNotEmpty()) View.VISIBLE else View.GONE
                    binding.tvEmpty.visibility = if (movies.isEmpty()) View.VISIBLE else View.GONE
                    if (movies.isEmpty()) {
                        binding.tvEmpty.text = "Ничего не найдено"
                    }
                }
            }

            // Загрузка
            launch {
                viewModel.isLoading.collect { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    if (isLoading) binding.tvEmpty.visibility = View.GONE
                }
            }

            // Ошибки
            launch {
                viewModel.error.collect { error ->
                    error?.let {
                        Toast.makeText(this@SearchActivity, it, Toast.LENGTH_SHORT).show()
                        binding.tvEmpty.text = it
                        binding.tvEmpty.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}