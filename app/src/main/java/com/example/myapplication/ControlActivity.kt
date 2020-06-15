package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.ramotion.fluidslider.FluidSlider
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.reflect.KParameter

class ControlActivity : AppCompatActivity() {
    var prevThrottle: Float = 0.0f;
    var prevRudder: Float = 0.0f;
    var prevAliaron: Float = 0.0f;
    var prevElevator: Float = 0.0f;

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        // initial Rudder slider
        val maxRudder = 1
        val minRudder = -1
        val totalRudder = maxRudder - minRudder

        val rudderSlider = findViewById<FluidSlider>(R.id.rudder_slider)
        rudderSlider.colorBar = Color.BLUE
        rudderSlider.positionListener = { pos ->
            val newRudder = minRudder + (totalRudder * pos)
            var newVal = "${newRudder}"
            if (newRudder != 1.toFloat() && newRudder != 0.toFloat()) {
                newVal = newVal.substring(0, 4)
            }
            rudderSlider.bubbleText = newVal
            if (abs(newRudder - prevRudder) >= 0.02) {
                prevRudder = newRudder
                sendValues()
            }
        }
        rudderSlider.position = 0.5f
        rudderSlider.startText = "$minRudder"
        rudderSlider.endText = "$maxRudder"

        // initial Throttle slider
        val maxThrottle = 0
        val minThrottle = 1
        val totalThrotle = maxThrottle - minThrottle

        val slidervertical = findViewById<FluidSlider>(R.id.throttle_slider)
        slidervertical.colorBar = Color.BLUE
        slidervertical.positionListener = { pos ->
            val newThrottle = minThrottle + (totalThrotle * pos)
            var newVal = "${newThrottle}"
            if (newThrottle != 1.toFloat() && newThrottle != 0.toFloat()) {
                newVal = newVal.substring(0, 4)
            }
            slidervertical.bubbleText = newVal
            if (abs(newThrottle - prevThrottle) >= 0.01) {
                prevThrottle = newThrottle
                sendValues()
            }

        }
        slidervertical.position = 1.0f
        slidervertical.startText = "$minThrottle"
        slidervertical.endText = "$maxThrottle"

        // initial joystick
        val joystick = joystickView as JoystickView
        joystick.setOnMoveListener { angle, strength ->
            val inRadians = angle * PI / 180.0
            val newAlieron = (cos(inRadians) * strength / 100.0).toFloat()
            val newElevator = (sin(inRadians) * strength / 100.0).toFloat()
            var sendFlag = false
            if (abs(prevAliaron - newAlieron) >= 0.01) {
                prevAliaron = newAlieron
                sendFlag = true
            }
            if (abs(prevElevator - newElevator) >= 0.01) {
                prevElevator = newElevator
                sendFlag = true
            }
            if (sendFlag) {
                sendValues()
            }
        }

        // start the endless loop
        getImage()
    }

    fun sendValues() {
        lifecycleScope.launch {
            postCommand(
                (prevAliaron*100).toInt().toDouble()/100.0,
                (prevRudder*100).toInt().toDouble()/100.0,
                (prevElevator*100).toInt().toDouble()/100.0,
                (prevThrottle*100).toInt().toDouble()/100.0
            )
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun getImage() {
        lifecycleScope.launch {
            while (true) {
                val result = getScreenshot(flight_simulator_image, )
                delay(250);
            }
        }
    }
}