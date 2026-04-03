package com.example.bonepredict.data.remote

import com.example.bonepredict.data.model.*
import retrofit2.http.*

interface BoneApiService {
    @POST("api/patients")
    suspend fun addPatient(@Body patient: Patient): Patient

    @GET("api/patients")
    suspend fun getPatients(): List<Patient>

    @GET("api/patients-with-risk")
    suspend fun getPatientsWithRisk(@Query("doctorId") doctorId: String): List<Patient>

    @GET("api/patients/{patientId}/report")
    suspend fun getPatientReport(@retrofit2.http.Path("patientId") patientId: String): PatientReportResponse

    @GET("api/clinical-data/{patientId}")
    suspend fun getClinicalDataByPatient(@retrofit2.http.Path("patientId") patientId: String): List<ClinicalData>

    @POST("api/clinical-data")
    suspend fun addClinicalData(@Body data: ClinicalData): ClinicalDataResponse

    @POST("predict")
    suspend fun predict(@Body request: PredictionRequest): Prediction

    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("profile")
    suspend fun getProfile(@Query("email") email: String): UserProfile

    @POST("profile/update")
    suspend fun updateProfile(@Body profile: UserProfile): AuthResponse
}

data class UserProfile(
    val name: String,
    val email: String,
    val phone: String? = null,
    val specialty: String? = null,
    val institution: String? = null,
    val license_no: String? = null
)

data class PatientReportResponse(
    val patient: Patient,
    val prediction: com.example.bonepredict.data.model.Prediction?
)

data class ClinicalDataResponse(val id: String)
data class PredictionRequest(val clinicalDataId: String)
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)
data class AuthResponse(val message: String, val name: String? = null, val error: String? = null)
