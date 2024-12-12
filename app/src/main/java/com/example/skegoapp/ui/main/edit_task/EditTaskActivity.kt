package com.example.skegoapp.ui.main.edit_task

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.skegoapp.R
import com.example.skegoapp.data.pref.Task
import com.example.skegoapp.data.pref.UpdateTaskRequest
import com.example.skegoapp.data.pref.UserPreference
import com.example.skegoapp.data.pref.dataStore
import com.example.skegoapp.data.remote.repository.TaskRepository
import com.example.skegoapp.data.remote.retrofit.ApiConfig
import com.example.skegoapp.ui.main.task.TaskViewModel
import com.example.skegoapp.ui.main.task.TaskViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.util.Calendar
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class EditTaskActivity : AppCompatActivity() {

    private lateinit var userPreference: UserPreference
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var task: Task // Data task yang akan di-update

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        task = intent.getParcelableExtra("TASK")!!

        // Inisialisasi UserPreference dan ViewModel
        userPreference = UserPreference.getInstance(dataStore)
        val apiService = ApiConfig.getApiService()
        val repository = TaskRepository(apiService)
        taskViewModel = ViewModelProvider(
            this,
            TaskViewModelFactory(repository)
        )[TaskViewModel::class.java]

        setupDropdowns()

        // Isi field dengan data task
        findViewById<TextInputEditText>(R.id.et_title_task).setText(task.title)
        findViewById<TextInputEditText>(R.id.et_due_date).setText(task.deadline)
        // Isi nilai awal dropdown dengan data dari task
        findViewById<AutoCompleteTextView>(R.id.dropdown_difficulty).setText(task.difficulty, false)
        findViewById<AutoCompleteTextView>(R.id.dropdown_category).setText(task.category, false)
        findViewById<AutoCompleteTextView>(R.id.dropdown_duration).setText(task.duration, false)

        findViewById<TextInputEditText>(R.id.et_detail).setText(task.detail)

        findViewById<Button>(R.id.btn_save).setOnClickListener {
            updateTask()
        }

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
        val difficultyDropdown = findViewById<AutoCompleteTextView>(R.id.dropdown_difficulty)
        difficultyDropdown.setAdapter(difficultyAdapter)
        difficultyDropdown.threshold = 0 // Tampilkan semua opsi langsung

        // Dropdown untuk Category
        val categoryArray = resources.getStringArray(R.array.category_array)
        val categoryAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryArray)
        val categoryDropdown = findViewById<AutoCompleteTextView>(R.id.dropdown_category)
        categoryDropdown.setAdapter(categoryAdapter)
        categoryDropdown.threshold = 0

        // Dropdown untuk Duration
        val durationArray = resources.getStringArray(R.array.duration_array)
        val durationAdapter =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, durationArray)
        val durationDropdown = findViewById<AutoCompleteTextView>(R.id.dropdown_duration)
        durationDropdown.setAdapter(durationAdapter)
        durationDropdown.threshold = 0
    }


    private fun updateTask() {
        val title = findViewById<TextInputEditText>(R.id.et_title_task).text.toString()
        val dueDate = findViewById<TextInputEditText>(R.id.et_due_date).text.toString()
        val difficulty =
            findViewById<AutoCompleteTextView>(R.id.dropdown_difficulty).text.toString()
        val category = findViewById<AutoCompleteTextView>(R.id.dropdown_category).text.toString()
        val duration = findViewById<AutoCompleteTextView>(R.id.dropdown_duration).text.toString()
        val detail = findViewById<TextInputEditText>(R.id.et_detail).text.toString()

        if (title.isEmpty() || dueDate.isEmpty() || difficulty.isEmpty() || category.isEmpty() || duration.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                val taskId = task.taskId
                val request = UpdateTaskRequest(
                    user_id = user.userId,
                    task_name = title,
                    difficulty_level = difficulty,
                    deadline = dueDate,
                    priority_score = task.priority,
                    duration = duration,
                    status = task.status,
                    category = category,
                    detail = detail
                )

                taskViewModel.updateTask(taskId, request)
            }
        }

        taskViewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show()
            }
        }

        taskViewModel.error.observe(this) { errorMessage: String? ->
            errorMessage?.let { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
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

    fun formatDueDate(dateString: String, formatType: String): String {
        return try {
            // Parse tanggal dari format backend
            val zonedDateTime = ZonedDateTime.parse(dateString)

            // Format ke format yang diinginkan
            val outputFormat = when (formatType) {
                "itemTask" -> DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault()) // Contoh: 26 Nov
                "detailTask" -> DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault()) // Contoh: 26 November 2024
                else -> DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()) // Default format
            }
            zonedDateTime.format(outputFormat)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

}
