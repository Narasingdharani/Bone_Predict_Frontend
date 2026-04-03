package com.example.bonepredict.ui.screens.patients

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bonepredict.ui.MainViewModel

@Composable
fun PatientHistoryScreen(navController: NavController? = null, viewModel: MainViewModel? = null, patientId: String) {
    val detailFlow: kotlinx.coroutines.flow.StateFlow<com.example.bonepredict.models.Patient?> = viewModel?.currentPatientDetails ?: kotlinx.coroutines.flow.MutableStateFlow(null)
    val patientDetails by detailFlow.collectAsState()
    val predictionFlow: kotlinx.coroutines.flow.StateFlow<List<com.example.bonepredict.models.Prediction>> = viewModel?.patientPredictions ?: kotlinx.coroutines.flow.MutableStateFlow(emptyList())
    val predictions by predictionFlow.collectAsState()

    LaunchedEffect(patientId) {
        viewModel?.fetchPatientDetails(patientId.toIntOrNull() ?: 0)
    }

    Scaffold(
        containerColor = Color(0xFFF8F9FB),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = { navController?.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Color(0xFF1E293B), modifier = Modifier.size(28.dp))
                }
                Text(
                    text = "Patient History",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 20.sp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Patient Basic Info
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White) {
                Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = Color(0xFFE8F1FF)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, tint = Color(0xFF4A90E2), modifier = Modifier.size(28.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = patientDetails?.let { it.firstName + " " + it.lastName } ?: "Loading...", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                        Text(text = "Patient ID: $patientId", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B)))
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "Previous Predictions", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(16.dp))

                if (predictions?.isEmpty() != false) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.History, null, modifier = Modifier.size(48.dp), tint = Color(0xFFCBD5E1))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No prediction history found", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B)))
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(predictions) { prediction ->
                            HistoryCard(
                                date = prediction.date,
                                risk = prediction.riskCategory,
                                confidence = "${prediction.confidenceScore}%",
                                onClick = {
                                    if (viewModel != null) viewModel.selectedPrediction = prediction
                                    navController?.navigate("result_summary")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryCard(date: String, risk: String, confidence: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = date, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Confidence: $confidence", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)))
                }
            }
            Surface(
                color = when (risk) {
                    "High" -> Color(0xFFEF5350)
                    "Medium" -> Color(0xFFFB8C00)
                    "Low" -> Color(0xFF66BB6A)
                    else -> Color(0xFF94A3B8)
                }.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = risk,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = when (risk) {
                            "High" -> Color(0xFFEF5350)
                            "Medium" -> Color(0xFFFB8C00)
                            "Low" -> Color(0xFF66BB6A)
                            else -> Color(0xFF94A3B8)
                        }
                    )
                )
            }
        }
    }
}
