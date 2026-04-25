package com.example.wewatch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wewatch.controllers.MovieController
import com.example.wewatch.databinding.ActivityMainBinding
import com.example.wewatch.models.Movie
import com.example.wewatch.views.MainView
import com.example.wewatch.views.adapters.MovieAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: MovieController
    private lateinit var adapter: MovieAdapter

    companion object {
        private const val ADD_MOVIE_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Включаем Edge-to-Edge
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Обработка отступов для вырезов и системных баров
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        controller = MovieController(this, this)
        setupRecyclerView()
        setupClickListeners()
        controller.loadMovies()
    }

    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            movies = emptyList(),
            onItemClick = { movie ->
                Toast.makeText(this, movie.title, Toast.LENGTH_SHORT).show()
            },
            onSelectionChange = { movie, isSelected ->
                controller.updateMovieSelection(movie, isSelected)
            }
        )

        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupClickListeners() {
        // Сразу открываем поиск (согласно вашему пожеланию)
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivityForResult(intent, ADD_MOVIE_REQUEST)
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.btnCancel.setOnClickListener {
            controller.clearSelection()
        }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Удаление")
            .setMessage("Удалить выбранные фильмы?")
            .setPositiveButton("Да") { _, _ -> controller.deleteSelectedMovies() }
            .setNegativeButton("Нет", null)
            .show()
    }

    override fun showEmptyState() {
        binding.rvMovies.visibility = android.view.View.GONE
        binding.emptyStateLayout.visibility = android.view.View.VISIBLE
        binding.selectionBar.visibility = android.view.View.GONE
    }

    override fun showMovieList(movies: List<Movie>) {
        binding.rvMovies.visibility = android.view.View.VISIBLE
        binding.emptyStateLayout.visibility = android.view.View.GONE
        adapter.updateMovies(movies)
    }

    override fun showLoading() { binding.progressBar.visibility = android.view.View.VISIBLE }
    override fun hideLoading() { binding.progressBar.visibility = android.view.View.GONE }
    override fun showError(message: String) { Toast.makeText(this, message, Toast.LENGTH_LONG).show() }

    override fun updateSelectionMode(isActive: Boolean) {
        if (isActive) {
            binding.selectionBar.visibility = android.view.View.VISIBLE
            binding.fabAdd.hide()
        } else {
            binding.selectionBar.visibility = android.view.View.GONE
            binding.fabAdd.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_MOVIE_REQUEST && resultCode == RESULT_OK) {
            val movie = data?.getParcelableExtra<Movie>("selected_movie")
            movie?.let { controller.addMovie(it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
    }
}