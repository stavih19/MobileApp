package com.example.myapplication

import kotlinx.coroutines.Deferred
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*


interface Api {
    @Headers("Connection:close")
    @GET("/screenshot")
    fun getImg(): Call<ResponseBody>

    @POST("/api/command")
    fun postCommand(@Body command: Command): Call<Command>

}
