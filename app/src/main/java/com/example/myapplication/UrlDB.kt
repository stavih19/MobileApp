package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UrlDB {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(url: UrlAddressList)

    @Query("SELECT urlString from urls_table") // SELECT * from urls_table order by urlString limit 5
    fun getLastFive(): List<String>
}