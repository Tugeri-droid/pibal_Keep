package com.example.pibal_keep

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashActivity : AppCompatActivity() {
    //todo: Deklarasi variabel timer Splash Screen Muncul

    private val SPLASH_TIME_OUT:Long = 3000 //delay 3 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //kode untuk menjalankan main Screen timer Splash Screen habis

        Handler().postDelayed(Runnable(){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        },SPLASH_TIME_OUT)

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}