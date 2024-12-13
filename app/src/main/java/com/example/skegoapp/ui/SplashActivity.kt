package com.example.skegoapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.skegoapp.R
import com.example.skegoapp.data.pref.UserPreference
import com.example.skegoapp.data.pref.dataStore
import com.example.skegoapp.ui.main.MainActivity
import com.example.skegoapp.ui.onboarding.OnboardingActivity
import kotlinx.coroutines.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val splashDuration = 2000L // Duration in milliseconds (2 seconds)
    private lateinit var userPreference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize UserPreference with the dataStore
        userPreference = UserPreference.getInstance(applicationContext.dataStore)

        // Launch coroutine using lifecycleScope
        lifecycleScope.launch {
            delay(splashDuration)
            checkSessionAndNavigate()
        }
    }

    private suspend fun checkSessionAndNavigate() {
        // Get the session data
        userPreference.getSession().collect { session ->
            if (session.isLogin) {
                // If session exists, navigate to MainActivity
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                // If no session, navigate to OnboardingActivity or LoginActivity
                startActivity(Intent(this@SplashActivity, OnboardingActivity::class.java))
            }
            finish()
        }
    }

    // Animasi transisi
    override fun startActivity(intent: Intent?) {
        super.startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}


