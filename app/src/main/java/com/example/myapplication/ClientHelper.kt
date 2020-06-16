package com.example.myapplication

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.service.voice.VoiceInteractionSession
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.room.Room
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

fun getScreenshot(imageView: ImageView, url: String, flag: StopFlag, massage: TextView) {
    Picasso.get().load(url + "/screenshot")
        .into(imageView, object : com.squareup.picasso.Callback {
            override fun onSuccess() {

            }

            @SuppressLint("SetTextI18n")
            override fun onError(e: java.lang.Exception?) {
                flag.flag = true
                massage.text = "connection is failed"
            }
        })
}

suspend fun postCommand(
    aileron: Double,
    rudder: Double,
    elevator: Double,
    throttle: Double
): Boolean {
    try {
        withContext(Dispatchers.IO) {

            val url = URL("http://10.0.2.2:65011/api/command")
            // 1. create HttpURLConnection
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")

            // 2. build JSON object
            val jsonObject = buidJsonObject(aileron, rudder, elevator, throttle)
            // 3. add JSON content to POST request body
            setPostRequestContent(conn, jsonObject)

            // 4. make POST request to the given URL
            conn.connect()

            // 5. return response message
            conn.responseMessage + ""
        }
        return true
    } catch (e: Exception) {
        return false
    }
    //return false
}

@Throws(JSONException::class)
fun buidJsonObject(aileron: Double, rudder: Double, elevator: Double, throttle: Double)
        : JSONObject {

    val jsonObject = JSONObject()
    jsonObject.accumulate("aileron", aileron)
    jsonObject.accumulate("rudder", rudder)
    jsonObject.accumulate("elevator", elevator)
    jsonObject.accumulate("throttle", throttle)

    //jsonObject.accumulate("tags", etTags.getText().toString())

    return jsonObject
}

@Throws(IOException::class)
fun setPostRequestContent(conn: HttpURLConnection, jsonObject: JSONObject) {

    val os = conn.outputStream
    val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
    writer.write(jsonObject.toString())
    Log.i(MainActivity::class.java.toString(), jsonObject.toString())
    writer.flush()
    writer.close()
    os.close()
}

