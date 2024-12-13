package com.example.skegoapp.ui.main.add_routine

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skegoapp.data.pref.Routine
import com.example.skegoapp.data.pref.UserPreference
import com.example.skegoapp.data.pref.dataStore
import com.example.skegoapp.data.remote.retrofit.ApiConfig
import com.example.skegoapp.databinding.ActivityAddRoutineBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddRoutineActivity : AppCompatActivity() {

    // Inisialisasi View Binding
    private lateinit var binding: ActivityAddRoutineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengaktifkan View Binding
        binding = ActivityAddRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup tombol kembali
        binding.btnBack.setOnClickListener {
            finish() // Menutup activity dan kembali ke activity sebelumnya
        }

        // Setup date picker untuk input tanggal
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        // Setup time picker untuk input waktu
        binding.etTime.setOnClickListener {
            showTimePicker()
        }

        // Setup kategori rutinitas menggunakan AutoCompleteTextView (sebelumnya Spinner)
        val categories = listOf("Work", "Personal", "Exercise", "Study", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.dropdownFrequency.setAdapter(adapter)

        // Setup tombol simpan
        binding.btnSave.setOnClickListener {
            saveRoutine()
        }
    }

    /**
     * Fungsi untuk menampilkan DatePicker dialog
     */
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

    /**
     * Fungsi untuk menampilkan TimePicker dialog
     */
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

    /**
     * Fungsi untuk menyimpan rutinitas
     */
    private fun saveRoutine() {
        val routineId = 0
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

        // Retrieve userId from UserPreference
        MainScope().launch {
            val userPreference = UserPreference.getInstance(applicationContext.dataStore)
            val userSession = userPreference.getSession().first()  // Get the user session data
            val userId = userSession.userId  // Extract userId

            // Create Routine object with the retrieved userId
            val routine = Routine(routineId, title, date, time, location, detail, category, userId)

            // Send routine to server via Retrofit
            ApiConfig.getApiService().addRoutine(routine).enqueue(object : Callback<Routine> {
                override fun onResponse(call: Call<Routine>, response: Response<Routine>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddRoutineActivity, "Routine added successfully", Toast.LENGTH_SHORT).show()

                        // Notify RoutineFragment to update the calendar
                        setResult(RESULT_OK)  // Optionally pass the data if needed
                        finish() // Kembali ke layar sebelumnya
                    } else {
                        Toast.makeText(this@AddRoutineActivity, "Failed to add routine", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Routine>, t: Throwable) {
                    Toast.makeText(this@AddRoutineActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
}
