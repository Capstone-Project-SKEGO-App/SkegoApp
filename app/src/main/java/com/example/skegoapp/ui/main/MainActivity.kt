package com.example.skegoapp.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.skegoapp.R
import com.example.skegoapp.ui.main.add_routine.AddRoutineActivity
import com.example.skegoapp.ui.main.add_task.AddTaskActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Cek apakah activity ini sudah dimulai
        Log.d("MainActivity", "MainActivity started successfully.")

        // Ambil reference dari BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.nav_view)

        // Ambil reference dari NavHostFragment
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main2) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up BottomNavigationView untuk bekerja dengan NavController
        bottomNavigationView.setupWithNavController(navController)

        // Ambil reference dari FAB
        val fab: FloatingActionButton = findViewById(R.id.fab)

        // Set listener untuk FAB
        fab.setOnClickListener {
            // Tampilkan BottomSheetDialog
            showBottomSheetDialog()
        }
    }

    @SuppressLint("InflateParams")
    private fun showBottomSheetDialog() {
        // Buat instance BottomSheetDialog
        val bottomSheetDialog = BottomSheetDialog(this)

        // Inflate layout untuk BottomSheetDialog
        val view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet, null)
        bottomSheetDialog.setContentView(view)

        // Atur tindakan klik untuk opsi di BottomSheet
        view.findViewById<View>(R.id.btn_add_task)?.setOnClickListener {
            // Navigasi ke AddTaskActivity
            startActivity(Intent(this, AddTaskActivity::class.java))
            bottomSheetDialog.dismiss() // Tutup BottomSheet
        }

        view.findViewById<View>(R.id.btn_add_routine)?.setOnClickListener {
            // Navigasi ke AddRoutineActivity
            startActivity(Intent(this, AddRoutineActivity::class.java))
            bottomSheetDialog.dismiss() // Tutup BottomSheet
        }

        // Tampilkan BottomSheetDialog
        bottomSheetDialog.show()
    }
}
