package com.example.skegoapp.ui.main.add_task

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.skegoapp.R
import com.example.skegoapp.data.pref.AddTaskRequest
import com.example.skegoapp.data.pref.UserPreference
import com.example.skegoapp.data.pref.dataStore
import com.example.skegoapp.data.remote.repository.TaskRepository
import com.example.skegoapp.data.remote.retrofit.ApiConfig
import com.example.skegoapp.ui.main.task.TaskViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.util.Calendar

class AddTaskActivity : AppCompatActivity() {

    private lateinit var userPreference: UserPreference
    private lateinit var addTaskViewModel: AddTaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Inisialisasi UserPreference
        userPreference = UserPreference.getInstance(dataStore)

        // Inisialisasi ViewModel dengan repository
        val apiService = ApiConfig.getApiService()
        val repository = TaskRepository(apiService)
        addTaskViewModel = ViewModelProvider(
            this,
            AddTaskViewModelFactory(repository)
        )[AddTaskViewModel::class.java]

        // Tombol Back
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }

        // Inisialisasi dropdown
        setupDropdowns()

        // Tombol Save
        findViewById<Button>(R.id.btn_save).setOnClickListener {
            saveTask()
        }

        // Input Due Date
        val dueDateEditText = findViewById<TextInputEditText>(R.id.et_due_date)
        dueDateEditText.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener { showDatePicker() }
        }
    }

    private fun setupDropdowns() {
        // Dropdown untuk Difficulty
        val difficultyArray = resources.getStringArray(R.array.difficulty_array)
        val difficultyAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, difficultyArray)
        findViewById<AutoCompleteTextView>(R.id.dropdown_difficulty).setAdapter(difficultyAdapter)

        // Dropdown untuk Category
        val categoryArray = resources.getStringArray(R.array.category_array)
        val categoryAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryArray)
        findViewById<AutoCompleteTextView>(R.id.dropdown_category).setAdapter(categoryAdapter)

        // Dropdown untuk Duration
        val durationArray = resources.getStringArray(R.array.duration_array)
        val durationAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, durationArray)
        findViewById<AutoCompleteTextView>(R.id.dropdown_duration).setAdapter(durationAdapter)
    }

    private fun saveTask() {
        val title = findViewById<TextInputEditText>(R.id.et_title_task).text.toString()
        val dueDate = findViewById<TextInputEditText>(R.id.et_due_date).text.toString()
        val difficulty =
            findViewById<AutoCompleteTextView>(R.id.dropdown_difficulty).text.toString()
        val category = findViewById<AutoCompleteTextView>(R.id.dropdown_category).text.toString()
        val duration = findViewById<AutoCompleteTextView>(R.id.dropdown_duration).text.toString()
        val detail = findViewById<TextInputEditText>(R.id.et_detail).text.toString()

        // Validasi input
        if (title.isEmpty() || dueDate.isEmpty() || difficulty.isEmpty() || category.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                val userId = user.userId
                if (userId != 0) {
                    val request = AddTaskRequest(
                        user_id = userId,
                        task_name = title,
                        difficulty_level = difficulty,
                        deadline = dueDate,
                        priority_score = null,
                        duration = duration,
                        status = "Not Started",
                        category = category,
                        detail = detail
                    )

                    // Panggil ViewModel untuk menambahkan task
                    addTaskViewModel.addTask(request)
                } else {
                    Toast.makeText(this@AddTaskActivity, "User not logged in!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        // Hapus yang ini dari AddTaskActivity
        addTaskViewModel.taskAdded.observe(this) { taskAdded ->
            if (taskAdded) {
                Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK) // Menyimpan hasil sukses
                finish() // Menutup AddTaskActivity
            } else {
                Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show()
            }
        }


        // Menangani error message jika ada
        addTaskViewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val dueDateEditText = findViewById<TextInputEditText>(R.id.et_due_date)
            dueDateEditText.setText(
                String.format(
                    "%02d/%02d/%d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear
                )
            )
        }, year, month, day).show()
    }


}