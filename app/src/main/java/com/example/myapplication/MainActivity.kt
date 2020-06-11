package com.example.myapplication


import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import io.github.controlwear.virtual.joystick.android.JoystickView
import io.github.controlwear.virtual.joystick.android.JoystickView.OnMoveListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class MainActivity : AppCompatActivity() {

    lateinit var tvIsConnected: TextView
    lateinit var etTitle: EditText
    lateinit var etUrl: EditText
    lateinit var etTags: EditText
    lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tvResult = findViewById<TextView>(R.id.tvResult)


// TODO use this code to display image on screen with imageView id = imageView
        lifecycleScope.launch {
            val result = getScreenshot(imageView)
            postCommand(1.0,1.0,1.0,1.0)
        }

        //val joystick = joystickView as JoystickView


        val joystick = joystickView as JoystickView
        joystick.setOnMoveListener { angle, strength ->
            val inRadians = angle * PI / 180.0
            val x = cos(inRadians)*strength/100.0
            val y = sin(inRadians)*strength/100.0
            println("angle $angle strength $strength x $x y $y")
        }
    }




}