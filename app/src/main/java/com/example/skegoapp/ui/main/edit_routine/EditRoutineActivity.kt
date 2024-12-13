package com.example.skegoapp.ui.main.edit_routine

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skegoapp.data.pref.Routine
import com.example.skegoapp.data.pref.UserPreference
import com.example.skegoapp.data.pref.dataStore
import com.example.skegoapp.data.remote.retrofit.ApiConfig
import com.example.skegoapp.databinding.ActivityEditRoutineBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class EditRoutineActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditRoutineBinding
    private var routineId: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable View Binding
        binding = ActivityEditRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive data from the Intent
        routineId = intent.getIntExtra("ROUTINE_ID", 0)
        val title = intent.getStringExtra("ROUTINE_TITLE") ?: ""
        val date = intent.getStringExtra("ROUTINE_DATE") ?: ""
        val time = intent.getStringExtra("ROUTINE_TIME") ?: ""
        val location = intent.getStringExtra("ROUTINE_LOCATION") ?: ""
        val detail = intent.getStringExtra("ROUTINE_DETAIL") ?: ""
        val category = intent.getStringExtra("ROUTINE_CATEGORY") ?: ""

        // Set the received data in the form fields
        binding.etTitleRoutine.setText(title)
        binding.etDate.setText(date)
        binding.etTime.setText(time)
        binding.etLocation.setText(location)
        binding.etDetail.setText(detail)
        binding.dropdownFrequency.setText(category, false)

        // Setup back button
        binding.btnBack.setOnClickListener {
            finish() // Close the activity and return to the previous one
        }

        // Setup date picker for selecting the date
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        // Setup time picker for selecting the time
        binding.etTime.setOnClickListener {
            showTimePicker()
        }

        // Setup category dropdown
        val categories = listOf("Work", "Personal", "Exercise", "Study", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.dropdownFrequency.setAdapter(adapter)

        // Setup save button
        binding.btnSave.setOnClickListener {
            updateRoutine()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.etDate.setText(formattedDate)
        }, year, month, day).show()
    }

    @SuppressLint("DefaultLocale")
    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            binding.etTime.setText(formattedTime)
        }, hour, minute, true).show()
    }

    private fun updateRoutine() {
        val title = binding.etTitleRoutine.text.toString().trim()
        val date = binding.etDate.text.toString().trim()
        val time = binding.etTime.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val detail = binding.etDetail.text.toString().trim()
        val category = binding.dropdownFrequency.text.toString().trim()

        if (title.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty() || detail.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("UpdateRoutine", "Routine ID: $routineId")  // Log the routine ID

        // Fetch the user session and get userId
        MainScope().launch {
            val userPreference = UserPreference.getInstance(applicationContext.dataStore)
            val userSession = userPreference.getSession().first()
            val userId = userSession.userId

            // Ensure userId is valid
            if (userId != null) {
                val updatedRoutine = Routine(
                    routineId = routineId,
                    title = title,
                    date = date,
                    time = time,
                    location = location,
                    detail = detail,
                    category = category,
                    userId = userId  // Pass userId correctly here
                )

                // Proceed to update the routine via API call
                ApiConfig.getApiService().updateRoutine(routineId, updatedRoutine)
                    .enqueue(object : Callback<Routine> {
                        override fun onResponse(call: Call<Routine>, response: Response<Routine>) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@EditRoutineActivity,
                                    "Routine updated successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                setResult(RESULT_OK)  // Set the result as OK
                                finish()  // Close the activity and return to the previous one
                            } else {
                                Toast.makeText(
                                    this@EditRoutineActivity,
                                    "Failed to update routine",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<Routine>, t: Throwable) {
                            Toast.makeText(
                                this@EditRoutineActivity,
                                "Error: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            } else {
                Toast.makeText(
                    this@EditRoutineActivity,
                    "User session error: userId is null",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
