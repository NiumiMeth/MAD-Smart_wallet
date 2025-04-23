package com.example.madlab_exam3

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import com.example.madlab_exam3.models.Transaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*
import java.util.Locale

class more : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()
    private val transactionKey = "expense_transactions"
    private lateinit var backupDataText: TextView
    private lateinit var restoreDataText: TextView
    private lateinit var mainCurrencyText: TextView
    private lateinit var themeSelectorText: TextView
    private lateinit var backButton: TextView
    private lateinit var accountTitle: TextView
    private lateinit var accountInfoCard: CardView
    private lateinit var userProfileImage: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var accountOptionsCard: CardView
    private lateinit var categoriesText: TextView
    private lateinit var scheduledTransactionText: TextView
    private lateinit var setBudgetTextView: TextView
    private lateinit var backupRestoreCard: CardView
    private lateinit var otherOptionsCard: CardView
    private lateinit var bottomNavView: BottomNavigationView

    private val backupFileName = "transactions_backup.json"
    private var currentTheme: Int = AppCompatDelegate.getDefaultNightMode()
    private var currentCurrency: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_more)

        // Initialize Views after setting content view
        backButton = findViewById(R.id.backButton)
        accountTitle = findViewById(R.id.accountTitle)
        accountInfoCard = findViewById(R.id.accountInfoCard)
        userProfileImage = findViewById(R.id.userProfileImage)
        userNameTextView = findViewById(R.id.userName)
        userEmailTextView = findViewById(R.id.userEmail)
        accountOptionsCard = findViewById(R.id.accountOptionsCard)
        categoriesText = findViewById(R.id.categoriesText)
        scheduledTransactionText = findViewById(R.id.scheduledTransactionText)
        mainCurrencyText = findViewById(R.id.mainCurrencyText) // Correct initialization
        setBudgetTextView = findViewById(R.id.setBudget)
        backupDataText = findViewById(R.id.backupDataText)
        restoreDataText = findViewById(R.id.restoreDataText)
        backupRestoreCard = findViewById(R.id.backupRestoreCard)
        otherOptionsCard = findViewById(R.id.otherOptionsCard)
        bottomNavView = findViewById(R.id.bottomNav)
        themeSelectorText = findViewById(R.id.advancedText)

        // Retrieve current theme and currency from SharedPreferences
        sharedPreferences = getSharedPreferences("MyFinanceApp", MODE_PRIVATE)
        currentTheme = sharedPreferences.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        currentCurrency = sharedPreferences.getString("currency", "LKR") ?: "LKR"
        applyTheme(currentTheme)

        // Update UI text elements with current values
        updateCurrencyText()
        updateThemeText()

        // Initialize Click Listeners
        backButton.setOnClickListener {
            finish()
        }

        categoriesText.setOnClickListener {
            val intent = Intent(this@more, CategorySpending::class.java)
            startActivity(intent)
        }

        scheduledTransactionText.setOnClickListener {
            val intent = Intent(this@more, Add_Expense::class.java)
            startActivity(intent)
        }

        userNameTextView.setOnClickListener { startActivity(Intent(this@more, profile::class.java)) }
        userProfileImage.setOnClickListener { startActivity(Intent(this@more, profile::class.java)) }

        setBudgetTextView.setOnClickListener {
            val intent = Intent(this@more, set_budget::class.java)
            startActivity(intent)
        }

        backupDataText.setOnClickListener {
            backupTransactionsInternal()
        }

        restoreDataText.setOnClickListener {
            restoreTransactionsInternal()
        }

        mainCurrencyText.setOnClickListener {
            showCurrencySelectorDialog()
        }

        themeSelectorText.setOnClickListener {
            showThemeSelectorDialog()
        }

        // Bottom Navigation Setup
        bottomNavView.selectedItemId = R.id.more
        bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    startActivity(Intent(this@more, Home::class.java))
                    finish()
                    true
                }
                R.id.addExpense -> {
                    startActivity(Intent(this@more, Add_Expense::class.java))
                    true
                }
                R.id.more -> {
                    true
                }
                else -> false
            }
        }

        // Apply Animations
        applyAnimations()
    }

    private fun applyAnimations() {
        val slideInLeft = AnimationUtils.loadAnimation(this, R.anim.slide_in_left)
        val slideInTop = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up)

        backButton.startAnimation(slideInLeft)
        accountTitle.startAnimation(slideInTop)
        accountInfoCard.startAnimation(scaleUp)
        userProfileImage.startAnimation(slideInLeft)
        userNameTextView.startAnimation(slideInTop)
        userEmailTextView.startAnimation(slideInTop.apply { startOffset = 100 })
        accountOptionsCard.startAnimation(fadeIn.apply { startOffset = 200 })
        backupRestoreCard.startAnimation(fadeIn.apply { startOffset = 300 })
        otherOptionsCard.startAnimation(fadeIn.apply { startOffset = 400 })
        bottomNavView.startAnimation(slideInTop.apply { startOffset = 500 })
    }

    private fun updateCurrencyText() {
        mainCurrencyText.text = getString(R.string.currency_setting, currentCurrency)
    }

    private fun updateThemeText() {
        val themeText = when (currentTheme) {
            AppCompatDelegate.MODE_NIGHT_YES -> getString(R.string.dark_mode)
            AppCompatDelegate.MODE_NIGHT_NO -> getString(R.string.light_mode)
            else -> getString(R.string.system_default)
        }
        themeSelectorText.text = getString(R.string.theme_setting, themeText)
    }

    private fun showCurrencySelectorDialog() {
        val currencies = arrayOf("LKR", "USD", "EUR", "GBP", "JPY") // Add more as needed
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.select_currency)
            .setSingleChoiceItems(currencies, currencies.indexOf(currentCurrency)) { dialog, which ->
                val selectedCurrency = currencies[which]
                if (selectedCurrency != currentCurrency) {
                    currentCurrency = selectedCurrency
                    sharedPreferences.edit().putString("currency", currentCurrency).apply()
                    updateCurrencyText()
                    Toast.makeText(this, getString(R.string.currency_changed, currentCurrency), Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
        builder.create().show()
    }

    private fun showThemeSelectorDialog() {
        val themes = arrayOf(getString(R.string.light_mode), getString(R.string.dark_mode), getString(R.string.system_default))
        val themeModes = arrayOf(AppCompatDelegate.MODE_NIGHT_NO, AppCompatDelegate.MODE_NIGHT_YES, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val currentThemeIndex = themeModes.indexOf(currentTheme)

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(R.string.select_theme)
            .setSingleChoiceItems(themes, currentThemeIndex) { dialog, which ->
                val selectedTheme = themeModes[which]
                if (selectedTheme != currentTheme) {
                    currentTheme = selectedTheme
                    sharedPreferences.edit().putInt("theme", currentTheme).apply()
                    applyTheme(currentTheme)
                    updateThemeText()
                    recreate()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
        builder.create().show()
    }

    private fun applyTheme(themeMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
    }

    private fun getTransactions(): List<Transaction> {
        val json = sharedPreferences.getString(transactionKey, null)
        return if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Transaction>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun saveTransactions(transactions: List<Transaction>) {
        val jsonString = gson.toJson(transactions)
        sharedPreferences.edit().putString(transactionKey, jsonString).apply()
    }

    private fun backupTransactionsInternal() {
        val transactions = getTransactions()
        val jsonString = gson.toJson(transactions)

        try {
            val file = File(filesDir, backupFileName)
            FileOutputStream(file).use { it.write(jsonString.toByteArray()) }
            Toast.makeText(this, R.string.backup_successful_internal, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.backup_failed, e.localizedMessage), Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun restoreTransactionsInternal() {
        val backupFile = File(filesDir, backupFileName)
        if (backupFile.exists()) {
            try {
                FileInputStream(backupFile).use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        val jsonString = reader.readText()
                        val type = object : TypeToken<List<Transaction>>() {}.type
                        val restoredTransactions: List<Transaction> = gson.fromJson(jsonString, type) ?: emptyList()
                        saveTransactions(restoredTransactions)
                        Toast.makeText(this, R.string.restore_successful_internal, Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, getString(R.string.restore_failed_internal, e.localizedMessage), Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        } else {
            Toast.makeText(this, R.string.no_internal_backup_found, Toast.LENGTH_SHORT).show()
        }
    }
}
