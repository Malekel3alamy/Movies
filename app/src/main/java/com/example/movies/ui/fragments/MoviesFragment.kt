package com.example.movies.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.movies.R
import com.example.movies.databinding.FragmentMoviesBinding
import com.example.movies.ui.MainActivity
import com.example.movies.ui.MoviesViewModel
import com.example.movies.utils.Resources

class MoviesFragment : Fragment(R.layout.fragment_movies) {
    lateinit var moviesViewModel: MoviesViewModel
    lateinit var binding : FragmentMoviesBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMoviesBinding.bind(view)

        moviesViewModel = (activity as MainActivity).moviesViewModel

        // Getting Movie Id
        if (arguments!= null){
    val id = arguments?.getInt("id")
    if (id != null){
        moviesViewModel.getMovieDetails(id)

        Log.d("IdNotNull","$id")
    }else{
        Log.d("id","$id")
    }
}
        val details =  moviesViewModel.details

                     binding.detailsTitle.text = details?.title
                     binding.detailsViews.text = "Views : ${details?.popularity}"
                     binding.detailsDate.text =  details?.release_date.toString()
                     binding.detailsOverview.text=details?.overview
                     Glide.with(view).load("https://image.tmdb.org/t/p/w500/${details?.poster_path}").into(binding.moviesMovieImage)
             }
    }


