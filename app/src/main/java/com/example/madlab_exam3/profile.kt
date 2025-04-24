package com.example.madlab_exam3

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)


        val more = findViewById<TextView>(R.id.more1)

        more.setOnClickListener {
            val intent = Intent(this@profile, more::class.java)
            startActivity(intent)
        }

        // Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this@profile, Home::class.java))  // Assuming Home is your "More" page
                    finish()// Handle Wallets page, e.g., navigate to Wallets activity/fragment
                    true
                }
                R.id.addExpense -> {
                    // Open Add Expense Activity
                    startActivity(Intent(this@profile, Add_Expense::class.java))
                    true
                }
                R.id.more -> {
                    startActivity(Intent(this@profile, more::class.java))
                    true
                }
                else -> false
            }
        }
    }
}