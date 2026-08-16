package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Bloc
import com.example.data.model.TaskItem
import com.example.data.model.TeamLeader
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.ConstructionGreen
import com.example.ui.theme.ConstructionRed
import com.example.ui.theme.ConstructionYellow
import com.example.ui.theme.SlateNavyCard
import com.example.ui.theme.SlateNavyDark

@Composable
fun DateSelectorBar(
    currentDate: String,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onPreviousDay,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_prev_day")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Jour précédent",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToday() }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Date du jour",
                    tint = AmberDark,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Toucher pour aujourd'hui",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            IconButton(
                onClick = onNextDay,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("btn_next_day")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Jour suivant",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (status) {
        "Terminé", "Livré" -> Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
        "En cours" -> Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
        "À faire", "Planifié" -> Pair(Color(0xFFE3F2FD), Color(0xFF1565C0))
        "Urgente", "En attente" -> Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
        else -> Pair(Color(0xFFF1F5F9), Color(0xFF475569))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = status,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PriorityBadge(priority: String, modifier: Modifier = Modifier) {
    val color = when (priority) {
        "Urgente" -> ConstructionRed
        "Haute" -> AmberDark
        "Moyenne" -> ConstructionYellow
        else -> Color(0xFF64748B)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = priority,
            color = color,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun WorkerCountStepper(
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        IconButton(
            onClick = onDecrement,
            enabled = count > 0,
            modifier = Modifier
                .size(32.dp)
                .testTag("btn_stepper_minus")
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = "Diminuer ouvriers",
                tint = if (count > 0) AmberDark else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Engineering,
                contentDescription = null,
                tint = AmberDark,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        IconButton(
            onClick = onIncrement,
            modifier = Modifier
                .size(32.dp)
                .testTag("btn_stepper_plus")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Augmenter ouvriers",
                tint = AmberDark,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// Dialog: Add / Edit Bloc
@Composable
fun AddBlocDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, code: String, desc: String, surface: String, color: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var surface by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FF9800") }

    val colors = listOf("#FF9800", "#2196F3", "#4CAF50", "#9C27B0", "#E91E63", "#00BCD4")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Apartment, contentDescription = null, tint = AmberDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nouveau Bloc de Chantier", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (code.isEmpty()) {
                            code = "BLOC-" + it.trim().take(3).uppercase()
                        }
                    },
                    label = { Text("Nom du Bloc (ex: Bloc A, Bâtiment 2)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_bloc_name")
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Code Identifiant (ex: BLOC-A)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_bloc_code")
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description (ex: Bâtiment R+5 Logements)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = surface,
                    onValueChange = { surface = it },
                    label = { Text("Surface / Étage (ex: R+4 • 1 200 m²)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    "Couleur repère :",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    colors.forEach { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColor = hex }
                                .border(
                                    width = if (selectedColor == hex) 3.dp else 1.dp,
                                    color = if (selectedColor == hex) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                )
                        ) {
                            if (selectedColor == hex) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp).align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, code, desc, surface, selectedColor)
                    }
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AmberDark),
                modifier = Modifier.testTag("btn_confirm_add_bloc")
            ) {
                Text("Ajouter le Bloc")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

// Dialog: Add Task to a Bloc
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    blocs: List<Bloc>,
    preselectedBlocId: Long?,
    onDismiss: () -> Unit,
    onConfirm: (blocId: Long, title: String, category: String, priority: String, targetDate: String, desc: String) -> Unit
) {
    var selectedBlocId by remember {
        mutableStateOf(preselectedBlocId ?: blocs.firstOrNull()?.id ?: 0L)
    }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Gros Œuvre") }
    var priority by remember { mutableStateOf("Haute") }
    var targetDate by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    val categories = listOf(
        "Gros Œuvre",
        "Ferraillage",
        "Béton",
        "Maçonnerie",
        "Électricité",
        "Plomberie",
        "Étanchéité",
        "Finitions",
        "Peinture"
    )
    val priorities = listOf("Basse", "Moyenne", "Haute", "Urgente")

    var expandedBlocDropdown by remember { mutableStateOf(false) }
    var expandedCatDropdown by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Engineering, contentDescription = null, tint = AmberDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nouvelle Tâche de Bloc", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Bloc selection dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedBlocDropdown,
                    onExpandedChange = { expandedBlocDropdown = !expandedBlocDropdown }
                ) {
                    val currentBlocName = blocs.find { it.id == selectedBlocId }?.name ?: "Sélectionner un bloc"
                    OutlinedTextField(
                        value = currentBlocName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Bloc concerné") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBlocDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedBlocDropdown,
                        onDismissRequest = { expandedBlocDropdown = false }
                    ) {
                        blocs.forEach { bloc ->
                            DropdownMenuItem(
                                text = { Text(bloc.name) },
                                onClick = {
                                    selectedBlocId = bloc.id
                                    expandedBlocDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Intitulé tâche (ex: Tâche 1 - Coffrage voiles)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_task_title")
                )

                // Category selection
                ExposedDropdownMenuBox(
                    expanded = expandedCatDropdown,
                    onExpandedChange = { expandedCatDropdown = !expandedCatDropdown }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Corps d'état / Catégorie") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCatDropdown) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCatDropdown,
                        onDismissRequest = { expandedCatDropdown = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    expandedCatDropdown = false
                                }
                            )
                        }
                    }
                }

                // Priority chips
                Column {
                    Text(
                        "Priorité :",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        priorities.forEach { p ->
                            val isSelected = priority == p
                            FilledTonalButton(
                                onClick = { priority = p },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = if (isSelected) AmberDark else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(p, fontSize = 12.sp)
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Détails / Instructions spécifiques") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && selectedBlocId > 0) {
                        onConfirm(selectedBlocId, title, category, priority, targetDate, desc)
                    }
                },
                enabled = title.isNotBlank() && selectedBlocId > 0,
                colors = ButtonDefaults.buttonColors(containerColor = AmberDark),
                modifier = Modifier.testTag("btn_confirm_add_task")
            ) {
                Text("Ajouter la Tâche")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

// Dialog: Add / Edit Team Leader (Chef d'équipe)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChefDialog(
    blocs: List<Bloc>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, specialty: String, totalWorkers: Int, defaultBlocId: Long?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("Gros Œuvre & Coffrage") }
    var totalWorkersStr by remember { mutableStateOf("10") }
    var selectedBlocId by remember { mutableStateOf<Long?>(blocs.firstOrNull()?.id) }

    val specialties = listOf(
        "Gros Œuvre & Coffrage",
        "Ferraillage & Béton Armé",
        "Maçonnerie & Cloisonnement",
        "Électricité & Réseaux",
        "Plomberie & Fluides",
        "Finitions & Peinture"
    )
    var expandedSpecialty by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = AmberDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nouveau Chef d'Équipe", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du Chef (ex: Chef Équipe 1 - Ahmed)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_chef_name")
                )

                OutlinedTextField(
                    value = totalWorkersStr,
                    onValueChange = { totalWorkersStr = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Nombre total d'ouvriers dans l'équipe") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_chef_workers")
                )

                ExposedDropdownMenuBox(
                    expanded = expandedSpecialty,
                    onExpandedChange = { expandedSpecialty = !expandedSpecialty }
                ) {
                    OutlinedTextField(
                        value = specialty,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Spécialité") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSpecialty) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedSpecialty,
                        onDismissRequest = { expandedSpecialty = false }
                    ) {
                        specialties.forEach { spec ->
                            DropdownMenuItem(
                                text = { Text(spec) },
                                onClick = {
                                    specialty = spec
                                    expandedSpecialty = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Téléphone (optionnel)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val workersCount = totalWorkersStr.toIntOrNull() ?: 10
                    if (name.isNotBlank() && workersCount > 0) {
                        onConfirm(name, phone, specialty, workersCount, selectedBlocId)
                    }
                },
                enabled = name.isNotBlank() && (totalWorkersStr.toIntOrNull() ?: 0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = AmberDark),
                modifier = Modifier.testTag("btn_confirm_add_chef")
            ) {
                Text("Enregistrer le Chef")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
