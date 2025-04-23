package com.example.madlab_exam3

import android.Manifest
import android.animation.ObjectAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.madlab_exam3.models.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class set_budget : AppCompatActivity() {

    private lateinit var budgetAmountInput: EditText
    private lateinit var setBudgetButton: Button
    private lateinit var progressPercentageText: TextView
    private lateinit var spentOfLimitText: TextView
    private lateinit var remainingBudgetText: TextView
    private lateinit var circularProgressBar: CircularProgressIndicator
    private lateinit var budgetProgressGroup: LinearLayout
    private lateinit var budgetWarningTextView: TextView
    private lateinit var mainCurrencyText: TextView // Corrected initialization
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val transactionKey = "expense_transactions"
    private val budgetKey = "monthly_budget"

    private var currentCurrency: String = "" // Holds the selected currency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_budget)

        // Initialize Views
        budgetAmountInput = findViewById(R.id.budgetAmountInput)
        setBudgetButton = findViewById(R.id.setBudgetButton)
        progressPercentageText = findViewById(R.id.progressPercentageText)
        spentOfLimitText = findViewById(R.id.spentOfLimitText)
        remainingBudgetText = findViewById(R.id.remainingBudgetText)
        circularProgressBar = findViewById(R.id.circular_progress_indicator)
        budgetProgressGroup = findViewById(R.id.budgetProgressGroup)
        budgetWarningTextView = findViewById(R.id.budgetWarningTextView)
        mainCurrencyText = findViewById(R.id.mainCurrencyText) // Initialize currency text view

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyFinanceApp", MODE_PRIVATE)

        // Retrieve the selected currency and budget from SharedPreferences
        currentCurrency = sharedPreferences.getString("currency", "LKR") ?: "LKR"
        loadBudgetAndUpdateUI()

        // Button Click Listener for Setting the Budget
        setBudgetButton.setOnClickListener {
            val input = budgetAmountInput.text.toString()
            if (input.isNotEmpty()) {
                val budget = input.toDoubleOrNull()
                if (budget != null && budget > 0) {
                    // Save the new budget
                    saveBudget(budget)

                    // Recalculate and update the progress UI with the newly set budget
                    updateProgressUI(budget, true) // Enable animation

                    // Show the progress group with animation
                    showProgressGroupWithAnimation()

                    // Check for budget warning and trigger notification
                    val totalSpent = calculateTotalSpent()
                    val remaining = budget - totalSpent
                    val progressPercent = ((totalSpent / budget) * 100).toInt().coerceIn(0, 100)
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
                    }
                } else {
                    Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Budget cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Show Currency Selector Dialog when currency text is clicked
        mainCurrencyText.setOnClickListener {
            showCurrencySelectorDialog()
        }

        // Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this@set_budget, Home::class.java))
                    finish()
                    true
                }
                R.id.addExpense -> {
                    startActivity(Intent(this@set_budget, Add_Expense::class.java))
                    true
                }
                R.id.more -> {
                    startActivity(Intent(this@set_budget, more::class.java))
                    true
                }
                else -> false
            }
        }
    }

    // Function to update the currency text displayed on the screen
    private fun updateCurrencyText() {
        val currencySymbol = when (currentCurrency) {
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            "LKR" -> "LKR"
            else -> "$"
        }
        mainCurrencyText.text = getString(R.string.currency_setting, currentCurrency, currencySymbol)
    }


    // Function to display the currency selection dialog
    private fun showCurrencySelectorDialog() {
        val currencies = arrayOf("LKR", "USD", "EUR", "GBP", "JPY") // Add more as needed
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.select_currency)
            .setSingleChoiceItems(currencies, currencies.indexOf(currentCurrency)) { dialog, which ->
                val selectedCurrency = currencies[which]
                if (selectedCurrency != currentCurrency) {
                    currentCurrency = selectedCurrency
                    sharedPreferences.edit().putString("currency", currentCurrency).apply()
                    updateCurrencyText() // Update the currency symbol text
                    Toast.makeText(this, getString(R.string.currency_changed, currentCurrency), Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
        builder.create().show()
    }

    // Function to apply animations to UI components
    private fun showProgressGroupWithAnimation() {
        budgetProgressGroup.apply {
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f
            visibility = View.VISIBLE
            animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(300).start()
        }
    }

    // Function to save the budget value to SharedPreferences
    private fun saveBudget(budget: Double) {
        val editor = sharedPreferences.edit()
        editor.putFloat(budgetKey, budget.toFloat()) // Save new budget
        editor.apply()
    }

    // Function to load budget and update the UI
    private fun loadBudgetAndUpdateUI() {
        val budget = sharedPreferences.getFloat(budgetKey, 0f).toDouble()
        if (budget > 0) {
            budgetAmountInput.setText(budget.toString())
            updateProgressUI(budget, false) // No animation on load
            budgetProgressGroup.visibility = View.VISIBLE
            budgetProgressGroup.alpha = 1f // Ensure it's visible without animation if budget exists
            budgetProgressGroup.scaleX = 1f
            budgetProgressGroup.scaleY = 1f
            updateCurrencyText() // Ensure currency symbol is updated on load
        } else {
            updateProgressUI(0.0, false)
            budgetProgressGroup.visibility = View.GONE
        }
    }

    private var currentProgressAnimator: ObjectAnimator? = null

    // Function to update progress UI
    private fun updateProgressUI(budget: Double, animate: Boolean) {
        val totalSpent = calculateTotalSpent()
        val progressPercent = if (budget > 0) ((totalSpent / budget) * 100).toInt().coerceIn(0, 100) else 0

        // Determine the relevant currency symbol based on currentCurrency
        val currencySymbol = when (currentCurrency) {
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            "LKR" -> "LKR"
            else -> "$" // Default to USD if currency is not found
        }

        spentOfLimitText.text = "$currencySymbol${"%.2f".format(totalSpent)} / $currencySymbol${"%.2f".format(budget)}"
        remainingBudgetText.text = "Remaining: $currencySymbol${"%.2f".format(budget - totalSpent)}"

        if (progressPercent >= 80) {
            budgetWarningTextView.text = "Warning: You have used 80% or more of your budget!"
            budgetWarningTextView.visibility = View.VISIBLE
            circularProgressBar.setIndicatorColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            progressPercentageText.setTextColor(ContextCompat.getColor(this, R.color.transportation_color))
        } else {
            budgetWarningTextView.text = ""
            budgetWarningTextView.visibility = View.GONE
            circularProgressBar.setIndicatorColor(ContextCompat.getColor(this, R.color.piechart))
            progressPercentageText.setTextColor(ContextCompat.getColor(this, R.color.transportation_color))
        }
// Update the progress bar
        progressPercentageText.text = "$progressPercent%"

        if (animate) {
            currentProgressAnimator?.cancel() // Cancel any ongoing animation
            currentProgressAnimator = ObjectAnimator.ofInt(circularProgressBar, "progress", circularProgressBar.progress, progressPercent)
            currentProgressAnimator?.duration = 500 // Animation duration
            currentProgressAnimator?.addUpdateListener { animation ->
                progressPercentageText.text = "${animation.animatedValue}%"
            }
            currentProgressAnimator?.start()
        } else {
            circularProgressBar.progress = progressPercent
            progressPercentageText.text = "$progressPercent%"
        }
    }

    // Function to display the budget warning notification
    private fun showBudgetWarningNotification(title: String, message: String) {
        val notificationBuilder = NotificationCompat.Builder(this, "budget_warning_channel")
            .setSmallIcon(R.drawable.notification) // Use your notification icon here
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("budget_warning_channel", "Budget Warnings", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build()) // Unique notification ID
    }

    // Helper function to calculate total expenses from stored transactions
    private fun calculateTotalSpent(): Double {
        val transactions = getStoredTransactions()
        return transactions.filter { it.type == "Expense" }.sumOf { it.amount }
    }

    private fun getStoredTransactions(): List<Transaction> {
        val json = sharedPreferences.getString(transactionKey, null)

        return if (json != null && json.isNotEmpty()) {
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
}
