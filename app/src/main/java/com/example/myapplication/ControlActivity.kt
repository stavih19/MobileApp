package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.gson.GsonBuilder
import com.ramotion.fluidslider.FluidSlider
import io.github.controlwear.virtual.joystick.android.JoystickView
import kotlinx.android.synthetic.main.activity_control.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.net.ProtocolException
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// the activity with the user can control the FlightSimulator
class ControlActivity : AppCompatActivity() {
    // initial the four parameters to post
    var prevThrottle: Float = 0.0f
    var prevRudder: Float = 0.0f
    var prevAliaron: Float = 0.0f
    var prevElevator: Float = 0.0f

    // url address string
    var url: String = ""

    // stop flag for the get image function loop
    var stopFlag = false

    // minimum rate of difference to post vales
    val change = 0.5

    // constructor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        // get the url address from DB
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
            if (abs(newThrottle - prevThrottle) >= change) {
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
            if (abs(prevAliaron - newAlieron) >= change) {
                prevAliaron = newAlieron
                sendFlag = true
            }
            if (abs(prevElevator - newElevator) >= change) {
                prevElevator = newElevator
                sendFlag = true
            }
            if (sendFlag) {
                sendValues()
            }
        }

        // start the "endless" loop
        getImage()
    }

    // send the four values
    fun sendValues() {
        CoroutineScope(IO).launch {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val retrofit = Retrofit.Builder()
                .baseUrl("$url/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            val api = retrofit.create(Api::class.java)
            api.postCommand(
                Command(
                    (prevAliaron * 100).toInt().toDouble() / 100.0,
                    (prevRudder * 100).toInt().toDouble() / 100.0,
                    (prevElevator * 100).toInt().toDouble() / 100.0,
                    (prevThrottle * 100).toInt().toDouble() / 100.0
                )
            ).enqueue(object : Callback<Command> {
                override fun onResponse(call: Call<Command>, response: Response<Command>) {

                }

                // in case there is failure in the post request
                override fun onFailure(call: Call<Command>, t: Throwable) {
                    failedToSend("error post command")
                }
            })
        }
    }

    // display message to the user
    private fun failedToSend(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 0)
        toast.show()
    }

    // when the user push back button
    override fun onBackPressed() {
        finish()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // ask for image
    fun httpGetImage() {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("$url/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val api = retrofit.create(Api::class.java)
        api.getImg().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                val I = response.body()?.byteStream()
                val B = BitmapFactory.decodeStream(I)
                runOnUiThread {
                    flight_simulator_image.setImageBitmap(B)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                try {
                    failedToSend("protocol error")
                } catch (e: Exception) {
                    throttle_slider.visibility = View.INVISIBLE
                    rudder_slider.visibility = View.INVISIBLE
                    joystickView.visibility = View.INVISIBLE
                    flight_simulator_image.visibility = View.INVISIBLE

                    massage.visibility = View.VISIBLE
                    back_button.visibility = View.VISIBLE
                    stay_button.visibility = View.VISIBLE
                    massage.text = "Error: from server"
                    stopFlag = true
                }
            }
        })
    }

    // ask for image until something get wrong
    fun getImage() {
        CoroutineScope(IO).launch {
            while (!stopFlag) {
                httpGetImage()
                delay(300)
            }
        }
    }

    // when the user want to go home page after some failure happen
    fun goHome(view: View) {
        finish()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // when the user want to stay in the control page after some failure happen
    fun stay(view: View) {
        throttle_slider.visibility = View.VISIBLE
        rudder_slider.visibility = View.VISIBLE
        joystickView.visibility = View.VISIBLE
        flight_simulator_image.visibility = View.VISIBLE

        massage.visibility = View.INVISIBLE
        back_button.visibility = View.INVISIBLE
        stay_button.visibility = View.INVISIBLE

        // start the "endless" loop again
        stopFlag = false
        getImage()
    }
}
