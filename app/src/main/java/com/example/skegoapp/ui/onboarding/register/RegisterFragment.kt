package com.example.skegoapp.ui.onboarding.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.skegoapp.R
import com.example.skegoapp.databinding.FragmentRegisterBinding
import com.example.skegoapp.ui.mycustomview.MyButton
import com.example.skegoapp.ui.mycustomview.MyEditText
import com.example.skegoapp.ui.onboarding.OnboardingActivity
import com.example.skegoapp.ui.onboarding.ViewModelFactory

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var usernameEditText: MyEditText
    private lateinit var emailEditText: MyEditText
    private lateinit var passwordEditText: MyEditText
    private lateinit var registerButton: MyButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi custom view dari binding
        usernameEditText = binding.editTextName
        emailEditText = binding.editTextEmail
        passwordEditText = binding.editTextPassword
        registerButton = binding.buttonRegister

        // Set tipe input untuk email dan password
        usernameEditText.setInputType(MyEditText.InputType.USERNAME)
        emailEditText.setInputType(MyEditText.InputType.EMAIL)
        passwordEditText.setInputType(MyEditText.InputType.PASSWORD)

        // Aktifkan/Nonaktifkan tombol register
        setRegisterButtonEnabled()

        // Set listener untuk input teks
        usernameEditText.addTextChangedListener(textWatcher)
        emailEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)

        val spannableString = SpannableString("Already Have Account? Log In!")
        val colorSpan = ForegroundColorSpan(
            ContextCompat.getColor(requireContext(), R.color.orange_text)
        )
        spannableString.setSpan(colorSpan, 22, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.textViewLoginAccount.text = spannableString

        binding.textViewLoginAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Set action tombol register
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.register(username, email, password)
            } else {
                showErrorDialog("Please fill all fields.")
            }
        }

        // Observasi status registrasi
        viewModel.registerStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                "success" -> showRegisterSuccessDialog()
                "error" -> showErrorDialog("An error occurred")
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Show/hide loading indicator if necessary
        }
    }


    private fun setRegisterButtonEnabled() {
        val isUsernameValid = usernameEditText.text.toString().isNotEmpty()
        val isEmailValid = emailEditText.text.toString().isNotEmpty() && emailEditText.error == null
        val isPasswordValid = passwordEditText.text.toString().length >= 8
        registerButton.isEnabled = isUsernameValid && isEmailValid && isPasswordValid
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            setRegisterButtonEnabled()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun showRegisterSuccessDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Success")
            setMessage("Registration successful! Please login.")
            setPositiveButton("OK") { _, _ ->
                val intent = Intent(requireContext(), OnboardingActivity::class.java)
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
