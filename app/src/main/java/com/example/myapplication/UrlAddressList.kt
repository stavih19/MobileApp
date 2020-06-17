package com.example.myapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// declare the table for the DB
@Entity(tableName = "urls_table")
class UrlAddressList() {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    // url field
    @ColumnInfo(name = "urlString")
    var url: String = ""
}