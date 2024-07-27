package com.example.movies.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.movies.R
import com.example.movies.databinding.ActivityMainBinding
import com.example.movies.repo.MoviesRepo
import com.example.movies.room.MoviesDatabase
import com.example.movies.worker.UpdateWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var moviesViewModel: MoviesViewModel
     lateinit var navController: NavController


    @RequiresApi(Build.VERSION_CODES.O)
    fun initWorker(){
        val consttraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicWorkRequest = PeriodicWorkRequestBuilder<UpdateWorker>(
            4, TimeUnit.HOURS)
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.LINEAR,
                duration = Duration.ofSeconds(15)
            ).setConstraints(consttraints)
            .build()

        val workManager = WorkManager.getInstance(applicationContext)
        workManager.enqueue(periodicWorkRequest)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initWorker()

        val moviesRepo = MoviesRepo(MoviesDatabase.getInstance(this))
        val moviesViewModelProviderFactory = MoviesViewModelProviderFactory(application, moviesRepo)
        moviesViewModel =
            ViewModelProvider(this, moviesViewModelProviderFactory).get(MoviesViewModel::class.java)


        // NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)


        if(navController.currentDestination?.id == R.id.moviesFragment){
            binding.bottomNavigationView.visibility= View.INVISIBLE
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.searchActivity -> {
                val intent = Intent(this, SearchActvity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}