package com.example.movies.models

data class MovieResponse(
    val dates: Dates,
    val page: Int,
    val results: MutableList<Movie>,
    val total_pages: Int,
    val total_results: Int
)