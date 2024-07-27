package com.example.movies.api

import com.example.movies.utils.Constants.Companion.API_KEY
import com.example.movies.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit

class RetrofitInstance {
    companion object {


        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor{chain ->
                    val original = chain.request()
                    val http =original.url.newBuilder().addQueryParameter("api_key", API_KEY).build()
                    val request = original.newBuilder().url(http).build()
                    return@addInterceptor chain.proceed(request)

                }
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .connectTimeout(1, TimeUnit.MINUTES)
                .retryOnConnectionFailure(true)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
        val api: MoviesApi = retrofit.create(MoviesApi::class.java)



    }


}
    /*  val api: MoviesApi by lazy {
            retrofit.create(MoviesApi::class.java)

        }*/