package com.example.wewatch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wewatch.databinding.ActivityMainBinding
import com.example.wewatch.models.Movie
import com.example.wewatch.viewmodels.MainViewModel
import com.example.wewatch.views.adapters.MovieAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    // Регистрация контракта для получения результата из SearchActivity
    private val searchActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val movie = result.data?.getParcelableExtra<Movie>("selected_movie")
            movie?.let { viewModel.addMovie(it) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            movies = emptyList(),
            onItemClick = { movie ->
                Toast.makeText(this, movie.title, Toast.LENGTH_SHORT).show()
            },
            onSelectionChange = { movie, isSelected ->
                viewModel.updateMovieSelection(movie, isSelected)
            }
        )

        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            searchActivityResultLauncher.launch(intent)
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.btnCancel.setOnClickListener {
            viewModel.clearSelection()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Наблюдаем за списком фильмов
                launch {
                    viewModel.movies.collect { movies ->
                        if (movies.isEmpty()) {
                            showEmptyState()
                        } else {
                            showMovieList(movies)
                            val selectedCount = movies.count { it.isSelected }
                            updateSelectionMode(selectedCount > 0)
                        }
                    }
                }

                // Наблюдаем за статусом загрузки
                launch {
                    viewModel.isLoading.collect { isLoading ->
                        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                // Наблюдаем за ошибками
                launch {
                    viewModel.errorMessage.collect { message ->
                        message?.let {
                            Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Удаление")
            .setMessage("Удалить выбранные фильмы?")
            .setPositiveButton("Да") { _, _ -> viewModel.deleteSelectedMovies() }
            .setNegativeButton("Нет", null)
            .show()
    }

    private fun showEmptyState() {
        binding.rvMovies.visibility = View.GONE
        binding.emptyStateLayout.visibility = View.VISIBLE
        binding.selectionBar.visibility = View.GONE
    }

    private fun showMovieList(movies: List<Movie>) {
        binding.rvMovies.visibility = View.VISIBLE
        binding.emptyStateLayout.visibility = View.GONE
        adapter.updateMovies(movies)
    }

    private fun updateSelectionMode(isActive: Boolean) {
        if (isActive) {
            binding.selectionBar.visibility = View.VISIBLE
            binding.fabAdd.hide()
        } else {
            binding.selectionBar.visibility = View.GONE
            binding.fabAdd.show()
        }
    }
}
