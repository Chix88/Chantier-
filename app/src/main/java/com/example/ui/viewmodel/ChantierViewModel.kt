package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChantierDatabase
import com.example.data.model.AllocationDetail
import com.example.data.model.Bloc
import com.example.data.model.BlocWorkerSummary
import com.example.data.model.ChefWithAllocations
import com.example.data.model.DailyReport
import com.example.data.model.TaskAllocation
import com.example.data.model.TaskItem
import com.example.data.model.TaskWorkerSummary
import com.example.data.model.TeamLeader
import com.example.data.repository.ChantierRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChantierViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChantierRepository

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRENCH)

    // Current selected date for allocations and reports (defaults to today)
    private val _selectedDate = MutableStateFlow(dateFormat.format(Date()))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    // Selected Bloc for focused view / filtering (null = all blocs)
    private val _selectedBlocId = MutableStateFlow<Long?>(null)
    val selectedBlocId: StateFlow<Long?> = _selectedBlocId.asStateFlow()

    // Snackbar / Feedback message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        val db = ChantierDatabase.getDatabase(application)
        repository = ChantierRepository(db.chantierDao())

        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    val allBlocs: StateFlow<List<Bloc>> = repository.allBlocs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allChefs: StateFlow<List<TeamLeader>> = repository.allChefs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTasks: StateFlow<List<TaskItem>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDailyReports: StateFlow<List<DailyReport>> = repository.allDailyReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live reactive allocations for current date
    val currentAllocations: StateFlow<List<AllocationDetail>> = _selectedDate
        .flatMapLatest { date ->
            repository.getDetailedAllocationsForDate(date)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chefs with their current day allocations and worker balance
    val chefsWithAllocations: StateFlow<List<ChefWithAllocations>> = combine(
        allChefs,
        currentAllocations
    ) { chefs, allocations ->
        chefs.map { chef ->
            val chefAllocs = allocations.filter { it.chefId == chef.id }
            val totalAssigned = chefAllocs.filter { !it.isSecondaryTask }.sumOf { it.workersCount }
            ChefWithAllocations(
                chef = chef,
                allocatedWorkers = totalAssigned,
                remainingWorkers = chef.totalWorkers - totalAssigned,
                allocations = chefAllocs
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Summary per Bloc for the selected date
    val blocWorkerSummaries: StateFlow<List<BlocWorkerSummary>> = combine(
        allBlocs,
        allTasks,
        currentAllocations
    ) { blocs, tasks, allocations ->
        blocs.map { bloc ->
            val blocTasks = tasks.filter { it.blocId == bloc.id }
            val blocAllocs = allocations.filter { it.blocId == bloc.id }
            val totalWorkers = blocAllocs.filter { !it.isSecondaryTask }.sumOf { it.workersCount }
            val chefs = blocAllocs.map { it.chefName }.distinct()
            BlocWorkerSummary(
                blocId = bloc.id,
                blocName = bloc.name,
                blocColorHex = bloc.colorHex,
                totalWorkers = totalWorkers,
                activeTasksCount = blocTasks.count { it.status != "Terminé" },
                chefsAssigned = chefs
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Summary per Task Category for the selected date
    val taskWorkerSummaries: StateFlow<List<TaskWorkerSummary>> = currentAllocations.combine(allTasks) { allocations, tasks ->
        val categoryMap = mutableMapOf<String, Pair<Int, MutableSet<Long>>>()
        allocations.forEach { alloc ->
            val cur = categoryMap.getOrPut(alloc.taskCategory) { Pair(0, mutableSetOf()) }
            categoryMap[alloc.taskCategory] = Pair(cur.first + alloc.workersCount, cur.second.apply { add(alloc.taskId) })
        }
        categoryMap.map { (cat, pair) ->
            TaskWorkerSummary(
                taskCategory = cat,
                totalWorkers = pair.first,
                taskCount = pair.second.size
            )
        }.sortedByDescending { it.totalWorkers }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Global Stats for the date
    val totalWorkersMobilized: StateFlow<Int> = currentAllocations.combine(allChefs) { allocations, _ ->
        allocations.filter { !it.isSecondaryTask }.sumOf { it.workersCount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setDateToToday() {
        _selectedDate.value = dateFormat.format(Date())
    }

    fun setDatePreviousDay() {
        try {
            val date = dateFormat.parse(_selectedDate.value) ?: Date()
            val cal = Calendar.getInstance().apply {
                time = date
                add(Calendar.DAY_OF_MONTH, -1)
            }
            _selectedDate.value = dateFormat.format(cal.time)
        } catch (_: Exception) {}
    }

    fun setDateNextDay() {
        try {
            val date = dateFormat.parse(_selectedDate.value) ?: Date()
            val cal = Calendar.getInstance().apply {
                time = date
                add(Calendar.DAY_OF_MONTH, 1)
            }
            _selectedDate.value = dateFormat.format(cal.time)
        } catch (_: Exception) {}
    }

    fun setSelectedBlocId(id: Long?) {
        _selectedBlocId.value = id
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showToast(msg: String) {
        _userMessage.value = msg
    }

    // --- BLOC ACTIONS ---
    fun addBloc(name: String, code: String, description: String, surfaceInfo: String, colorHex: String) {
        viewModelScope.launch {
            repository.insertBloc(
                Bloc(
                    name = name.trim(),
                    code = code.trim().ifEmpty { "BLOC-${name.take(3).uppercase()}" },
                    description = description.trim(),
                    surfaceInfo = surfaceInfo.trim(),
                    colorHex = colorHex
                )
            )
            _userMessage.value = "Bloc \"$name\" créé avec succès"
        }
    }

    fun updateBloc(bloc: Bloc) {
        viewModelScope.launch {
            repository.updateBloc(bloc)
            _userMessage.value = "Bloc mis à jour"
        }
    }

    fun deleteBloc(bloc: Bloc) {
        viewModelScope.launch {
            repository.deleteBloc(bloc)
            if (_selectedBlocId.value == bloc.id) {
                _selectedBlocId.value = null
            }
            _userMessage.value = "Bloc supprimé"
        }
    }

    // --- TEAM LEADER ACTIONS ---
    fun addTeamLeader(name: String, phone: String, specialty: String, totalWorkers: Int, defaultBlocId: Long?) {
        viewModelScope.launch {
            repository.insertChef(
                TeamLeader(
                    name = name.trim(),
                    phone = phone.trim(),
                    specialty = specialty.trim(),
                    totalWorkers = totalWorkers,
                    defaultBlocId = defaultBlocId
                )
            )
            _userMessage.value = "Chef d'équipe \"$name\" ajouté ($totalWorkers ouvriers)"
        }
    }

    fun updateTeamLeader(leader: TeamLeader) {
        viewModelScope.launch {
            repository.updateChef(leader)
            _userMessage.value = "Chef d'équipe mis à jour"
        }
    }

    fun deleteTeamLeader(leader: TeamLeader) {
        viewModelScope.launch {
            repository.deleteChef(leader)
            _userMessage.value = "Chef d'équipe supprimé"
        }
    }

    // --- TASK ACTIONS ---
    fun addTask(blocId: Long, title: String, category: String, priority: String, targetDate: String, description: String, workQuantity: Double = 0.0, workUnit: String = "", rendement: Double = 0.0) {
        viewModelScope.launch {
            repository.insertTask(
                TaskItem(
                    blocId = blocId,
                    title = title.trim(),
                    category = category.trim(),
                    priority = priority,
                    targetDate = targetDate.trim(),
                    description = description.trim(),
                    workQuantity = workQuantity,
                    workUnit = workUnit,
                    rendement = rendement
                )
            )
            _userMessage.value = "Tâche \"$title\" ajoutée"
        }
    }

    fun updateTask(task: TaskItem) {
        viewModelScope.launch {
            repository.updateTask(task)
            _userMessage.value = "Tâche \"${task.title}\" modifiée avec succès"
        }
    }

    fun moveTaskUp(task: TaskItem) {
        viewModelScope.launch {
            repository.moveTaskUp(task)
            _userMessage.value = "Ordre mis à jour : tâche déplacée vers le haut"
        }
    }

    fun moveTaskDown(task: TaskItem) {
        viewModelScope.launch {
            repository.moveTaskDown(task)
            _userMessage.value = "Ordre mis à jour : tâche déplacée vers le bas"
        }
    }

    fun updateTaskStatus(taskId: Long, newStatus: String, percent: Int) {
        viewModelScope.launch {
            repository.updateTaskProgress(taskId, newStatus, percent)
            _userMessage.value = "Statut tâche mis à jour : $newStatus ($percent%)"
        }
    }

    fun deleteTask(task: TaskItem) {
        viewModelScope.launch {
            repository.deleteTask(task)
            _userMessage.value = "Tâche supprimée"
        }
    }

    // --- ALLOCATION ACTIONS ---
    fun setWorkerAllocation(
        chefId: Long,
        blocId: Long,
        taskId: Long,
        workersCount: Int,
        note: String = "",
        customRendement: Double = 0.0
    ) {
        viewModelScope.launch {
            repository.setWorkerAllocation(
                date = _selectedDate.value,
                chefId = chefId,
                blocId = blocId,
                taskId = taskId,
                workersCount = workersCount,
                note = note,
                customRendement = customRendement
            )
        }
    }

    fun setDualWorkerAllocation(
        chefId: Long,
        blocId1: Long,
        taskId1: Long,
        rendement1: Double,
        blocId2: Long,
        taskId2: Long,
        rendement2: Double,
        workersCount: Int,
        note: String = ""
    ) {
        viewModelScope.launch {
            repository.setDualWorkerAllocation(
                date = _selectedDate.value,
                chefId = chefId,
                blocId1 = blocId1,
                taskId1 = taskId1,
                rendement1 = rendement1,
                blocId2 = blocId2,
                taskId2 = taskId2,
                rendement2 = rendement2,
                workersCount = workersCount,
                note = note
            )
            _userMessage.value = "2 tâches affectées à la même équipe avec succès"
        }
    }

    fun incrementTaskWorker(chefId: Long, blocId: Long, taskId: Long, currentCount: Int) {
        viewModelScope.launch {
            repository.setWorkerAllocation(
                date = _selectedDate.value,
                chefId = chefId,
                blocId = blocId,
                taskId = taskId,
                workersCount = currentCount + 1
            )
        }
    }

    fun decrementTaskWorker(chefId: Long, blocId: Long, taskId: Long, currentCount: Int) {
        viewModelScope.launch {
            if (currentCount > 0) {
                repository.setWorkerAllocation(
                    date = _selectedDate.value,
                    chefId = chefId,
                    blocId = blocId,
                    taskId = taskId,
                    workersCount = currentCount - 1
                )
            }
        }
    }

    fun deleteAllocation(allocationId: Long) {
        viewModelScope.launch {
            repository.deleteAllocation(allocationId)
            _userMessage.value = "Affectation retirée"
        }
    }

    // --- DAILY REPORT ACTIONS ---
    fun saveDailyReportArchive(weather: String, notes: String, incidents: String) {
        viewModelScope.launch {
            val date = _selectedDate.value
            val totalMobilized = currentAllocations.value.sumOf { it.workersCount }
            val activeBlocs = currentAllocations.value.map { it.blocId }.distinct().size
            val activeTasks = currentAllocations.value.map { it.taskId }.distinct().size
            val summaryText = generateFullReportText(weather, notes, incidents)

            val report = DailyReport(
                date = date,
                weather = weather,
                siteManagerNotes = notes,
                incidentsOrDeliveries = incidents,
                totalWorkers = totalMobilized,
                totalBlocsActive = activeBlocs,
                totalTasksActive = activeTasks,
                reportJsonSummary = summaryText
            )
            repository.saveDailyReport(report)
            _userMessage.value = "Rapport journalier du $date enregistré dans l'historique"
        }
    }

    // Generate formatted printable/shareable text
    fun generateFullReportText(weather: String = "Ensoleillé", notes: String = "", incidents: String = ""): String {
        val date = _selectedDate.value
        val allocations = currentAllocations.value
        val totalWorkers = allocations.sumOf { it.workersCount }
        val chefs = chefsWithAllocations.value
        val blocSummaries = blocWorkerSummaries.value.filter { it.totalWorkers > 0 }

        val sb = java.lang.StringBuilder()
        sb.appendLine("==========================================")
        sb.appendLine("🏗️ RAPPORT QUOTIDIEN DE CHANTIER")
        sb.appendLine("📅 Date : $date")
        sb.appendLine("☀️ Météo : $weather")
        sb.appendLine("👷 Total Ouvriers Mobilisés : $totalWorkers")
        sb.appendLine("==========================================")
        sb.appendLine()

        sb.appendLine("🏢 RÉPARTITION PAR BLOC & CHEF D'ÉQUIPE :")
        sb.appendLine("------------------------------------------")
        if (chefs.isEmpty() || allocations.isEmpty()) {
            sb.appendLine("Aucune affectation enregistrée pour cette date.")
        } else {
            // Group allocations by Chef then Bloc
            chefs.forEach { chefWithAlloc ->
                val chef = chefWithAlloc.chef
                val chefAllocs = chefWithAlloc.allocations
                if (chefAllocs.isNotEmpty()) {
                    val allocated = chefWithAlloc.allocatedWorkers
                    sb.appendLine("👷 ${chef.name} (Spécialité : ${chef.specialty})")
                    sb.appendLine("   Capacité équipe : ${chef.totalWorkers} ouvriers | Mobilisés : $allocated/${chef.totalWorkers}")

                    val byBloc = chefAllocs.groupBy { it.blocName }
                    byBloc.forEach { (blocName, allocList) ->
                        val blocSum = allocList.sumOf { it.workersCount }
                        sb.appendLine("   📍 $blocName (Total : $blocSum ouvriers) :")
                        allocList.forEach { alloc ->
                            sb.appendLine("      • ${alloc.taskTitle} [${alloc.taskCategory}] : ${alloc.workersCount} ouvrier(s)")
                        }
                    }
                    sb.appendLine()
                }
            }
        }

        sb.appendLine("📊 SYNTHÈSE DES TÂCHES ET CORPS D'ÉTAT :")
        sb.appendLine("------------------------------------------")
        val taskCatMap = allocations.groupBy { it.taskCategory }
        taskCatMap.forEach { (cat, allocs) ->
            val sum = allocs.sumOf { it.workersCount }
            sb.appendLine("• $cat : $sum ouvriers (${allocs.size} postes)")
        }
        sb.appendLine()

        if (notes.isNotBlank()) {
            sb.appendLine("📝 OBSERVATIONS & AVANCEMENT :")
            sb.appendLine(notes)
            sb.appendLine()
        }

        if (incidents.isNotBlank()) {
            sb.appendLine("⚠️ INCIDENTS / LIVRAISONS / SÉCURITÉ :")
            sb.appendLine(incidents)
            sb.appendLine()
        }

        sb.appendLine("==========================================")
        sb.appendLine("Généré par Gestion Chantier Pro")
        return sb.toString()
    }
}
