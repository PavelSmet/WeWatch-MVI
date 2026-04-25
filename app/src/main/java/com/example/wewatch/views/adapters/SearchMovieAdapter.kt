package com.example.wewatch.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wewatch.databinding.ItemSearchMovieBinding
import com.example.wewatch.models.Movie

class SearchMovieAdapter(
    private var movies: List<Movie>,
    private val onMovieClick: (Movie) -> Unit,
    private val onMovieLongClick: (Movie) -> Unit
) : RecyclerView.Adapter<SearchMovieAdapter.SearchViewHolder>() {

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemSearchMovieBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    inner class SearchViewHolder(
        private val binding: ItemSearchMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.apply {
                tvTitle.text = movie.title
                tvYear.text = movie.year
                tvGenre.text = movie.genre

                Glide.with(root.context)
                    .load(movie.posterUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(ivPoster)

                root.setOnClickListener {
                    Toast.makeText(root.context, "Для выбора удерживайте", Toast.LENGTH_SHORT).show()
                }

                // Правый клик (долгое нажатие) - выбираем фильм
                root.setOnLongClickListener {
                    onMovieLongClick(movie)
                    true
                }
            }
        }
    }
}