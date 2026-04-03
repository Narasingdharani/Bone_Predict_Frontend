package com.example.bonepredict.ui.screens.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bonepredict.ui.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun GenerateReportScreen(navController: NavController? = null) {
    var isGenerating by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3500)
        isGenerating = false
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
                    text = "Report Generation",
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isGenerating) {
                CircularProgressIndicator(color = Color(0xFF4A90E2), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Compiling clinical findings...", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF64748B)))
            } else {
                Box(modifier = Modifier.size(96.dp).clip(CircleShape).background(Color(0xFFE8F1FF)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Description, null, tint = Color(0xFF4A90E2), modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(text = "Report Ready", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "The comprehensive clinical report has been compiled and is ready for your review.", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF64748B)), textAlign = TextAlign.Center)

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = { navController?.navigate("report_preview") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
                ) {
                    Text(text = "Preview Report", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
fun ReportPreviewScreen(navController: NavController? = null) {
    Scaffold(
        containerColor = Color(0xFFF1F5F9),
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
                    text = "Clinical Report",
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(4.dp), color = Color.White, shadowElevation = 4.dp) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "BONE PREDICT ANALYSIS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4A90E2)))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Patient Report: #BP-2024-089", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    
                    Divider(modifier = Modifier.padding(vertical = 20.dp), color = Color(0xFFF1F5F9))
                    
                    ReportSection("Patient Demographics", "Age: 45 | Gender: Male | Ethnicity: Caucasian")
                    ReportSection("Risk Assessment", "Risk Level: HIGH (85%) | Model: Random Forest (XAI)")
                    ReportSection("Principal Findings", "Significant cortical thinness detected in posterior mandible region. Trabecular spacing exceeds healthy threshold by 22%.")
                    ReportSection("Clinical Recommendation", "Socket preservation suggested. Immediate graft placement recommended post-extraction.")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { navController?.navigate("export_report") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Icon(Icons.Default.PictureAsPdf, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Export as PDF", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun ReportSection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF64748B)))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = content, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp))
    }
}

@Composable
fun ExportReportScreen(navController: NavController? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(96.dp).clip(CircleShape).background(Color(0xFFE8F5E9)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.FileDownload, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Report Exported", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "The PDF version of the clinical report has been saved to your device and shared with the patient profile.", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF64748B)), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                navController?.navigate("dashboard") {
                    popUpTo("dashboard") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
        ) {
            Text(text = "Return to Dashboard", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}
