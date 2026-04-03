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
import com.example.bonepredict.ui.components.FactorItem
import com.example.bonepredict.ui.components.MetricBar

@Composable
fun RiskAssessmentScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
    val prediction = viewModel?.selectedPrediction

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
                    text = "Risk Assessment",
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Risk Circle
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { (prediction?.riskScore ?: 0.85f) },
                    modifier = Modifier.size(200.dp),
                    color = when(prediction?.riskCategory) {
                        "High" -> Color(0xFFEF5350)
                        "Moderate", "Medium" -> Color(0xFFFB8C00)
                        else -> Color(0xFF66BB6A)
                    },
                    strokeWidth = 12.dp,
                    trackColor = when(prediction?.riskCategory) {
                        "High" -> Color(0xFFFFEBEE)
                        "Moderate", "Medium" -> Color(0xFFFFF3E0)
                        else -> Color(0xFFE8F5E9)
                    }
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${((prediction?.riskScore ?: 0.85f) * 100).toInt()}%",
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    )
                    Text(
                        text = "${prediction?.riskCategory ?: "High"} Risk",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = when(prediction?.riskCategory) {
                                "High" -> Color(0xFFEF5350)
                                "Moderate", "Medium" -> Color(0xFFFB8C00)
                                else -> Color(0xFF66BB6A)
                            },
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(text = "Key Risk Factors", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))

            FactorItem("Trabecular Pattern", "High Impact", true, "Sparse trabecular network detected in CBCT")
            FactorItem("Cortical Thickness", "Moderate", true, "1.2mm thickness below 1.5mm threshold")
            FactorItem("Systemic Markers", "Low Impact", false, "Age and BMI within normal range")

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController?.navigate("risk_explanation") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Text(text = "View Detailed Explanation", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun RiskExplanationScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
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
                    text = "AI Explanation (XAI)",
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
            Text(text = "SHAP Feature Contribution", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Text(text = "Quantitative analysis of how each clinical feature pushed the model toward 'High Risk'.", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)))
            
            Spacer(modifier = Modifier.height(32.dp))
            
            MetricBar("Cortical Thickness (mm)", "0.35 (Impact)", 0.85f, Color(0xFFEF5350))
            MetricBar("Trabecular Spacing", "0.22 (Impact)", 0.65f, Color(0xFFFB8C00))
            MetricBar("Patient Age", "0.12 (Impact)", 0.45f, Color(0xFFFFCA28))
            MetricBar("Bone Density (HU)", "0.08 (Impact)", 0.35f, Color(0xFF66BB6A))
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color(0xFFE8F1FF)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFF4A90E2))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Clinical Insight", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4A90E2)))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "The combination of decreased cortical thickness and increased trabecular spacing accounts for 57% of the total risk score.", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF1E293B), lineHeight = 20.sp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController?.navigate("bone_loss_visual") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Text(text = "Examine Bone Loss Visual", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun BoneLossVisualScreen(navController: NavController? = null) {
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
                    text = "Ridge Visualizer",
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "3D Simulation of Bone Resorption", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(24.dp))
            
            // Placeholder for Visualizer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "[ 3D BONE MODEL RENDERING ]", color = Color.White, style = MaterialTheme.typography.labelLarge)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(text = "Projected State (12 Months)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = "Estimated 2.4mm vertical bone loss and 1.2mm horizontal loss.", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B)), textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = { navController?.navigate("confidence_analysis") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Text(text = "Analyze Confidence Scores", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun ConfidenceAnalysisScreen(navController: NavController? = null) {
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
                    text = "Confidence Analysis",
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
            Text(text = "Model Reliability Index", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(text = "Our AI provides a confidence score for each prediction based on data consistency and historical accuracy.", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B)))
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 0.94f },
                        modifier = Modifier.size(160.dp),
                        color = Color(0xFF66BB6A),
                        strokeWidth = 10.dp,
                        trackColor = Color(0xFFE8F5E9)
                    )
                    Text(text = "94%", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF66BB6A)))
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Text(text = "Confidence Drivers", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))
            
            ConfidenceFactor("High Image Contrast", 0.98f)
            ConfidenceFactor("Complete Demographic Data", 0.95f)
            ConfidenceFactor("Model Consensus", 0.92f)

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController?.navigate("result_summary") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Text(text = "Proceed to Final Summary", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun ConfidenceFactor(label: String, score: Float) {
    Column(modifier = Modifier.padding(vertical = 10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(text = "${(score * 100).toInt()}%", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4A90E2)))
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { score },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = Color(0xFF4A90E2),
            trackColor = Color(0xFFE8F1FF)
        )
    }
}

@Composable
fun ResultSummaryScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
    val prediction = viewModel?.selectedPrediction

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
                    text = "Analysis Result",
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
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = Color.White) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                when(prediction?.riskCategory) {
                                    "High" -> Color(0xFFFFEBEE)
                                    "Moderate", "Medium" -> Color(0xFFFFF3E0)
                                    else -> Color(0xFFE8F5E9)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when(prediction?.riskCategory) {
                                "High" -> Icons.Default.Warning
                                "Moderate", "Medium" -> Icons.Default.Info
                                else -> Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = when(prediction?.riskCategory) {
                                "High" -> Color(0xFFEF5350)
                                "Moderate", "Medium" -> Color(0xFFFB8C00)
                                else -> Color(0xFF66BB6A)
                            },
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "${prediction?.riskCategory ?: "High"} Risk Level",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Alveolar Resorption Prediction",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B))
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SummaryStatBox("Confidence", "${prediction?.confidenceScore ?: 94.2}%", Modifier.weight(1f))
                        SummaryStatBox("Accuracy", "97.5%", Modifier.weight(1f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(text = "Clinical Actions Required", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))
            
            ActionItem("Immediate peridontal cleaning recommended")
            ActionItem("Schedule tooth extraction within 30 days")
            ActionItem("Pre-maxillary bone graft for dental implants")

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { navController?.navigate("recommendations") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Text(text = "Get Treatment Plan", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun SummaryStatBox(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), color = Color(0xFFF1F5F9)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun ActionItem(text: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF4A90E2)))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}
