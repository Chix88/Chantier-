package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "team_leaders")
data class TeamLeader(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,             // e.g. "Chef Équipe 1 (Ahmed)"
    val phone: String = "",       // e.g. "06 12 34 56 78"
    val specialty: String = "Gros Œuvre", // "Gros Œuvre", "Ferraillage", "Béton", "Maçonnerie", "Électricité", "Plomberie"
    val totalWorkers: Int = 10,   // Nombre total d'ouvriers sous sa responsabilité (ex: 10, 15)
    val defaultBlocId: Long? = null,
    val isActive: Boolean = true
)
