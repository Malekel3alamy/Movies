package com.example.movies.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.movies.repo.MoviesRepo

class MoviesViewModelProviderFactory(val app : Application, val  moviesRepo: MoviesRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MoviesViewModel(moviesRepo) as T
    }
}