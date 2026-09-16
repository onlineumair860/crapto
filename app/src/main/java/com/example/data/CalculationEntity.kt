package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculations")
data class CalculationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val coinName: String,
    val coinPrice: String,
    val dollarAmount: String,
    val totalCoinsExact: String,
    val totalCoinsCompact: String,
    val timestamp: Long = System.currentTimeMillis()
)
