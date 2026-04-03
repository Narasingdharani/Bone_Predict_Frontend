package com.example.bonepredict.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bonepredict.ui.MainViewModel

@Composable
fun ForgotPasswordScreen(navController: NavController? = null, viewModel: MainViewModel? = null) {
    var email by remember { mutableStateOf("") }
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
        Spacer(modifier = Modifier.height(32.dp))
        Text("Forgot Password", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E)))
        Spacer(modifier = Modifier.height(12.dp))
        Text("Enter your email address to receive a verification code.", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF74777F)))
        Spacer(modifier = Modifier.height(48.dp))
        Text("Email Address", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1C1E)), modifier = Modifier.padding(bottom = 8.dp))
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color.White) {
            TextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("doctor@hospital.com", color = Color(0xFFC4C7C5)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
        }
        if (viewModel?.authError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (viewModel.authError ?: "").toString(),
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = {
                isLoading = true
                viewModel?.sendOtp(email, onComplete = { isLoading = false }) {
                    navController?.navigate("otp_verification/$email")
                }
            },
            enabled = email.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A90E2),
                disabledContainerColor = Color(0xFFE0E0E0)
            )
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("Continue", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.White))
        }
    }
}

@Composable
fun OtpVerificationScreen(navController: NavController? = null, viewModel: MainViewModel? = null, email: String) {
    var otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController?.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Back", tint = Color(0xFF1A1C1E), modifier = Modifier.size(32.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE6F7F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = Color(0xFF2DC4B6),
                modifier = Modifier.size(40.dp).alpha(0.1f)
            )
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF2DC4B6),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Verify OTP", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E)))
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "We've sent a 6-digit verification code to your\nemail address.",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF74777F)),
            textAlign = TextAlign.Center
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF4A90E2), fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        val focusRequesters = remember { List(6) { FocusRequester() } }
        
        LaunchedEffect(Unit) {
            focusRequesters[0].requestFocus()
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            otpValues.forEachIndexed { index, value ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    TextField(
                        value = value,
                        onValueChange = { newVal ->
                            if (newVal.length <= 1) {
                                otpValues[index] = newVal
                                if (newVal.isNotEmpty() && index < 5) {
                                    focusRequesters[index + 1].requestFocus()
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .focusRequester(focusRequesters[index]),
                        textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                        singleLine = true
                    )
                }
            }
        }

        if (viewModel?.authError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (viewModel.authError ?: "").toString(),
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        TextButton(onClick = {
            isLoading = true
            viewModel?.sendOtp(email, onComplete = { isLoading = false }) {}
        }) {
            Text("Resend code", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF4A90E2)))
        }

        Spacer(modifier = Modifier.height(48.dp))
        
        val isOtpComplete = otpValues.all { it.isNotEmpty() }
        
        Button(
            onClick = {
                val otpCode = otpValues.joinToString("")
                isLoading = true
                viewModel?.verifyOtp(email, otpCode, onComplete = { isLoading = false }) {
                    navController?.navigate("reset_password/$email")
                }
            },
            enabled = isOtpComplete && !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A90E2),
                disabledContainerColor = Color(0xFF90C2F3).copy(alpha = 0.6f)
            )
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("Verify & Continue", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.White))
        }
    }
}

@Composable
fun ResetPasswordScreen(navController: NavController? = null, viewModel: MainViewModel? = null, email: String) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val hasMinLength = newPassword.length >= 8
    val hasUppercase = newPassword.any { it.isUpperCase() }
    val hasNumber = newPassword.any { it.isDigit() }
    val passwordsMatch = newPassword.isNotEmpty() && newPassword == confirmPassword

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
        
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFE8F1FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFF4A90E2),
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text("Reset Password", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E)))
        Spacer(modifier = Modifier.height(12.dp))
        Text("Create a strong new password for your\naccount.", style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF74777F)))
        
        Spacer(modifier = Modifier.height(40.dp))
        
        Text("New Password", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1C1E)), modifier = Modifier.padding(bottom = 8.dp))
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color.White) {
            TextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = { Text("Enter new password", color = Color(0xFFC4C7C5)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Confirm Password", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1C1E)), modifier = Modifier.padding(bottom = 8.dp))
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color.White) {
            TextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Re-enter password", color = Color(0xFFC4C7C5)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text("Requirements", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF74777F)), modifier = Modifier.padding(bottom = 12.dp))
        
        RequirementItem("At least 8 characters", hasMinLength)
        RequirementItem("Contains uppercase letter", hasUppercase)
        RequirementItem("Contains a number", hasNumber)
        RequirementItem("Passwords match", passwordsMatch)

        if (viewModel?.authError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = (viewModel.authError ?: "").toString(),
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(48.dp))
        
        Button(
            onClick = {
                isLoading = true
                viewModel?.resetPassword(email, newPassword, onComplete = { isLoading = false }) {
                    navController?.navigate("password_reset_success") {
                        popUpTo("signin") { inclusive = false }
                    }
                }
            },
            enabled = hasMinLength && hasUppercase && hasNumber && passwordsMatch && !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
        ) {
            if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            else Text("Reset Password", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.White))
        }
    }
}

@Composable
fun RequirementItem(text: String, isMet: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(if (isMet) Color(0xFF4CB050).copy(alpha = 0.1f) else Color(0xFF74777F).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (isMet) Color(0xFF4CB050) else Color(0xFF74777F))
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isMet) Color(0xFF1C1B1F) else Color(0xFF74777F)
            )
        )
    }
}

@Composable
fun PasswordResetSuccessScreen(navController: NavController? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF4CB050),
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Password Reset!",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1E))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Your password has been\nsuccessfully updated. You can now\nsign in with your new credentials.",
            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF74777F)),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                navController?.navigate("signin") {
                    popUpTo("signin") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
        ) {
            Text("Back to Sign In", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = Color.White))
        }
    }
}
