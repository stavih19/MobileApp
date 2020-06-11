package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
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
        val newUrl = findViewById<TextView>(R.id.urlinput)
        val obj = UrlAddressList()
        obj.url = newUrl.text.toString()
        Room.databaseBuilder(this, ListDatabase::class.java, "url_history")
            .allowMainThreadQueries().build().urlDatabase.insert(obj)

        // connect

        // in case we did connect
        val intent = Intent(this, ControlActivity::class.java)
        startActivity(intent)
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