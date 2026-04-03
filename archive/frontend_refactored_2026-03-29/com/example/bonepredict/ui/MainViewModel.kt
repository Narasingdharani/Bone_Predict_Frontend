package com.example.bonepredict.ui

import androidx.lifecycle.*
import com.example.bonepredict.models.*
import com.example.bonepredict.data.repository.BoneRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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
    var selectedPrediction by mutableStateOf<Prediction?>(null)

    // Patient Details & History
    private val _currentPatientDetails = MutableStateFlow<Patient?>(null)
    val currentPatientDetails: StateFlow<Patient?> = _currentPatientDetails
    
    private val _patientPredictions = MutableStateFlow<List<Prediction>>(emptyList())
    val patientPredictions: StateFlow<List<Prediction>> = _patientPredictions

    // Patients
    private val _patients = MutableStateFlow<List<PatientWithRisk>>(emptyList())
    val patients: StateFlow<List<PatientWithRisk>> = _patients


    fun addPatient(patient: Patient, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.addPatient(patient)
            try {
                // Sync to remote
                val savedPatient = apiService.addPatient(patient)
                val currentList = _patients.value.toMutableList()
                currentList.add(PatientWithRisk(
                    id = savedPatient.id,
                    firstName = savedPatient.firstName,
                    lastName = savedPatient.lastName,
                    dob = savedPatient.dob,
                    gender = savedPatient.gender,
                    contactNumber = savedPatient.contactNumber,
                    doctorId = savedPatient.doctorId,
                    createdAt = savedPatient.createdAt,
                    riskStatus = "Pending"
                ))
                _patients.value = currentList
                onSuccess()
            } catch (e: Exception) {
                authError = "Failed to save to server: ${e.message}"
                // Add to local list anyway so UI can proceed
                val currentList = _patients.value.toMutableList()
                currentList.add(PatientWithRisk(
                    id = patient.id,
                    firstName = patient.firstName,
                    lastName = patient.lastName,
                    dob = patient.dob,
                    gender = patient.gender,
                    contactNumber = patient.contactNumber,
                    doctorId = patient.doctorId,
                    createdAt = patient.createdAt,
                    riskStatus = "Pending"
                ))
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
            
            // Update local patients list status for dashboard
            val currentList = _patients.value.toMutableList()
            val patientId = currentNewPatient?.id
            val patientIndex = currentList.indexOfFirst { it.id == patientId }
            if (patientIndex != -1) {
                currentList[patientIndex] = currentList[patientIndex].copy(
                    riskStatus = prediction.riskCategory
                )
                _patients.value = currentList
            }

            try {
                apiService.savePrediction(prediction)
            } catch (e: Exception) {
                // Log sync error
            }
        }
    }

    // Auth State
    var currentUser by mutableStateOf<String?>(null)
    var userEmail by mutableStateOf<String?>(null)
    var doctorId by mutableStateOf<String?>(null)
    var authError by mutableStateOf<String?>(null)

    fun fetchPatients() {
        viewModelScope.launch {
            try {
                _patients.value = apiService.getPatientsWithRisk(doctorId)
            } catch (e: Exception) {
                // Keep local fallback or handle error
                authError = "Failed to fetch patients: ${e.message}"
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
                    userEmail = email.trim()
                    doctorId = response.doctorId
                    onSuccess()
                } else {
                    authError = response.error ?: "Invalid credentials"
                }
                
                // Fetch profile after successful login
                userEmail?.let { email ->
                    fetchProfile(email)
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
                if (response.name != null || response.message?.contains("successful", ignoreCase = true) == true) {
                    userEmail = email.trim()
                    doctorId = response.doctorId
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

    fun logout() {
        currentUser = null
        userEmail = null
        doctorId = null
        userProfile = null
        _patients.value = emptyList()
        resetWizard()
    }

    // Profile State
    var userProfile by mutableStateOf<UserProfile?>(null)
    val userPhone: String get() = userProfile?.phone ?: ""

    fun fetchProfile(email: String) {
        viewModelScope.launch {
            try {
                userProfile = apiService.getProfile(email)
            } catch (e: Exception) {
                // Ignore or handle
                android.util.Log.e("BonePredict", "Failed to fetch profile: ${e.message}")
            }
        }
    }

    fun updateProfile(profile: UserProfile, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.updateProfile(profile)
                if (response.message?.contains("success", ignoreCase = true) == true) {
                    userProfile = profile
                    currentUser = profile.name
                    onSuccess()
                } else {
                    authError = response.error ?: "Failed to update profile"
                }
            } catch (e: Exception) {
                authError = "Connection error: ${e.message}"
            }
        }
    }

    fun sendOtp(email: String, onComplete: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authError = null
            try {
                val response = apiService.sendOtp(OtpRequest(email.trim()))
                if (response.error == null) {
                    onSuccess()
                } else {
                    authError = response.error
                }
            } catch (e: Exception) {
                authError = "Connection error: ${e.message}"
            } finally {
                onComplete()
            }
        }
    }

    fun verifyOtp(email: String, otp: String, onComplete: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authError = null
            try {
                val response = apiService.verifyOtp(VerifyOtpRequest(email.trim(), otp.trim()))
                if (response.error == null) {
                    onSuccess()
                } else {
                    authError = response.error
                }
            } catch (e: Exception) {
                authError = "Connection error: ${e.message}"
            } finally {
                onComplete()
            }
        }
    }

    fun resetPassword(email: String, newPassword: String, onComplete: () -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            authError = null
            try {
                val response = apiService.resetPassword(ResetPasswordRequest(email.trim(), newPassword.trim()))
                if (response.error == null) {
                    onSuccess()
                } else {
                    authError = response.error
                }
            } catch (e: Exception) {
                authError = "Connection error: ${e.message}"
            } finally {
                onComplete()
            }
        }
    }

    fun resetWizard() {
        currentNewPatient = null
        currentClinicalData = null
        currentPrediction = null
    }

    suspend fun loadClinicalData(patientId: String): List<ClinicalDataDetail> {
        return try {
            apiService.getPatientClinicalData(patientId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun fetchPatientDetails(id: Int) {
        viewModelScope.launch {
            val patient = repository.getPatientById(id.toString())
            _currentPatientDetails.value = patient
            repository.getPredictionsForPatient(id.toString()).collect {
                _patientPredictions.value = it
            }
        }
    }

    suspend fun loadPredictions(clinicalDataId: String): List<PredictionDetail> {
        return try {
            apiService.getPredictions(clinicalDataId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Predictions (Example for fetching)
    fun getPredictions(clinicalDataId: String): LiveData<List<Prediction>> {
        return repository.getPredictionsForData(clinicalDataId).asLiveData()
    }

    private fun Patient.toWithRisk(riskStatus: String = "Pending"): PatientWithRisk {
        return PatientWithRisk(
            id = this.id,
            firstName = this.firstName,
            lastName = this.lastName,
            dob = this.dob,
            gender = this.gender,
            contactNumber = this.contactNumber,
            doctorId = this.doctorId,
            createdAt = this.createdAt,
            riskStatus = riskStatus
        )
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
