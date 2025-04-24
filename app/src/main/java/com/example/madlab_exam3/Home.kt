package com.example.madlab_exam3

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madlab_exam3.models.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.Locale

class Home : AppCompatActivity() {

    private lateinit var greetingText: TextView
    lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val transactionKey = "expense_transactions"
    private val budgetKey = "monthly_budget"
    private lateinit var notificationTextView: TextView
    private lateinit var budgetProgressBar: ProgressBar
    private lateinit var budgetPercentageTextView: TextView
    private lateinit var budgetTextView: TextView
    private lateinit var addBudgetButtonImageView: ImageView
    private lateinit var history: TextView
    private lateinit var transactionHistoryRecyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var profileicon: ImageView

    private var currentCurrency: String = "LKR"  // Store current currency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // Bottom Navigation Setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // No need to restart the activity if already on the home page
                    true
                }
                R.id.addExpense -> {
                    startActivity(Intent(this@Home, Add_Expense::class.java))
                    true
                }
                R.id.more -> {
                    startActivity(Intent(this@Home, more::class.java))
                    true
                }
                else -> false
            }
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyFinanceApp", Context.MODE_PRIVATE)

        // Initialize Views
        greetingText = findViewById(R.id.greetingText)
        profileicon = findViewById(R.id.profileIcon)
        notificationTextView = findViewById(R.id.notificationTextView)
        budgetProgressBar = findViewById(R.id.budgetProgressBar)
        budgetPercentageTextView = findViewById(R.id.budgetPercentageText)
        history = findViewById(R.id.budgetLabel)
        budgetTextView = findViewById(R.id.budgetText)
        addBudgetButtonImageView = findViewById(R.id.addBudgetButton)
        val categoriesTitleTextView = findViewById<TextView>(R.id.categoriesTitle)
        transactionHistoryRecyclerView = findViewById(R.id.transactionHistoryRecyclerView)
        transactionHistoryRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with an empty list initially
        transactionAdapter = TransactionAdapter(emptyList())
        transactionHistoryRecyclerView.adapter = transactionAdapter

        // Load and display initial data
        loadHomePageData()

        // Navigation Listeners
        categoriesTitleTextView.setOnClickListener {
            val intent = Intent(this@Home, Transactionhistory::class.java)
            startActivity(intent)
        }

        profileicon.setOnClickListener {
            val intent = Intent(this@Home, profile::class.java)
            startActivity(intent)
        }

        addBudgetButtonImageView.setOnClickListener {
            val intent = Intent(this@Home, set_budget::class.java)
            startActivity(intent)
        }

        history.setOnClickListener {
            val intent = Intent(this@Home, Transactionhistory::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload data every time the activity comes to the foreground
        loadHomePageData()
    }

    private fun loadHomePageData() {
        // Load recent transactions
        val recentTransactions = loadRecentTransactions()
        transactionAdapter.updateData(recentTransactions)

        // Load budget information
        loadBudgetInfo()
    }

    private fun loadRecentTransactions(): List<Transaction> {
        val transactionsJson = sharedPreferences.getString(transactionKey, null)
        val type: Type = object : TypeToken<List<Transaction>>() {}.type
        val allTransactions: List<Transaction> = gson.fromJson(transactionsJson, type) ?: emptyList()

        // Get the most recent transactions (e.g., the last 5)
        return allTransactions.takeLast(5).reversed()
    }

    private fun loadBudgetInfo() {
        val budget = sharedPreferences.getFloat(budgetKey, 0f).toDouble()
        val totalSpent = calculateTotalSpent()
        val remaining = budget - totalSpent
        val progressPercent = if (budget > 0) ((totalSpent / budget) * 100).toInt().coerceIn(0, 100) else 0

        // Get the relevant currency symbol based on currentCurrency
        val currencySymbol = when (currentCurrency) {
            "USD" -> "$"
            "EUR" -> "€"
            "GBP" -> "£"
            "JPY" -> "¥"
            "LKR" -> "LKR"
            else -> "$" // Default to USD if currency is not found
        }

        // Update Budget Progress Bar on Home Screen
        budgetProgressBar.max = 100
        budgetProgressBar.progress = progressPercent
        budgetPercentageTextView.text = String.format(Locale.getDefault(), "%d%%", progressPercent)

        // Use currency symbol to display the budget and spent values
        budgetTextView.text = String.format(getString(R.string.budget_amount_home1), currencySymbol, "%.2f".format(budget))

        // Check for budget warning and trigger local notification
        if (progressPercent >= 80 && budget > 0) {
            val notificationTitle = getString(R.string.budget_warning_title)
            val notificationMessage = String.format(
                Locale.getDefault(),
                getString(R.string.budget_warning_details2),
                currencySymbol, "%.2f".format(budget),
                "%.2f".format(totalSpent),
                "%.2f".format(remaining)
            )
            showBudgetWarningNotification(notificationTitle, notificationMessage)
            notificationTextView.visibility = View.GONE // Optionally hide the in-app notification
        } else {
            notificationTextView.visibility = View.GONE
        }
    }


    private fun showBudgetWarningNotification(title: String, message: String) {
        val channelId = "budget_warning_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification) // Use your notification icon here
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Budget Warnings", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build()) // Unique notification ID
    }

    private fun calculateTotalSpent(): Double {
        val transactions = getStoredTransactions()
        return transactions.filter { it.type == "Expense" }.sumOf { it.amount }
    }

    private fun getStoredTransactions(): List<Transaction> {
        val json = sharedPreferences.getString(transactionKey, null)
        return if (!json.isNullOrEmpty()) {
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

    override fun onDestroy() {
        super.onDestroy()
        //        unregisterReceiver(budgetWarningReceiver) // Unregister receiver if you keep using it
    }
}

// Adapter for displaying transactions in the RecyclerView
class TransactionAdapter(private var transactions: List<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.txtTitle)
        val categoryTextView: TextView = itemView.findViewById(R.id.txtCategory)
        val dateTextView: TextView = itemView.findViewById(R.id.txtDate)
        val amountTextView: TextView = itemView.findViewById(R.id.txtAmount)
        val transactionTypeTextView: TextView = itemView.findViewById(R.id.txtTransactionType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val currentTransaction = transactions[position]
        holder.titleTextView.text = currentTransaction.title
        holder.categoryTextView.text = currentTransaction.category
        holder.dateTextView.text = currentTransaction.date
        holder.amountTextView.text = String.format(
            Locale.getDefault(),
            "%s %.2f",
            (holder.itemView.context as Home).currentCurrencySymbol(),
            currentTransaction.amount
        )
        holder.transactionTypeTextView.text = currentTransaction.type
        if (currentTransaction.type == "Expense") {
            holder.amountTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.expenseColor))
            holder.transactionTypeTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.expenseColor))
        } else {
            holder.amountTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.incomeColor))
            holder.transactionTypeTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.incomeColor))
        }
    }

    override fun getItemCount() = transactions.size

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}

// Extension function to get the currency symbol based on the stored currency code
fun Home.currentCurrencySymbol(): String {
    return when (sharedPreferences.getString("currency", "LKR")) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "JPY" -> "¥"
        "LKR" -> "LKR"
        else -> "$" // Default to USD
    }
}