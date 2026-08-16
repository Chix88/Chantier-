package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocs")
data class Bloc(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,             // e.g. "Bloc A"
    val code: String,             // e.g. "BLOC-A"
    val description: String = "", // e.g. "Bâtiment Résidentiel - R+4"
    val status: String = "En cours", // "Planifié", "En cours", "Finitions", "Livré"
    val surfaceInfo: String = "", // e.g. "1 450 m²"
    val colorHex: String = "#FF9800",
    val createdAt: Long = System.currentTimeMillis()
)
