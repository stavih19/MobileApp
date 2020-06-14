package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.ramotion.fluidslider.FluidSlider
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_control.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.reflect.KParameter

class ControlActivity : AppCompatActivity() {
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        val maxhorizon = 1
        val minhorizon = -1
        val totalhorizon = maxhorizon - minhorizon

        val sliderhorizon = findViewById<FluidSlider>(R.id.horizon_slider)
        sliderhorizon.colorBar = Color.BLUE
        sliderhorizon.positionListener = { pos ->
            var newVal = "${minhorizon + (totalhorizon * pos)}"
            if (newVal.toFloat() != 1.toFloat()) {
                newVal = newVal.substring(0, 4)
            }
            sliderhorizon.bubbleText = newVal

        }
        sliderhorizon.position = 0.3f
        sliderhorizon.startText = "$minhorizon"
        sliderhorizon.endText = "$maxhorizon"

        val maxvertical = 1
        val minvertical = -1
        val totalvertical = maxvertical - minvertical

        val slidervertical = findViewById<FluidSlider>(R.id.vertical_slider)
        slidervertical.colorBar = Color.BLUE
        slidervertical.positionListener = { pos ->
            var newVal = "${minvertical + (totalvertical * pos).toFloat()}"
            if (newVal.toFloat() != 1.toFloat()) {
                newVal = newVal.substring(0, 4)
            }
            slidervertical.bubbleText = newVal

        }
        slidervertical.position = 0.3f
        slidervertical.startText = "$minvertical"
        slidervertical.endText = "$maxvertical"

        //joystick
        val joystick = joystickView as JoystickView
        joystick.setOnMoveListener { angle, strength ->
            val inRadians = angle * PI / 180.0
            val x = cos(inRadians) *strength/100.0
            val y = sin(inRadians) *strength/100.0
            println("angle $angle strength $strength x $x y $y")
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}