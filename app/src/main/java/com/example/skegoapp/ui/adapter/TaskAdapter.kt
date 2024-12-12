package com.example.skegoapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skegoapp.R
import com.example.skegoapp.data.pref.Task

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TaskAdapter(
    private var tasks: List<Task>,
    private val itemClickListener: (Task) -> Unit,
    private val deleteClickListener: (Task) -> Unit,
    private val editClickListener: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.task_title)
        val dueDate: TextView = itemView.findViewById(R.id.due_date)
        val difficulty: TextView = itemView.findViewById(R.id.task_difficulty)
        val taskType: TextView = itemView.findViewById(R.id.task_category)
        val deleteButton: ImageButton = itemView.findViewById(R.id.icon_delete)
        val editButton: ImageButton = itemView.findViewById(R.id.icon_edit)// Icon delete

        init {
            itemView.setOnClickListener {
                itemClickListener(tasks[adapterPosition])
            }
            deleteButton.setOnClickListener {
                deleteClickListener(tasks[adapterPosition]) // Trigger delete callback
            }

            editButton.setOnClickListener {
                editClickListener(tasks[adapterPosition]) // Trigger edit callback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]

        holder.taskTitle.text = currentTask.title
        holder.difficulty.text = currentTask.difficulty
        holder.taskType.text = currentTask.category
        holder.dueDate.text = "Due ${formatDueDate(currentTask.deadline, "itemTask")}"

        // Set background color based on priority
        holder.difficulty.setBackgroundColor(
            holder.itemView.context.getColor(
                when (currentTask.difficulty) {
                    "HIGH" -> R.color.red
                    "MEDIUM" -> R.color.yellow
                    "LOW" -> R.color.green
                    else -> R.color.gray
                }
            )
        )

        holder.taskType.setBackgroundColor(
            holder.itemView.context.getColor(
                when (currentTask.category) {
                    "WORK" -> R.color.blue
                    "SCHOOL" -> R.color.orange
                    "PERSONAL" -> R.color.purple
                    else -> R.color.white
                }
            )
        )
    }

    override fun getItemCount(): Int = tasks.size

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

