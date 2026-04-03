package com.example.bonepredict.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bonepredict.ui.MainViewModel

@Composable
fun SignUpScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        IconButton(onClick = { navController?.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Back", tint = Color(0xFF1A1C1E), modifier = Modifier.size(32.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Text(
            text = "Join our clinical network and start predicting",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF74777F))
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Name Field
        AuthField(label = "Full Name", value = name, onValueChange = { name = it }, placeholder = "Dr. John Doe", icon = Icons.Default.Person)
        
        Spacer(modifier = Modifier.height(20.dp))

        // Email Field
        AuthField(label = "Email Address", value = email, onValueChange = { email = it }, placeholder = "doctor@hospital.com", icon = Icons.Default.Email, keyboardType = KeyboardType.Email)

        Spacer(modifier = Modifier.height(20.dp))

        // Phone Field
        AuthField(label = "Phone Number", value = phone, onValueChange = { phone = it }, placeholder = "+1 234 567 890", icon = Icons.Default.Phone, keyboardType = KeyboardType.Phone)

        Spacer(modifier = Modifier.height(20.dp))

        // Password Field
        AuthField(label = "Password", value = password, onValueChange = { password = it }, placeholder = "Create a strong password", icon = Icons.Default.Lock, isPassword = true)

        if (viewModel?.authError != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = viewModel.authError ?: "",
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Sign Up Button
        Button(
            onClick = {
                isLoading = true
                viewModel?.register(name, email, password, onComplete = { isLoading = false }) {
                    navController?.navigate("dashboard") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            },
            enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Already have an account? ", color = Color(0xFF74777F))
            Text(
                text = "Sign In",
                modifier = Modifier.clickable { navController?.navigate("signin") },
                style = MaterialTheme.typography.labelLarge.copy(
                    color = Color(0xFF4A90E2),
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun AuthField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1C1E)
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color.White
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = Color(0xFFC4C7C5)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                leadingIcon = { Icon(icon, null, tint = Color(0xFF4A90E2)) },
                visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                singleLine = true
            )
        }
    }
}
