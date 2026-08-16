package com.example.data.repository

import com.example.data.local.ChantierDao
import com.example.data.model.AllocationDetail
import com.example.data.model.Bloc
import com.example.data.model.DailyReport
import com.example.data.model.TaskAllocation
import com.example.data.model.TaskItem
import com.example.data.model.TeamLeader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChantierRepository(private val dao: ChantierDao) {

    val allBlocs: Flow<List<Bloc>> = dao.getAllBlocs()
    val allChefs: Flow<List<TeamLeader>> = dao.getAllTeamLeaders()
    val allTasks: Flow<List<TaskItem>> = dao.getAllTasks()
    val allDailyReports: Flow<List<DailyReport>> = dao.getAllDailyReports()

    fun getTasksForBloc(blocId: Long): Flow<List<TaskItem>> = dao.getTasksForBloc(blocId)

    fun getAllocationsForDate(date: String): Flow<List<TaskAllocation>> =
        dao.getAllocationsForDate(date)

    // Combine Blocs, Tasks, Chefs, Allocations for a specific date to give high-level detailed structures
    fun getDetailedAllocationsForDate(date: String): Flow<List<AllocationDetail>> {
        return combine(
            dao.getAllocationsForDate(date),
            dao.getAllTeamLeaders(),
            dao.getAllBlocs(),
            dao.getAllTasks()
        ) { allocations, chefs, blocs, tasks ->
            val chefMap = chefs.associateBy { it.id }
            val blocMap = blocs.associateBy { it.id }
            val taskMap = tasks.associateBy { it.id }

            allocations.mapNotNull { alloc ->
                val chef = chefMap[alloc.chefId]
                val bloc = blocMap[alloc.blocId]
                val task = taskMap[alloc.taskId]

                if (chef != null && bloc != null && task != null) {
                    AllocationDetail(
                        allocationId = alloc.id,
                        date = alloc.date,
                        chefId = chef.id,
                        chefName = chef.name,
                        chefSpecialty = chef.specialty,
                        chefTotalWorkers = chef.totalWorkers,
                        blocId = bloc.id,
                        blocName = bloc.name,
                        blocCode = bloc.code,
                        blocColorHex = bloc.colorHex,
                        taskId = task.id,
                        taskTitle = task.title,
                        taskCategory = task.category,
                        taskStatus = task.status,
                        taskPriority = task.priority,
                        workersCount = alloc.workersCount,
                        note = alloc.note
                    )
                } else null
            }
        }
    }

    // CRUD for Bloc
    suspend fun insertBloc(bloc: Bloc): Long = dao.insertBloc(bloc)
    suspend fun updateBloc(bloc: Bloc) = dao.updateBloc(bloc)
    suspend fun deleteBloc(bloc: Bloc) = dao.deleteBloc(bloc)

    // CRUD for Team Leader
    suspend fun insertChef(chef: TeamLeader): Long = dao.insertTeamLeader(chef)
    suspend fun updateChef(chef: TeamLeader) = dao.updateTeamLeader(chef)
    suspend fun deleteChef(chef: TeamLeader) = dao.deleteTeamLeader(chef)

    // CRUD for Task
    suspend fun insertTask(task: TaskItem): Long = dao.insertTask(task)
    suspend fun updateTask(task: TaskItem) = dao.updateTask(task)
    suspend fun deleteTask(task: TaskItem) = dao.deleteTask(task)
    suspend fun updateTaskProgress(taskId: Long, status: String, percent: Int) =
        dao.updateTaskProgress(taskId, status, percent)

    // Allocation updates
    suspend fun setWorkerAllocation(
        date: String,
        chefId: Long,
        blocId: Long,
        taskId: Long,
        workersCount: Int,
        note: String = ""
    ) {
        if (workersCount <= 0) {
            dao.deleteAllocationByChefAndTask(date, chefId, taskId)
        } else {
            val allocation = TaskAllocation(
                date = date,
                chefId = chefId,
                blocId = blocId,
                taskId = taskId,
                workersCount = workersCount,
                note = note
            )
            dao.insertOrUpdateAllocation(allocation)
        }
    }

    suspend fun deleteAllocation(allocationId: Long) = dao.deleteAllocation(allocationId)

    // Daily reports
    suspend fun getDailyReport(date: String): DailyReport? = dao.getDailyReport(date)
    suspend fun saveDailyReport(report: DailyReport) = dao.insertOrUpdateDailyReport(report)

    // Initial Seed Data setup
    suspend fun seedInitialDataIfNeeded() {
        if (dao.getBlocCount() == 0) {
            val blocAId = dao.insertBloc(
                Bloc(
                    name = "Bloc A",
                    code = "BLOC-A",
                    description = "Bâtiment Logements - R+4",
                    status = "En cours",
                    surfaceInfo = "1 850 m²",
                    colorHex = "#FF9800"
                )
            )
            val blocBId = dao.insertBloc(
                Bloc(
                    name = "Bloc B",
                    code = "BLOC-B",
                    description = "Bâtiment Bureaux & Commerces",
                    status = "En cours",
                    surfaceInfo = "2 400 m²",
                    colorHex = "#2196F3"
                )
            )
            val blocCId = dao.insertBloc(
                Bloc(
                    name = "Bloc C",
                    code = "BLOC-C",
                    description = "Zone Parking & Sous-Sol",
                    status = "En cours",
                    surfaceInfo = "3 100 m²",
                    colorHex = "#4CAF50"
                )
            )

            // Team Leaders (Chefs d'équipe)
            val chef1Id = dao.insertTeamLeader(
                TeamLeader(
                    name = "Chef Équipe 1 (Ahmed)",
                    phone = "06 11 22 33 44",
                    specialty = "Gros Œuvre & Coffrage",
                    totalWorkers = 10,
                    defaultBlocId = blocAId
                )
            )

            val chef2Id = dao.insertTeamLeader(
                TeamLeader(
                    name = "Chef Équipe 2 (Karim)",
                    phone = "06 55 66 77 88",
                    specialty = "Ferraillage & Béton Armé",
                    totalWorkers = 15,
                    defaultBlocId = blocCId
                )
            )

            val chef3Id = dao.insertTeamLeader(
                TeamLeader(
                    name = "Chef Équipe 3 (Marc)",
                    phone = "06 99 88 77 66",
                    specialty = "Maçonnerie & Cloisonnement",
                    totalWorkers = 8,
                    defaultBlocId = blocBId
                )
            )

            // Tasks for Bloc A
            val taskA1 = dao.insertTask(
                TaskItem(
                    blocId = blocAId,
                    title = "Coffrage des voiles RDC",
                    category = "Gros Œuvre",
                    status = "En cours",
                    priority = "Haute",
                    completionPercent = 60,
                    description = "Pose des banches métalliques et serrage"
                )
            )
            val taskA2 = dao.insertTask(
                TaskItem(
                    blocId = blocAId,
                    title = "Ferraillage dalles et linteaux",
                    category = "Ferraillage",
                    status = "En cours",
                    priority = "Haute",
                    completionPercent = 45,
                    description = "Positionnement des treillis et calage"
                )
            )
            val taskA3 = dao.insertTask(
                TaskItem(
                    blocId = blocAId,
                    title = "Coulage et vibration béton",
                    category = "Béton",
                    status = "En cours",
                    priority = "Urgente",
                    completionPercent = 30,
                    description = "Coulage dalle haute RDC avec pompe"
                )
            )
            val taskA4 = dao.insertTask(
                TaskItem(
                    blocId = blocAId,
                    title = "Décoffrage et ragréage",
                    category = "Finitions",
                    status = "À faire",
                    priority = "Moyenne",
                    completionPercent = 10,
                    description = "Nettoyage banches et traitement de surface"
                )
            )

            // Tasks for Bloc C
            val taskC1 = dao.insertTask(
                TaskItem(
                    blocId = blocCId,
                    title = "Ferraillage massif radier",
                    category = "Ferraillage",
                    status = "En cours",
                    priority = "Haute",
                    completionPercent = 70,
                    description = "Pose aciers HA20 et écarteurs"
                )
            )
            val taskC2 = dao.insertTask(
                TaskItem(
                    blocId = blocCId,
                    title = "Coulage radier sous-sol",
                    category = "Béton",
                    status = "En cours",
                    priority = "Urgente",
                    completionPercent = 50,
                    description = "Rotation 4 toupies de béton C30/37"
                )
            )
            val taskC3 = dao.insertTask(
                TaskItem(
                    blocId = blocCId,
                    title = "Étanchéité murs enterrés",
                    category = "Gros Œuvre",
                    status = "En cours",
                    priority = "Moyenne",
                    completionPercent = 25,
                    description = "Application bicouche bitumineux"
                )
            )

            // Tasks for Bloc B
            val taskB1 = dao.insertTask(
                TaskItem(
                    blocId = blocBId,
                    title = "Élévation briques 2ème étage",
                    category = "Maçonnerie",
                    status = "En cours",
                    priority = "Moyenne",
                    completionPercent = 40,
                    description = "Pose briques alvéolaires avec mortier isolant"
                )
            )
            val taskB2 = dao.insertTask(
                TaskItem(
                    blocId = blocBId,
                    title = "Pose pré-cadres menuiseries",
                    category = "Maçonnerie",
                    status = "À faire",
                    priority = "Moyenne",
                    completionPercent = 15,
                    description = "Vérification niveaux laser"
                )
            )

            // Current date allocation sample (Today)
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            // Chef 1 (10 ouvriers) on Bloc A:
            // 2 ouvriers tâche 1, 2 ouvriers tâche 2, 5 ouvriers tâche 3, 1 ouvrier tâche 4
            dao.insertOrUpdateAllocation(
                TaskAllocation(date = todayStr, chefId = chef1Id, blocId = blocAId, taskId = taskA1, workersCount = 2, note = "Équipe Coffrage")
            )
            dao.insertOrUpdateAllocation(
                TaskAllocation(date = todayStr, chefId = chef1Id, blocId = blocAId, taskId = taskA2, workersCount = 2, note = "Équipe Ferraillage")
            )
            dao.insertOrUpdateAllocation(
                TaskAllocation(date = todayStr, chefId = chef1Id, blocId = blocAId, taskId = taskA3, workersCount = 5, note = "Équipe Coulage Béton")
            )
            dao.insertOrUpdateAllocation(
                TaskAllocation(date = todayStr, chefId = chef1Id, blocId = blocAId, taskId = taskA4, workersCount = 1, note = "Nettoyage & Sécurité")
            )

            // Chef 2 (15 ouvriers) on Bloc C:
            // 6 ouvriers tâche C1, 6 ouvriers tâche C2, 3 ouvriers tâche C3
            dao.insertOrUpdateAllocation(
                TaskAllocation(date = todayStr, chefId = chef2Id, blocId = blocCId, taskId = taskC1, workersCount = 6, note = "Ferrailleurs en place")
            )
            dao.insertOrUpdateAllocation(
                TaskAllocation(date = todayStr, chefId = chef2Id, blocId = blocCId, taskId = taskC2, workersCount = 6, note = "Guidage toupie & vibrateurs")
            )
            dao.insertOrUpdateAllocation(
                TaskAllocation(date = todayStr, chefId = chef2Id, blocId = blocCId, taskId = taskC3, workersCount = 3, note = "Application primaire étanchéité")
            )

            // Chef 3 (8 ouvriers) on Bloc B:
            // 5 ouvriers tâche B1, 3 ouvriers tâche B2
            dao.insertOrUpdateAllocation(
                TaskAllocation(date = todayStr, chefId = chef3Id, blocId = blocBId, taskId = taskB1, workersCount = 5, note = "Maçons brique")
            )
            dao.insertOrUpdateAllocation(
                TaskAllocation(date = todayStr, chefId = chef3Id, blocId = blocBId, taskId = taskB2, workersCount = 3, note = "Ajustage menuiseries")
            )

            // Create initial daily report record
            dao.insertOrUpdateDailyReport(
                DailyReport(
                    date = todayStr,
                    weather = "Ensoleillé - 23°C",
                    siteManagerNotes = "Chantier opérationnel sans retard majeur. Bon rythme de coulage sur le Bloc A et le radier Bloc C.",
                    incidentsOrDeliveries = "Livraison de 45 tonnes de ferraillage à 08h30. R.A.S sécurité.",
                    totalWorkers = 33,
                    totalBlocsActive = 3,
                    totalTasksActive = 9,
                    reportJsonSummary = "Rapport initial pré-configuré"
                )
            )
        }
    }
}
