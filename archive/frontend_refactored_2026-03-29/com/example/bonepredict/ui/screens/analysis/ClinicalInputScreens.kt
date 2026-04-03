package com.example.bonepredict.ui.screens.analysis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import com.example.bonepredict.ui.components.PatientInputField
import com.example.bonepredict.ui.components.GenderDropdown

@Composable
fun DemographicsScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
    var ethinicity by remember { mutableStateOf("") }
    var occupation by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }

    AnalysisStepScreen(
        title = "Demographics",
        step = 1,
        totalSteps = 3,
        onBack = { navController?.popBackStack() },
        onNext = { navController?.navigate("medical_history") }
    ) {
        Text("Ethnicity", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.height(8.dp))
        PatientInputField(value = ethinicity, onValueChange = { ethinicity = it }, placeholder = "e.g. Caucasian, Asian, etc.")

        Spacer(modifier = Modifier.height(24.dp))

        Text("Occupation", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.height(8.dp))
        PatientInputField(value = occupation, onValueChange = { occupation = it }, placeholder = "e.g. Engineer, Teacher, etc.")

        Spacer(modifier = Modifier.height(24.dp))

        Text("Education Level", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.height(8.dp))
        PatientInputField(value = education, onValueChange = { education = it }, placeholder = "e.g. Bachelor's, Master's, etc.")
    }
}

@Composable
fun MedicalHistoryScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
    var systemicDiseases by remember { mutableStateOf("") }
    var medications by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }

    AnalysisStepScreen(
        title = "Medical History",
        step = 2,
        totalSteps = 3,
        onBack = { navController?.popBackStack() },
        onNext = { navController?.navigate("peridontal_status") }
    ) {
        Text("Systemic Diseases", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.height(8.dp))
        PatientInputField(value = systemicDiseases, onValueChange = { systemicDiseases = it }, placeholder = "e.g. Diabetes, Hypertension")

        Spacer(modifier = Modifier.height(24.dp))

        Text("Current Medications", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.height(8.dp))
        PatientInputField(value = medications, onValueChange = { medications = it }, placeholder = "List current medications")

        Spacer(modifier = Modifier.height(24.dp))

        Text("Allergies", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.height(8.dp))
        PatientInputField(value = allergies, onValueChange = { allergies = it }, placeholder = "List any known allergies")
    }
}

@Composable
fun PeridontalStatusScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
    var probingDepth by remember { mutableStateOf("") }
    var cal by remember { mutableStateOf("") }
    var bleedingIndex by remember { mutableStateOf("") }

    AnalysisStepScreen(
        title = "Periodontal Status",
        step = 3,
        totalSteps = 3,
        onBack = { navController?.popBackStack() },
        onNext = { navController?.navigate("cbct") }
    ) {
        Text("Probing Depth (Average)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.height(8.dp))
        PatientInputField(value = probingDepth, onValueChange = { probingDepth = it }, placeholder = "mm")

        Spacer(modifier = Modifier.height(24.dp))

        Text("Clinical Attachment Loss (CAL)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.height(8.dp))
        PatientInputField(value = cal, onValueChange = { cal = it }, placeholder = "mm")

        Spacer(modifier = Modifier.height(24.dp))

        Text("Bleeding on Probing (%)", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
        Spacer(modifier = Modifier.height(8.dp))
        PatientInputField(value = bleedingIndex, onValueChange = { bleedingIndex = it }, placeholder = "Percentage")
    }
}

@Composable
fun AnalysisStepScreen(
    title: String,
    step: Int,
    totalSteps: Int,
    onBack: () -> Unit,
    onNext: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
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
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Color(0xFF1E293B), modifier = Modifier.size(28.dp))
                }
                Text(
                    text = title,
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
            // Step Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step $step of $totalSteps",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF4A90E2), fontWeight = FontWeight.Bold)
                )
                LinearProgressIndicator(
                    progress = { step.toFloat() / totalSteps },
                    modifier = Modifier.width(100.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF4A90E2),
                    trackColor = Color(0xFFE8F1FF)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            content()

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                Text(text = if (step == totalSteps) "Complete Input" else "Next Step", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
