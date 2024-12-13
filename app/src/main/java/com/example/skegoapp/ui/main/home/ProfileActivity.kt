package com.example.skegoapp.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.skegoapp.R
import com.example.skegoapp.data.pref.UserPreference
import com.example.skegoapp.data.pref.dataStore
import com.example.skegoapp.ui.AboutActivity
import com.example.skegoapp.ui.main.ForgotPasswordActivity
import com.example.skegoapp.ui.onboarding.OnboardingActivity
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var userPreference: UserPreference
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Inisialisasi UserPreference
        userPreference = UserPreference.getInstance(applicationContext.dataStore)

        // Cek status login menggunakan coroutine
        lifecycleScope.launch {
            val isLoggedIn = userPreference.isLoggedIn()
            if (!isLoggedIn) {
                // Jika tidak login, arahkan ke OnboardingActivity
                val intent = Intent(this@ProfileActivity, OnboardingActivity::class.java)
                startActivity(intent)
                finish() // Tutup ProfileActivity
                return@launch
            }
        }

        // Inisialisasi View
        usernameTextView = findViewById(R.id.username_text_view)
        emailTextView = findViewById(R.id.email_text_view)
        logoutButton = findViewById(R.id.button_logout)

        // Menampilkan data login
        displayUserInfo()

        // Logout ketika tombol logout ditekan
        logoutButton.setOnClickListener {
            logout()
        }

        // Setup "Reset Password" button click
        findViewById<LinearLayout>(R.id.linearLayout).setOnClickListener {
            // Navigate to ForgotPasswordActivity
            val intent = Intent(this@ProfileActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        findViewById<LinearLayout>(R.id.linearLayout2).setOnClickListener {
            // Navigate to ForgotPasswordActivity
            val intent = Intent(this@ProfileActivity, AboutActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayUserInfo() {
        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                // Menampilkan data username dan email
                usernameTextView.text = user.username
                emailTextView.text = user.email
            }
        }
    }

    private fun logout() {
        lifecycleScope.launch {
            userPreference.logout() // Hapus data session
            // Clear the back stack and prevent going back to ProfileActivity
            val intent = Intent(this@ProfileActivity, OnboardingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
            startActivity(intent)
            finish() // Pastikan ProfileActivity selesai
        }
    }
}
