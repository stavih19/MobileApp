package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// the home page activity
class MainActivity : AppCompatActivity() {
    // the url address
    var url: String = ""

    // constructor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // insert url list into the history list
        val list = findViewById<ListView>(R.id.historyList)
        var conncetHistory = Room.databaseBuilder(this, ListDatabase::class.java, "url_history")
            .allowMainThreadQueries().build().urlDatabase.getLastFive()
        conncetHistory = conncetHistory.asReversed()
        val adapter: ArrayAdapter<String>
        adapter =
            ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, conncetHistory)
        list.adapter = adapter
        list.setOnItemClickListener { parent, view, position, id ->
            val textViewUrl = findViewById<TextView>(R.id.urlinput)
            textViewUrl.text = list.getItemAtPosition(position).toString()
        }
    }

    // when user want to connect to simulator
    fun tryConnect(view: View) {
        val newUrl = findViewById<TextView>(R.id.urlinput)
        val obj = UrlAddressList()
        obj.url = newUrl.text.toString()
        url = newUrl.text.toString()

        // try to delete the url form DB
        Room.databaseBuilder(this, ListDatabase::class.java, "url_history")
            .allowMainThreadQueries().build().urlDatabase.deleteByUrl(obj.url)
        // insert the url to the DB
        Room.databaseBuilder(this, ListDatabase::class.java, "url_history")
            .allowMainThreadQueries().build().urlDatabase.insert(obj)

        // in case we connected
        httpGetImage()
    }

    // show to user message
    private fun failedToSend(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // try to get image from user
    fun httpGetImage() {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("$url/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        api.getImg().enqueue(object : Callback<ResponseBody> {
            // in case we got the an image from controller
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    changeToControlActivity()
                } else{
                    failedToSend("Error getting first image " + response.code()
                            + " "+response.message() )
                }
            }

            // in case we did not got an image
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                failedToSend("TCP FAILURE while trying to get first image")
            }
        })
    }

    // send the user to the control activity
    private fun changeToControlActivity() {
        // in case we did connect
        val intent = Intent(this, ControlActivity::class.java)
        startActivity(intent)
    }

    // clear the url text when click on it
    fun clearFiled(view: View) {
        val url = findViewById<TextView>(R.id.urlinput)
        url.text = ""
    }

    // delete all the urls in the DB
    fun deleteDB(view: View) {
        Room.databaseBuilder(this, ListDatabase::class.java, "url_history")
            .allowMainThreadQueries().build().urlDatabase.deleteAll()

        finish()
        startActivity(intent)
    }
}