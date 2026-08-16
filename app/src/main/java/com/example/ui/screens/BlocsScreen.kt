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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
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
import com.example.data.model.BlocWorkerSummary
import com.example.data.model.TaskItem
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.ConstructionGreen
import com.example.ui.theme.ConstructionRed
import com.example.ui.theme.ConstructionYellow
import com.example.ui.theme.SlateNavyCard
import com.example.ui.theme.SlateNavyDark

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
    onDeleteTask: (TaskItem) -> Unit,
    onDeleteBloc: (Bloc) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredBlocs = if (selectedBlocId != null) {
        blocs.filter { it.id == selectedBlocId }
    } else {
        blocs
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header summary bar
            item {
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
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            )
                            Text(
                                text = "${blocs.size} Blocs • ${tasks.size} Tâches enregistrées",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Button(
                            onClick = onAddBlocClick,
                            colors = ButtonDefaults.buttonColors(containerColor = AmberDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_add_bloc_header")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nouveau Bloc", fontSize = 13.sp)
                        }
                    }

                    // Horizontal Filter Chips for Blocs
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedBlocId == null,
                                onClick = { onSelectBloc(null) },
                                label = { Text("Tous les Blocs (${blocs.size})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AmberDark,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(blocs) { bloc ->
                            val isSelected = selectedBlocId == bloc.id
                            val blocColor = try {
                                Color(android.graphics.Color.parseColor(bloc.colorHex))
                            } catch (_: Exception) { AmberPrimary }

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
                                                .background(blocColor, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(bloc.name)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = blocColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Blocs list
            if (filteredBlocs.isEmpty()) {
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
                                Icons.Default.Apartment,
                                contentDescription = null,
                                tint = AmberDark,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Aucun bloc configuré",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Cliquez sur 'Nouveau Bloc' pour découper votre chantier en zones opérationnelles.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredBlocs) { bloc ->
                    val blocTasks = tasks.filter { it.blocId == bloc.id }
                    val blocAllocs = allocations.filter { it.blocId == bloc.id }
                    val summary = blocSummaries.find { it.blocId == bloc.id }

                    BlocCardItem(
                        bloc = bloc,
                        tasks = blocTasks,
                        allocations = blocAllocs,
                        totalWorkers = summary?.totalWorkers ?: 0,
                        onAddTask = { onAddTaskClick(bloc.id) },
                        onUpdateTaskStatus = onUpdateTaskStatus,
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
    tasks: List<TaskItem>,
    allocations: List<AllocationDetail>,
    totalWorkers: Int,
    onAddTask: () -> Unit,
    onUpdateTaskStatus: (taskId: Long, status: String, percent: Int) -> Unit,
    onDeleteTask: (TaskItem) -> Unit,
    onDeleteBloc: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }

    val blocColor = try {
        Color(android.graphics.Color.parseColor(bloc.colorHex))
    } catch (_: Exception) { AmberPrimary }

    val completedTasksCount = tasks.count { it.status == "Terminé" }
    val progressPercent = if (tasks.isNotEmpty()) (completedTasksCount * 100) / tasks.size else 0

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top colored indicator bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(blocColor)
            )

            // Header Row
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
                            .size(42.dp)
                            .background(blocColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = null,
                            tint = blocColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = bloc.name,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = bloc.code,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        if (bloc.description.isNotBlank() || bloc.surfaceInfo.isNotBlank()) {
                            Text(
                                text = listOf(bloc.description, bloc.surfaceInfo).filter { it.isNotBlank() }.joinToString(" • "),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Mobilized workers badge
                    Surface(
                        color = if (totalWorkers > 0) AmberDark.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.Engineering,
                                contentDescription = null,
                                tint = if (totalWorkers > 0) AmberDark else Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$totalWorkers ouvriers",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (totalWorkers > 0) AmberDark else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Options Bloc")
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
                        onClick = { isExpanded = !isExpanded }
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Réduire" else "Déplier"
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
                    text = "Avancement : $completedTasksCount/${tasks.size} tâches terminées",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$progressPercent%",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (progressPercent == 100) ConstructionGreen else AmberDark
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
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Expandable Tasks Section
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TÂCHES DU BLOC (${tasks.size})",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                        )

                        TextButton(
                            onClick = onAddTask,
                            modifier = Modifier.testTag("btn_add_task_to_bloc_${bloc.id}")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ajouter Tâche", fontSize = 12.sp, color = AmberDark)
                        }
                    }

                    if (tasks.isEmpty()) {
                        Text(
                            text = "Aucune tâche introduite pour ce bloc. Ajoutez par exemple : Coffrage, Ferraillage, Coulage béton, etc.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    } else {
                        tasks.forEach { task ->
                            val taskAllocs = allocations.filter { it.taskId == task.id }
                            TaskRowItem(
                                task = task,
                                allocations = taskAllocs,
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

@Composable
fun TaskRowItem(
    task: TaskItem,
    allocations: List<AllocationDetail>,
    onUpdateStatus: (newStatus: String, percent: Int) -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var showOptions by remember { mutableStateOf(false) }

    val totalAssignedWorkers = allocations.sumOf { it.workersCount }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (task.status == "Terminé")
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(12.dp)
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
                            color = if (task.status == "Terminé") MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = task.category,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        PriorityBadge(priority = task.priority)

                        StatusBadge(status = task.status)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Total workers on this task
                    Surface(
                        color = if (totalAssignedWorkers > 0) AmberDark else Color.Gray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
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
                                text = { Text("Modifier Avancement") },
                                onClick = {
                                    showOptions = false
                                    showDialog = true
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
                        tint = AmberDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = allocations.joinToString(" • ") { "${it.chefName} : ${it.workersCount} ouvrier(s)" },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            Text("Avancement de la tâche", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(task.title, fontWeight = FontWeight.SemiBold)

                Text("Statut :", style = MaterialTheme.typography.labelMedium)
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
                                containerColor = if (isSelected) AmberDark else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text(s, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Progression :", style = MaterialTheme.typography.labelMedium)
                    Text("${percent.toInt()}%", fontWeight = FontWeight.Bold, color = AmberDark)
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
                        thumbColor = AmberDark,
                        activeTrackColor = AmberDark
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(status, percent.toInt()) },
                colors = ButtonDefaults.buttonColors(containerColor = AmberDark)
            ) {
                Text("Valider")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}
