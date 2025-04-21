package com.example.madlab_exam3

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.madlab_exam3.models.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class set_budget : AppCompatActivity() {

    private lateinit var budgetAmountInput: EditText
    private lateinit var setBudgetButton: Button
    private lateinit var progressPercentageText: TextView
    private lateinit var spentOfLimitText: TextView
    private lateinit var remainingBudgetText: TextView
    private lateinit var circularProgressBar: CircularProgressIndicator
    private lateinit var budgetProgressGroup: LinearLayout // Reference to the progress group
    private lateinit var budgetWarningTextView: TextView // Reference to the new warning TextView

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val transactionKey = "expense_transactions"
    private val budgetKey = "monthly_budget"

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
        budgetProgressGroup = findViewById(R.id.budgetProgressGroup) // Initialize the progress group
        budgetWarningTextView = findViewById(R.id.budgetWarningTextView) // Initialize the warning TextView

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyFinanceApp", Context.MODE_PRIVATE)

        // Load existing budget and update UI
        loadBudgetAndUpdateUI()

        // Initially hide the progress group (alpha is 0, scale is 0.8)

        // Button Click
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

                    Toast.makeText(this, "Budget set successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Budget cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

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

    private fun showProgressGroupWithAnimation() {
        budgetProgressGroup.apply {
            alpha = 0f
            scaleX = 0.8f
            scaleY = 0.8f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .start()
        }
    }

    private fun saveBudget(budget: Double) {
        val editor = sharedPreferences.edit()
        editor.putFloat(budgetKey, budget.toFloat()) // Save new budget
        editor.apply()
    }

    private fun loadBudgetAndUpdateUI() {
        val budget = sharedPreferences.getFloat(budgetKey, 0f).toDouble()
        if (budget > 0) {
            budgetAmountInput.setText(budget.toString())
            updateProgressUI(budget, false) // No animation on load
            budgetProgressGroup.visibility = View.VISIBLE
            budgetProgressGroup.alpha = 1f // Ensure it's visible without animation if budget exists
            budgetProgressGroup.scaleX = 1f
            budgetProgressGroup.scaleY = 1f
        } else {
            updateProgressUI(0.0, false)
            budgetProgressGroup.visibility = View.GONE
        }
    }

    private var currentProgressAnimator: ObjectAnimator? = null

    private fun updateProgressUI(budget: Double, animate: Boolean) {
        val totalSpent = calculateTotalSpent()
        val progressPercent = if (budget > 0) ((totalSpent / budget) * 100).toInt().coerceIn(0, 100) else 0

        spentOfLimitText.text = "$${"%.2f".format(totalSpent)} / $${"%.2f".format(budget)}"
        remainingBudgetText.text = "Remaining: $${"%.2f".format(budget - totalSpent)}"

        if (progressPercent >= 80) {
            budgetWarningTextView.text = "Warning: You have used 80% or more of your budget!"
            budgetWarningTextView.visibility = View.VISIBLE
            // You can also change the color of the progress bar or text here if you want
            circularProgressBar.setIndicatorColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            progressPercentageText.setTextColor(ContextCompat.getColor(this, R.color.transportation_color))
            spentOfLimitText.setTextColor(ContextCompat.getColor(this, R.color.expencessection))
            remainingBudgetText.setTextColor(ContextCompat.getColor(this, R.color.expencessection))
        } else {
            budgetWarningTextView.text = ""
            budgetWarningTextView.visibility = View.GONE
            // Reset the color if the usage is below 80%
            circularProgressBar.setIndicatorColor(ContextCompat.getColor(this, R.color.piechart))
            progressPercentageText.setTextColor(ContextCompat.getColor(this, R.color.transportation_color))
            spentOfLimitText.setTextColor(ContextCompat.getColor(this, R.color.expencessection))
            remainingBudgetText.setTextColor(ContextCompat.getColor(this, R.color.expencessection))
        }

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