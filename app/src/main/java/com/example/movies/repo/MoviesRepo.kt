package com.example.movies.repo

import com.example.movies.api.RetrofitInstance
import com.example.movies.models.Movie
import com.example.movies.room.MoviesDatabase

class MoviesRepo (val db:MoviesDatabase) {


   suspend fun getTopRatedMovies(pageNumber:Int) =
      RetrofitInstance.api.getTopRatedMovies(pageNumber)

   suspend fun getPopularMovies(pageNumber:Int) =
      RetrofitInstance.api.getPopularMovies(pageNumber)

   suspend fun getNowPlayingMovies(pageNumber:Int) =RetrofitInstance. api.getNowPlayingMovies(pageNumber)
   suspend fun getUpcomingMovies(pageNumber:Int) = RetrofitInstance.api.getUpcomingMovies(pageNumber)


     // Search For Movies
   suspend fun  search(keyWords: String,pageNumber:Int) = RetrofitInstance.api.searchForMovies(keyWords, pageNumber)
// Get Details
   suspend fun  getDetails(movie_id:Int) = RetrofitInstance.api.getDetails(movie_id)


   // Insert Data To Room
   suspend fun  upsert(moviesList:List<Movie>) = db.getMoviesDao().upsert(moviesList)

   // Delete All Room Database
   suspend fun deleteAll() = db.getMoviesDao().deleteAllMovies()

   // Get All Data From Room
   fun getAllData() = db.getMoviesDao().getAllMovies()

}