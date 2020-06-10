package com.example.myapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "urls_table")
class UrlAddressList() {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

    @ColumnInfo(name = "urlString")
    var url: String = ""
}