package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.ramotion.fluidslider.FluidSlider
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.reflect.KParameter

class ControlActivity : AppCompatActivity() {
    var prevThrottle: Float = 0.0f
    var prevRudder: Float = 0.0f
    var prevAliaron: Float = 0.0f
    var prevElevator: Float = 0.0f

    var url: String = ""

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        // get the url address
        val connectHitsory = Room.databaseBuilder(this, ListDatabase::class.java, "url_history")
            .allowMainThreadQueries().build().urlDatabase.getLastFive()
        url = connectHitsory.get(0)

        // initial Rudder slider
        val maxRudder = 1
        val minRudder = -1
        val totalRudder = maxRudder - minRudder

        val rudderSlider = findViewById<FluidSlider>(R.id.rudder_slider)
        rudderSlider.colorBar = Color.BLUE
        rudderSlider.positionListener = { pos ->
            val newRudder = minRudder + (totalRudder * pos)
            var newVal = newRudder
            if (newRudder != 1.toFloat() && newRudder != 0.toFloat()) {
                newVal = ((newVal * 100).toInt().toDouble() / 100.0).toFloat()
            }
            rudderSlider.bubbleText = newVal.toString()
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
            var newVal = newThrottle
            if (newThrottle != 1.toFloat() && newThrottle != 0.toFloat()) {
                newVal = ((newVal * 100).toInt().toDouble() / 100.0).toFloat()
            }
            slidervertical.bubbleText = newVal.toString()
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

    @SuppressLint("WrongConstant", "ShowToast")
    fun sendValues() = lifecycleScope.launch {
        val status = withTimeoutOrNull(10000) {
            postCommand(
                (prevAliaron * 100).toInt().toDouble() / 100.0,
                (prevRudder * 100).toInt().toDouble() / 100.0,
                (prevElevator * 100).toInt().toDouble() / 100.0,
                (prevThrottle * 100).toInt().toDouble() / 100.0
            )
        }
        if (status == null) {
            Toast.makeText(this@ControlActivity, "the server is delay", 5).show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun getImage() {
        var switch = 1
        var stopFlag = false
        lifecycleScope.launch {
            while (!stopFlag) {
                var result = false
                if(switch == 1){
                    result = getScreenshot(flight_simulator_image, url)
                } else if(switch == 2){
                    result = getScreenshot(flight_simulator_image2, url)
                } else if(switch == 3){
                    result = getScreenshot(flight_simulator_image3, url)
                } else if(switch == 4){
                    result = getScreenshot(flight_simulator_image4, url)
                } else if(switch == 5){
                    result = getScreenshot(flight_simulator_image5, url)
                } else if(switch == 6){
                    result = getScreenshot(flight_simulator_image6, url)
                    switch = 0
                }
                switch++
                if (!result) {
                    stopFlag = true
                }

                if (stopFlag) { // TODO handle the returned result
                    throttle_slider.visibility = View.INVISIBLE
                    rudder_slider.visibility = View.INVISIBLE
                    joystickView.visibility = View.INVISIBLE
                    flight_simulator_image.visibility = View.INVISIBLE

                    massage.visibility = View.VISIBLE
                    back_button.visibility = View.VISIBLE
                    stay_button.visibility = View.VISIBLE
                }

                delay(300)
            }
        }
    }

    fun goHome(view: View) {
        //Toast.makeText(this, "connections is unstable", 5.toInt()).show()

        finish()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun stay(view: View) {
        throttle_slider.visibility = View.VISIBLE
        rudder_slider.visibility = View.VISIBLE
        joystickView.visibility = View.VISIBLE
        flight_simulator_image.visibility = View.VISIBLE

        massage.visibility = View.INVISIBLE
        back_button.visibility = View.INVISIBLE
        stay_button.visibility = View.INVISIBLE
    }
}
