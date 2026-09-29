package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val category: String, // "Fetur (Petrol)", "Servicing / Gyara", "Kudin Wuta (NEPA)", "Sauran Abubuwa"
    val amount: Double,
    val notes: String
)
