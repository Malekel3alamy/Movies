package com.example.movies.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.movies.R
import com.example.movies.models.Movie

class MovieRecyclerAdapter( private val onItemClicked: (Movie) -> Unit)
    : RecyclerView.Adapter<MovieRecyclerAdapter.MyViewHolder>() {
    class MyViewHolder (view : View) : ViewHolder(view){

        val movieImage = view.findViewById<ImageView>(R.id.movie_image)
        val title      = view.findViewById<TextView>(R.id.movie_title)
        val date   = view.findViewById<TextView>(R.id.date)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.movie_item,parent,false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
return differ.currentList.size    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val movie = differ.currentList[position]

        holder.title.text = movie.title
        holder.date.text = movie.release_date
        Glide.with(holder.itemView).load("https://image.tmdb.org/t/p/w500/${movie.poster_path}").into(holder.movieImage)
         holder.itemView.setOnClickListener {
             onItemClicked(movie)
         }

    }

    private val differCallback = object : DiffUtil.ItemCallback<Movie>() {
        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)



}