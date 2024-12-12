package com.example.skegoapp.ui.main.add_task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skegoapp.data.remote.repository.TaskRepository

class AddTaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddTaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
