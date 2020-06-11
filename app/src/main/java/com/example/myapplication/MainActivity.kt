package com.example.myapplication


import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import com.example.myapplication.*


class MainActivity : AppCompatActivity() {

    lateinit var tvIsConnected: TextView
    lateinit var etTitle: EditText
    lateinit var etUrl: EditText
    lateinit var etTags: EditText
    lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvIsConnected = findViewById<TextView>(R.id.tvIsConnected)
        etTitle = findViewById<EditText>(R.id.etTitle)
        etUrl = findViewById<EditText>(R.id.etUrl)
        etTags = findViewById<EditText>(R.id.etTags)
        tvResult = findViewById<TextView>(R.id.tvResult)


// TODO use this code to display image on screen with imageView id = imageView
        lifecycleScope.launch {
            val result = getScreenshot(imageView)
            postCommand(1.0,1.0,1.0,1.0)
        }


    }




}