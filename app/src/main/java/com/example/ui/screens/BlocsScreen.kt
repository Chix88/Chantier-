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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AllocationDetail
import com.example.data.model.Bloc
import com.example.data.model.BlocWorkerSummary
import com.example.data.model.TaskItem
import com.example.ui.theme.ConstructionGreen
import com.example.ui.theme.ConstructionRed
import com.example.ui.theme.ConstructionYellow
import com.example.ui.theme.NeutralPillBg
import com.example.ui.theme.NeutralPillText
import com.example.ui.theme.OnVibrantBlueContainer
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.VibrantBlueContainer
import com.example.ui.theme.VibrantBluePrimary
import com.example.ui.theme.VibrantBlueLight
import com.example.ui.theme.VibrantPurpleDeep
import com.example.ui.theme.VibrantPurple

@Composable
fun BlocsScreen(
    blocs: List<Bloc>,
    tasks: List<TaskItem>,
    allocations: List<AllocationDetail>,
    blocSummaries: List<BlocWorkerSummary>,
    selectedBlocId: Long?,
    onSelectBloc: (Long?) -> Unit,
    onAddBlocClick: () -> Unit,
    onAddTaskClick: (Long?) -> Unit,
    onUpdateTaskStatus: (taskId: Long, status: String, percent: Int) -> Unit,
    onEditTask: (TaskItem) -> Unit,
    onMoveTaskUp: (TaskItem) -> Unit,
    onMoveTaskDown: (TaskItem) -> Unit,
    onDeleteTask: (TaskItem) -> Unit,
    onDeleteBloc: (Bloc) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredBlocs = remember(blocs, selectedBlocId) {
        if (selectedBlocId != null) {
            blocs.filter { it.id == selectedBlocId }
        } else {
            blocs
        }
    }

    val tasksByBloc = remember(tasks) { tasks.groupBy { it.blocId } }
    val allocsByBloc = remember(allocations) { allocations.groupBy { it.blocId } }
    val summariesByBloc = remember(blocSummaries) { blocSummaries.associateBy { it.blocId } }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header summary bar
            item(key = "header_summary") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Organisation des Blocs",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimaryLight
                                )
                            )
                            Text(
                                text = "${blocs.size} Blocs • ${tasks.size} Tâches enregistrées",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondaryLight
                                )
                            )
                        }

                        Button(
                            onClick = onAddBlocClick,
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantBluePrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("btn_add_bloc_header")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nouveau Bloc", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Horizontal Filter Chips for Blocs
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item(key = "chip_all") {
                            FilterChip(
                                selected = selectedBlocId == null,
                                onClick = { onSelectBloc(null) },
                                label = { Text("Tous (${blocs.size})", fontWeight = FontWeight.Medium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = VibrantBluePrimary,
                                    selectedLabelColor = Color.White,
                                    containerColor = NeutralPillBg,
                                    labelColor = NeutralPillText
                                ),
                                shape = RoundedCornerShape(100.dp),
                                border = null
                            )
                        }
                        items(blocs, key = { it.id }) { bloc ->
                            val isSelected = selectedBlocId == bloc.id
                            val blocColor = remember(bloc.colorHex) {
                                try {
                                    Color(android.graphics.Color.parseColor(bloc.colorHex))
                                } catch (_: Exception) { VibrantBluePrimary }
                            }

                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    onSelectBloc(if (isSelected) null else bloc.id)
                                },
                                label = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(if (isSelected) Color.White else blocColor, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(bloc.name, fontWeight = FontWeight.Medium)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = blocColor,
                                    selectedLabelColor = Color.White,
                                    containerColor = NeutralPillBg,
                                    labelColor = NeutralPillText
                                ),
                                shape = RoundedCornerShape(100.dp),
                                border = null
                            )
                        }
                    }
                }
            }

            // Blocs list
            if (filteredBlocs.isEmpty()) {
                item(key = "empty_blocs") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                Icons.Default.Apartment,
                                contentDescription = null,
                                tint = VibrantBluePrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Aucun bloc configuré",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Cliquez sur 'Nouveau Bloc' pour découper votre chantier en zones opérationnelles.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryLight
                            )
                        }
                    }
                }
            } else {
                items(filteredBlocs, key = { it.id }) { bloc ->
                    val blocTasks = tasksByBloc[bloc.id] ?: emptyList()
                    val blocAllocs = allocsByBloc[bloc.id] ?: emptyList()
                    val summary = summariesByBloc[bloc.id]

                    BlocCardItem(
                        bloc = bloc,
                        allBlocs = blocs,
                        tasks = blocTasks,
                        allocations = blocAllocs,
                        totalWorkers = summary?.totalWorkers ?: 0,
                        onAddTask = { onAddTaskClick(bloc.id) },
                        onUpdateTaskStatus = onUpdateTaskStatus,
                        onEditTask = onEditTask,
                        onMoveTaskUp = onMoveTaskUp,
                        onMoveTaskDown = onMoveTaskDown,
                        onDeleteTask = onDeleteTask,
                        onDeleteBloc = { onDeleteBloc(bloc) }
                    )
                }
            }
        }
    }
}

