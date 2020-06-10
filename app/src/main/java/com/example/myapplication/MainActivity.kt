package com.example.myapplication

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import java.net.URL
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.ListMenuItemView
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // insert into the history list
        val list = findViewById<ListView>(R.id.historyList)

        var conncetHistory = Room.databaseBuilder(this, ListDatabase::class.java, "url_history")
            .allowMainThreadQueries().build().urlDatabase.getLastFive()
        conncetHistory = conncetHistory.asReversed()
        val adapter: ArrayAdapter<String>
        adapter =
            ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, conncetHistory)
        list.adapter = adapter
        list.setOnItemClickListener { parent, view, position, id ->
            val url = findViewById<TextView>(R.id.urlinput)
            url.text = list.getItemAtPosition(position).toString()
        }
    }

    fun tryConnect(view: View) {

    }

    private fun displayNavigationScreen() {

    }

    fun clearFiled(view: View): Unit {
        val url = findViewById<TextView>(R.id.urlinput)
        url.text = ""
    }

    fun deleteDB(view: View) {
        Room.databaseBuilder(this, ListDatabase::class.java, "url_history")
            .allowMainThreadQueries().build().urlDatabase.deleteAll()

        finish()
        startActivity(intent)
    }
}