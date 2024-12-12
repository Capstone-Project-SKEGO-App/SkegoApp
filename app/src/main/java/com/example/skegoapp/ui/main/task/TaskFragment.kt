package com.example.skegoapp.ui.main.task

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skegoapp.data.pref.UserPreference
import com.example.skegoapp.data.pref.dataStore
import com.example.skegoapp.data.remote.repository.TaskRepository
import com.example.skegoapp.data.remote.retrofit.ApiConfig
import com.example.skegoapp.databinding.FragmentTaskBinding
import com.example.skegoapp.ui.adapter.TaskAdapter
import com.example.skegoapp.ui.main.add_task.AddTaskActivity
import com.example.skegoapp.ui.main.detail_task.DetailTaskActivity
import com.example.skegoapp.ui.main.edit_task.EditTaskActivity
import kotlinx.coroutines.launch

class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private val addTaskRequestCode = 1

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)

        // Inisialisasi ViewModel dengan TaskRepository
        val apiService = ApiConfig.getApiService()
        val repository = TaskRepository(apiService)
        taskViewModel = ViewModelProvider(
            this,
            TaskViewModelFactory(repository)
        )[TaskViewModel::class.java]

        // Inisialisasi UserPreference
        userPreference = UserPreference.getInstance(requireContext().dataStore)

        setupRecyclerView()
        observeViewModel()

        // Ambil userId dari sesi dan fetch tasks
        getUserIdAndFetchTasks()

        // Set listener untuk tombol icon_add
        binding.iconAdd.setOnClickListener {
            // Arahkan ke AddTaskActivity
            val intent = Intent(requireContext(), AddTaskActivity::class.java)
            startActivityForResult(intent, addTaskRequestCode)
        }

        taskViewModel.priorityUpdateResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Priority scores updated!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update priority scores", Toast.LENGTH_SHORT).show()
            }
        }

        taskViewModel.sortedTasks.observe(viewLifecycleOwner) { sortedTasks ->
            // Update UI dengan data tugas yang telah diurutkan
            taskAdapter.updateTasks(sortedTasks)
        }

        binding.btnGenerate.setOnClickListener {
            lifecycleScope.launch {
                userPreference.getSession().collect { user ->
                    taskViewModel.updatePriorityScores(user.userId)
                }
            }
        }


        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == addTaskRequestCode && resultCode == Activity.RESULT_OK) {
            // Setelah berhasil menambahkan task, muat ulang task
            getUserIdAndFetchTasks()  // Memanggil fetchTasks untuk memperbarui RecyclerView
        }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            emptyList(),
            itemClickListener = { task ->
                val intent = Intent(requireContext(), DetailTaskActivity::class.java)
                intent.putExtra("TASK", task)
                startActivity(intent)
            },
            deleteClickListener = { task ->
                taskViewModel.deleteTask(task.taskId)
            },
            editClickListener = { task ->
                val intent = Intent(requireContext(), EditTaskActivity::class.java)
                intent.putExtra("TASK", task)
                startActivityForResult(intent, addTaskRequestCode)
            },
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskAdapter
        }
    }


    private fun observeViewModel() {
        taskViewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            taskAdapter.updateTasks(tasks) // Perbarui daftar task di adapter
        }

        taskViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        taskViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
        taskViewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Task updated successfully!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack() // Kembali ke fragment sebelumnya
            }
        }

    }

    private fun getUserIdAndFetchTasks() {
        lifecycleScope.launch {
            userPreference.getSession().collect { userSession ->
                if (userSession.userId != 0) {
                    // Memanggil fetchTasks di TaskViewModel untuk mendapatkan data terbaru
                    taskViewModel.fetchTasks(userSession.userId)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "User ID not found. Please log in again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}