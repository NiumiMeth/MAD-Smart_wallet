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
    private lateinit var transactionList: MutableList<Transaction>
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val transactionKey = "expense_transactions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactionhistory)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerTransactions)
        categoryFilterSpinner = findViewById(R.id.categoryFilterSpinner)
        typeFilterSpinner = findViewById(R.id.typeFilterSpinner)

        // Initialize the transaction list
        transactionList = mutableListOf()

        // Setup the recycler view
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with an empty list initially
        adapter = Transe_history_Adaptor(transactionList, this)
        recyclerView.adapter = adapter

        sharedPreferences = getSharedPreferences("MyFinanceApp", Context.MODE_PRIVATE)

        // Initialize spinners and load data
        setupSpinners()
        loadTransactions()

        // Check if a new transaction was passed from Add_Expense
        if (intent != null && intent.hasExtra("new_transaction")) {
            val newTransaction = intent.getParcelableExtra<Transaction>("new_transaction")
            newTransaction?.let {
                transactionList.add(it)  // Add the new transaction to the list
                adapter.notifyItemInserted(transactionList.size - 1)  // Notify adapter to update the UI
            }
        }
    }

    private fun setupSpinners() {
        // Setup category spinner
        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("All Categories") + getCategories()
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categoryFilterSpinner.adapter = categoryAdapter

        // Setup type spinner
        val typeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            listOf("All Types", "Income", "Expense")
        )
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeFilterSpinner.adapter = typeAdapter

        // Set listeners to apply filters when the spinner selection changes
        categoryFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterTransactions()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        typeFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterTransactions()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun getCategories(): List<String> {
        return transactionList.map { it.category }.distinct()
    }

    private fun loadTransactions() {
        val json = sharedPreferences.getString(transactionKey, null)
        val type = object : TypeToken<List<Transaction>>() {}.type
        val loadedTransactions = gson.fromJson<List<Transaction>>(json, type) ?: emptyList()
        transactionList.clear()
        transactionList.addAll(loadedTransactions)
        filterTransactions()  // Apply any filters after loading data
    }

    private fun filterTransactions() {
        val selectedCategory = categoryFilterSpinner.selectedItem?.toString() ?: "All Categories"
        val selectedType = typeFilterSpinner.selectedItem?.toString() ?: "All Types"

        val filteredList = transactionList.filter { transaction ->
            val categoryMatch = (selectedCategory == "All Categories" || transaction.category == selectedCategory)
            val typeMatch = (selectedType == "All Types" || transaction.type == selectedType)
            categoryMatch && typeMatch
        }

        adapter.updateData(filteredList)  // Update the adapter with filtered data
    }

    // Handle editing a transaction (open EditTransactionActivity with the transaction data)
    fun onEditTransaction(position: Int) {
        val transaction = transactionList[position]
        val intent = Intent(this, EditTransactionActivity::class.java)
        intent.putExtra("transaction_to_edit", transaction)  // Pass the transaction to Edit
        startActivityForResult(intent, REQUEST_CODE_EDIT_TRANSACTION)  // Use the request code here
    }

    // Handle deleting a transaction
    fun onDeleteTransaction(position: Int) {
        val transactionToDelete = transactionList[position]

        // Remove the transaction from the list
        transactionList.removeAt(position)
        adapter.notifyItemRemoved(position)

        // Update the transactions in SharedPreferences
        saveTransactions(transactionList)

        // Display confirmation
        Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
    }

    // Save updated list of transactions to SharedPreferences
    private fun saveTransactions(Transactions: List<Transaction>) {
        val json = gson.toJson(Transactions)
        sharedPreferences.edit().putString(transactionKey, json).apply()
    }

    // Handle result when editing a transaction
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_EDIT_TRANSACTION && resultCode == RESULT_OK) {
            val updatedTransaction = data?.getParcelableExtra<Transaction>("edited_transaction") // Changed key here
            updatedTransaction?.let {
                // Find the index of the edited transaction and update it
                val position = transactionList.indexOfFirst { it.id == updatedTransaction.id } // Use the ID to find the correct item
                if (position != -1) {
                    transactionList[position] = it
                    adapter.notifyItemChanged(position)
                    // Save updated transactions in SharedPreferences
                    saveTransactions(transactionList)
                    Toast.makeText(this, "Transaction updated", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error updating transaction", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}