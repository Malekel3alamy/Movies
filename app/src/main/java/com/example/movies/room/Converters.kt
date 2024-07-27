package com.example.movies.room

import androidx.room.TypeConverter
import com.example.movies.models.Dates
import com.example.movies.models.Movie
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromDate (date : Dates) : String {
        return date.maximum
    }

    @TypeConverter
    fun toDate(maximum: String) : Dates{
        return Dates(maximum,maximum)
    }
    @TypeConverter
    fun fromIntList(list : List<Int>) : Int {

        return list[0]
    }
    @TypeConverter
    fun listToGson(value :List<Movie>) =Gson().toJson(value)

    @TypeConverter
    fun gsonToList(value : String) = Gson().fromJson(value,Array<Movie>::class.java).toList()


    @TypeConverter
    fun toList(id:Int) : List<Int> {

        return listOf(id)
    }





}