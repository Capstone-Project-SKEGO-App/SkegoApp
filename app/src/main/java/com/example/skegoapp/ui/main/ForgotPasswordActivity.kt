package com.example.skegoapp.ui.main

import android.app.ProgressDialog.show
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.skegoapp.R
import com.example.skegoapp.data.remote.repository.ForgotPasswordRepository
import com.example.skegoapp.data.remote.retrofit.ApiConfig
import com.example.skegoapp.databinding.ActivityForgotPasswordBinding
import com.example.skegoapp.ui.main.home.ProfileActivity
import com.example.skegoapp.ui.mycustomview.MyButton
import com.example.skegoapp.ui.mycustomview.MyEditText
import com.example.skegoapp.ui.onboarding.forgot_password.ForgotPasswordViewModel
import com.example.skegoapp.ui.onboarding.forgot_password.ForgotPasswordViewModelFactory

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    private lateinit var emailEditText: MyEditText
    private lateinit var oldPasswordEditText: MyEditText
    private lateinit var newPasswordEditText: MyEditText
    private lateinit var submitButton: MyButton

    private lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize custom views
        emailEditText = binding.editTextEmail
        oldPasswordEditText = binding.editTextOldPassword
        newPasswordEditText = binding.editTextNewPassword
        submitButton = binding.buttonChangePassword

        // Set input types for custom MyEditText views
        emailEditText.setInputType(MyEditText.InputType.EMAIL)
        oldPasswordEditText.setInputType(MyEditText.InputType.PASSWORD)
        newPasswordEditText.setInputType(MyEditText.InputType.PASSWORD)

        // Enable or disable the submit button based on input
        setSubmitButtonEnabled()

        // Add TextWatchers to handle validation dynamically
        emailEditText.addTextChangedListener(textWatcher)
        oldPasswordEditText.addTextChangedListener(textWatcher)
        newPasswordEditText.addTextChangedListener(textWatcher)

        // Initialize ViewModel
        val factory = ForgotPasswordViewModelFactory(ForgotPasswordRepository(ApiConfig.getApiService()))
        viewModel = ViewModelProvider(this, factory).get(ForgotPasswordViewModel::class.java)

        // Handle submit button click
        submitButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val oldPassword = oldPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()

            // Check if all fields are filled
            if (email.isNotEmpty() && oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                // Call ViewModel to change password
                viewModel.changePassword(email, oldPassword, newPassword)
            } else {
                showErrorDialog("Please fill all the fields.")
            }
        }

        // Observe ViewModel response for password change status
        viewModel.passwordUpdateStatus.observe(this, Observer { response ->
            if (response != null) {
                // Show success message
                Toast.makeText(this, response.message, Toast.LENGTH_LONG).show()
                // Navigate to LoginActivity after success
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish() // Finish the ForgotPasswordActivity
            } else {
                // Show error message if password update failed
                Toast.makeText(this, "Failed to update password", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setSubmitButtonEnabled() {
        // Enable the submit button if all fields are valid
        val isEmailValid = emailEditText.text.toString().isNotEmpty() && emailEditText.error == null
        val isOldPasswordValid = oldPasswordEditText.text.toString().length >= 8
        val isNewPasswordValid = newPasswordEditText.text.toString().length >= 8
        submitButton.isEnabled = isEmailValid && isOldPasswordValid && isNewPasswordValid
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Update button state based on current input validation
            setSubmitButtonEnabled()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }
}
