package com.example.bonepredict.data.remote

import com.example.bonepredict.models.*
import retrofit2.http.*

interface BoneApiService {
    @POST("api/patients")
    suspend fun addPatient(@Body patient: Patient): Patient

    @GET("api/patients")
    suspend fun getPatients(@Query("doctorId") doctorId: String? = null): List<Patient>

    @POST("api/clinical-data")
    suspend fun addClinicalData(@Body data: ClinicalData): ClinicalDataResponse

    @POST("predict")
    suspend fun predict(@Body request: PredictionRequest): Prediction

    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
    @POST("api/predictions")
    suspend fun savePrediction(@Body prediction: Prediction): Prediction

    @GET("api/patients-with-risk")
    suspend fun getPatientsWithRisk(@Query("doctorId") doctorId: String? = null): List<PatientWithRisk>

    @POST("api/send-otp")
    suspend fun sendOtp(@Body request: OtpRequest): GenericResponse

    @POST("api/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): GenericResponse

    @POST("api/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): GenericResponse

    @GET("api/clinical-data/{patientId}")
    suspend fun getPatientClinicalData(@Path("patientId") patientId: String): List<ClinicalDataDetail>

    @GET("api/predictions")
    suspend fun getPredictions(@Query("clinicalDataId") clinicalDataId: String): List<PredictionDetail>

    @GET("profile")
    suspend fun getProfile(@Query("email") email: String): UserProfile

    @POST("profile/update")
    suspend fun updateProfile(@Body profile: UserProfile): GenericResponse
}

data class UserProfile(
    val name: String,
    val email: String,
    val phone: String? = null,
    val specialty: String? = null,
    val institution: String? = null,
    val license_no: String? = null
)

data class PatientWithRisk(
    val id: String,
    val firstName: String,
    val lastName: String,
    val dob: String,
    val gender: String,
    val contactNumber: String,
    val doctorId: String,
    val createdAt: Long,
    val riskStatus: String
) {
    val name: String get() = "$firstName $lastName"
    val riskLevel: String get() = riskStatus
}

data class ClinicalDataResponse(val id: String)
data class PredictionRequest(val clinicalDataId: String)
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)
data class AuthResponse(val message: String? = null, val name: String? = null, val doctorId: String? = null, val error: String? = null)

data class OtpRequest(val email: String)
data class VerifyOtpRequest(val email: String, val otp: String)
data class ResetPasswordRequest(val email: String, val password: String)
data class GenericResponse(val message: String? = null, val error: String? = null)

data class ClinicalDataDetail(
    val id: String,
    val patientId: String,
    val weight: Float?,
    val smokingStatus: String?,
    val alcoholConsumption: String?,
    val hasDiabetes: Boolean?,
    val hasHypertension: Boolean?,
    val hasOsteoporosis: Boolean?,
    val probingDepth: Float?,
    val cal: Float?,
    val bleedingOnProbing: Boolean?,
    val bleedingIndex: Float?,
    val plaqueIndex: Float?,
    val toothMobility: String?,
    val gingivalPhenotype: String?,
    val cbctImageUrl: String?,
    val createdAt: Long?
)

data class PredictionDetail(
    val id: String,
    val clinicalDataId: String,
    val riskScore: Float?,
    val riskCategory: String?,
    val modelUsed: String?,
    val confidenceScore: Float?,
    val resultsSummary: String?,
    val createdAt: Long?
) {
    val riskLevel: String? get() = riskCategory
    val date: String? get() = if (createdAt != null) java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(createdAt)) else null
}
