package com.example.skegoapp.ui.main.add_task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skegoapp.data.pref.AddTaskRequest
import com.example.skegoapp.data.remote.repository.TaskRepository
import kotlinx.coroutines.launch

class AddTaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _taskAdded = MutableLiveData<Boolean>()
    val taskAdded: LiveData<Boolean> get() = _taskAdded

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun addTask(request: AddTaskRequest) {
        viewModelScope.launch {
            try {
                val response = repository.addTask(request)
                _taskAdded.value = true
                _errorMessage.value = response.message
            } catch (e: Exception) {
                _taskAdded.value = false
                _errorMessage.value = e.message
            }
        }
    }
}