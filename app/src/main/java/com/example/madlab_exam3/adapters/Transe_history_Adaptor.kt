package com.example.madlab_exam3.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madlab_exam3.R
import com.example.madlab_exam3.Transactionhistory
import com.example.madlab_exam3.models.Transaction

class Transe_history_Adaptor(private var transactionList: MutableList<Transaction>, private val activity: Transactionhistory) :
    RecyclerView.Adapter<Transe_history_Adaptor.TransactionViewHolder>() {

    // ViewHolder for each item in the list
    class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.txtTitle)
        val amount: TextView = view.findViewById(R.id.txtAmount)
        val category: TextView = view.findViewById(R.id.txtCategory)
        val date: TextView = view.findViewById(R.id.txtDate)
        val editButton: ImageView = view.findViewById(R.id.imgEdit)
        val deleteButton: ImageView = view.findViewById(R.id.imgDelete)
        val transactionType: TextView = view.findViewById(R.id.txtTransactionType) // Missing findViewById
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.title.text = transaction.title
        holder.amount.text = "Rs. %.2f".format(transaction.amount)
        holder.category.text = transaction.category
        holder.date.text = transaction.date

        // Set the transaction type dynamically
        val transactionType = if (transaction.type == "Expense") activity.getString(R.string.expense) else activity.getString(R.string.income)
        holder.transactionType.text = transactionType

        // Set color for the transaction type (Expense -> Red, Income -> Green)
        val transactionColor = if (transaction.type == "Expense") "#FF4081" else "#388E3C"
        holder.transactionType.setTextColor(Color.parseColor(transactionColor))

        // Set the amount text color based on type
        holder.amount.setTextColor(Color.parseColor(transactionColor)) // You can also set the amount color

        // Set click listeners for edit and delete
        holder.editButton.setOnClickListener {
            activity.onEditTransaction(position)
        }

        holder.deleteButton.setOnClickListener {
            activity.onDeleteTransaction(position)
        }
    }

    override fun getItemCount(): Int = transactionList.size

    // Method to update the data
    fun updateData(newData: List<Transaction>) {
        transactionList.clear()
        transactionList.addAll(newData)
        notifyDataSetChanged()  // Notify the adapter to refresh the RecyclerView
    }
}