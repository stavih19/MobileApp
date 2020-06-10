package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UrlDB {
    @Insert
    fun insert(url: String)

    @Query("SELECT * from urls_table") // SELECT * from urls_table order by urlString limit 5
    fun getLastFive(): List<String>
}