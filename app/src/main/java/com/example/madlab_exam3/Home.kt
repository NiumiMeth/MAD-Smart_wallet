package com.example.madlab_exam3

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.madlab_exam3.models.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

class Home : AppCompatActivity() {

    private lateinit var greetingText: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val transactionKey = "expense_transactions"
    private val budgetKey = "monthly_budget"
    private lateinit var notificationTextView: TextView
    private lateinit var budgetProgressBar: ProgressBar
    private lateinit var budgetPercentageTextView: TextView
    private lateinit var budgetTextView: TextView
    private lateinit var addBudgetButtonImageView: ImageView

    private var currentCurrency: String = "LKR"  // Store current currency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this@Home, Home::class.java))
                    finish()
                    true
                }
                R.id.addExpense -> {
                    startActivity(Intent(this@Home, Add_Expense::class.java))
                    true
                }
                R.id.more -> {
                    startActivity(Intent(this@Home, more::class.java))
                    true
                }
                else -> false
            }
        }



    // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyFinanceApp", Context.MODE_PRIVATE)

        // Retrieve the currency from SharedPreferences
        currentCurrency = sharedPreferences.getString("currency", "LKR") ?: "LKR"

        // Initialize Views
        greetingText = findViewById(R.id.greetingText)
        notificationTextView = findViewById(R.id.notificationTextView)
        budgetProgressBar = findViewById(R.id.budgetProgressBar)
        budgetPercentageTextView = findViewById(R.id.budgetPercentageText)
        budgetTextView = findViewById(R.id.budgetText)
        addBudgetButtonImageView = findViewById(R.id.addBudgetButton)
        val categoriesTitleTextView = findViewById<TextView>(R.id.categoriesTitle)

        // Load and display budget information
        loadBudgetInfo()

        // Navigation Listeners
        categoriesTitleTextView.setOnClickListener {
            val intent = Intent(this@Home, ExpenseHistoryActivity::class.java)
            startActivity(intent)
        }

        addBudgetButtonImageView.setOnClickListener {
            val intent = Intent(this@Home, set_budget::class.java)
            startActivity(intent)
        }
    }

    private fun loadBudgetInfo() {
        val budget = sharedPreferences.getFloat(budgetKey, 0f).toDouble()
        val totalSpent = calculateTotalSpent()
        val remaining = budget - totalSpent
        val progressPercent = if (budget > 0) ((totalSpent / budget) * 100).toInt().coerceIn(0, 100) else 0

        // Get the relevant currency symbol based on currentCurrency
        val currencySymbol = when (currentCurrency) {
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            "LKR" -> "LKR"
            else -> "$" // Default to USD if currency is not found
        }

        // Update Budget Progress Bar on Home Screen
        budgetProgressBar.max = 100
        budgetProgressBar.progress = progressPercent
        budgetPercentageTextView.text = String.format(Locale.getDefault(), "%d%%", progressPercent)

        // Use currency symbol to display the budget and spent values
        budgetTextView.text = String.format(getString(R.string.budget_amount_home1), currencySymbol, "%.2f".format(budget))

        // Check for budget warning and trigger local notification
        if (progressPercent >= 80 && budget > 0) {
            val notificationTitle = getString(R.string.budget_warning_title)
            val notificationMessage = String.format(
                Locale.getDefault(),
                getString(R.string.budget_warning_details2),
                currencySymbol, "%.2f".format(budget),
                "%.2f".format(totalSpent),
                "%.2f".format(remaining)
            )
            showBudgetWarningNotification(notificationTitle, notificationMessage)
            notificationTextView.visibility = View.GONE // Optionally hide the in-app notification
        } else {
            notificationTextView.visibility = View.GONE
        }
    }


    private fun showBudgetWarningNotification(title: String, message: String) {
        val channelId = "budget_warning_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification) // Use your notification icon here
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Budget Warnings", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build()) // Unique notification ID
    }

    private fun calculateTotalSpent(): Double {
        val transactions = getStoredTransactions()
        return transactions.filter { it.type == "Expense" }.sumOf { it.amount }
    }

    private fun getStoredTransactions(): List<Transaction> {
        val json = sharedPreferences.getString(transactionKey, null)
        return if (!json.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<Transaction>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        unregisterReceiver(budgetWarningReceiver) // Unregister receiver if you keep using it
    }
}
