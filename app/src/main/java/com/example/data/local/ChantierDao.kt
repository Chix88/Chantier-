package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Bloc
import com.example.data.model.DailyReport
import com.example.data.model.TaskAllocation
import com.example.data.model.TaskItem
import com.example.data.model.TeamLeader
import kotlinx.coroutines.flow.Flow

@Dao
interface ChantierDao {

    // --- BLOCS ---
    @Query("SELECT * FROM blocs ORDER BY name ASC")
    fun getAllBlocs(): Flow<List<Bloc>>

    @Query("SELECT * FROM blocs WHERE id = :id")
    suspend fun getBlocById(id: Long): Bloc?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBloc(bloc: Bloc): Long

    @Update
    suspend fun updateBloc(bloc: Bloc)

    @Delete
    suspend fun deleteBloc(bloc: Bloc)

    @Query("SELECT COUNT(*) FROM blocs")
    suspend fun getBlocCount(): Int

    // --- TEAM LEADERS (CHEFS D'EQUIPE) ---
    @Query("SELECT * FROM team_leaders WHERE isActive = 1 ORDER BY name ASC")
    fun getAllTeamLeaders(): Flow<List<TeamLeader>>

    @Query("SELECT * FROM team_leaders WHERE id = :id")
    suspend fun getTeamLeaderById(id: Long): TeamLeader?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeamLeader(leader: TeamLeader): Long

    @Update
    suspend fun updateTeamLeader(leader: TeamLeader)

    @Delete
    suspend fun deleteTeamLeader(leader: TeamLeader)

    @Query("SELECT COUNT(*) FROM team_leaders")
    suspend fun getTeamLeaderCount(): Int

    // --- TASKS ---
    @Query("SELECT * FROM tasks ORDER BY blocId ASC, orderIndex ASC, id ASC")
    fun getAllTasks(): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE blocId = :blocId ORDER BY orderIndex ASC, id ASC")
    fun getTasksForBloc(blocId: Long): Flow<List<TaskItem>>

    @Query("SELECT * FROM tasks WHERE blocId = :blocId ORDER BY orderIndex ASC, id ASC")
    suspend fun getTasksListForBloc(blocId: Long): List<TaskItem>

    @Query("SELECT MAX(orderIndex) FROM tasks WHERE blocId = :blocId")
    suspend fun getMaxOrderIndexForBloc(blocId: Long): Int?

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskItem): Long

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)

    @Query("UPDATE tasks SET status = :status, completionPercent = :percent WHERE id = :taskId")
    suspend fun updateTaskProgress(taskId: Long, status: String, percent: Int)

    // --- TASK ALLOCATIONS ---
    @Query("SELECT * FROM task_allocations WHERE date = :date")
    fun getAllocationsForDate(date: String): Flow<List<TaskAllocation>>

    @Query("SELECT * FROM task_allocations WHERE taskId = :taskId")
    fun getAllocationsForTask(taskId: Long): Flow<List<TaskAllocation>>

    @Query("SELECT * FROM task_allocations WHERE taskId = :taskId")
    suspend fun getAllocationsListForTask(taskId: Long): List<TaskAllocation>

    @Query("SELECT * FROM task_allocations WHERE date = :date AND chefId = :chefId")
    fun getAllocationsForChefAndDate(date: String, chefId: Long): Flow<List<TaskAllocation>>

    @Query("SELECT * FROM task_allocations WHERE date = :date AND blocId = :blocId")
    fun getAllocationsForBlocAndDate(date: String, blocId: Long): Flow<List<TaskAllocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAllocation(allocation: TaskAllocation): Long

    @Query("SELECT * FROM task_allocations WHERE id = :allocationId")
    suspend fun getAllocationById(allocationId: Long): TaskAllocation?

    @Query("DELETE FROM task_allocations WHERE id = :allocationId")
    suspend fun deleteAllocation(allocationId: Long)

    @Query("DELETE FROM task_allocations WHERE date = :date AND chefId = :chefId AND taskId = :taskId")
    suspend fun deleteAllocationByChefAndTask(date: String, chefId: Long, taskId: Long)

    @Query("SELECT SUM(workersCount) FROM task_allocations WHERE taskId = :taskId")
    suspend fun getTotalWorkersForTask(taskId: Long): Int?

    // --- DAILY REPORTS ---
    @Query("SELECT * FROM daily_reports ORDER BY date DESC")
    fun getAllDailyReports(): Flow<List<DailyReport>>

    @Query("SELECT * FROM daily_reports WHERE date = :date")
    suspend fun getDailyReport(date: String): DailyReport?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDailyReport(report: DailyReport)
}
