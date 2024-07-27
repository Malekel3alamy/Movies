package com.example.movies.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.adapter.MovieRecyclerAdapter
import com.example.movies.databinding.FragmentNowPlayingBinding
import com.example.movies.models.Movie
import com.example.movies.ui.MainActivity
import com.example.movies.ui.MoviesViewModel
import com.example.movies.utils.Constants
import com.example.movies.utils.Resources


class NowPlayingFragment : Fragment(R.layout.fragment_now_playing) {
    lateinit var moviesViewModel: MoviesViewModel
    lateinit var binding: FragmentNowPlayingBinding
    lateinit var moviesAdapter : MovieRecyclerAdapter

    private val pageNumber = 4

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNowPlayingBinding.bind(view)

        setUpRecycler()
     /*   moviesAdapter.setOnClickListener { movie ->

            val bundle = Bundle().apply {
                if (movie.id != null)
                    putInt("id", movie.id!!)
            }
                findNavController().navigate(R.id.action_nowPlayingFragment_to_moviesFragment,bundle)
        }*/
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ite_view_error = inflater.inflate(R.layout.item_error, null)

        moviesViewModel = (activity as MainActivity).moviesViewModel

        moviesViewModel.getNowPlayingMovies(pageNumber)

        if (moviesViewModel.internetConnection((activity as MainActivity).applicationContext)){
            moviesViewModel.nowPlayingMovies.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Resources.Success<*> -> {
                        hideProgressBar()
                        hideErrorMessage()
                        it.data?.let {
                            moviesAdapter.differ.submitList(it.results.toList())
                            moviesViewModel.upsertMovies(it.results.toList())

                            /*   val totalPages =
                                   it.total_results / Constants.QUERY_PAGE_SIZE + 2
                               isLastPage = moviesViewModel.pageNumber == totalPages
                               if (isLastPage) {
                                   binding.recyclerNowPlaying.setPadding(0, 0, 0, 0)
                               }*/
                        }
                    }
                    is Resources.Error -> {
                        hideProgressBar()
                        showErrorMessage(it.message.toString())
                        it.message?.let { message ->
                            Toast.makeText(activity, "Error$message", Toast.LENGTH_SHORT).show()
                            Log.d("APIERROR", message)
                            //  showErrorMessage(message)
                        }
                    }
                    is Resources.Loading -> {
                        showProgressBar()
                    }
                }

            })
        }else{

            moviesViewModel.getAllArticlesFromRoom().observe(viewLifecycleOwner, Observer {

                moviesAdapter.differ.submitList(it.toList())
            })
        }

        binding.itemMoviesError.retryButton.setOnClickListener {

            moviesViewModel.getNowPlayingMovies(pageNumber)
        }
    }

    private fun setUpRecycler(){
        moviesAdapter = MovieRecyclerAdapter{ movie ->

            val bundle = Bundle().apply {
                    putInt("id", movie.id!!)
                Log.d("movieId",movie.id.toString())
                Log.d("movieId",movie.title.toString())
                Log.d("movieId",movie.overview.toString())
            }
            if(movie.title!= null){
                findNavController().navigate(R.id.action_nowPlayingFragment_to_moviesFragment,bundle)
            }
        }

        binding.recyclerNowPlaying.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(activity)
             addOnScrollListener(this@NowPlayingFragment.scrollListener)
        }
    }

    var isError = false
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.GONE
        isLoading = false
    }

    private fun showProgressBar(){

        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }
    private fun hideErrorMessage(){
        binding.itemMoviesError.errorText.visibility = View.INVISIBLE
        binding.itemMoviesError.retryButton.visibility=View.INVISIBLE
        isError = false
    }
    private fun showErrorMessage(message: String){
         binding.itemMoviesError.errorText.visibility=View.VISIBLE
        binding.itemMoviesError.retryButton.visibility=View.VISIBLE
        isError = true
        binding.itemMoviesError.errorText.text = message
    }

    val scrollListener  = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNoErrors = !isError
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanvisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNoErrors && isNotLoadingAndNotLastPage
                    && isNotAtBeginning && isTotalMoreThanvisible && isScrolling
            if (shouldPaginate) {
                moviesViewModel.getPopularMovies(pageNumber)
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {

                isScrolling = true
            }
        }
    }
}