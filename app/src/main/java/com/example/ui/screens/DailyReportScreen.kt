package com.example.ui.screens

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AllocationDetail
import com.example.data.model.Bloc
import com.example.data.model.BlocWorkerSummary
import com.example.data.model.ChefWithAllocations
import com.example.data.model.TaskWorkerSummary
import com.example.ui.theme.ConstructionGreen
import com.example.ui.theme.ConstructionYellow
import com.example.ui.theme.NeutralPillBg
import com.example.ui.theme.NeutralPillText
import com.example.ui.theme.OutlineLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TextSecondaryLight
import com.example.ui.theme.VibrantBlue
import com.example.ui.theme.VibrantBlueContainer
import com.example.ui.theme.VibrantBluePrimary
import com.example.ui.theme.VibrantBlueLight
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantPurpleDeep
import com.example.ui.theme.OnVibrantBlueContainer

@Composable
fun DailyReportScreen(
    currentDate: String,
    allocations: List<AllocationDetail>,
    chefsWithAllocations: List<ChefWithAllocations>,
    blocSummaries: List<BlocWorkerSummary>,
    taskSummaries: List<TaskWorkerSummary>,
    totalWorkersMobilized: Int,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onToday: () -> Unit,
    onSaveArchive: (weather: String, notes: String, incidents: String) -> Unit,
    onGenerateReportText: (weather: String, notes: String, incidents: String) -> String,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var weather by remember { mutableStateOf("Ensoleillé - 23°C") }
    var notes by remember { mutableStateOf("Chantier en bonne cadence opérationnelle. Toutes les équipes sont postées.") }
    var incidents by remember { mutableStateOf("Aucun incident à signaler. Livraison matériaux conforme.") }

    val weatherOptions = listOf(
        "Ensoleillé - 23°C",
        "Nuageux - 19°C",
        "Pluvieux - 15°C",
        "Forte chaleur - 32°C",
        "Vent fort"
    )

    val activeBlocsCount = remember(blocSummaries) { blocSummaries.count { it.totalWorkers > 0 } }
    val activeTasksCount = remember(allocations) { allocations.map { it.taskId }.distinct().size }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Selector Bar
            item(key = "date_selector") {
                DateSelectorBar(
                    currentDate = currentDate,
                    onPreviousDay = onPreviousDay,
                    onNextDay = onNextDay,
                    onToday = onToday
                )
            }

            // Top Official Header Card (Vibrant Blue & Purple Theme)
            item(key = "top_header_card") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF001D36)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(VibrantBluePrimary, RoundedCornerShape(14.dp)),
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
                                        text = "Rapport de Chantier",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 17.sp
                                        )
                                    )
                                    Text(
                                        text = "Récapitulatif automatique des effectifs",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFFD3E4FF),
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }

                            Surface(
                                color = VibrantBluePrimary.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(100.dp)
                            ) {
                                Text(
                                    text = "Auto-Généré",
                                    color = Color(0xFFD3E4FF),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Key Metrics Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$totalWorkersMobilized",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFD3E4FF)
                                    )
                                )
                                Text(
                                    text = "Ouvriers Mobilisés",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8E9099))
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$activeBlocsCount",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "Blocs Actifs",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8E9099))
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$activeTasksCount",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = ConstructionGreen
                                    )
                                )
                                Text(
                                    text = "Tâches en Cours",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF8E9099))
                                )
                            }
                        }
                    }
                }
            }

            // Section 1: Detailed Breakdown by Chef & Bloc
            item(key = "section_chefs") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineLight)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "1. Répartition par Chef & par Bloc",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryLight
                                )
                            )
                            Icon(Icons.Default.Group, contentDescription = null, tint = VibrantBluePrimary)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (chefsWithAllocations.isEmpty() || allocations.isEmpty()) {
                            Text(
                                text = "Aucune affectation d'ouvriers pour le moment sur cette date.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryLight
                            )
                        } else {
                            chefsWithAllocations.forEach { chefWithAlloc ->
                                val chef = chefWithAlloc.chef
                                val chefAllocs = chefWithAlloc.allocations

                                if (chefAllocs.isNotEmpty()) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        color = Color(0xFFF1F4F9),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = chef.name,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = TextPrimaryLight
                                                )
                                                Surface(
                                                    color = VibrantBlueContainer,
                                                    shape = RoundedCornerShape(100.dp)
                                                ) {
                                                    Text(
                                                        text = "Total : ${chefWithAlloc.allocatedWorkers}/${chef.totalWorkers} ouvriers",
                                                        color = OnVibrantBlueContainer,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }

                                            Text(
                                                text = "Spécialité : ${chef.specialty}",
                                                fontSize = 11.sp,
                                                color = TextSecondaryLight
                                            )

                                            HorizontalDivider(
                                                modifier = Modifier.padding(vertical = 6.dp),
                                                color = OutlineLight.copy(alpha = 0.5f)
                                            )

                                            val byBloc = chefAllocs.groupBy { it.blocName }
                                            byBloc.forEach { (blocName, taskList) ->
                                                val blocSum = taskList.sumOf { it.workersCount }
                                                Text(
                                                    text = "📍 $blocName ($blocSum ouvriers)",
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    color = VibrantBluePrimary
                                                )
                                                taskList.forEach { alloc ->
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(start = 12.dp, top = 2.dp, bottom = 2.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text(
                                                            text = "• ${alloc.taskTitle} (${alloc.taskCategory})",
                                                            fontSize = 12.sp,
                                                            color = TextPrimaryLight
                                                        )
                                                        Text(
                                                            text = "${alloc.workersCount} ouvrier(s)",
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 12.sp,
                                                            color = VibrantBluePrimary
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Synthesis per Task Category
            item(key = "section_categories") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineLight)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "2. Répartition par Corps d'État & Tâches",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryLight
                                )
                            )
                            Icon(Icons.Default.Engineering, contentDescription = null, tint = VibrantBluePrimary)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (taskSummaries.isEmpty()) {
                            Text(
                                text = "Aucune tâche actuellement alimentée en main d'œuvre.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondaryLight
                            )
                        } else {
                            taskSummaries.forEach { summary ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(VibrantBluePrimary, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = summary.taskCategory,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = TextPrimaryLight
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "(${summary.taskCount} poste(s))",
                                            fontSize = 11.sp,
                                            color = TextSecondaryLight
                                        )
                                    }

                                    Surface(
                                        color = VibrantBlueContainer,
                                        shape = RoundedCornerShape(100.dp)
                                    ) {
                                        Text(
                                            text = "${summary.totalWorkers} ouvriers",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp,
                                            color = OnVibrantBlueContainer,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 3: Journal, Météo & Notes du Conducteur
            item(key = "section_notes") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineLight)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "3. Journal & Observations Chantier",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                        )

                        Text("Météo du jour :", style = MaterialTheme.typography.labelMedium, color = TextSecondaryLight)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            weatherOptions.take(3).forEach { option ->
                                val isSelected = weather == option
                                Surface(
                                    color = if (isSelected) VibrantBluePrimary else NeutralPillBg,
                                    shape = RoundedCornerShape(100.dp),
                                    modifier = Modifier
                                        .clickable { weather = option }
                                        .weight(1f)
                                ) {
                                    Text(
                                        text = option.substringBefore(" -"),
                                        color = if (isSelected) Color.White else NeutralPillText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("Observations du Conducteur de Travaux") },
                            modifier = Modifier.fillMaxWidth().testTag("input_report_notes"),
                            maxLines = 3,
                            shape = RoundedCornerShape(14.dp)
                        )

                        OutlinedTextField(
                            value = incidents,
                            onValueChange = { incidents = it },
                            label = { Text("Livraisons matériaux / Incidents / Sécurité") },
                            modifier = Modifier.fillMaxWidth().testTag("input_report_incidents"),
                            maxLines = 2,
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                }
            }

            // Export & Share Action Buttons
            item(key = "section_actions") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Share via Android Intent
                    Button(
                        onClick = {
                            val reportText = onGenerateReportText(weather, notes, incidents)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Rapport de Chantier - $currentDate")
                                putExtra(Intent.EXTRA_TEXT, reportText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Partager le rapport de chantier"))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = VibrantBluePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_share_daily_report")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Partager le Rapport (WhatsApp / Email / SMS)", fontWeight = FontWeight.Bold)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Copy to Clipboard
                        OutlinedButton(
                            onClick = {
                                val reportText = onGenerateReportText(weather, notes, incidents)
                                clipboardManager.setText(AnnotatedString(reportText))
                                onShowToast("Rapport copié dans le presse-papier !")
                            },
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, VibrantBluePrimary),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_copy_daily_report")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp), tint = VibrantBluePrimary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copier", fontSize = 13.sp, color = VibrantBluePrimary, fontWeight = FontWeight.SemiBold)
                        }

                        // Save Archive into Room DB
                        Button(
                            onClick = {
                                onSaveArchive(weather, notes, incidents)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = VibrantPurpleDeep),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_save_daily_report")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Archiver", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

