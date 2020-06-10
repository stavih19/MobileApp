package com.example.myapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface UrlDB {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(url: UrlAddressList)

    @Query("SELECT urlString from urls_table order by id desc limit 5")
    fun getLastFive(): List<String>

    @Query("DELETE FROM urls_table")
    fun deleteAll()
}