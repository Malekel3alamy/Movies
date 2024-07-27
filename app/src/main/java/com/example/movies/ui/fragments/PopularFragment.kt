package com.example.movies.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.AbsListView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movies.R
import com.example.movies.adapter.MovieRecyclerAdapter
import com.example.movies.databinding.FragmentPopularBinding
import com.example.movies.ui.MainActivity
import com.example.movies.ui.MoviesViewModel
import com.example.movies.utils.Constants

class PopularFragment : Fragment(R.layout.fragment_popular) {
    lateinit var moviesViewModel: MoviesViewModel
    lateinit var binding: FragmentPopularBinding
    lateinit var moviesAdapter : MovieRecyclerAdapter

    lateinit var retryButton : Button
    lateinit var errorText : TextView
    lateinit var itemError: CardView
    private val pageNumber = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPopularBinding.bind(view)



        itemError = view.findViewById(R.id.itemMoviesError)
        val inflater =
            requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val ite_view_error = inflater.inflate(R.layout.item_error, null)

        retryButton = ite_view_error.findViewById(R.id.retryButton)
        errorText = ite_view_error.findViewById(R.id.errorText)




        moviesViewModel = (activity as MainActivity).moviesViewModel
        setUpRecycler()
        moviesViewModel.getPopularMovies(pageNumber)

        if (moviesViewModel.internetConnection((activity as MainActivity).applicationContext)){
        moviesViewModel.popularMovies.observe(viewLifecycleOwner, Observer {
            when (it) {
                is com.example.movies.utils.Resources.Success<*> -> {
                    hideProgressBar()
                    hideErrorMessage()
                    it.data?.let {
                        moviesAdapter.differ.submitList(it.results.toList())
                  /*      val totalPages =
                            it.total_results / com.example.movies.utils.Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = moviesViewModel.pageNumber == totalPages
                        if (isLastPage) {
                            binding.recyclerPopulars.setPadding(0, 0, 0, 0)
                        }*/

                    }
                }

                is com.example.movies.utils.Resources.Error -> {
                    hideProgressBar()
                    it.message?.let { message ->
                        Toast.makeText(activity, "Error$message", Toast.LENGTH_SHORT).show()
                        Log.d("APIERROR", message)
                      //  showErrorMessage(message)
                    }
                }

                is com.example.movies.utils.Resources.Loading -> {
                    showProgressBar()

                }
            }

        })
    }else{

            moviesViewModel.getAllArticlesFromRoom().observe(viewLifecycleOwner, Observer {

                moviesAdapter.differ.submitList(it.toList())
            })
        }

        retryButton.setOnClickListener {

            moviesViewModel.getPopularMovies(pageNumber)
        }
    }

    private fun setUpRecycler(){
        moviesAdapter = MovieRecyclerAdapter{ movie ->
            val bundle = Bundle().apply {
                if (movie.id != null)
                    putInt("id", movie.id!!)
            }
            findNavController().navigate(R.id.action_popularFragment_to_moviesFragment,bundle)

        }

        binding.recyclerPopulars.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(activity)
           addOnScrollListener(this@PopularFragment.scrollListener)
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
        itemError.visibility = View.INVISIBLE
        isError = false
    }
    private fun showErrorMessage(message: String){
        itemError.visibility = View.VISIBLE
        isError = true
        errorText.text = message
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