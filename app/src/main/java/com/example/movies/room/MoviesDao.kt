package com.example.movies.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movies.models.Movie
import com.example.movies.models.MovieResponse


@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(result :List<Movie>)

    @Query(" SELECT * FROM  movies")
     fun getAllMovies() : LiveData<List<Movie>>

    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()




}