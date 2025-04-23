package com.example.madlab_exam3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madlab_exam3.models.CategoryItem
import java.text.NumberFormat
import java.util.Locale

class CategorySpendingAdapter(private val categorySpendingList: List<CategoryItem>) :
    RecyclerView.Adapter<CategorySpendingAdapter.CategorySpendingViewHolder>() {

    class CategorySpendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryNameTextView: TextView = itemView.findViewById(R.id.categoryNameTextView)
        val totalSpentTextView: TextView = itemView.findViewById(R.id.totalSpentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorySpendingViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategorySpendingViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategorySpendingViewHolder, position: Int) {
        val currentItem = categorySpendingList[position]
        holder.categoryNameTextView.text = currentItem.categoryName
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "LK")) // Adjust locale as needed
        holder.totalSpentTextView.text = currencyFormat.format(currentItem.totalSpent)
    }

    override fun getItemCount() = categorySpendingList.size
}