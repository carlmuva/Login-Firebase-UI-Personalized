package com.example.loginfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val screenSplash = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Thread.sleep(1000)
        screenSplash.setKeepOnScreenCondition{true}
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}