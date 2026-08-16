package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.Bloc
import com.example.data.model.DailyReport
import com.example.data.model.TaskAllocation
import com.example.data.model.TaskItem
import com.example.data.model.TeamLeader

@Database(
    entities = [
        Bloc::class,
        TeamLeader::class,
        TaskItem::class,
        TaskAllocation::class,
        DailyReport::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ChantierDatabase : RoomDatabase() {

    abstract fun chantierDao(): ChantierDao

    companion object {
        @Volatile
        private var INSTANCE: ChantierDatabase? = null

        fun getDatabase(context: Context): ChantierDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChantierDatabase::class.java,
                    "chantier_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
