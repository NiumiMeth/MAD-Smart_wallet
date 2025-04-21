package com.example.madlab_exam3

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.madlab_exam3.models.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.NumberFormat
import java.util.Locale

class Home : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val transactionKey = "expense_transactions"
    private val budgetKey = "monthly_budget"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyFinanceApp", Context.MODE_PRIVATE)

        // Initialize views
        val pro = findViewById<ImageView>(R.id.profileIcon)
        val addBudgetButton = findViewById<ImageView>(R.id.addBudgetButton)
        val category = findViewById<TextView>(R.id.categoriesTitle)
        val history = findViewById<TextView>(R.id.budgetLabel)
        val budgetProgressBar = findViewById<ProgressBar>(R.id.budgetProgressBar)
        val totalSpentText = findViewById<TextView>(R.id.totalSpentText)
        val remainingBudgetText = findViewById<TextView>(R.id.remainingBudgetText)

        // Load and display budget data
        updateBudgetProgress()

        // Set click listeners
        category.setOnClickListener {
            startActivity(Intent(this, ExpenseHistoryActivity::class.java))
        }

        addBudgetButton.setOnClickListener {
            startActivity(Intent(this, set_budget::class.java))
        }

        pro.setOnClickListener {
            startActivity(Intent(this, profile::class.java))
        }

        history.setOnClickListener {
            Log.d("Home", "Navigating to Transactionhistory")
            startActivity(Intent(this, Transactionhistory::class.java))
        }

        // Bottom Navigation Setup
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to this activity
        updateBudgetProgress()
    }

    private fun updateBudgetProgress() {
        val budgetProgressBar = findViewById<ProgressBar>(R.id.budgetProgressBar)
        val totalSpentText = findViewById<TextView>(R.id.totalSpentText)
        val remainingBudgetText = findViewById<TextView>(R.id.remainingBudgetText)

        // Get budget and transactions
        val budget = sharedPreferences.getFloat(budgetKey, 0f).toDouble()
        val transactions = getStoredTransactions()
        val expenses = transactions.filter { it.type == "Expense" }
        val totalSpent = expenses.sumOf { it.amount }

        // Format currency
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            maximumFractionDigits = 2
            currency = java.util.Currency.getInstance("LKR")
        }

        // Update UI
        if (budget > 0) {
            val progress = ((totalSpent / budget) * 100).toInt().coerceAtMost(100)
            budgetProgressBar.progress = progress
            totalSpentText.text = currencyFormat.format(totalSpent)
            remainingBudgetText.text = "Remaining: ${currencyFormat.format(budget - totalSpent)}"
        } else {
            budgetProgressBar.progress = 0
            totalSpentText.text = currencyFormat.format(totalSpent)
            remainingBudgetText.text = "Set budget to track progress"
        }
    }

    private fun getStoredTransactions(): List<Transaction> {
        val json = sharedPreferences.getString(transactionKey, null)
        val type = object : TypeToken<List<Transaction>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun setupBottomNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true
                R.id.addExpense -> {
                    startActivity(Intent(this, Add_Expense::class.java))
                    true
                }
                R.id.more -> {
                    startActivity(Intent(this, more::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}