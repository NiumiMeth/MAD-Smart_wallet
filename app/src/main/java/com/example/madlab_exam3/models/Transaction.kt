package com.example.madlab_exam3.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val id: Long = System.currentTimeMillis(), // Unique ID
    var title: String,
    var amount: Double,
    var category: String,
    var date: String,
    var type: String // "Income" or "Expense"
) : Parcelable
