package com.example.myapplication

import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

fun getScreenshot(imageView: ImageView, url: String): Boolean {
    //TODO check fail
    val token = (Math.random()*100000).toInt()
    Picasso.get().load(url + "/screenshot?" +token).noFade().into(imageView)
    return true
}


suspend fun postCommand(aileron:Double, rudder:Double, elevator:Double, throttle:Double): Boolean {
    try {
        val result = withContext(Dispatchers.IO) {

            val url = URL("http://10.0.2.2:65011/api/command")
            // 1. create HttpURLConnection
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")

            // 2. build JSON object
            val jsonObject = buidJsonObject(aileron, rudder,elevator,throttle)
            // 3. add JSON content to POST request body
            setPostRequestContent(conn, jsonObject)

            // 4. make POST request to the given URL
            conn.connect()

            // 5. return response message
            conn.responseMessage + ""
        }
        return true
    } catch(e:Exception) {
        return false
    }
    return false
}

@Throws(JSONException::class)
fun buidJsonObject(aileron:Double, rudder:Double, elevator:Double, throttle:Double)
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

