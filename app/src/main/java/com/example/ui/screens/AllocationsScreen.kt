package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberLight
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.ConstructionGreen
import com.example.ui.theme.ConstructionRed
import com.example.ui.theme.ConstructionYellow
import com.example.ui.theme.SlateNavyCard
import com.example.ui.theme.SlateNavyDark

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
    onSetAllocation: (chefId: Long, blocId: Long, taskId: Long, count: Int, note: String) -> Unit,
    onDeleteAllocation: (Long) -> Unit,
    onAddChefClick: () -> Unit,
    onDeleteChef: (TeamLeader) -> Unit,
    modifier: Modifier = Modifier
) {
    var assigningChef by remember { mutableStateOf<TeamLeader?>(null) }

    val totalAvailableCapacity = chefsWithAllocations.sumOf { it.chef.totalWorkers }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Navigation Header
            item {
                DateSelectorBar(
                    currentDate = currentDate,
                    onPreviousDay = onPreviousDay,
                    onNextDay = onNextDay,
                    onToday = onToday
                )
            }

            // Global Mobilization Capacity Card
            item {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = SlateNavyDark),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Affectation Opérationnelle",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Liaison Chefs d'équipe > Blocs > Tâches > Ouvriers",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                )
                            }

                            Button(
                                onClick = onAddChefClick,
                                colors = ButtonDefaults.buttonColors(containerColor = AmberDark),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("btn_add_chef_screen")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Nouveau Chef", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats counters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MobilizationMetric(
                                label = "Ouvriers Mobilisés",
                                value = "$totalWorkersMobilized",
                                color = AmberLight,
                                icon = Icons.Default.Engineering
                            )

                            MobilizationMetric(
                                label = "Capacité Totale",
                                value = "$totalAvailableCapacity",
                                color = Color.White,
                                icon = Icons.Default.Group
                            )

                            MobilizationMetric(
                                label = "Taux d'affectation",
                                value = if (totalAvailableCapacity > 0)
                                    "${(totalWorkersMobilized * 100) / totalAvailableCapacity}%"
                                else "0%",
                                color = if (totalWorkersMobilized == totalAvailableCapacity) ConstructionGreen else ConstructionYellow,
                                icon = Icons.Default.CheckCircle
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val globalRatio = if (totalAvailableCapacity > 0)
                            (totalWorkersMobilized.toFloat() / totalAvailableCapacity.toFloat()).coerceIn(0f, 1f)
                        else 0f

                        LinearProgressIndicator(
                            progress = { globalRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = AmberDark,
                            trackColor = Color(0xFF334155)
                        )
                    }
                }
            }

            // List of Team Leaders with allocations
            if (chefsWithAllocations.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
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
                                tint = AmberDark,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Aucun chef d'équipe configuré",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Ajoutez vos chefs d'équipe et renseignez le nombre d'ouvriers sous leur responsabilité.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(chefsWithAllocations) { item ->
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
                        onDeleteChef = { onDeleteChef(item.chef) }
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
                onConfirm = { blocId, taskId, workersCount, note ->
                    onSetAllocation(chef.id, blocId, taskId, workersCount, note)
                    assigningChef = null
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
    onDeleteChef: () -> Unit
) {
    val chef = chefWithAlloc.chef
    val totalAssigned = chefWithAlloc.allocatedWorkers
    val maxWorkers = chef.totalWorkers
    val remaining = chefWithAlloc.remainingWorkers

    var showMenu by remember { mutableStateOf(false) }

    val statusColor = when {
        totalAssigned == maxWorkers -> ConstructionGreen
        totalAssigned < maxWorkers -> AmberDark
        else -> ConstructionRed // Over-allocated alert
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(AmberDark.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Engineering,
                            contentDescription = null,
                            tint = AmberDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = chef.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = chef.specialty,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )
                            if (chef.phone.isNotBlank()) {
                                Text(
                                    text = " • ${chef.phone}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                        }
                    }
                }

                // Balance Badge & Menu
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = statusColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$totalAssigned / $maxWorkers ouvriers",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 12.sp,
                                color = statusColor
                            )
                            Text(
                                text = if (remaining >= 0) "$remaining dispo." else "${-remaining} en trop !",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (remaining >= 0) statusColor else ConstructionRed
                            )
                        }
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu Chef")
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

            // Progress balance indicator
            val ratio = if (maxWorkers > 0) (totalAssigned.toFloat() / maxWorkers.toFloat()).coerceIn(0f, 1f) else 0f
            LinearProgressIndicator(
                progress = { ratio },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Allocated Tasks List (Grouped by Bloc)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (chefWithAlloc.allocations.isEmpty()) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Aucune tâche affectée pour cette date. Cliquez ci-dessous pour distribuer les ${chef.totalWorkers} ouvriers sur les tâches des blocs.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    // Group by Bloc
                    val groupedByBloc = chefWithAlloc.allocations.groupBy { it.blocName }
                    groupedByBloc.forEach { (blocName, allocList) ->
                        val blocTotal = allocList.sumOf { it.workersCount }
                        val blocColorHex = allocList.firstOrNull()?.blocColorHex ?: "#FF9800"
                        val blocColor = try {
                            Color(android.graphics.Color.parseColor(blocColorHex))
                        } catch (_: Exception) { AmberPrimary }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(blocColor, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = blocName,
                                            fontWeight = FontWeight.ExtraBold,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                    Text(
                                        text = "$blocTotal ouvriers au total",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                allocList.forEach { alloc ->
                                    AllocationRowItem(
                                        alloc = alloc,
                                        onIncrement = { onIncrement(alloc) },
                                        onDecrement = { onDecrement(alloc) },
                                        onDelete = { onDeleteAllocation(alloc.allocationId) }
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
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
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Affecter une tâche de Bloc à ${chef.name.take(15)}",
                        fontSize = 13.sp,
                        color = AmberDark
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alloc.taskTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = alloc.taskCategory,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )
                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    Text(
                        text = alloc.taskStatus,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (alloc.taskStatus == "Terminé") ConstructionGreen else AmberDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Stepper [-] [N ouvriers] [+]
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
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// Dialog: Assign Task to Chef
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignTaskToChefDialog(
    chef: TeamLeader,
    blocs: List<Bloc>,
    tasks: List<TaskItem>,
    onDismiss: () -> Unit,
    onConfirm: (blocId: Long, taskId: Long, workersCount: Int, note: String) -> Unit
) {
    var selectedBlocId by remember {
        mutableStateOf(chef.defaultBlocId ?: blocs.firstOrNull()?.id ?: 0L)
    }

    val availableTasksForBloc = tasks.filter { it.blocId == selectedBlocId }

    var selectedTaskId by remember {
        mutableStateOf(availableTasksForBloc.firstOrNull()?.id ?: 0L)
    }

    // Keep selectedTaskId updated when bloc changes
    val currentBlocTasks = tasks.filter { it.blocId == selectedBlocId }
    if (currentBlocTasks.none { it.id == selectedTaskId } && currentBlocTasks.isNotEmpty()) {
        selectedTaskId = currentBlocTasks.first().id
    }

    var workersCountStr by remember { mutableStateOf("2") }
    var note by remember { mutableStateOf("") }

    var expandedBlocMenu by remember { mutableStateOf(false) }
    var expandedTaskMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Engineering, contentDescription = null, tint = AmberDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Affecter Tâche à ${chef.name}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Choisissez le bloc et la tâche à confier à cette équipe :",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Bloc selection dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedBlocMenu,
                    onExpandedChange = { expandedBlocMenu = !expandedBlocMenu }
                ) {
                    val currentBloc = blocs.find { it.id == selectedBlocId }
                    OutlinedTextField(
                        value = currentBloc?.name ?: "Sélectionner un Bloc",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Bloc") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBlocMenu) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedBlocMenu,
                        onDismissRequest = { expandedBlocMenu = false }
                    ) {
                        blocs.forEach { bloc ->
                            DropdownMenuItem(
                                text = { Text(bloc.name) },
                                onClick = {
                                    selectedBlocId = bloc.id
                                    val tasksForThisBloc = tasks.filter { it.blocId == bloc.id }
                                    selectedTaskId = tasksForThisBloc.firstOrNull()?.id ?: 0L
                                    expandedBlocMenu = false
                                }
                            )
                        }
                    }
                }

                // Task selection dropdown
                if (currentBlocTasks.isEmpty()) {
                    Text(
                        "Aucune tâche disponible dans ce bloc.",
                        color = ConstructionRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    ExposedDropdownMenuBox(
                        expanded = expandedTaskMenu,
                        onExpandedChange = { expandedTaskMenu = !expandedTaskMenu }
                    ) {
                        val currentTask = currentBlocTasks.find { it.id == selectedTaskId }
                        OutlinedTextField(
                            value = currentTask?.title ?: "Sélectionner une Tâche",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Tâche à accomplir") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTaskMenu) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTaskMenu,
                            onDismissRequest = { expandedTaskMenu = false }
                        ) {
                            currentBlocTasks.forEach { task ->
                                DropdownMenuItem(
                                    text = { Text("${task.title} [${task.category}]") },
                                    onClick = {
                                        selectedTaskId = task.id
                                        expandedTaskMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Workers Count Input
                OutlinedTextField(
                    value = workersCountStr,
                    onValueChange = { workersCountStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Nombre d'ouvriers affectés sur cette tâche") },
                    modifier = Modifier.fillMaxWidth().testTag("input_allocated_workers"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note / Instructions spéciales (optionnel)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val count = workersCountStr.toIntOrNull() ?: 1
                    if (selectedBlocId > 0 && selectedTaskId > 0 && count > 0) {
                        onConfirm(selectedBlocId, selectedTaskId, count, note)
                    }
                },
                enabled = selectedBlocId > 0 && selectedTaskId > 0 && (workersCountStr.toIntOrNull() ?: 0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = AmberDark),
                modifier = Modifier.testTag("btn_confirm_task_allocation")
            ) {
                Text("Valider l'Affectation")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
