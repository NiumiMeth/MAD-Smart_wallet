package com.example.madlab_exam3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madlab_exam3.models.Expense
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class Add_Expense : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter
    private lateinit var expenseList: MutableList<Expense>
    private lateinit var transactionTypeRadioGroup: RadioGroup
    private lateinit var incomeRadioButton: RadioButton
    private lateinit var expenseRadioButton: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // Initialize views
        recyclerView = findViewById(R.id.transactionHistoryRecyclerView)
        transactionTypeRadioGroup = findViewById(R.id.transactionTypeRadioGroup)
        incomeRadioButton = findViewById(R.id.radioIncome)
        expenseRadioButton = findViewById(R.id.radioExpense)
        val expenseTitle = findViewById<TextInputEditText>(R.id.transactionTitle)
        val expenseAmount = findViewById<TextInputEditText>(R.id.transactionAmount)
        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val expenseDate = findViewById<TextInputEditText>(R.id.transactionDate)
        val saveExpenseButton = findViewById<Button>(R.id.saveTransactionButton)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        val backButton = findViewById<TextView>(R.id.backButton)

        // Initialize RecyclerView and adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the expense list (use shared data or database in a real app)
        expenseList = mutableListOf(
            Expense(R.drawable.ob1, "Food", 2000.00, "20/02/2025"),
            Expense(R.drawable.ob1, "Transport", 1500.00, "21/02/2025")
        )

        // Set up the adapter
        adapter = ExpenseAdapter(expenseList)
        recyclerView.adapter = adapter

        // Save Expense Button functionality
        saveExpenseButton.setOnClickListener {
            val title = expenseTitle.text.toString()
            val amount = expenseAmount.text.toString().toDoubleOrNull() ?: 0.0
            val category = categorySpinner.selectedItem.toString()
            val date = expenseDate.text.toString()
            val transactionType = if (incomeRadioButton.isChecked) "Income" else "Expense"

            // Create new expense object (you might want to include transactionType in your Expense model)
            val newExpense = Expense(R.drawable.ob1, category, amount, date)

            // Add new expense to the list and notify the adapter
            expenseList.add(newExpense)
            adapter.notifyDataSetChanged()  // Notify the adapter to refresh the RecyclerView

            // Clear the input fields after saving
            expenseTitle.text = null
            expenseAmount.text = null
            expenseDate.text = null
            expenseRadioButton.isChecked = true // Reset to default expense for next entry
        }

        // Set up Bottom Navigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this@Add_Expense, Home::class.java))
                    finish()
                    true
                }
                R.id.addExpense -> {
                    true
                }
                R.id.more -> {
                    startActivity(Intent(this@Add_Expense, more::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }

        // Toolbar functionality for back button
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}