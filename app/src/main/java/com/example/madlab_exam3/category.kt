package com.example.madlab_exam3

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class category : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this@category, Home::class.java))
                    true
                }
                R.id.addExpense -> {
                    startActivity(Intent(this@category, Add_Expense::class.java))
                    true
                }
                R.id.more -> {
                    // Open the "More" page (this is likely the current page)
                    startActivity(Intent(this@category, more::class.java))  // Assuming Home is your "More" page
                    finish() // Optionally, finish the current activity to prevent returning back to it
                    true
                }
                else -> false
            }
        }

    }
    }