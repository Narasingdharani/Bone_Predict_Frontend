package com.example.bonepredict.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.text.style.TextAlign
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bonepredict.ui.MainViewModel
import com.example.bonepredict.ui.components.FaqItem
import com.example.bonepredict.ui.components.SettingNavigationItem

@Composable
fun SettingsScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
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
                    text = "Settings",
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
            Text(text = "App Preferences", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF64748B)))
            Spacer(modifier = Modifier.height(16.dp))
            
            SettingSwitchItem("Biometric Login", true, Icons.Default.Fingerprint)
            SettingSwitchItem("Dark Mode", false, Icons.Default.DarkMode)
            SettingSwitchItem("Push Notifications", true, Icons.Default.Notifications)

            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Support & Legal", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF64748B)))
            Spacer(modifier = Modifier.height(16.dp))
            
            SettingNavigationItem(icon = Icons.Default.Help, label = "Frequently Asked Questions", iconBg = Color(0xFFF1F5F9), iconTint = Color(0xFF4A90E2)) { navController?.navigate("faq") }
            SettingNavigationItem(icon = Icons.Default.Security, label = "Privacy Policy", iconBg = Color(0xFFF1F5F9), iconTint = Color(0xFF4A90E2)) { navController?.navigate("privacy") }
            SettingNavigationItem(icon = Icons.Default.Description, label = "Terms of Service", iconBg = Color(0xFFF1F5F9), iconTint = Color(0xFF4A90E2)) { navController?.navigate("terms") }
            SettingNavigationItem(icon = Icons.Default.SupportAgent, label = "Contact Support", iconBg = Color(0xFFF1F5F9), iconTint = Color(0xFF4A90E2)) { }

            Spacer(modifier = Modifier.height(48.dp))
            
            Text(text = "Version 2.1.0 (Build 450)", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8)))
        }
    }
}

@Composable
fun SettingSwitchItem(label: String, isEnabled: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    var checked by remember { mutableStateOf(isEnabled) }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF4A90E2))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = checked, onCheckedChange = { checked = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF4A90E2)))
        }
    }
}

@Composable
fun FaqScreen(navController: NavController? = null) {
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
                    text = "Help Center",
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
            FaqItem("How accurate is BonePredict?", "Our models are trained on over 50,000 clinically validated CBCT scans, achieving an accuracy rate of 94.2% on diverse patient demographics.")
            FaqItem("What file formats are supported?", "We currently support DICOM (standard clinical format), as well as high-resolution JPEG and PNG images for preliminary analysis.")
            FaqItem("Is patient data secure?", "Yes, all data is encrypted end-to-end using AES-256 standards and comply with international HIPAA regulations.")
            FaqItem("How can I export the reports?", "Reports can be exported as high-fidelity PDF documents directly from the Result Summary screen.")
        }
    }
}

@Composable
fun PrivacyPolicyScreen(navController: NavController? = null) {
    LegalTextScreen(navController, "Privacy Policy", "At BonePredict, we prioritize the confidentiality of clinical data...")
}

@Composable
fun TermsOfServiceScreen(navController: NavController? = null) {
    LegalTextScreen(navController, "Terms of Service", "By using BonePredict, you agree to the following clinical protocols...")
}

@Composable
fun LegalTextScreen(navController: NavController?, title: String, content: String) {
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
                Text(text = title, modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), fontSize = 20.sp))
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp).verticalScroll(rememberScrollState())) {
            Text(text = content, style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp, color = Color(0xFF1E293B)))
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Last updated: March 2024", style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8)))
        }
    }
}
