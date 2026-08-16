package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_reports")
data class DailyReport(
    @PrimaryKey
    val date: String,             // Format YYYY-MM-DD (unique per day)
    val weather: String = "Ensoleillé", // "Ensoleillé", "Nuageux", "Pluvieux", "Chaleur intense", "Vent fort"
    val siteManagerNotes: String = "",
    val incidentsOrDeliveries: String = "",
    val totalWorkers: Int = 0,
    val totalBlocsActive: Int = 0,
    val totalTasksActive: Int = 0,
    val reportJsonSummary: String = "", // Saved text snapshot
    val createdAt: Long = System.currentTimeMillis()
)
