package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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

    @SuppressLint("WrongConstant", "ShowToast")
    fun tryConnect(view: View) {
        val newUrl = findViewById<TextView>(R.id.urlinput)
        val obj = UrlAddressList()
        obj.url = newUrl.text.toString()

        Room.databaseBuilder(this, ListDatabase::class.java, "url_history")
            .allowMainThreadQueries().build().urlDatabase.deleteByUrl(obj.url)

        Room.databaseBuilder(this, ListDatabase::class.java, "url_history")
            .allowMainThreadQueries().build().urlDatabase.insert(obj)

        // in case we did not connect
        // if statment
        Toast.makeText(this, "connection is failed", 5).show()

        // in case we did connect
        val intent = Intent(this, ControlActivity::class.java)
        startActivity(intent)
    }

    fun clearFiled(view: View) {
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