package com.example.myapplication

import androidx.room.*

@Dao
interface UrlDB {
    // insert a url to the table
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(url: UrlAddressList)

    // get the last five url in the table
    @Query("SELECT urlString from urls_table order by id desc limit 5")
    fun getLastFive(): List<String>

    // delete url from the table
    @Query("DELETE FROM urls_table WHERE urls_table.urlString=:url")
    fun deleteByUrl(url: String)

    // delete all the table's content
    @Query("DELETE FROM urls_table")
    fun deleteAll()
}