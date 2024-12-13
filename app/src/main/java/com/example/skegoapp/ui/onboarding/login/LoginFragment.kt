package com.example.skegoapp.ui.onboarding.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.skegoapp.R
import com.example.skegoapp.databinding.FragmentLoginBinding
import com.example.skegoapp.ui.main.MainActivity
import com.example.skegoapp.ui.mycustomview.MyButton
import com.example.skegoapp.ui.mycustomview.MyEditText
import com.example.skegoapp.ui.onboarding.ViewModelFactory

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // Initialize ViewModel with UserRepository and UserPreference
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var emailEditText: MyEditText
    private lateinit var passwordEditText: MyEditText
    private lateinit var loginButton: MyButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailEditText = binding.editTextEmail
        passwordEditText = binding.editTextPassword
        loginButton = binding.buttonLogin

        emailEditText.setInputType(MyEditText.InputType.EMAIL)
        passwordEditText.setInputType(MyEditText.InputType.PASSWORD)

        setLoginButtonEnabled()

        emailEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)

        val spannableString = SpannableString("Don't Have Account? Create one!")
        val colorSpan = ForegroundColorSpan(
            ContextCompat.getColor(requireContext(), R.color.orange_text)
        )
        spannableString.setSpan(colorSpan, 20, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textViewCreateAccount.text = spannableString

        binding.textViewForgotPassword.setOnClickListener {
            // Navigasi ke ForgotPasswordFragment
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        binding.textViewCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            requireActivity().supportFragmentManager.popBackStack()
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Call the login method from the ViewModel
                viewModel.login(email, password)
            } else {
                showErrorDialog("Please enter a valid email and password.")
            }
        }

        // Observe login status
        viewModel.loginStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                "success" -> showLoginSuccessDialog()
                "error" -> showErrorDialog("Invalid email or password.")
            }
        }

        // Observe loading status
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading indicator if necessary
        }
    }

    private fun setLoginButtonEnabled() {
        val isEmailValid = emailEditText.text.toString().isNotEmpty() && emailEditText.error == null
        val isPasswordValid = passwordEditText.text.toString().length >= 8
        loginButton.isEnabled = isEmailValid && isPasswordValid
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setLoginButtonEnabled()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun showLoginSuccessDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Success")
            setMessage("Login successful! Welcome back.")
            setPositiveButton("Continue") { _, _ ->
                val intent = Intent(requireContext(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            create()
            show()
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Error")
            setMessage(message)
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


