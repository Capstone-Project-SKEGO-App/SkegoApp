package com.example.skegoapp.ui.main.routine

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.skegoapp.databinding.FragmentRoutineBinding
import com.example.skegoapp.ui.adapter.RoutineAdapter
import com.example.skegoapp.ui.main.add_routine.AddRoutineActivity
import com.example.skegoapp.data.remote.retrofit.ApiConfig
import com.example.skegoapp.data.pref.Routine
import com.example.skegoapp.data.pref.UserPreference
import com.example.skegoapp.data.pref.dataStore
import com.example.skegoapp.data.remote.retrofit.ApiService
import com.example.skegoapp.ui.adapter.CalendarAdapter
import com.example.skegoapp.ui.main.home.ProfileActivity
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RoutineFragment : Fragment() {

    private var _binding: FragmentRoutineBinding? = null
    private val binding get() = _binding!!

    private lateinit var monthCalendarAdapter: CalendarAdapter
    private lateinit var calendar: Calendar
    private lateinit var routineAdapter: RoutineAdapter
    private var routines: List<Routine> = emptyList()
    private lateinit var userPreference: UserPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutineBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize user preference to get user data
        userPreference = UserPreference.getInstance(requireContext().dataStore)

        // Initialize the calendar instance
        calendar = Calendar.getInstance()

        // Setup calendar components and month navigation buttons
        setupMonthCalendar()
        setupMonthNavigation()
        setupCalendarButton()
        setupAddRoutineButton()
        setupMenuButton()

        // Check if user is logged in and load routines if logged in
        checkUserSession()

        return root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the result is OK
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ADD_ROUTINE -> {
                    // Refresh routines when a new routine is added
                    checkUserSession()  // Re-fetch routines from the API or your data source
                }
                REQUEST_CODE_EDIT_ROUTINE -> {
                    // Reload the data if the routine was edited
                    checkUserSession()

                }
                else -> {
                    // Handle other request codes if necessary
                }
            }
        }
    }


    /**
     * Checks if the user is logged in and loads routines accordingly.
     */
    private fun checkUserSession() {
        lifecycleScope.launch {
            userPreference.getSession().collect { user ->
                if (!user.isLogin) {
                    Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                } else {
                    // Load routines if the user is logged in
                    loadRoutines(user.userId)
                }
            }
        }
    }

    /**
     * Sets up the horizontal calendar for the current month.
     */
    private fun setupMonthCalendar() {
        updateCalendarDays() // Update the list of days in the current month
        highlightToday() // Highlight today's date
    }

    /**
     * Updates the calendar days and connects it with the calendar adapter.
     */
    private fun updateCalendarDays() {
        val daysInMonth = getDatesInMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH))

        // Initialize the CalendarAdapter with click listener for date selection
        monthCalendarAdapter = CalendarAdapter(daysInMonth, routines) { selectedDate ->
            // Display selected date in the UI
            binding.currentDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(selectedDate)

            // Filter routines based on the selected date
            val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
            lifecycleScope.launch {
                userPreference.getSession().collect { user ->
                    if (user.isLogin) {
                        loadRoutines(user.userId, formattedDate) // Call API to filter routines
                    }
                }
            }
        }

        // Display the calendar in a horizontal layout
        binding.horizontalCalendar.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.horizontalCalendar.adapter = monthCalendarAdapter
    }

    /**
     * Sets up the buttons for month navigation (previous/next).
     */
    private fun setupMonthNavigation() {
        binding.prevMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, -1) // Navigate to previous month
            updateCalendarDays()
        }
        binding.nextMonth.setOnClickListener {
            calendar.add(Calendar.MONTH, 1) // Navigate to next month
            updateCalendarDays()
        }
    }

    /**
     * Highlights today's date in the calendar.
     */
    private fun highlightToday() {
        val today = Calendar.getInstance().time
        monthCalendarAdapter.highlightDate(today) // Highlight today's date
    }

    /**
     * Sets up the menu button that opens ProfileActivity.
     */
    private fun setupMenuButton() {
        binding.iconMenu.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Sets up the calendar button that opens a date picker.
     */
    private fun setupCalendarButton() {
        binding.iconCalendar.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(parentFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { selection ->
                val selectedDate = Date(selection)
                val formattedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
                binding.currentDate.text = formattedDate

                lifecycleScope.launch {
                    userPreference.getSession().collect { user ->
                        if (user.isLogin) {
                            loadRoutines(user.userId, formattedDate) // Filter by selected date
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns all dates in a particular month.
     */
    private fun getDatesInMonth(year: Int, month: Int): List<Date> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val dates = mutableListOf<Date>()
        val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (i in 1..maxDay) {
            dates.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dates
    }

    /**
     * Loads routines from the API for a particular user and date (optional).
     */
    private fun loadRoutines(userId: Int, date: String? = null) {
        // Show the progress bar while loading routines
        binding.loadingSpinner.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = if (date != null) {
                    ApiConfig.getApiService().getRoutinesByUserIdAndDate(userId, date)
                } else {
                    ApiConfig.getApiService().getRoutinesByUserId(userId)
                }

                if (response.isSuccessful) {
                    routines = response.body() ?: emptyList()
                    updateRoutineList(routines, ApiConfig.getApiService()) // Pass apiService here
                } else {
                    Toast.makeText(requireContext(), "Failed to load routines", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                // Hide the progress bar after the operation is completed
                binding.loadingSpinner.visibility = View.GONE
            }
        }
    }


    /**
     * Updates the list of routines in the RecyclerView.
     */
    private fun updateRoutineList(routines: List<Routine>, apiService: ApiService) {
        routineAdapter = RoutineAdapter(routines.toMutableList(), apiService)  // Pass only routineList and apiService
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = routineAdapter
    }

    /**
     * Sets up the add routine button to navigate to AddRoutineActivity.
     */
    private fun setupAddRoutineButton() {
        binding.addRoutine.setOnClickListener {
            // Intent to open the AddRoutineActivity
            val intent = Intent(requireContext(), AddRoutineActivity::class.java)
            startActivityForResult(intent, 1)
        }
    }

    companion object {
        const val REQUEST_CODE_ADD_ROUTINE = 1
        const val REQUEST_CODE_EDIT_ROUTINE = 2
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
