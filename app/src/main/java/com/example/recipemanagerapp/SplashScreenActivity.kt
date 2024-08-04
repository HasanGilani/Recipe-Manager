package com.example.recipemanagerapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val appNameTextView: TextView = findViewById(R.id.appNameTextView)
        val dropDownBounce = AnimationUtils.loadAnimation(this, R.anim.drop_down_bounce)
        appNameTextView.startAnimation(dropDownBounce)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // Delay for 3 seconds
    }
}