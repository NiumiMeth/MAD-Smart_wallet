package com.example.madlab_exam3

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.madlab_exam3.models.CategoryItem

class CategorySpending : AppCompatActivity() {

    private lateinit var categorySpendingRecyclerView: RecyclerView
    private lateinit var categorySpendingAdapter: CategorySpendingAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var currentCurrency: String = "LKR"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyFinanceApp", MODE_PRIVATE)
        currentCurrency = sharedPreferences.getString("currency", "LKR") ?: "LKR"

        // Initialize RecyclerView
        categorySpendingRecyclerView = findViewById(R.id.categorySpendingRecyclerView)
        categorySpendingRecyclerView.layoutManager = LinearLayoutManager(this)

        // Load initial data
        loadCategorySpendingData()

        // Register SharedPreferences listener for currency changes
        sharedPreferences.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == "currency") {
                currentCurrency = sharedPreferences.getString("currency", "LKR") ?: "LKR"
                loadCategorySpendingData()
            }
        }
    }

    override fun onDestroy() {
        // Unregister listener to prevent memory leaks
        sharedPreferences.unregisterOnSharedPreferenceChangeListener { _, _ -> }
        super.onDestroy()
    }

    private fun loadCategorySpendingData() {
        val allExpenses = getAllExpenses()
        val expensesByCategory = allExpenses.groupBy { it.category }

        val categorySpendingItems = mutableListOf<CategoryItem>()

        for ((category, expenses) in expensesByCategory) {
            val totalSpent = expenses.sumOf { it.amount }
            categorySpendingItems.add(CategoryItem(categoryName = category, totalSpent = totalSpent))
        }

        if (::categorySpendingAdapter.isInitialized) {
            // Update existing adapter
            categorySpendingAdapter.updateData(categorySpendingItems, currentCurrency)
        } else {
            // Initialize adapter
            categorySpendingAdapter = CategorySpendingAdapter(categorySpendingItems, currentCurrency)
            categorySpendingRecyclerView.adapter = categorySpendingAdapter
        }
    }

    private fun getAllExpenses(): List<ExpenseTransaction> {
        val sharedPreferences = getSharedPreferences("MyFinanceApp", MODE_PRIVATE)
        val gson = Gson()
        val transactionKey = "expense_transactions"
        val json = sharedPreferences.getString(transactionKey, null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<ExpenseTransaction>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
}

data class ExpenseTransaction(val category: String, val amount: Double, /* other properties */)