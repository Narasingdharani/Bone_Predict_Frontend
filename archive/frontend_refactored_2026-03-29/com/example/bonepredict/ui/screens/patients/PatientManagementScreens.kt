package com.example.bonepredict.ui.screens.patients

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bonepredict.ui.MainViewModel
import com.example.bonepredict.ui.components.PatientInputField
import com.example.bonepredict.ui.components.GenderDropdown

@Composable
fun PatientsScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
    val patientsFlow: kotlinx.coroutines.flow.StateFlow<List<com.example.bonepredict.data.remote.PatientWithRisk>> = viewModel?.patients ?: kotlinx.coroutines.flow.MutableStateFlow(emptyList())
    val patients by patientsFlow.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController?.navigate("add_patient") },
                containerColor = Color(0xFF4A90E2),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Add Patient")
            }
        },
        containerColor = Color(0xFFF8F9FB)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
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
                    text = "My Patients",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 20.sp),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                // Search Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by name or ID...", color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF94A3B8)) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (patients.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Group, null, modifier = Modifier.size(64.dp), tint = Color(0xFFCBD5E1))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No patients found", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF64748B)))
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(patients.filter { it.firstName.contains(searchQuery, ignoreCase = true) || it.lastName.contains(searchQuery, ignoreCase = true) }) { patientWithRisk ->
                            PatientCard(
                                name = patientWithRisk.name,
                                id = "ID: ${patientWithRisk.id}",
                                risk = if (patientWithRisk.riskLevel.isNotBlank()) patientWithRisk.riskLevel else "Not Analyzed",
                                riskColor = when (patientWithRisk.riskLevel) {
                                    "High" -> Color(0xFFEF5350)
                                    "Medium" -> Color(0xFFFB8C00)
                                    "Low" -> Color(0xFF66BB6A)
                                    else -> Color(0xFF94A3B8)
                                },
                                onClick = { navController?.navigate("patient_history/${patientWithRisk.id}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddPatientScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var isLoading by remember { mutableStateOf(false) }

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
                    text = "Add New Patient",
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
            Text("Full Name", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
            Spacer(modifier = Modifier.height(8.dp))
            PatientInputField(value = name, onValueChange = { name = it }, placeholder = "Enter patient's full name")

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Age", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                    Spacer(modifier = Modifier.height(8.dp))
                    PatientInputField(value = age, onValueChange = { age = it }, placeholder = "Years", keyboardType = KeyboardType.Number)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gender", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold))
                    Spacer(modifier = Modifier.height(8.dp))
                    GenderDropdown(selectedGender = gender, onGenderSelected = { gender = it })
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    isLoading = true
                    viewModel?.addPatient(
                        com.example.bonepredict.models.Patient(
                            id = java.util.UUID.randomUUID().toString(),
                            doctorId = viewModel?.currentUser ?: "DOC-1",
                            firstName = name.substringBefore(" "),
                            lastName = name.substringAfter(" ", ""),
                            dob = "2000-01-01",
                            gender = gender,
                            contactNumber = ""
                        )
                    ) {
                        isLoading = false
                        navController?.popBackStack()
                    }
                },
                enabled = name.isNotBlank() && age.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Create Record", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun PatientCard(name: String, id: String, risk: String?, riskColor: Color, onClick: () -> Unit) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Color(0xFFE8F1FF)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, null, tint = Color(0xFF4A90E2), modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = id, style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)))
            }
            Surface(color = riskColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                Text(
                    text = risk ?: "N/A",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(color = riskColor, fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
