package com.example.skegoapp.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skegoapp.R
import com.example.skegoapp.data.pref.Routine
import com.example.skegoapp.ui.main.detail_routine.DetailRoutineActivity
import com.example.skegoapp.data.remote.retrofit.ApiService
import com.example.skegoapp.ui.main.edit_routine.EditRoutineActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoutineAdapter(
    private val routineList: MutableList<Routine>,  // Changed to MutableList for removal
    private val apiService: ApiService            // Added ApiService for delete functionality
) : RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder>() {

    // Create and return the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoutineViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_routine, parent, false)
        return RoutineViewHolder(view)
    }

    override fun onBindViewHolder(holder: RoutineViewHolder, position: Int) {
        val routine = routineList[position]
        holder.routineTitle.text = routine.title
        holder.routineTime.text = routine.time
        holder.routineLocation.text = routine.location
        holder.routineDetail.text = routine.detail

        // Set click listener for the routine card to navigate to DetailRoutineActivity
        holder.itemView.setOnClickListener {
            val context = it.context
            val intent = Intent(context, DetailRoutineActivity::class.java)
            Log.d("RoutineAdapter", "Routine: ${routine.title}")  // Display log
            intent.putExtra("ROUTINE", routine)
            context.startActivity(intent)
        }

        // Set click listener for the edit button
        holder.btnEdit.setOnClickListener { v ->
            // Handle Edit action
            Log.d("RoutineAdapter", "Edit: ${routine.title}")

            // Pass data to EditRoutineActivity
            val context = v.context
            val intent = Intent(context, EditRoutineActivity::class.java).apply {
                putExtra("ROUTINE_ID", routine.routineId)
                putExtra("ROUTINE_TITLE", routine.title)
                putExtra("ROUTINE_DATE", routine.date)
                putExtra("ROUTINE_TIME", routine.time)
                putExtra("ROUTINE_LOCATION", routine.location)
                putExtra("ROUTINE_DETAIL", routine.detail)
                putExtra("ROUTINE_CATEGORY", routine.category)
            }
            context.startActivity(intent)
        }

        // Set click listener for the delete button
        holder.btnDelete.setOnClickListener { v ->
            // Handle Delete action
            Log.d("RoutineAdapter", "Delete: ${routine.title}")
            showDeleteConfirmationDialog(routine, position, v.context)  // Show confirmation dialog
        }
    }

    // Return the total count of items
    override fun getItemCount(): Int {
        return routineList.size
    }

    // ViewHolder class for the Routine item
    inner class RoutineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val routineTitle: TextView = view.findViewById(R.id.routine_title)
        val routineTime: TextView = view.findViewById(R.id.routine_time)
        val routineLocation: TextView = view.findViewById(R.id.routine_location)
        val routineDetail: TextView = view.findViewById(R.id.routine_description)
        val btnEdit: ImageView = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageView = view.findViewById(R.id.btn_delete)
    }

    // Function to show confirmation dialog for deletion
    private fun showDeleteConfirmationDialog(routine: Routine, position: Int, context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete this routine?")
            .setPositiveButton("Yes") { dialog, which ->
                deleteRoutine(routine, position)  // Call delete function if confirmed
            }
            .setNegativeButton("No", null)  // Null will dismiss the dialog on click
            .show()
    }

    private fun deleteRoutine(routine: Routine, position: Int) {
        // First, notify the user about the deletion (but don't modify the list yet)
        apiService.deleteRoutine(routine.routineId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // API success: Remove routine from the list and notify the adapter
                    Log.d("RoutineAdapter", "Routine deleted: ${routine.title}")

                    // Ensure that you remove the item from the list and update the adapter correctly
                    routineList.removeAt(position)
                    notifyItemRemoved(position)

                    // Optionally, notify the adapter of any subsequent item changes (if necessary)
                    notifyItemRangeChanged(position, routineList.size - position)
                } else {
                    // API failed: Log the error and do not modify the list
                    Log.e("RoutineAdapter", "Error deleting routine: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // In case of network failure, log the error and do not modify the list
                Log.e("RoutineAdapter", "Error: ${t.message}")
            }
        })
    }
}