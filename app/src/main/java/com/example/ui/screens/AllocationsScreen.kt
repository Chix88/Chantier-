package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AllocationDetail
import com.example.data.model.Bloc
import com.example.data.model.ChefWithAllocations
import com.example.data.model.TaskItem
import com.example.data.model.TeamLeader
import com.example.ui.theme.ConstructionGreen
import com.example.ui.theme.ConstructionRed
import com.example.ui.theme.ConstructionYellow
import com.example.ui.theme.NeutralPillBg
import com.example.ui.theme.NeutralPillText
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.TextMutedLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueContainer
import com.example.ui.theme.VibrantBluePrimary
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantPurpleDeep
import com.example.ui.theme.OnVibrantBlueContainer

@Composable
fun AllocationsScreen(
    currentDate: String,
    chefsWithAllocations: List<ChefWithAllocations>,
    blocs: List<Bloc>,
    tasks: List<TaskItem>,
    totalWorkersMobilized: Int,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
    onIncrementTaskWorker: (chefId: Long, blocId: Long, taskId: Long, count: Int) -> Unit,
    onDecrementTaskWorker: (chefId: Long, blocId: Long, taskId: Long, count: Int) -> Unit,
    onSetAllocation: (chefId: Long, blocId: Long, taskId: Long, count: Int, note: String, customRendement: Double) -> Unit,
    onSetDualAllocation: (chefId: Long, blocId1: Long, taskId1: Long, rendement1: Double, blocId2: Long, taskId2: Long, rendement2: Double, count: Int, note: String) -> Unit,
    onDeleteAllocation: (Long) -> Unit,
    onAddChefClick: () -> Unit,
    onDeleteChef: (TeamLeader) -> Unit,
    onEditChefCapacity: (TeamLeader, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var assigningChef by remember { mutableStateOf<TeamLeader?>(null) }
    var editingChef by remember { mutableStateOf<TeamLeader?>(null) }
    var editingCapacityStr by remember { mutableStateOf("") }
    
    val totalAvailableCapacity = remember(chefsWithAllocations) { chefsWithAllocations.sumOf { it.chef.totalWorkers } }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Navigation Header with Vibrant Pills
            item(key = "header_date_nav") {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                    DateSelectorBar(
                        currentDate = currentDate,
                        onPreviousDay = onPreviousDay,
                        onNextDay = onNextDay,
                        onToday = onToday
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                    ) {
                        Surface(
                            color = VibrantBluePrimary,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = currentDate,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            color = NeutralPillBg,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "${blocs.size} Blocs Actifs",
                                color = NeutralPillText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            color = VibrantBlueContainer,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "$totalWorkersMobilized / $totalAvailableCapacity Ouvriers",
                                color = OnVibrantBlueContainer,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Quick Rapport Quotidien Purple Banner Card
            item(key = "banner_report_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = VibrantPurpleContainer),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0BCFF))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                .size(48.dp)
                                .background(VibrantPurpleDeep, RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Rapport Quotidien",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = VibrantPurpleDeep
                                )
                                Text(
                                    text = "Prêt pour consultation et synthèse",
                                    fontSize = 12.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }

                        Button(
                            onClick = onAddChefClick,
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPurpleDeep),
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier.testTag("btn_add_chef_screen")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nouveau Chef", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // List of Team Leaders with allocations
            if (chefsWithAllocations.isEmpty()) {
                item(key = "empty_chefs_card") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineLight)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = VibrantBluePrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Aucun chef d'équipe configuré",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimaryLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Ajoutez vos chefs d'équipe et renseignez le nombre d'ouvriers sous leur responsabilité.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryLight
                            )
                        }
                    }
                }
            } else {
                items(chefsWithAllocations, key = { it.chef.id }) { item ->
                    ChefAllocationCard(
                        chefWithAlloc = item,
                        blocs = blocs,
                        allTasks = tasks,
                        onIncrement = { alloc ->
                            onIncrementTaskWorker(
                                item.chef.id,
                                alloc.blocId,
                                alloc.taskId,
                                alloc.workersCount
                            )
                        },
                        onDecrement = { alloc ->
                            onDecrementTaskWorker(
                                item.chef.id,
                                alloc.blocId,
                                alloc.taskId,
                                alloc.workersCount
                            )
                        },
                        onDeleteAllocation = onDeleteAllocation,
                        onAssignNewTaskClick = { assigningChef = item.chef },
                        onDeleteChef = { onDeleteChef(item.chef) },
                        onEditCapacityClick = { 
                            editingChef = item.chef
                            editingCapacityStr = item.chef.totalWorkers.toString()
                        }
                    )
                }
            }
        }

        // Modal dialog to assign a new task to selected Chef
        assigningChef?.let { chef ->
            AssignTaskToChefDialog(
                chef = chef,
                blocs = blocs,
                tasks = tasks,
                onDismiss = { assigningChef = null },
                onConfirmSingle = { blocId, taskId, workersCount, note, customRendement ->
                    onSetAllocation(chef.id, blocId, taskId, workersCount, note, customRendement)
                    assigningChef = null
                },
                onConfirmDual = { blocId1, taskId1, r1, blocId2, taskId2, r2, count, note ->
                    onSetDualAllocation(chef.id, blocId1, taskId1, r1, blocId2, taskId2, r2, count, note)
                    assigningChef = null
                }
            )
        }
        
        // Modal dialog to edit chef's capacity
        editingChef?.let { chef ->
            AlertDialog(
                onDismissRequest = { editingChef = null },
                title = { Text("Modifier l'effectif de ${chef.name}") },
                text = {
                    OutlinedTextField(
                        value = editingCapacityStr,
                        onValueChange = { editingCapacityStr = it },
                        label = { Text("Capacité totale d'ouvriers") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val newCapacity = editingCapacityStr.toIntOrNull()
                            if (newCapacity != null && newCapacity >= 0) {
                                onEditChefCapacity(chef, newCapacity)
                                editingChef = null
                            }
                        }
                    ) {
                        Text("Enregistrer")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { editingChef = null }) { Text("Annuler") }
                }
            )
        }
    }
}

