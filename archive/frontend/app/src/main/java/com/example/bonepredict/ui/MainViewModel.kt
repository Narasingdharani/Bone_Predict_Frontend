package com.example.bonepredict.ui

import androidx.lifecycle.*
import com.example.bonepredict.data.model.*
import com.example.bonepredict.data.repository.BoneRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.bonepredict.data.remote.*

class MainViewModel(
    private val repository: BoneRepository,
    private val apiService: BoneApiService
) : ViewModel() {

    // Wizard State
    var currentNewPatient by mutableStateOf<Patient?>(null)
    var currentClinicalData by mutableStateOf<ClinicalData?>(null)
    var currentPrediction by mutableStateOf<Prediction?>(null)

    // Patients
    private val _patients = MutableStateFlow<List<Patient>>(emptyList())
    val patients: StateFlow<List<Patient>> = _patients

    fun fetchPatients() {
        viewModelScope.launch {
            try {
                // Use the logged-in doctor's email as the unique ID for filtering
                val docId = currentUserEmail ?: "doctor_test"
                _patients.value = apiService.getPatientsWithRisk(docId)
            } catch (e: Exception) {
                authError = "Failed to fetch patients: ${e.message}"
            }
        }
    }

    fun loadPatientReport(patientId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val report = apiService.getPatientReport(patientId)
                currentNewPatient = report.patient
                currentPrediction = report.prediction
            } catch (e: Exception) {
                authError = "Could not load report: ${e.message}"
            } finally {
                onDone()
            }
        }
    }

    fun addPatient(patient: Patient, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addPatient(patient)
            try {
                // Sync to remote
                val savedPatient = apiService.addPatient(patient)
                val currentList = _patients.value.toMutableList()
                currentList.add(savedPatient)
                _patients.value = currentList
                onSuccess()
            } catch (e: Exception) {
                authError = "Failed to save to server: ${e.message}"
                // Add to local list anyway so UI can proceed
                val currentList = _patients.value.toMutableList()
                currentList.add(patient)
                _patients.value = currentList
                onSuccess()
            }
        }
    }

    // Clinical Data
    fun addClinicalData(data: ClinicalData) {
        viewModelScope.launch {
            repository.addClinicalData(data)
            currentClinicalData = data
            try {
                apiService.addClinicalData(data)
            } catch (e: Exception) {
                // Log sync error
            }
        }
    }

    fun savePrediction(prediction: Prediction) {
        viewModelScope.launch {
            repository.addPrediction(prediction)
            currentPrediction = prediction
        }
    }

    // Auth State
    var currentUser by mutableStateOf<String?>(null)
    var currentUserEmail by mutableStateOf<String?>(null)
    var userProfile by mutableStateOf<UserProfile?>(null)
    var authError by mutableStateOf<String?>(null)

    fun fetchProfile(email: String) {
        viewModelScope.launch {
            try {
                userProfile = apiService.getProfile(email)
            } catch (e: Exception) {
                android.util.Log.e("BonePredict", "Error fetching profile: ${e.message}")
            }
        }
    }

    fun updateProfile(profile: UserProfile, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.updateProfile(profile)
                if (response.message.contains("success", ignoreCase = true)) {
                    userProfile = profile
                    currentUser = profile.name
                    onSuccess()
                } else {
                    authError = response.error ?: "Update failed"
                }
            } catch (e: Exception) {
                authError = "Connection error: ${e.message}"
            }
        }
    }

    fun login(email: String, password: String, onComplete: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authError = null
            try {
                val response = apiService.login(LoginRequest(email.trim(), password.trim()))
                if (response.name != null) {
                    currentUser = response.name
                    currentUserEmail = email.trim()
                    fetchProfile(email.trim())
                    onSuccess()
                } else {
                    authError = response.error ?: "Invalid credentials"
                }
            } catch (e: Exception) {
                authError = "Connection error: ${e.message}"
            } finally {
                onComplete()
            }
        }
    }

    fun register(name: String, email: String, password: String, onComplete: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authError = null
            try {
                android.util.Log.d("BonePredict", "Registering user: $name, $email")
                val response = apiService.register(RegisterRequest(name.trim(), email.trim(), password.trim()))
                if (response.name != null || response.message.contains("successful", ignoreCase = true)) {
                    onSuccess()
                } else {
                    authError = response.error ?: "Registration failed"
                }
            } catch (e: Exception) {
                authError = "Connection error: ${e.message}"
            } finally {
                onComplete()
            }
        }
    }

    fun sendOtp(email: String, onComplete: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authError = null
            // Mocking OTP send
            delay(1500)
            onSuccess()
            onComplete()
        }
    }

    fun resetPassword(email: String, newPassword: String, onComplete: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authError = null
            // Mocking password reset
            delay(1500)
            onSuccess()
            onComplete()
        }
    }

    fun resetWizard() {
        currentNewPatient = null
        currentClinicalData = null
        currentPrediction = null
    }

    fun logout() {
        currentUser = null
        currentUserEmail = null
        userProfile = null
        _patients.value = emptyList()
        authError = null
        resetWizard()
    }
}

class MainViewModelFactory(private val repository: BoneRepository) : ViewModelProvider.Factory {
    private val apiService: BoneApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.85.147.176:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BoneApiService::class.java)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
