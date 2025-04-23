package com.example.madlab_exam3

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category) // Using the dedicated layout

        categorySpendingRecyclerView = findViewById(R.id.categorySpendingRecyclerView)
        categorySpendingRecyclerView.layoutManager = LinearLayoutManager(this)

        loadCategorySpendingData()
    }

    private fun loadCategorySpendingData() {
        val allExpenses = getAllExpenses()
        val expensesByCategory = allExpenses.groupBy { it.category }

        val categorySpendingItems = mutableListOf<CategoryItem>()

        for ((category, expenses) in expensesByCategory) {
            val totalSpent = expenses.sumOf { it.amount }
            categorySpendingItems.add(CategoryItem(category, totalSpent))
        }

        categorySpendingAdapter = CategorySpendingAdapter(categorySpendingItems)
        categorySpendingRecyclerView.adapter = categorySpendingAdapter
    }

    // Assuming your expense data is stored in SharedPreferences as a JSON string
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

// Assuming this is your Expense data model
data class ExpenseTransaction(val category: String, val amount: Double, /* other properties */)