package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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

        val stopFlag: StopFlag = StopFlag()
        lifecycleScope.launch {
            getScreenshot(fake_image, obj.url, stopFlag, fake_text, false)
        }
        Thread.sleep(100)
        if (!stopFlag.flag) {
            Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show()
        } else {        // in case we did connect
            val intent = Intent(this, ControlActivity::class.java)
            startActivity(intent)
        }
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