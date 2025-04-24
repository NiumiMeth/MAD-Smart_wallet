package com.example.madlab_exam3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madlab_exam3.models.CategoryItem
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class CategorySpendingAdapter(
    private var categoryItems: List<CategoryItem>,
    private var currencyCode: String
) : RecyclerView.Adapter<CategorySpendingAdapter.CategoryViewHolder>() {

    private var maxSpending: Float = categoryItems.maxOfOrNull { it.totalSpent }?.toFloat() ?: 1f

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
        val totalSpentTextView: TextView = itemView.findViewById(R.id.totalSpentTextView)
        val spendingProgress: ProgressBar = itemView.findViewById(R.id.spendingProgress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryItem = categoryItems[position]

        // Set category name
        holder.categoryNameTextView.text = categoryItem.categoryName

        // Format total spent based on selected currency
        val currencyFormat = NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance(currencyCode)
            val locale = when (currencyCode) {
                "USD" -> Locale.US
                "EUR" -> Locale("en", "FR")
                "GBP" -> Locale.UK
                "JPY" -> Locale.JAPAN
                "LKR" -> Locale("en", "LK")
                else -> Locale.getDefault()
            }
            maximumFractionDigits = if (currencyCode == "JPY") 0 else 2
            minimumFractionDigits = if (currencyCode == "JPY") 0 else 2

        }
        holder.totalSpentTextView.text = currencyFormat.format(categoryItem.totalSpent)

        // Calculate progress
        val progress = if (maxSpending > 0 && categoryItem.totalSpent > 0) {
            ((categoryItem.totalSpent / maxSpending) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
        holder.spendingProgress.progress = progress
    }

    override fun getItemCount(): Int = categoryItems.size.takeIf { it > 0 } ?: 0

    fun updateData(newItems: List<CategoryItem>, newCurrencyCode: String) {
        categoryItems = newItems
        currencyCode = newCurrencyCode
        maxSpending = newItems.maxOfOrNull { it.totalSpent }?.toFloat() ?: 1f
        notifyDataSetChanged()
    }
}