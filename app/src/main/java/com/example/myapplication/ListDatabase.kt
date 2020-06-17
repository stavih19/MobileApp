package com.example.myapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UrlAddressList::class], version = 1, exportSchema = false)
abstract class ListDatabase : RoomDatabase() {
    abstract val urlDatabase: UrlDB

    // singleton get instance object
    companion object {
        @Volatile
        private var INSTANCE: ListDatabase? = null
        fun getInsatnce(context: Context): ListDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = newDatabase(context)
                }
                INSTANCE = instance
                return instance
            }
        }

        // create the first time
        private fun newDatabase(context: Context): ListDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ListDatabase::class.java, "url_history"
            ).fallbackToDestructiveMigration().build()
        }
    }
}