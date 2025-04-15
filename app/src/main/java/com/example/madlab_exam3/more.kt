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
        val profileimage = findViewById<ImageView>(R.id.userProfileImage)

        category.setOnClickListener {
            val intent = Intent(this@more, ExpenseHistoryActivity::class.java)
            startActivity(intent)
        }
        category.setOnClickListener {
            val intent = Intent(this@more, Add_Expense::class.java)
            startActivity(intent)
        }
        profile.setOnClickListener {
            val intent = Intent(this@more, profile::class.java)
            startActivity(intent)
        }
        profileimage.setOnClickListener {
            val intent = Intent(this@more, profile::class.java)
            startActivity(intent)
        }




        // Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.more

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this@more, Home::class.java))  // Assuming Home is your "More" page
                    finish()// Handle Wallets page, e.g., navigate to Wallets activity/fragment
                    true
                }
                R.id.addExpense -> {
                    // Open Add Expense Activity
                    startActivity(Intent(this@more, Add_Expense::class.java))
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