package com.example.movies.api

import com.example.movies.models.Movie
import com.example.movies.models.MovieResponse
import com.example.movies.models.details.DetailsResponse
import com.example.movies.utils.Constants.Companion.API_KEY
import com.example.movies.utils.Resources
import org.intellij.lang.annotations.Language
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApi {

    @GET("search/movie")
    suspend fun searchForMovies(

        @Query("query")
        query: String ,
        @Query("page")
        pageNumber: Int =1 ,
    ) : Response<MovieResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("page")
        page : Int  ,


    ): Response<MovieResponse>
    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(

        @Query("page")
        page : Int  ,

    ) : Response<MovieResponse>
    @GET("movie/popular")
    suspend fun getPopularMovies(

        @Query("page")
        page : Int  ,


    ) : Response<MovieResponse>
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(

        @Query("page")
        page : Int

    ): Response<MovieResponse>

    @GET("movie/{movie_id}")
    suspend fun getDetails(
        @Path("movie_id")
        movie_id:Int
    ):DetailsResponse
}