package com.example.pibal_keep

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class Time : AppCompatActivity() {
    private var seconds = 0
    private var running = false  //is the stopwatch running?
    private var wasRunning = false

    override fun onCreate(savedInstanceState: Bundle?){
        if (savedInstanceState != null) { // Get the previous state of the stopwatch if the activity has been destroyed and recreated
            seconds = savedInstanceState.getInt("seconds")
            running = savedInstanceState.getBoolean("running")
            wasRunning = savedInstanceState.getBoolean("wasRunning")
        }
        runTimer()

        val mulai: Button =findViewById(R.id.start_button)
        val selesai: Button =findViewById(R.id.finish_button)

        mulai.setOnClickListener{
            running= true
        }
        selesai.setOnClickListener{
            running= false
        }
    }

    fun runTimer() {
        val timeView = findViewById<View>(R.id.time_view) as TextView  // Get the text view.
        val handler = Handler()// Creates a new Handler
        /* Call the post() method, passing in a new Runnable. The post() method processes code without a delay, so the code in the Runnable will run almost immediately. */
        handler.post(object : Runnable {
            override fun run() {
                val minutes = seconds % 3600 / 60
                val secs = seconds % 60
                // Format the seconds into hours, minutes, and seconds.
                val time = String.format(
                    Locale.getDefault(),
                    "%02d:%02d",
                    minutes, secs
                )
                timeView.text = time   // Set the text view text.
                if (running) { seconds++ } // If running is true, increment the  seconds variable.
                handler.postDelayed(this, 1000) // Post the code again with a delay of 1 second.
            }
        })
    }
}