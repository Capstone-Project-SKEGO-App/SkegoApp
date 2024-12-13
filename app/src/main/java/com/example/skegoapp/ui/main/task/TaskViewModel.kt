package com.example.skegoapp.ui.main.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skegoapp.data.pref.Task
import com.example.skegoapp.data.pref.UpdateTaskRequest
import com.example.skegoapp.data.remote.repository.TaskRepository
import com.example.skegoapp.ui.toTask
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    private val _priorityUpdateResult = MutableLiveData<Boolean>()
    val priorityUpdateResult: LiveData<Boolean> = _priorityUpdateResult

    private val _sortedTasks = MutableLiveData<List<Task>>()
    val sortedTasks: LiveData<List<Task>> = _sortedTasks

    private var currentUserId: Int = 0

    fun fetchTasks(userId: Int) {
        currentUserId = userId
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getTasks(userId)
                _tasks.value = response.map { responseItem ->
                    Task(
                        id = responseItem.userId,
                        title = responseItem.taskName,
                        deadline = responseItem.deadline,
                        difficulty = responseItem.difficultyLevel,
                        priority = responseItem.priorityScore,
                        category = responseItem.category,
                        status = responseItem.status,
                        detail = responseItem.detail,
                        duration = responseItem.duration,
                        fullDeadline = responseItem.deadline,
                        taskId = responseItem.idTask
                    )
                }
                _error.value = null
            } catch (e: Exception) {
                _error.value = "Failed to load tasks: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.deleteTask(taskId)
                _error.value = response.message
                fetchTasks(currentUserId)
            } catch (e: Exception) {
                _error.value = "Failed to delete task: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateTask(taskId: Int, request: UpdateTaskRequest) {
        viewModelScope.launch {
            _loading.value = true
            try {
                repository.updateTask(taskId, request)
                _updateSuccess.value = true
                _error.value = null
                // Reload tasks agar perubahan terlihat
                fetchTasks(currentUserId)
            } catch (e: Exception) {
                _updateSuccess.value = false
                _error.value = "Failed to update task: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun updatePriorityScores(userId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.postGenerate(userId)
                _priorityUpdateResult.postValue(response.message == "Priority scores updated successfully")
                if (response.message == "Priority scores updated successfully") {
                    fetchSortedTasks(userId)
                }
            } catch (e: Exception) {
                _priorityUpdateResult.postValue(false)
            } finally {
                _loading.value = false
            }
        }
    }

    private fun fetchSortedTasks(userId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val tasksResponse = repository.getSortedTasks(userId)
                val mappedTasks = tasksResponse.tasks.map { it.toTask() }

                _sortedTasks.postValue(mappedTasks)
            } catch (e: Exception) {
                _sortedTasks.postValue(emptyList())
            } finally {
                _loading.value = false
            }
        }
    }
}