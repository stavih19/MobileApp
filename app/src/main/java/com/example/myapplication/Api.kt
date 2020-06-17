package com.example.myapplication

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @Headers("Connection:close")
    @GET("/screenshot")
    fun getImg(): Call<ResponseBody>

    @POST("/api/command")
    fun postCommand(@Body command: Command): Call<Command>

}
