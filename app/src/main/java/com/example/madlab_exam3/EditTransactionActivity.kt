package com.example.madlab_exam3

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.madlab_exam3.models.Transaction
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class EditTransactionActivity : AppCompatActivity() {

    private lateinit var toolbarTitle: TextView
    private lateinit var transactionTypeRadioGroup: RadioGroup
    private lateinit var incomeRadioButton: RadioButton
    private lateinit var expenseRadioButton: RadioButton
    private lateinit var titleInputLayout: TextInputLayout
    private lateinit var transactionTitle: TextInputEditText
    private lateinit var amountInputLayout: TextInputLayout
    private lateinit var transactionAmount: TextInputEditText
    private lateinit var categoryLabel: TextView
    private lateinit var categorySpinner: Spinner
    private lateinit var selectedDateTextView: TextView
    private lateinit var buttonDatePicker: Button
    private lateinit var saveButton: MaterialButton
    private lateinit var cancelButton: MaterialButton
    private lateinit var bottomNav: com.google.android.material.bottomnavigation.BottomNavigationView

    private var transactionToEdit: Transaction? = null
    private lateinit var categories: List<String>
    private var selectedDateString: String = "" // To store the date as a String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction) // Using the dedicated edit layout

        // Initialize views using the IDs from activity_edit_transaction.xml
        toolbarTitle = findViewById(R.id.toolbarTitle)
        transactionTypeRadioGroup = findViewById(R.id.transactionTypeRadioGroup)
        incomeRadioButton = findViewById(R.id.incomeRadioButton)
        expenseRadioButton = findViewById(R.id.expenseRadioButton)
        titleInputLayout = findViewById(R.id.titleInputLayout)
        transactionTitle = findViewById(R.id.transactionTitle)
        amountInputLayout = findViewById(R.id.amountInputLayout)
        transactionAmount = findViewById(R.id.transactionAmount)
        categoryLabel = findViewById(R.id.categoryLabel)
        categorySpinner = findViewById(R.id.categorySpinner)
        selectedDateTextView = findViewById(R.id.selectedDateTextView)
        buttonDatePicker = findViewById(R.id.buttonDatePicker)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        bottomNav = findViewById(R.id.bottomNav)

        // Set up Date Picker
        updateDateTextView()
        buttonDatePicker.setOnClickListener {
            showDatePickerDialog()
        }

        // Get the transaction to edit from the intent
        transactionToEdit = intent.getParcelableExtra("transaction_to_edit")

        // Load categories (you might need to fetch this dynamically)
        categories = listOf("Food", "Transport", "Bills", "Salary", "Other") // Example categories

        // Setup the category spinner
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        // Pre-fill the form with the transaction data
        transactionToEdit?.let { transaction ->
            toolbarTitle.text = "Edit Transaction" // Ensure title is set
            this.transactionTitle.setText(transaction.title)
            transactionAmount.setText(transaction.amount.toString())
            val categoryIndex = categories.indexOf(transaction.category)
            if (categoryIndex != -1) {
                categorySpinner.setSelection(categoryIndex)
            }
            if (transaction.type == "Income") {
                incomeRadioButton.isChecked = true
            } else if (transaction.type == "Expense") {
                expenseRadioButton.isChecked = true
            }
            selectedDateString = transaction.date // Set the existing date string
            updateDateTextView()
        }

        // Set listener for the save button
        saveButton.setOnClickListener {
            saveEditedTransaction()
        }

        // Set listener for the cancel button
        cancelButton.setOnClickListener {
            finish() // Go back to Transactionhistory
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, yearSelected, monthOfYear, dayOfMonth ->
                selectedDateString = "$yearSelected-${String.format("%02d", monthOfYear + 1)}-${String.format("%02d", dayOfMonth)}"
                updateDateTextView()
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun updateDateTextView() {
        selectedDateTextView.text = if (selectedDateString.isNotEmpty()) selectedDateString else "Pick Date"
    }

    private fun saveEditedTransaction() {
        val title = transactionTitle.text.toString().trim()
        val amountText = transactionAmount.text.toString()
        val selectedCategory = categorySpinner.selectedItem.toString()
        val selectedType = if (incomeRadioButton.isChecked) "Income" else "Expense"

        if (title.isNotEmpty() && amountText.isNotEmpty()) {
            val amount = amountText.toDouble()

            val editedTransaction = Transaction(
                id = transactionToEdit?.id ?: System.currentTimeMillis(), // Keep original ID
                title = title,
                amount = amount,
                category = selectedCategory,
                date = selectedDateString, // Pass the selected date string
                type = selectedType
            )

            val resultIntent = Intent()
            resultIntent.putExtra("edited_transaction", editedTransaction)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        } else {
            Toast.makeText(this, "Title and Amount are required", Toast.LENGTH_SHORT).show()
        }
    }
}