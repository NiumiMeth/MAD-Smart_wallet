package com.example.madlab_exam3

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madlab_exam3.adapters.Transe_history_Adaptor
import com.example.madlab_exam3.models.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Transactionhistory : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_EDIT_TRANSACTION = 1
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: Transe_history_Adaptor
    private lateinit var categoryFilterSpinner: Spinner
    private lateinit var typeFilterSpinner: Spinner
    private lateinit var allTransactions: MutableList<Transaction> // Store all loaded transactions
    private lateinit var filteredTransactions: MutableList<Transaction> // List displayed in RecyclerView
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val transactionKey = "expense_transactions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactionhistory)

        // Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this@Transactionhistory, Home::class.java))
                    true
                }
                R.id.addExpense -> {
                    startActivity(Intent(this@Transactionhistory, Add_Expense::class.java))
                    true
                }
                R.id.more -> {
                    startActivity(Intent(this@Transactionhistory, more::class.java))
                    true
                }
                else -> false
            }
        }

        // Initialize views
        recyclerView = findViewById(R.id.recyclerTransactions)
        categoryFilterSpinner = findViewById(R.id.categoryFilterSpinner)
        typeFilterSpinner = findViewById(R.id.typeFilterSpinner)

        // Initialize transaction lists
        allTransactions = mutableListOf()
        filteredTransactions = mutableListOf()

        // Setup the recycler view
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = Transe_history_Adaptor(filteredTransactions, this)
        recyclerView.adapter = adapter

        sharedPreferences = getSharedPreferences("MyFinanceApp", Context.MODE_PRIVATE)

        // Load transactions from SharedPreferences
        loadTransactions()

        // Setup the spinners
        setupSpinners()

        // Check for a new transaction passed from Add_Expense
        intent?.getParcelableExtra<Transaction>("new_transaction")?.let { newTransaction ->
            allTransactions.add(newTransaction)
            saveTransactions(allTransactions)
            applyFilters() // Re-apply filters to include the new transaction
        }
    }

    private fun loadTransactions() {
        val json = sharedPreferences.getString(transactionKey, null)
        val type = object : TypeToken<List<Transaction>>() {}.type
        val loadedTransactions = gson.fromJson<List<Transaction>>(json, type) ?: emptyList()
        allTransactions.clear()
        allTransactions.addAll(loadedTransactions)
        applyFilters() // Apply initial filters after loading
    }

    private fun setupSpinners() {
        // Setup category spinner
        val categories = mutableListOf("All Categories")
        categories.addAll(allTransactions.map { it.category }.distinct())
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoryFilterSpinner.adapter = categoryAdapter

        // Setup type spinner
        val types = listOf("All Types", "Income", "Expense")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeFilterSpinner.adapter = typeAdapter

        // Set listeners to apply filters when the spinner selection changes
        categoryFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        typeFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun applyFilters() {
        val selectedCategory = categoryFilterSpinner.selectedItem?.toString() ?: "All Categories"
        val selectedType = typeFilterSpinner.selectedItem?.toString() ?: "All Types"

        filteredTransactions.clear()
        filteredTransactions.addAll(allTransactions.filter { transaction ->
            val categoryMatch = (selectedCategory == "All Categories" || transaction.category == selectedCategory)
            val typeMatch = (selectedType == "All Types" || transaction.type == selectedType)
            categoryMatch && typeMatch
        })

        adapter.notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    // Handle editing a transaction
    fun onEditTransaction(position: Int) {
        val transaction = filteredTransactions[position] // Use filtered list for editing
        val intent = Intent(this, EditTransactionActivity::class.java)
        intent.putExtra("transaction_to_edit", transaction)
        startActivityForResult(intent, REQUEST_CODE_EDIT_TRANSACTION)
    }

    // Handle deleting a transaction
    fun onDeleteTransaction(position: Int) {
        val transactionToDelete = filteredTransactions[position] // Use filtered list for deletion
        val indexInAll = allTransactions.indexOfFirst { it.id == transactionToDelete.id }

        if (indexInAll != -1) {
            allTransactions.removeAt(indexInAll)
            saveTransactions(allTransactions)
            applyFilters() // Re-apply filters after deletion
            Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
        }
    }

    // Save updated list of transactions to SharedPreferences
    private fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        sharedPreferences.edit().putString(transactionKey, json).apply()
    }

    // Handle result when editing a transaction
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_EDIT_TRANSACTION && resultCode == RESULT_OK) {
            val updatedTransaction = data?.getParcelableExtra<Transaction>("edited_transaction")
            updatedTransaction?.let {
                val indexInAll = allTransactions.indexOfFirst { transaction -> transaction.id == it.id }
                if (indexInAll != -1) {
                    allTransactions[indexInAll] = it
                    saveTransactions(allTransactions)
                    applyFilters() // Re-apply filters after updating
                    Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error updating transaction", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}