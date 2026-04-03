package com.example.bonepredict.ui.screens.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bonepredict.ui.MainViewModel
import com.example.bonepredict.ui.components.HyperparameterCard
import kotlinx.coroutines.delay

@Composable
fun DataValidationScreen(navController: NavController? = null) {
    var isValidating by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000)
        isValidating = false
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
                    text = "Data Validation",
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
            if (isValidating) {
                CircularProgressIndicator(color = Color(0xFF4A90E2), modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Validating radiological data...", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF64748B)))
            } else {
                Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFFE8F5E9)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "Validation Complete", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "No anomalies detected in the CBCT scan.", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B)))
                
                Spacer(modifier = Modifier.height(48.dp))
                
                Button(
                    onClick = { navController?.navigate("feature_selection") },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
                ) {
                    Text(text = "Continue to Analysis", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
fun FeatureSelectionScreen(navController: NavController? = null) {
    AnalysisStepScreen(
        title = "Feature Selection",
        step = 1,
        totalSteps = 2,
        onBack = { navController?.popBackStack() },
        onNext = { navController?.navigate("preprocessing") }
    ) {
        Text(text = "Radiographic Features", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(16.dp))
        
        FeatureToggleItem("Trabecular Pattern", true)
        FeatureToggleItem("Cortical Bone Thickness", true)
        FeatureToggleItem("Alveolar Bone Density", true)
        FeatureToggleItem("Ridge Width Variance", false)

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(text = "Clinical Markers", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(16.dp))
        
        FeatureToggleItem("Probing Depth (3D Mapping)", true)
        FeatureToggleItem("CAL History Integration", true)
    }
}

@Composable
fun RandomForestScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
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
                    text = "Model Configuration",
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
            Text(text = "Random Forest Algorithm", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            Text(text = "High-accuracy ensemble learning for multi-factor clinical sets.", style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF64748B)))
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(text = "Hyperparameters", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HyperparameterCard("N_ESTIMATORS", "100", Modifier.weight(1f))
                HyperparameterCard("MAX_DEPTH", "15", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HyperparameterCard("MIN_SAMPLES", "2", Modifier.weight(1f))
                HyperparameterCard("CRITERION", "Gini", Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { navController?.navigate("training_model") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Text(text = "Start Model Training", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun FeatureToggleItem(label: String, isSelected: Boolean) {
    var checked by remember { mutableStateOf(isSelected) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable { checked = !checked }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF4A90E2))
        )
    }
}

@Composable
fun SelectMLScreen(navController: NavController? = null) {
    // Basic screen for choosing algorithm
    AnalysisStepScreen(
        title = "Model Selection",
        step = 1,
        totalSteps = 1,
        onBack = { navController?.popBackStack() },
        onNext = { navController?.navigate("random_forest") }
    ) {
        Text(text = "Select Prediction Engine", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(16.dp))
        
        MLModelChoiceCard("Random Forest", "94.2% Accuracy", true)
        Spacer(modifier = Modifier.height(12.dp))
        MLModelChoiceCard("Neural Network", "91.5% Accuracy", false)
        Spacer(modifier = Modifier.height(12.dp))
        MLModelChoiceCard("Logistic Regression", "86.3% Accuracy", false)
    }
}

@Composable
fun MLModelChoiceCard(title: String, accuracy: String, isSelected: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = if (isSelected) BorderStroke(2.dp, Color(0xFF4A90E2)) else null
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = accuracy, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)))
            }
            if (isSelected) {
                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4A90E2))
            }
        }
    }
}

@Composable
fun TrainingModelScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
    var progress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(50)
            progress += 0.01f
        }
        navController?.navigate("prediction_ready")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(120.dp),
            color = Color(0xFF4A90E2),
            strokeWidth = 8.dp,
            trackColor = Color(0xFFE8F1FF)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Training AI Model...", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Text(text = "${(progress * 100).toInt()}% Complete", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF64748B)))
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TrainingStat("Epochs", "150/150", Modifier.weight(1f))
            TrainingStat("Learning Rate", "0.001", Modifier.weight(1f))
        }
    }
}

@Composable
fun TrainingStat(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), color = Color.White) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B)))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun PredictionReadyScreen(navController: NavController? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier.size(96.dp).clip(CircleShape).background(Color(0xFFE8F5E9)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Bolt, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Prediction Ready!", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "The AI has finished processing the input features and the multi-factor prediction is available.", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF64748B)), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { navController?.navigate("risk_assessment") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
        ) {
            Text(text = "View Result Summary", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun PreprocessingScreen(navController: NavController? = null) {
    var progress by remember { mutableFloatStateOf(0f) }
    
    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(30)
            progress += 0.02f
        }
        navController?.navigate("select_ml")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
            color = Color(0xFF4A90E2),
            trackColor = Color(0xFFE8F1FF)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Preprocessing Data...", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
        Text(text = "${(progress * 100).toInt()}% Complete", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF64748B)))
    }
}

@Composable
fun DemographicFeaturesScreen(navController: NavController? = null) {
    // Similar to Feature Selection
    AnalysisStepScreen(
        title = "Demographic Mapping",
        step = 2,
        totalSteps = 2,
        onBack = { navController?.popBackStack() },
        onNext = { navController?.navigate("preprocessing") }
    ) {
        Text(text = "Map Features for AI", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(16.dp))
        
        FeatureToggleItem("Age Weighting", true)
        FeatureToggleItem("Lifestyle Factors", true)
        FeatureToggleItem("Socioeconomic Index", false)
    }
}
