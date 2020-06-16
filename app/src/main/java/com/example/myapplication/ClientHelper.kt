package com.example.myapplication

import android.R
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.google.gson.GsonBuilder
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


fun getScreenshot(imageView: ImageView, url: String): Boolean {
    val gson = GsonBuilder()
        .setLenient()
        .create()
    val retrofit = Retrofit.Builder()
        .baseUrl("$url/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    val api = retrofit.create(Api::class.java)
    val body = api.getImg().enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
            val I = response?.body()?.byteStream()
            val B = BitmapFactory.decodeStream(I)
            imageView.setImageBitmap(B)
        }
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

        }
    })
    return true
}


suspend fun postCommand(
    aileron: Double,
    rudder: Double,
    elevator: Double,
    throttle: Double
): Boolean {
    try {
        val result = withContext(Dispatchers.IO) {

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

