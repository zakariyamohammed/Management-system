package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "charging_records")
data class ChargingRecord(
    @PrimaryKey
    val id: String, // e.g. "CHG-00001"
    val customerName: String,
    val phoneNumber: String,
    val brand: String,
    val model: String,
    val color: String,
    val cableType: String,
    val staffName: String,
    val imei: String,
    val condition: String,
    val locker: String, // e.g. "Locker A-01"
    val fee: Double,
    val status: String, // "Charging", "Ready", "Delivered"
    val timestamp: String, // e.g. "2026-09-29 08:15"
    val pickupTime: String? = null
)
