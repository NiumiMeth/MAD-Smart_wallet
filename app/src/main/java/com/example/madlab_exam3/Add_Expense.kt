package com.example.madlab_exam3

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.madlab_exam3.models.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Add_Expense : AppCompatActivity() {

    private lateinit var dateTextView: TextView
    private lateinit var datePickerButton: Button
    private lateinit var titleEditText: EditText
    private lateinit var amountEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var transactionTypeRadioGroup: RadioGroup // RadioGroup for selecting Income/Expense
    private lateinit var sharedPreferences: SharedPreferences

    private val gson = Gson()
    private val transactionKey = "expense_transactions"

    private var selectedYear = Calendar.getInstance(Locale.getDefault()).get(Calendar.YEAR)
    private var selectedMonth = Calendar.getInstance(Locale.getDefault()).get(Calendar.MONTH)
    private var selectedDay = Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_MONTH)

    private var totalSpent: Double = 0.0 // Track total expense amount

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        // Initialize views
        dateTextView = findViewById(R.id.selectedDateTextView)
        datePickerButton = findViewById(R.id.buttonDatePicker)
        titleEditText = findViewById(R.id.transactionTitle)
        amountEditText = findViewById(R.id.transactionAmount)
        categorySpinner = findViewById(R.id.categorySpinner)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        transactionTypeRadioGroup = findViewById(R.id.transactionTypeRadioGroup) // Initialize RadioGroup

        sharedPreferences = getSharedPreferences("MyFinanceApp", Context.MODE_PRIVATE)

        setupCategorySpinner()
        updateDateTextView()

        // Date Picker listener
        datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }

        // Save Transaction listener
        saveButton.setOnClickListener {
            saveExpenseTransaction()
        }

        // Cancel Transaction listener
        cancelButton.setOnClickListener {
            finish() // Close the activity when Cancel button is clicked
        }

        // Enable the back button in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.addExpense

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this@Add_Expense, Home::class.java))
                    true
                }
                R.id.addExpense -> true
                R.id.more -> {
                    startActivity(Intent(this@Add_Expense, more::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.expense_categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                selectedYear = year
                selectedMonth = monthOfYear
                selectedDay = dayOfMonth
                updateDateTextView()
            },
            selectedYear,
            selectedMonth,
            selectedDay
        )
        datePickerDialog.show()
    }

    private fun updateDateTextView() {
        val formattedDate = String.format(
            Locale.getDefault(),
            "%02d/%02d/%04d", // Format the date as dd/MM/yyyy
            selectedDay,
            selectedMonth + 1,
            selectedYear
        )
        dateTextView.text = formattedDate
    }

    private fun saveExpenseTransaction() {
        val title = titleEditText.text.toString().trim()
        val amountStr = amountEditText.text.toString().trim()
        val category = categorySpinner.selectedItem.toString()
        val date = dateTextView.text.toString()

        // Get selected transaction type from RadioGroup (Income or Expense)
        val selectedTransactionType = findViewById<RadioButton>(transactionTypeRadioGroup.checkedRadioButtonId).text.toString()

        if (title.isEmpty() || amountStr.isEmpty() || category.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill all fields with valid data", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val amount = amountStr.toDouble()
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show()
                return
            }

            // Create a new transaction with the selected type (Expense or Income)
            val newTransaction = Transaction(
                title = title,
                amount = amount,
                category = category,
                date = date,
                type = selectedTransactionType // Set the transaction type (Expense or Income)
            )

            // Update totalSpent only if the transaction type is Expense
            if (selectedTransactionType == "Expense") {
                totalSpent += amount
                saveBudget() // Save the updated totalSpent to SharedPreferences
            }

            // Save the new transaction
            saveNewTransaction(newTransaction)

            // Show a toast and go back
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()

            // Send the new transaction back to TransactionHistory
            val intent = Intent()
            intent.putExtra("new_transaction", newTransaction)
            setResult(RESULT_OK, intent)

            // Finish the activity to avoid double saves
            finish()

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNewTransaction(newTransaction: Transaction) {
        val existingTransactions = loadTransactions()
        existingTransactions.add(newTransaction)
        saveTransactions(existingTransactions)
    }

    private fun loadTransactions(): MutableList<Transaction> {
        val json = sharedPreferences.getString(transactionKey, null)
        val type = object : TypeToken<List<Transaction>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    private fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        sharedPreferences.edit().putString(transactionKey, json).apply()
    }

    private fun saveBudget() {
        val editor = sharedPreferences.edit()
        editor.putString("totalSpent", totalSpent.toString()) // Save the updated totalSpent
        editor.apply()
    }
}
