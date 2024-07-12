package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //--------load next activity after some delay---------
        Timer().schedule(object : TimerTask() {
            override fun run() {
                val intent=Intent(this@SplashScreenActivity,WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000)
    }
}