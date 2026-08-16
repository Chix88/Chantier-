package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AddBlocDialog
import com.example.ui.screens.AddChefDialog
import com.example.ui.screens.AddTaskDialog
import com.example.ui.screens.AllocationsScreen
import com.example.ui.screens.BlocsScreen
import com.example.ui.screens.DailyReportScreen
import com.example.ui.screens.HistoryReportsScreen
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateNavyCard
import com.example.ui.theme.SlateNavyDark
import com.example.ui.viewmodel.ChantierViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ChantierApp()
            }
        }
    }
}

enum class NavTab(val title: String, val testTag: String) {
    BLOCS("Blocs & Tâches", "tab_blocs"),
    ALLOCATIONS("Affectations", "tab_allocations"),
    DAILY_REPORT("Rapport Quotidien", "tab_daily_report"),
    HISTORY("Historique", "tab_history")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChantierApp(viewModel: ChantierViewModel = viewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var currentTab by remember { mutableStateOf(NavTab.ALLOCATIONS) }

    // Dialog control states
    var showAddBlocDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var preselectedBlocIdForTask by remember { mutableStateOf<Long?>(null) }
    var showAddChefDialog by remember { mutableStateOf(false) }

    // Observed state from ViewModel
    val currentDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val allBlocs by viewModel.allBlocs.collectAsStateWithLifecycle()
    val allChefs by viewModel.allChefs.collectAsStateWithLifecycle()
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val currentAllocations by viewModel.currentAllocations.collectAsStateWithLifecycle()
    val chefsWithAllocations by viewModel.chefsWithAllocations.collectAsStateWithLifecycle()
    val blocSummaries by viewModel.blocWorkerSummaries.collectAsStateWithLifecycle()
    val taskSummaries by viewModel.taskWorkerSummaries.collectAsStateWithLifecycle()
    val totalWorkersMobilized by viewModel.totalWorkersMobilized.collectAsStateWithLifecycle()
    val allDailyReports by viewModel.allDailyReports.collectAsStateWithLifecycle()
    val selectedBlocId by viewModel.selectedBlocId.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    // Handle toast messages
    LaunchedEffect(userMessage) {
        userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(AmberDark, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Engineering,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gestion Chantier",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.3.sp
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == NavTab.BLOCS,
                    onClick = { currentTab = NavTab.BLOCS },
                    icon = {
                        Icon(
                            if (currentTab == NavTab.BLOCS) Icons.Filled.Apartment else Icons.Outlined.Apartment,
                            contentDescription = "Blocs"
                        )
                    },
                    label = { Text("Blocs & Tâches", fontSize = 11.sp, fontWeight = if (currentTab == NavTab.BLOCS) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = AmberDark,
                        indicatorColor = AmberDark
                    ),
                    modifier = Modifier.testTag("tab_blocs")
                )

                NavigationBarItem(
                    selected = currentTab == NavTab.ALLOCATIONS,
                    onClick = { currentTab = NavTab.ALLOCATIONS },
                    icon = {
                        Icon(
                            if (currentTab == NavTab.ALLOCATIONS) Icons.Filled.Engineering else Icons.Outlined.Engineering,
                            contentDescription = "Affectations"
                        )
                    },
                    label = { Text("Affectations", fontSize = 11.sp, fontWeight = if (currentTab == NavTab.ALLOCATIONS) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = AmberDark,
                        indicatorColor = AmberDark
                    ),
                    modifier = Modifier.testTag("tab_allocations")
                )

                NavigationBarItem(
                    selected = currentTab == NavTab.DAILY_REPORT,
                    onClick = { currentTab = NavTab.DAILY_REPORT },
                    icon = {
                        Icon(
                            if (currentTab == NavTab.DAILY_REPORT) Icons.Filled.Description else Icons.Outlined.Description,
                            contentDescription = "Rapport"
                        )
                    },
                    label = { Text("Rapport Jour", fontSize = 11.sp, fontWeight = if (currentTab == NavTab.DAILY_REPORT) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = AmberDark,
                        indicatorColor = AmberDark
                    ),
                    modifier = Modifier.testTag("tab_daily_report")
                )

                NavigationBarItem(
                    selected = currentTab == NavTab.HISTORY,
                    onClick = { currentTab = NavTab.HISTORY },
                    icon = {
                        Icon(
                            if (currentTab == NavTab.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = "Historique"
                        )
                    },
                    label = { Text("Historique", fontSize = 11.sp, fontWeight = if (currentTab == NavTab.HISTORY) FontWeight.Bold else FontWeight.Normal) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = AmberDark,
                        indicatorColor = AmberDark
                    ),
                    modifier = Modifier.testTag("tab_history")
                )
            }
        },
        floatingActionButton = {
            when (currentTab) {
                NavTab.BLOCS -> {
                    FloatingActionButton(
                        onClick = { showAddTaskDialog = true },
                        containerColor = AmberDark,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("fab_add_task")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter Tâche")
                    }
                }
                NavTab.ALLOCATIONS -> {
                    FloatingActionButton(
                        onClick = { showAddChefDialog = true },
                        containerColor = AmberDark,
                        contentColor = Color.White,
                        modifier = Modifier.testTag("fab_add_chef")
                    ) {
                        Icon(Icons.Default.Group, contentDescription = "Ajouter Chef")
                    }
                }
                else -> {}
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavTab.BLOCS -> {
                    BlocsScreen(
                        blocs = allBlocs,
                        tasks = allTasks,
                        allocations = currentAllocations,
                        blocSummaries = blocSummaries,
                        selectedBlocId = selectedBlocId,
                        onSelectBloc = { viewModel.setSelectedBlocId(it) },
                        onAddBlocClick = { showAddBlocDialog = true },
                        onAddTaskClick = { blocId ->
                            preselectedBlocIdForTask = blocId
                            showAddTaskDialog = true
                        },
                        onUpdateTaskStatus = { taskId, status, percent ->
                            viewModel.updateTaskStatus(taskId, status, percent)
                        },
                        onDeleteTask = { viewModel.deleteTask(it) },
                        onDeleteBloc = { viewModel.deleteBloc(it) }
                    )
                }

                NavTab.ALLOCATIONS -> {
                    AllocationsScreen(
                        currentDate = currentDate,
                        chefsWithAllocations = chefsWithAllocations,
                        blocs = allBlocs,
                        tasks = allTasks,
                        totalWorkersMobilized = totalWorkersMobilized,
                        onPreviousDay = { viewModel.setDatePreviousDay() },
                        onNextDay = { viewModel.setDateNextDay() },
                        onToday = { viewModel.setDateToToday() },
                        onIncrementTaskWorker = { chefId, blocId, taskId, count ->
                            viewModel.incrementTaskWorker(chefId, blocId, taskId, count)
                        },
                        onDecrementTaskWorker = { chefId, blocId, taskId, count ->
                            viewModel.decrementTaskWorker(chefId, blocId, taskId, count)
                        },
                        onSetAllocation = { chefId, blocId, taskId, count, note ->
                            viewModel.setWorkerAllocation(chefId, blocId, taskId, count, note)
                        },
                        onDeleteAllocation = { viewModel.deleteAllocation(it) },
                        onAddChefClick = { showAddChefDialog = true },
                        onDeleteChef = { viewModel.deleteTeamLeader(it) }
                    )
                }

                NavTab.DAILY_REPORT -> {
                    DailyReportScreen(
                        currentDate = currentDate,
                        allocations = currentAllocations,
                        chefsWithAllocations = chefsWithAllocations,
                        blocSummaries = blocSummaries,
                        taskSummaries = taskSummaries,
                        totalWorkersMobilized = totalWorkersMobilized,
                        onPreviousDay = { viewModel.setDatePreviousDay() },
                        onNextDay = { viewModel.setDateNextDay() },
                        onToday = { viewModel.setDateToToday() },
                        onSaveArchive = { weather, notes, incidents ->
                            viewModel.saveDailyReportArchive(weather, notes, incidents)
                        },
                        onGenerateReportText = { weather, notes, incidents ->
                            viewModel.generateFullReportText(weather, notes, incidents)
                        },
                        onShowToast = { viewModel.showToast(it) }
                    )
                }

                NavTab.HISTORY -> {
                    HistoryReportsScreen(
                        reports = allDailyReports,
                        onSelectReportDate = { date ->
                            viewModel.setSelectedDate(date)
                            currentTab = NavTab.DAILY_REPORT
                        }
                    )
                }
            }
        }
    }

    // Dialog: Add Bloc
    if (showAddBlocDialog) {
        AddBlocDialog(
            onDismiss = { showAddBlocDialog = false },
            onConfirm = { name, code, desc, surface, color ->
                viewModel.addBloc(name, code, desc, surface, color)
                showAddBlocDialog = false
            }
        )
    }

    // Dialog: Add Task
    if (showAddTaskDialog) {
        AddTaskDialog(
            blocs = allBlocs,
            preselectedBlocId = preselectedBlocIdForTask,
            onDismiss = {
                showAddTaskDialog = false
                preselectedBlocIdForTask = null
            },
            onConfirm = { blocId, title, category, priority, targetDate, desc ->
                viewModel.addTask(blocId, title, category, priority, targetDate, desc)
                showAddTaskDialog = false
                preselectedBlocIdForTask = null
            }
        )
    }

    // Dialog: Add Team Leader (Chef d'équipe)
    if (showAddChefDialog) {
        AddChefDialog(
            blocs = allBlocs,
            onDismiss = { showAddChefDialog = false },
            onConfirm = { name, phone, specialty, totalWorkers, defaultBlocId ->
                viewModel.addTeamLeader(name, phone, specialty, totalWorkers, defaultBlocId)
                showAddChefDialog = false
            }
        )
    }
}
