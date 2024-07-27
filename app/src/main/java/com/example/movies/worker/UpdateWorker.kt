package com.example.movies.worker

import android.content.Context
import androidx.lifecycle.Observer
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.movies.ui.MainActivity

class UpdateWorker(context : Context, params : WorkerParameters) : CoroutineWorker(context, params)  {

    override suspend fun doWork(): Result {

        return try{
            val modelView = MainActivity().moviesViewModel
            modelView.updateMoviesDataAndApi()
            Result.success()
        }catch (e:Exception){
            Result.retry()
        }
    }

}

