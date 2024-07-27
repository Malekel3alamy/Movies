package com.example.movies.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movies.R
import com.example.movies.adapter.MovieRecyclerAdapter
import com.example.movies.databinding.ActivitySearchActvityBinding
import com.example.movies.repo.MoviesRepo
import com.example.movies.room.MoviesDatabase
import com.example.movies.utils.Constants
import com.example.movies.utils.Resources
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchActvity : AppCompatActivity() {
    lateinit var binding : ActivitySearchActvityBinding
    lateinit var moviesViewModel: MoviesViewModel
    lateinit var moviesAdapter : MovieRecyclerAdapter

    private val pageNumber = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchActvityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val moviesRepo = MoviesRepo(MoviesDatabase.getInstance(this))
        val moviesViewModelProviderFactory = MoviesViewModelProviderFactory(application, moviesRepo)
        moviesViewModel =
            ViewModelProvider(this, moviesViewModelProviderFactory).get(MoviesViewModel::class.java)
        setUpRecycler()

        var job:Job? = null


        binding.movieEditText.addTextChangedListener(object  :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

                    job?.cancel()
                    job = MainScope().launch {
                        delay(Constants.SEARCH_TIME_DELAY)
                        s?.let{
                            if (s.toString().isNotEmpty()) {
                                moviesViewModel.search(s.toString(),pageNumber)
                            }
                        }
                    }
            }

        })

        moviesViewModel.searchMovies.observe(this, Observer {
            when (it) {

                is Resources.Success<*> -> {

                    it.data?.let {
                        moviesAdapter.differ.submitList(it.results.toList())
                        val totalPages = it.total_results / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = moviesViewModel.searchMoviePage == totalPages
                        if (isLastPage) {
                            binding.recyclerSearch.setPadding(0, 0, 0, 0)
                        }
                    }
                }

                is Resources.Error -> {
                    it.message?.let { message ->
                        Toast.makeText(this, " Sorry Erro $message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resources.Loading -> {
                }
            }
        })
    }
    var isLastPage = false
    private fun setUpRecycler(){
        moviesAdapter = MovieRecyclerAdapter{ movie ->
            val bundle = Bundle().apply {
                if (movie.id != null&& movie!= null)
                    putInt("id", movie.id!!)
            }
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_searchFragment_to_moviesFragment,bundle)
        }

        binding.recyclerSearch.apply {
            adapter = moviesAdapter
            layoutManager = LinearLayoutManager(applicationContext)
            // addOnScrollListener(this@PopularFragment.scrollListener)
        }
    }

}