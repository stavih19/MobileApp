package com.example.myapplication

import com.google.gson.annotations.SerializedName

data class Command(@SerializedName("aileron") val Aileron: Double,
                   @SerializedName("rudder") val Rudder: Double,
                   @SerializedName("elevator") val Elevator: Double,
                   @SerializedName("throttle") val Throttle: Double
)
