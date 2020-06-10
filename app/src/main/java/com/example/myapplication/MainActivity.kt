package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import java.net.URL
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // insert into the history list
        val list = findViewById<ListView>(R.id.historyList)

        uiScope.launch {
            val conncetHistory = getlist()
            val adapter: ArrayAdapter<String>
            if (conncetHistory != null) {
                adapter =
                    ArrayAdapter(
                        this@MainActivity,
                        android.R.layout.simple_list_item_1,
                        conncetHistory.toList()
                    )
            }
        }
    }

    suspend fun getlist(): List<String> {
        val conncetHistory = ListDatabase.getInsatnce(this).urlDatabase.getLastFive()
        return conncetHistory
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun tryConnect(view: View): Unit {
        val url = findViewById<TextView>(R.id.urlinput).text.toString()
        if (url == "") {
            return
        }

        val apiURL = URL("https://localhost:44354/api/Connect")

        val conncetHistory = ListDatabase.getInsatnce(this).urlDatabase.insert(url)

        finish()
        startActivity(intent)
    }

/*
    val connection = apiURL.openConnection() as HttpsURLConnection
    connection.requestMethod = "POST"
    connection.doOutput = true

    val postData: ByteArray = url.toByteArray(StandardCharsets.UTF_8)

    connection.setRequestProperty("charset", "utf-8")
    connection.setRequestProperty("Content-length", postData.size.toString())
    connection.setRequestProperty("Content-Type", "application/json")

    try {
        val stream = connection.outputStream
        val outputStream: DataOutputStream = DataOutputStream(stream)
        outputStream.write(postData)
        outputStream.flush()
    } catch (exeception: Exception) {

    }
 */


    /*with(apiURL.openConnection() as HttpsURLConnection) { // https/**/
        requestMethod = "POST"

        val wr = OutputStreamWriter(outputStream);
        wr.write(url);
        wr.flush();

        BufferedReader(InputStreamReader(inputStream)).use {
            val response = StringBuffer()

            var inputLine = it.readLine()
            while (inputLine != null) {
                response.append(inputLine)
                inputLine = it.readLine()
            }

            if (response.toString() == "OK") {
                addSucssesURL(url)
                displayNavigationScreen()
            } else {
                // print alert massage
            }
        }
    }*/

    private fun displayNavigationScreen() {

    }

    fun cleaFiled(view: View) {
        val url = findViewById<TextView>(R.id.urlinput)
        url.text = ""
    }
}