@Composable
fun BlocCardItem(
    bloc: Bloc,
    allBlocs: List<Bloc>,
    tasks: List<TaskItem>,
    allocations: List<AllocationDetail>,
    totalWorkers: Int,
    onAddTask: () -> Unit,
    onUpdateTaskStatus: (taskId: Long, status: String, percent: Int) -> Unit,
    onEditTask: (TaskItem) -> Unit,
    onMoveTaskUp: (TaskItem) -> Unit,
    onMoveTaskDown: (TaskItem) -> Unit,
    onDeleteTask: (TaskItem) -> Unit,
    onDeleteBloc: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }

    val sortedTasks = remember(tasks) { tasks.sortedBy { it.orderIndex } }

    val blocColor = remember(bloc.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(bloc.colorHex))
        } catch (_: Exception) { VibrantBluePrimary }
    }

    val completedTasksCount = remember(sortedTasks) { sortedTasks.count { it.status == "Terminé" } }
    val progressPercent = remember(sortedTasks, completedTasksCount) {
        if (sortedTasks.isNotEmpty()) (completedTasksCount * 100) / sortedTasks.size else 0
    }
    val taskAllocsMap = remember(allocations) { allocations.groupBy { it.taskId } }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineLight)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top colored indicator bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(blocColor)
            )

            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(blocColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = null,
                            tint = blocColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = bloc.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryLight
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            val isRedundantCode = bloc.code.isBlank() ||
                                bloc.name.equals(bloc.code, ignoreCase = true) ||
                                bloc.name.replace(" ", "").equals(bloc.code.replace("-", "").replace(" ", ""), ignoreCase = true)

                            if (!isRedundantCode) {
                                Surface(
                                    color = NeutralPillBg,
                                    shape = RoundedCornerShape(100.dp)
                                ) {
                                    Text(
                                        text = bloc.code,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NeutralPillText,
                                        maxLines = 1,
                                        softWrap = false,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        val metaInfo = listOf(bloc.description, bloc.surfaceInfo).filter { it.isNotBlank() }.joinToString(" • ")
                        if (metaInfo.isNotBlank()) {
                            Text(
                                text = metaInfo,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondaryLight,
                                    fontSize = 12.sp
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Mobilized workers badge
                    Surface(
                        color = if (totalWorkers > 0) VibrantBlueContainer else NeutralPillBg,
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Engineering,
                                contentDescription = null,
                                tint = if (totalWorkers > 0) OnVibrantBlueContainer else TextSecondaryLight,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "$totalWorkers ouv.",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                maxLines = 1,
                                softWrap = false,
                                color = if (totalWorkers > 0) OnVibrantBlueContainer else TextSecondaryLight
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = { showMenu = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options Bloc", modifier = Modifier.size(20.dp))
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Ajouter une tâche") },
                                onClick = {
                                    showMenu = false
                                    onAddTask()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Supprimer le Bloc", color = ConstructionRed) },
                                onClick = {
                                    showMenu = false
                                    onDeleteBloc()
                                }
                            )
                        }
                    }

                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Réduire" else "Déplier",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Progress Summary Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Avancement : $completedTasksCount/${sortedTasks.size} tâches terminées",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondaryLight
                )
                Text(
                    text = "$progressPercent%",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (progressPercent == 100) ConstructionGreen else VibrantBluePrimary
                )
            }

            LinearProgressIndicator(
                progress = { progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (progressPercent == 100) ConstructionGreen else blocColor,
                trackColor = Color(0xFFF1F4F9)
            )

            // Expandable Tasks Section
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    HorizontalDivider(color = OutlineLight.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TÂCHES DU BLOC (${sortedTasks.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextSecondaryLight,
                                letterSpacing = 0.5.sp
                            )
                        )

                        TextButton(
                            onClick = onAddTask,
                            modifier = Modifier.testTag("btn_add_task_to_bloc_${bloc.id}")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = VibrantBluePrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ajouter Tâche", fontSize = 12.sp, color = VibrantBluePrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (sortedTasks.isEmpty()) {
                        Text(
                            text = "Aucune tâche introduite pour ce bloc. Ajoutez par exemple : Coffrage, Ferraillage, Coulage béton, etc.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryLight,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        sortedTasks.forEachIndexed { index, task ->
                            val taskAllocs = taskAllocsMap[task.id] ?: emptyList()
                            key(task.id) {
                                TaskRowItem(
                                    task = task,
                                    blocs = allBlocs,
                                    allocations = taskAllocs,
                                    canMoveUp = index > 0,
                                    canMoveDown = index < sortedTasks.size - 1,
                                    onMoveUp = { onMoveTaskUp(task) },
                                    onMoveDown = { onMoveTaskDown(task) },
                                    onEditTask = onEditTask,
                                    onUpdateStatus = { newStatus, newPercent ->
                                        onUpdateTaskStatus(task.id, newStatus, newPercent)
                                    },
                                    onDelete = { onDeleteTask(task) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskRowItem(
    task: TaskItem,
    blocs: List<Bloc>,
    allocations: List<AllocationDetail>,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onEditTask: (TaskItem) -> Unit,
    onUpdateStatus: (newStatus: String, percent: Int) -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }

    val totalAssignedWorkers = remember(allocations) { allocations.sumOf { it.workersCount } }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (task.status == "Terminé") Color(0xFFF8FAFC) else Color(0xFFF1F4F9),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (task.status == "Terminé") TextSecondaryLight else TextPrimaryLight
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(100.dp)
                        ) {
                            Text(
                                text = task.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextSecondaryLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }

                        PriorityBadge(priority = task.priority)

                        StatusBadge(status = task.status)
                    }

                    if (task.workQuantity > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val completedFmt = if (task.completedQuantity % 1.0 == 0.0) task.completedQuantity.toInt().toString() else String.format(java.util.Locale.FRANCE, "%.1f", task.completedQuantity)
                        val totalFmt = if (task.workQuantity % 1.0 == 0.0) task.workQuantity.toInt().toString() else String.format(java.util.Locale.FRANCE, "%.1f", task.workQuantity)
                        val rendementFmt = if (task.rendement % 1.0 == 0.0) task.rendement.toInt().toString() else String.format(java.util.Locale.FRANCE, "%.1f", task.rendement)
                        
                        Text(
                            text = "Avancement : $completedFmt / $totalFmt ${task.workUnit}  (Rendement: $rendementFmt/j)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (task.completedQuantity >= task.workQuantity) com.example.ui.theme.ConstructionGreen else com.example.ui.theme.TextSecondaryLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Reorder buttons (Move Up / Down)
                    IconButton(
                        onClick = onMoveUp,
                        enabled = canMoveUp,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowUpward,
                            contentDescription = "Monter tâche",
                            tint = if (canMoveUp) VibrantBluePrimary else Color(0xFFCBD5E1),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onMoveDown,
                        enabled = canMoveDown,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            Icons.Default.ArrowDownward,
                            contentDescription = "Descendre tâche",
                            tint = if (canMoveDown) VibrantBluePrimary else Color(0xFFCBD5E1),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    // Total workers on this task
                    Surface(
                        color = if (totalAssignedWorkers > 0) VibrantBluePrimary else Color(0xFFC4C7C5),
                        shape = RoundedCornerShape(100.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                Icons.Default.Engineering,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$totalAssignedWorkers",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = { showOptions = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options", modifier = Modifier.size(18.dp))
                        }
                        DropdownMenu(
                            expanded = showOptions,
                            onDismissRequest = { showOptions = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Modifier la Tâche (Quantité, Rendement...)") },
                                onClick = {
                                    showOptions = false
                                    showEditDialog = true
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp), tint = VibrantBluePrimary)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Modifier Avancement (%)") },
                                onClick = {
                                    showOptions = false
                                    showDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Monter dans la liste") },
                                onClick = {
                                    showOptions = false
                                    onMoveUp()
                                },
                                enabled = canMoveUp,
                                leadingIcon = {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Descendre dans la liste") },
                                onClick = {
                                    showOptions = false
                                    onMoveDown()
                                },
                                enabled = canMoveDown,
                                leadingIcon = {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (task.status == "Terminé") "Marquer En cours" else "Marquer Terminé") },
                                onClick = {
                                    showOptions = false
                                    if (task.status == "Terminé") {
                                        onUpdateStatus("En cours", 50)
                                    } else {
                                        onUpdateStatus("Terminé", 100)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Supprimer", color = ConstructionRed) },
                                onClick = {
                                    showOptions = false
                                    onDelete()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = ConstructionRed)
                                }
                            )
                        }
                    }
                }
            }

            // Show active worker allocations per chef
            if (allocations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        tint = VibrantBluePrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = allocations.joinToString(" • ") { "${it.chefName} : ${it.workersCount} ouvrier(s)" },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondaryLight,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }

    if (showDialog) {
        TaskProgressDialog(
            task = task,
            onDismiss = { showDialog = false },
            onConfirm = { newStatus, newPercent ->
                onUpdateStatus(newStatus, newPercent)
                showDialog = false
            }
        )
    }

    if (showEditDialog) {
        EditTaskDialog(
            task = task,
            blocs = blocs,
            onDismiss = { showEditDialog = false },
            onConfirm = { updatedTask ->
                onEditTask(updatedTask)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun TaskProgressDialog(
    task: TaskItem,
    onDismiss: () -> Unit,
    onConfirm: (status: String, percent: Int) -> Unit
) {
    var percent by remember { mutableStateOf(task.completionPercent.toFloat()) }
    var status by remember { mutableStateOf(task.status) }

    val statuses = listOf("À faire", "En cours", "En attente", "Terminé")

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Avancement de la tâche", fontWeight = FontWeight.Bold, color = TextPrimaryLight)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(task.title, fontWeight = FontWeight.SemiBold, color = TextPrimaryLight)

                Text("Statut :", style = MaterialTheme.typography.labelMedium, color = TextSecondaryLight)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    statuses.forEach { s ->
                        val isSelected = status == s
                        FilledTonalButton(
                            onClick = {
                                status = s
                                if (s == "Terminé") percent = 100f
                                if (s == "À faire") percent = 0f
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = if (isSelected) VibrantBluePrimary else NeutralPillBg,
                                contentColor = if (isSelected) Color.White else NeutralPillText
                            ),
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(s, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Progression :", style = MaterialTheme.typography.labelMedium, color = TextSecondaryLight)
                    Text("${percent.toInt()}%", fontWeight = FontWeight.Bold, color = VibrantBluePrimary)
                }

                Slider(
                    value = percent,
                    onValueChange = {
                        percent = it
                        if (it >= 100f) status = "Terminé"
                        else if (it > 0f && status == "À faire") status = "En cours"
                    },
                    valueRange = 0f..100f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = VibrantBluePrimary,
                        activeTrackColor = VibrantBluePrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(status, percent.toInt()) },
                colors = ButtonDefaults.buttonColors(containerColor = VibrantBluePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Valider", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler", color = TextSecondaryLight) }
        }
    )
}
