package com.example.movies.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movies.models.Movie
import com.example.movies.models.MovieResponse
import com.example.movies.models.details.DetailsResponse
import com.example.movies.repo.MoviesRepo
import com.example.movies.utils.Resources
import kotlinx.coroutines.launch
import retrofit2.Response

class MoviesViewModel(val moviesRepo: MoviesRepo) : ViewModel() {

    val popularMovies = MutableLiveData<Resources<MovieResponse>>()
    val nowPlayingMovies = MutableLiveData<Resources<MovieResponse>>()
    val topRatedMovies = MutableLiveData<Resources<MovieResponse>>()
    val upcomingMovies = MutableLiveData<Resources<MovieResponse>>()

    var details : DetailsResponse? =null


    val searchMovies = MutableLiveData<Resources<MovieResponse>>()

    var newSearchQuery : String? = null
    var oldSearchQuery:String? = null

    var  searchMoviePage = 1

    private var movieResponse : MovieResponse? = null
    private var searchMovieResponse : MovieResponse? = null



    var roomMovies : LiveData<ArrayList<Movie>>? = null


    fun getPopularMovies(page_number : Int) = viewModelScope.launch {
         popularMovies.postValue(Resources.Loading())
        try {
            popularMovies.postValue(handleMovieResponse(moviesRepo.getPopularMovies(page_number)))

        }catch (e:Exception){
        }

    }


    fun getUpComingMovies(page_number : Int) = viewModelScope.launch {
        upcomingMovies.postValue(Resources.Loading())

            upcomingMovies.postValue(handleMovieResponse(moviesRepo.getUpcomingMovies(page_number)))


    }

    fun getNowPlayingMovies(page_number : Int) = viewModelScope.launch {
        nowPlayingMovies.postValue(Resources.Loading())
        try {
            nowPlayingMovies.postValue(handleMovieResponse(moviesRepo.getNowPlayingMovies(page_number)))

        }catch (e:Exception){
            Log.d("ViewModel","${e.message}")
        }

    }

    fun getTopRatedMovies(page_number : Int) = viewModelScope.launch {
        topRatedMovies.postValue(Resources.Loading())
        try {
            topRatedMovies.postValue(handleMovieResponse(moviesRepo.getTopRatedMovies(page_number)))

        }catch (e:Exception){
            Log.d("ViewModel","${e.message}")
        }

    }

    // get Movie Details
    fun getMovieDetails(movie_id:Int) = viewModelScope.launch {
      details =   moviesRepo.getDetails(movie_id)
    }

    // handle network Response

    private fun handleMovieResponse(
        response : Response<MovieResponse>
    ) : Resources<MovieResponse>{

        if(response.isSuccessful){
            response.body()?.let{ resultResponse ->

                if(movieResponse == null){
                    movieResponse = resultResponse
                    Log.d("HandleResponse1","Movies: ${resultResponse.results}")
                }else{
                   val oldMovies = movieResponse?.results
                    val newMovies = resultResponse.results
                    oldMovies?.addAll(newMovies)

                }
                return Resources.Success(movieResponse?:resultResponse)

            }
        }
        return Resources.Error(response.message())

    }

    fun internetConnection(context: Context) : Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply{

            val internetStatus = getNetworkCapabilities(activeNetwork)?.run{
                when{
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->     true
                    else -> false
                }
            }
            return internetStatus?:false

        }

    }
    fun  search (keywords:String , page_number: Int) =viewModelScope.launch {
        searchMovies.postValue(Resources.Loading())

        searchMovies.postValue(handleSearchMovie(moviesRepo.search(keywords,page_number)))

    }

    private fun handleSearchMovie(
      response :  Response<MovieResponse>
    ) : Resources<MovieResponse>{
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                if(searchMovieResponse == null || newSearchQuery != oldSearchQuery){
                    searchMovieResponse = resultResponse
                    oldSearchQuery = newSearchQuery

                }else{
                    searchMoviePage++
                    val oldArticles = searchMovieResponse?.results
                    val newArticles = resultResponse.results

                    oldArticles?.addAll(newArticles)
                }
                return Resources.Success(searchMovieResponse?:resultResponse)
            }
        }
        return Resources.Error(response.message())

    }

//  Upsert Articles
fun  upsertMovies(moviesList : List<Movie>) = viewModelScope.launch {

    moviesRepo.upsert(moviesList)

}
// Get All Articles
    fun getAllArticlesFromRoom() = moviesRepo.getAllData()


    // delete data inside database
    fun deleteAll()=viewModelScope.launch {
        moviesRepo.deleteAll()
    }


fun  updateMoviesDataAndApi() = viewModelScope.launch {

    getUpComingMovies(2)
    getPopularMovies(1)
    getTopRatedMovies(3)
    getNowPlayingMovies(4)
    deleteAll()
    upsertMovies(roomMovies?.value!!.toList() )

}



}