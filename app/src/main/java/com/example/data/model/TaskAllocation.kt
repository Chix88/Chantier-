package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "task_allocations",
    foreignKeys = [
        ForeignKey(
            entity = TeamLeader::class,
            parentColumns = ["id"],
            childColumns = ["chefId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Bloc::class,
            parentColumns = ["id"],
            childColumns = ["blocId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskItem::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["chefId"]),
        Index(value = ["blocId"]),
        Index(value = ["taskId"]),
        Index(value = ["date", "chefId", "taskId"], unique = true)
    ]
)
data class TaskAllocation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,             // Format YYYY-MM-DD
    val chefId: Long,
    val blocId: Long,
    val taskId: Long,
    val workersCount: Int = 0,    // Nombre d'ouvriers affectés
    val note: String = "",
    val customRendement: Double = 0.0, // Rendement ajusté spécifique pour cette équipe
    val isSecondaryTask: Boolean = false, // Tâche combinée (évite le double comptage des mêmes ouvriers)
    val linkedTaskId: Long? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
