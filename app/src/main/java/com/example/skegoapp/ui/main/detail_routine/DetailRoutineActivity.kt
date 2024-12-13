package com.example.skegoapp.ui.main.detail_routine

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skegoapp.R
import com.example.skegoapp.data.pref.Routine

class DetailRoutineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_routine)

        // Retrieve the Routine object passed from the adapter
        val routine: Routine? = intent.getSerializableExtra("ROUTINE") as? Routine
        Log.d("DetailRoutineActivity", "Routine received: $routine")  // Menampilkan lo

        // Check if the routine object is not null
        if (routine != null) {
            // Display routine details
            findViewById<TextView>(R.id.routine_title).text = routine.title
            findViewById<TextView>(R.id.routine_detail_text_view).text = routine.time
            //findViewById<TextView>(R.id.detail_routine_location).text = routine.location
            findViewById<TextView>(R.id.routine_detail_text_view).text = routine.detail
            findViewById<TextView>(R.id.routine_category_text_view).text = routine.category
            findViewById<TextView>(R.id.routine_date_text_view).text = routine.date
        } else {
            Toast.makeText(this, "Error: Routine data not available", Toast.LENGTH_SHORT).show()
        }

    }
}
