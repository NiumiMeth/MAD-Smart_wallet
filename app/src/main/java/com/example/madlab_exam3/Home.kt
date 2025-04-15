package com.example.madlab_exam3

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val pro = findViewById<ImageView>(R.id.profileIcon)
        val addExpense = findViewById<ImageView>(R.id.imageView3)
        val category = findViewById<TextView>(R.id.categoriesTitle)
        // Navigate to styling when the category text is clicked
        category.setOnClickListener {
            val intent = Intent(this@Home, ExpenseHistoryActivity::class.java)
            startActivity(intent)
        }

        addExpense.setOnClickListener {
            val intent = Intent(this@Home, set_budget::class.java)
            startActivity(intent)
        }
        pro.setOnClickListener {
            val intent = Intent(this@Home, profile::class.java)
            startActivity(intent)
        }




        // Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Handle Wallets page, e.g., navigate to Wallets activity/fragment
                    true
                }
                R.id.addExpense -> {
                    // Open Add Expense Activity
                    startActivity(Intent(this@Home, Add_Expense::class.java))
                    true
                }
                R.id.more -> {
                    // Open the "More" page (this is likely the current page)
                    startActivity(Intent(this@Home, more::class.java))  // Assuming Home is your "More" page
                    finish() // Optionally, finish the current activity to prevent returning back to it
                    true
                }
                else -> false
            }
        }

    }
    }
