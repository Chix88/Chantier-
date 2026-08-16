package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Bloc::class,
            parentColumns = ["id"],
            childColumns = ["blocId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["blocId"])]
)
data class TaskItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val blocId: Long,
    val title: String,            // e.g. "Tâche 1 - Coffrage voiles", "Ferraillage dalles"
    val category: String = "Gros Œuvre",
    val status: String = "En cours", // "À faire", "En cours", "En attente", "Terminé"
    val priority: String = "Moyenne", // "Basse", "Moyenne", "Haute", "Urgente"
    val completionPercent: Int = 0, // 0 - 100
    val targetDate: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
