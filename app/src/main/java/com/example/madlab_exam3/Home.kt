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

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                // Handle notification permission denial gracefully
                // Possibly disable some features or show a message explaining why permission is needed
            }
        }

    private val budgetWarningReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val warningMessage = intent.getStringExtra("warningMessage")
            if (!warningMessage.isNullOrEmpty()) {
                notificationTextView.text = warningMessage
                notificationTextView.visibility = View.VISIBLE
            } else {
                notificationTextView.visibility = View.GONE
            }
            loadBudgetInfo() // Update budget UI on home screen as well
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Views
        val profileIcon = findViewById<ImageView>(R.id.profileIcon)
        greetingText = findViewById(R.id.greetingText)
        notificationTextView = findViewById(R.id.notificationTextView)
        budgetProgressBar = findViewById(R.id.budgetProgressBar)
        budgetPercentageTextView = findViewById(R.id.budgetPercentageText)
        budgetTextView = findViewById(R.id.budgetText)
        addBudgetButtonImageView = findViewById(R.id.addBudgetButton)
        val categoriesTitleTextView = findViewById<TextView>(R.id.categoriesTitle)
        val totalLabelTextView = findViewById<TextView>(R.id.totalLabel)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyFinanceApp", Context.MODE_PRIVATE)

        // Request notification permission if targeting Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Load and display budget information
        loadBudgetInfo()

        // Register the BroadcastReceiver
        val filter = IntentFilter("BUDGET_WARNING")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(budgetWarningReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(budgetWarningReceiver, filter)
        }

        // Navigation Listeners
        categoriesTitleTextView.setOnClickListener {
            val intent = Intent(this@Home, ExpenseHistoryActivity::class.java)
            startActivity(intent)
        }

        addBudgetButtonImageView.setOnClickListener {
            val intent = Intent(this@Home, set_budget::class.java)
            startActivity(intent)
        }
        profileIcon.setOnClickListener {
            val intent = Intent(this@Home, profile::class.java)
            startActivity(intent)
        }
        totalLabelTextView.setOnClickListener {
            val intent = Intent(this@Home, Transactionhistory::class.java)
            startActivity(intent)
        }

        // Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true
                R.id.addExpense -> {
                    startActivity(Intent(this@Home, Add_Expense::class.java))
                    true
                }
                R.id.more -> {
                    startActivity(Intent(this@Home, more::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadBudgetInfo() {
        val budget = sharedPreferences.getFloat(budgetKey, 0f).toDouble()
        val totalSpent = calculateTotalSpent()
        val remaining = budget - totalSpent
        val progressPercent = if (budget > 0) ((totalSpent / budget) * 100).toInt().coerceIn(0, 100) else 0

        // Update Budget Progress Bar on Home Screen
        budgetProgressBar.max = 100
        budgetProgressBar.progress = progressPercent
        budgetPercentageTextView.text = String.format(Locale.getDefault(), "%d%%", progressPercent)
        budgetTextView.text = String.format(Locale.getDefault(), getString(R.string.budget_amount_home), budget)

        // Check for budget warning and trigger local notification
        if (progressPercent >= 80 && budget > 0) {
            val notificationTitle = getString(R.string.budget_warning_title)
            val notificationMessage = String.format(
                Locale.getDefault(),
                getString(R.string.budget_warning_details),
                budget,
                totalSpent,
                remaining
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
            .setSmallIcon(R.drawable.notification) // Replace with your notification icon
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
        unregisterReceiver(budgetWarningReceiver) // Unregister receiver (if you keep using it)
    }
}
