package com.example.madlab_exam3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madlab_exam3.models.Expense
import com.example.madlab_exam3.adapters.ExpenseAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView

class ExpenseHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExpenseAdapter
    private lateinit var expenseList: MutableList<Expense>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_history)

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.expenseRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Sample data
        expenseList = mutableListOf()
        expenseList.add(Expense(R.drawable.ob1, "Food", 2000.00, "20/02/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Transport", 1500.00, "21/02/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Bills", 2500.00, "22/02/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Entertainment", 1200.00, "23/02/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Shopping", 3000.00, "24/02/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Health", 800.00, "25/02/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Rent", 5000.00, "26/02/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Savings", 4000.00, "27/02/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Subscriptions", 600.00, "28/02/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Gift", 1500.00, "01/03/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Travel", 3500.00, "02/03/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Education", 2000.00, "03/03/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Automobile", 2500.00, "04/03/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Home", 1800.00, "05/03/2025"))
        expenseList.add(Expense(R.drawable.ob1, "Other", 500.00, "06/03/2025"))

        // Set up the adapter
        adapter = ExpenseAdapter(expenseList)
        recyclerView.adapter = adapter

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this@ExpenseHistoryActivity, Home::class.java))
                    true
                }
                R.id.addExpense -> {
                    startActivity(Intent(this@ExpenseHistoryActivity, Add_Expense::class.java))
                    true
                }
                R.id.more -> {
                    // Open the "More" page (this is likely the current page)
                    startActivity(Intent(this@ExpenseHistoryActivity, more::class.java))  // Assuming Home is your "More" page
                    finish() // Optionally, finish the current activity to prevent returning back to it
                    true
                }
                else -> false
            }
        }
    }

}