@Composable
fun MobilizationMetric(
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun ChefAllocationCard(
    chefWithAlloc: ChefWithAllocations,
    blocs: List<Bloc>,
    allTasks: List<TaskItem>,
    onIncrement: (AllocationDetail) -> Unit,
    onDecrement: (AllocationDetail) -> Unit,
    onDeleteAllocation: (Long) -> Unit,
    onAssignNewTaskClick: () -> Unit,
    onDeleteChef: () -> Unit,
    onEditCapacityClick: () -> Unit
) {
    val chef = chefWithAlloc.chef
    val totalAssigned = chefWithAlloc.allocatedWorkers
    val maxWorkers = chef.totalWorkers
    val remaining = chefWithAlloc.remainingWorkers

    var showMenu by remember { mutableStateOf(false) }

    val statusColor = remember(totalAssigned, maxWorkers) {
        when {
            totalAssigned == maxWorkers -> ConstructionGreen
            totalAssigned < maxWorkers -> VibrantBluePrimary
            else -> ConstructionRed // Over-allocated alert
        }
    }

    val defaultBloc = remember(blocs, chef.defaultBlocId) { blocs.find { it.id == chef.defaultBlocId } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineLight)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CHEF D'ÉQUIPE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = VibrantBluePrimary,
                            letterSpacing = 1.5.sp,
                            fontSize = 10.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = chef.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimaryLight
                        )
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "Assigné au ",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondaryLight,
                                fontSize = 13.sp
                            )
                        )
                        Text(
                            text = defaultBloc?.name ?: "Tous Blocs",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = OnVibrantBlueContainer,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Effectif Badge
                    Surface(
                        color = VibrantBlueContainer,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "EFFECTIF",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnVibrantBlueContainer,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "$totalAssigned / $maxWorkers",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = OnVibrantBlueContainer
                            )
                        }
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu Chef", tint = TextSecondaryLight)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Affecter une tâche") },
                                onClick = {
                                    showMenu = false
                                    onAssignNewTaskClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Modifier effectif") },
                                onClick = {
                                    showMenu = false
                                    onEditCapacityClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Supprimer Chef", color = ConstructionRed) },
                                onClick = {
                                    showMenu = false
                                    onDeleteChef()
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress balance indicator
            val ratio = if (maxWorkers > 0) (totalAssigned.toFloat() / maxWorkers.toFloat()).coerceIn(0f, 1f) else 0f
            LinearProgressIndicator(
                progress = { ratio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = statusColor,
                trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Allocated Tasks List (Grouped by Bloc)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (chefWithAlloc.allocations.isEmpty()) {
                    Surface(
                        color = Color(0xFFF1F4F9),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Aucune tâche affectée pour cette date. Cliquez ci-dessous pour distribuer les ${chef.totalWorkers} ouvriers.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryLight,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    chefWithAlloc.allocations.forEach { alloc ->
                        key(alloc.allocationId) {
                            AllocationRowItem(
                                alloc = alloc,
                                onIncrement = { onIncrement(alloc) },
                                onDecrement = { onDecrement(alloc) },
                                onDelete = { onDeleteAllocation(alloc.allocationId) }
                            )
                        }
                    }
                }

                // Add Task Allocation Button for this Chef
                OutlinedButton(
                    onClick = onAssignNewTaskClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .testTag("btn_assign_task_to_chef_${chef.id}"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBluePrimary)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = VibrantBluePrimary)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Affecter une tâche à ${chef.name.take(15)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VibrantBluePrimary
                    )
                }
            }
        }
    }
}

@Composable
fun AllocationRowItem(
    alloc: AllocationDetail,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF1F4F9),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = alloc.taskTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryLight,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (alloc.isSecondaryTask) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = VibrantPurpleContainer,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "2e tâche partagée",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantPurpleDeep,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else if (alloc.linkedTaskId != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = VibrantBlueContainer,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = "1re tâche partagée",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnVibrantBlueContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = alloc.blocName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = VibrantBluePrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "•",
                        color = TextSecondaryLight,
                        fontSize = 10.sp
                    )
                    Text(
                        text = alloc.taskCategory,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextSecondaryLight,
                            fontSize = 11.sp
                        )
                    )
                    if (alloc.customRendement > 0.0) {
                        Text(
                            text = "•",
                            color = TextSecondaryLight,
                            fontSize = 10.sp
                        )
                        val rFmt = if (alloc.customRendement % 1.0 == 0.0) alloc.customRendement.toInt().toString() else String.format(java.util.Locale.FRANCE, "%.1f", alloc.customRendement)
                        Text(
                            text = "Rendement ajusté : $rFmt/j",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ConstructionGreen,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Stepper with Vibrant styling
                WorkerCountStepper(
                    count = alloc.workersCount,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Supprimer affectation",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// Dialog: Assign Task (or Dual Tasks) to Chef
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignTaskToChefDialog(
    chef: TeamLeader,
    blocs: List<Bloc>,
    tasks: List<TaskItem>,
    onDismiss: () -> Unit,
    onConfirmSingle: (blocId: Long, taskId: Long, workersCount: Int, note: String, customRendement: Double) -> Unit,
    onConfirmDual: (blocId1: Long, taskId1: Long, r1: Double, blocId2: Long, taskId2: Long, r2: Double, count: Int, note: String) -> Unit
) {
    var isDualTask by remember { mutableStateOf(false) }

    // Task 1 states
    var selectedBlocId1 by remember {
        mutableStateOf(chef.defaultBlocId ?: blocs.firstOrNull()?.id ?: 0L)
    }
    val tasksForBloc1 = tasks.filter { it.blocId == selectedBlocId1 }
    var selectedTaskId1 by remember {
        mutableStateOf(tasksForBloc1.firstOrNull()?.id ?: 0L)
    }
    if (tasksForBloc1.none { it.id == selectedTaskId1 } && tasksForBloc1.isNotEmpty()) {
        selectedTaskId1 = tasksForBloc1.first().id
    }
    val task1Obj = tasks.find { it.id == selectedTaskId1 }
    var rendement1Str by remember(selectedTaskId1) {
        mutableStateOf(if ((task1Obj?.rendement ?: 0.0) > 0) "${task1Obj?.rendement}" else "")
    }

    // Task 2 states (if dual task enabled)
    var selectedBlocId2 by remember {
        mutableStateOf(chef.defaultBlocId ?: blocs.firstOrNull()?.id ?: 0L)
    }
    val tasksForBloc2 = tasks.filter { it.blocId == selectedBlocId2 }
    var selectedTaskId2 by remember {
        mutableStateOf(tasksForBloc2.filter { it.id != selectedTaskId1 }.firstOrNull()?.id ?: tasksForBloc2.firstOrNull()?.id ?: 0L)
    }
    if (tasksForBloc2.none { it.id == selectedTaskId2 } && tasksForBloc2.isNotEmpty()) {
        selectedTaskId2 = tasksForBloc2.first().id
    }
    val task2Obj = tasks.find { it.id == selectedTaskId2 }
    var rendement2Str by remember(selectedTaskId2) {
        mutableStateOf(if ((task2Obj?.rendement ?: 0.0) > 0) "${task2Obj?.rendement}" else "")
    }

    var workersCountStr by remember { mutableStateOf("2") }
    var note by remember { mutableStateOf("") }

    var expandedBloc1 by remember { mutableStateOf(false) }
    var expandedTask1 by remember { mutableStateOf(false) }
    var expandedBloc2 by remember { mutableStateOf(false) }
    var expandedTask2 by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Engineering, contentDescription = null, tint = VibrantBluePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Affecter à ${chef.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimaryLight)
            }
        },
        text = {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dual task checkbox
                item {
                    Surface(
                        color = if (isDualTask) VibrantBlueContainer else Color(0xFFF1F4F9),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { isDualTask = !isDualTask }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            androidx.compose.material3.Checkbox(
                                checked = isDualTask,
                                onCheckedChange = { isDualTask = it },
                                colors = androidx.compose.material3.CheckboxDefaults.colors(
                                    checkedColor = VibrantBluePrimary
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Affecter 2 tâches au même groupe",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isDualTask) OnVibrantBlueContainer else TextPrimaryLight
                                )
                                Text(
                                    text = "Mêmes ouvriers avec ajustement du rendement par tâche",
                                    fontSize = 11.sp,
                                    color = if (isDualTask) OnVibrantBlueContainer.copy(alpha = 0.8f) else TextSecondaryLight
                                )
                            }
                        }
                    }
                }

                // Section: Task 1
                item {
                    Text(
                        text = if (isDualTask) "PREMIÈRE TÂCHE :" else "TÂCHE À AFFECTER :",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = VibrantBluePrimary,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                item {
                    // Bloc 1 selection
                    ExposedDropdownMenuBox(
                        expanded = expandedBloc1,
                        onExpandedChange = { expandedBloc1 = !expandedBloc1 }
                    ) {
                        val currentBloc = blocs.find { it.id == selectedBlocId1 }
                        OutlinedTextField(
                            value = currentBloc?.name ?: "Sélectionner un Bloc",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Bloc (Tâche 1)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBloc1) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedBloc1,
                            onDismissRequest = { expandedBloc1 = false }
                        ) {
                            blocs.forEach { bloc ->
                                DropdownMenuItem(
                                    text = { Text(bloc.name) },
                                    onClick = {
                                        selectedBlocId1 = bloc.id
                                        val tasksThisBloc = tasks.filter { it.blocId == bloc.id }
                                        selectedTaskId1 = tasksThisBloc.firstOrNull()?.id ?: 0L
                                        expandedBloc1 = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    // Task 1 selection
                    if (tasksForBloc1.isEmpty()) {
                        Text("Aucune tâche dans ce bloc.", color = ConstructionRed, style = MaterialTheme.typography.bodySmall)
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = expandedTask1,
                            onExpandedChange = { expandedTask1 = !expandedTask1 }
                        ) {
                            val currentTask = tasksForBloc1.find { it.id == selectedTaskId1 }
                            OutlinedTextField(
                                value = currentTask?.title ?: "Sélectionner une Tâche",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tâche 1") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTask1) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedTask1,
                                onDismissRequest = { expandedTask1 = false }
                            ) {
                                tasksForBloc1.forEach { task ->
                                    DropdownMenuItem(
                                        text = { Text("${task.title} [${task.category}]") },
                                        onClick = {
                                            selectedTaskId1 = task.id
                                            expandedTask1 = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    // Rendement Task 1 input
                    OutlinedTextField(
                        value = rendement1Str,
                        onValueChange = { rendement1Str = it },
                        label = { Text("Rendement spécifique Tâche 1 (qté/ouv/j)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("input_rendement_task1")
                    )
                }

                // If Dual Task is checked, show Section 2
                if (isDualTask) {
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = OutlineLight)
                        Text(
                            text = "DEUXIÈME TÂCHE (MÊME ÉQUIPE) :",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = VibrantPurpleDeep,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }

                    item {
                        // Bloc 2 selection
                        ExposedDropdownMenuBox(
                            expanded = expandedBloc2,
                            onExpandedChange = { expandedBloc2 = !expandedBloc2 }
                        ) {
                            val currentBloc = blocs.find { it.id == selectedBlocId2 }
                            OutlinedTextField(
                                value = currentBloc?.name ?: "Sélectionner un Bloc",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Bloc (Tâche 2)") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBloc2) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = expandedBloc2,
                                onDismissRequest = { expandedBloc2 = false }
                            ) {
                                blocs.forEach { bloc ->
                                    DropdownMenuItem(
                                        text = { Text(bloc.name) },
                                        onClick = {
                                            selectedBlocId2 = bloc.id
                                            val tasksThisBloc = tasks.filter { it.blocId == bloc.id }
                                            selectedTaskId2 = tasksThisBloc.firstOrNull()?.id ?: 0L
                                            expandedBloc2 = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Task 2 selection
                        if (tasksForBloc2.isEmpty()) {
                            Text("Aucune tâche dans ce bloc.", color = ConstructionRed, style = MaterialTheme.typography.bodySmall)
                        } else {
                            ExposedDropdownMenuBox(
                                expanded = expandedTask2,
                                onExpandedChange = { expandedTask2 = !expandedTask2 }
                            ) {
                                val currentTask = tasksForBloc2.find { it.id == selectedTaskId2 }
                                OutlinedTextField(
                                    value = currentTask?.title ?: "Sélectionner Tâche 2",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Tâche 2") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTask2) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedTask2,
                                    onDismissRequest = { expandedTask2 = false }
                                ) {
                                    tasksForBloc2.forEach { task ->
                                        DropdownMenuItem(
                                            text = { Text("${task.title} [${task.category}]") },
                                            onClick = {
                                                selectedTaskId2 = task.id
                                                expandedTask2 = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        // Rendement Task 2 input
                        OutlinedTextField(
                            value = rendement2Str,
                            onValueChange = { rendement2Str = it },
                            label = { Text("Rendement spécifique Tâche 2 (qté/ouv/j)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().testTag("input_rendement_task2")
                        )
                    }
                }

                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = OutlineLight)
                }

                // Team size input
                item {
                    OutlinedTextField(
                        value = workersCountStr,
                        onValueChange = { workersCountStr = it.filter { ch -> ch.isDigit() } },
                        label = { Text(if (isDualTask) "Nombre d'ouvriers de l'équipe mobilisée" else "Nombre d'ouvriers affectés") },
                        modifier = Modifier.fillMaxWidth().testTag("input_allocated_workers"),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                item {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text("Note / Consignes (optionnel)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val count = workersCountStr.toIntOrNull() ?: 1
                    val r1 = rendement1Str.toDoubleOrNull() ?: 0.0
                    val r2 = rendement2Str.toDoubleOrNull() ?: 0.0

                    if (isDualTask) {
                        if (selectedBlocId1 > 0 && selectedTaskId1 > 0 && selectedBlocId2 > 0 && selectedTaskId2 > 0 && count > 0) {
                            onConfirmDual(selectedBlocId1, selectedTaskId1, r1, selectedBlocId2, selectedTaskId2, r2, count, note)
                        }
                    } else {
                        if (selectedBlocId1 > 0 && selectedTaskId1 > 0 && count > 0) {
                            onConfirmSingle(selectedBlocId1, selectedTaskId1, count, note, r1)
                        }
                    }
                },
                enabled = if (isDualTask) {
                    selectedBlocId1 > 0 && selectedTaskId1 > 0 && selectedBlocId2 > 0 && selectedTaskId2 > 0 && (workersCountStr.toIntOrNull() ?: 0) > 0
                } else {
                    selectedBlocId1 > 0 && selectedTaskId1 > 0 && (workersCountStr.toIntOrNull() ?: 0) > 0
                },
                colors = ButtonDefaults.buttonColors(containerColor = VibrantBluePrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_confirm_task_allocation")
            ) {
                Text("Valider l'Affectation", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler", color = TextSecondaryLight) }
        }
    )
}

