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

class more : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_more)

        val addtran = findViewById<TextView>(R.id.scheduledTransactionText)
        val category = findViewById<TextView>(R.id.categoriesText)
        val profile = findViewById<TextView>(R.id.userName)
        val budget = findViewById<TextView>(R.id.setBudget)
        val profileimage = findViewById<ImageView>(R.id.userProfileImage)

        // Only one click listener for each text view

        // Category Click Listener
        category.setOnClickListener {
            val intent = Intent(this@more, ExpenseHistoryActivity::class.java)
            startActivity(intent)
        }

        // Add Transaction Click Listener
        addtran.setOnClickListener {
            val intent = Intent(this@more, Add_Expense::class.java)
            startActivity(intent)
        }

        // Profile Click Listener
        profile.setOnClickListener {
            val intent = Intent(this@more, profile::class.java)
            startActivity(intent)
        }

        // Profile Image Click Listener
        profileimage.setOnClickListener {
            val intent = Intent(this@more, profile::class.java)
            startActivity(intent)
        }

        // Set Budget Click Listener
        budget.setOnClickListener {
            val intent = Intent(this@more, set_budget::class.java)  // Navigate to set_budget page
            startActivity(intent)
        }

        // Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.more

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this@more, Home::class.java))  // Assuming Home is your "More" page
                    finish() // Handle Wallets page, e.g., navigate to Wallets activity/fragment
                    true
                }
                R.id.addExpense -> {
                    startActivity(Intent(this@more, Add_Expense::class.java)) // Open Add Expense Activity
                    true
                }
                R.id.more -> {
                    // Open the "More" page (this is likely the current page)
                    // Optionally, finish the current activity to prevent returning back to it
                    true
                }
                else -> false
            }
        }

    }
}
