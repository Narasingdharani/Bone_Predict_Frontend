package com.example.bonepredict.ui.screens.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bonepredict.ui.MainViewModel
import com.example.bonepredict.ui.components.RecommendationCard
import com.example.bonepredict.ui.components.ProcedureCard

@Composable
fun RecommendationsScreen(navController: NavController? = null) {
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
                    text = "Recommendations",
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
            Text(text = "Clinical Strategy", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))
            
            RecommendationCard(
                title = "Socket Preservation",
                desc = "Immediate graft placement to maintain ridge volume.",
                tag = "URGENT",
                tagColor = Color(0xFFEF5350),
                borderColor = Color(0xFFEF5350)
            ) { navController?.navigate("treatment_planning") }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            RecommendationCard(
                title = "Gingival Management",
                desc = "Deep cleaning and plaque control session.",
                tag = "3 MONTHS",
                tagColor = Color(0xFFFB8C00),
                borderColor = Color(0xFFFB8C00)
            ) { navController?.navigate("preventive_measures") }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = { navController?.navigate("generate_report") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Text(text = "Generate Clinical Report", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun PreventiveMeasuresScreen(navController: NavController? = null) {
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
                    text = "Preventive Care",
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
            Text(text = "Maintenance Protocol", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(24.dp))
            
            MeasureItem("Chlorhexidine Gluconate 0.12% Rinse")
            MeasureItem("Electric Toothbrush with Soft Bristles")
            MeasureItem("Professional Debridement (Every 3 months)")
            MeasureItem("Smoking Cessation Program")
        }
    }
}

@Composable
fun MeasureItem(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(modifier = Modifier.size(32.dp), shape = RoundedCornerShape(8.dp), color = Color(0xFFE8F5E9)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Check, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun TreatmentPlanningScreen(navController: NavController? = null) {
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
                    text = "Procedure Planning",
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
            Text(text = "Selected Option: Ridge Augmentation", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(24.dp))
            
            ProcedureCard(
                title = "Option A: Xenograft",
                desc = "Using bovine-derived bone matrix for high stability.",
                option = "Standard",
                isSelected = true,
                time = "45 Min",
                success = "96%",
                cost = "Medium"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ProcedureCard(
                title = "Option B: Autograft",
                desc = "Using patient's own bone for highest biocompatibility.",
                option = "Premium",
                isSelected = false,
                time = "90 Min",
                success = "99%",
                cost = "High"
            )
        }
    }
}
