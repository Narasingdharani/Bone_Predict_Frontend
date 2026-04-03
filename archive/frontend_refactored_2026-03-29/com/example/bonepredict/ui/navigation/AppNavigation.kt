package com.example.bonepredict.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bonepredict.ui.MainViewModel
import com.example.bonepredict.ui.screens.auth.*
import com.example.bonepredict.ui.screens.dashboard.*
import com.example.bonepredict.ui.screens.patients.*
import com.example.bonepredict.ui.screens.analysis.*
import com.example.bonepredict.ui.screens.settings.*

@Composable
fun AppNavigation(navController: NavHostController, viewModel: MainViewModel) {
    NavHost(navController = navController, startDestination = "splash") {
        // --- Authentication ---
        composable("splash") { SplashScreen(navController) }
        composable("welcome") { WelcomeScreen(navController) }
        composable("select_role") { SelectRoleScreen(navController) }
        composable("signin") { SignInScreen(navController, viewModel) }
        composable("signup") { SignUpScreen(navController, viewModel) }
        composable("forgot_password") { ForgotPasswordScreen(navController, viewModel) }
        composable("otp_verification/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            OtpVerificationScreen(navController, viewModel, email)
        }
        composable("reset_password/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetPasswordScreen(navController, viewModel, email)
        }
        composable("password_reset_success") { PasswordResetSuccessScreen(navController) }

        // --- Dashboard & Profile ---
        composable("dashboard") { DashboardScreen(navController, viewModel) }
        composable("profile") { ProfileScreen(navController, viewModel) }
        composable("edit_profile") { EditProfileScreen(navController, viewModel) }

        // --- Patient Management ---
        composable("patients") { PatientsScreen(navController, viewModel) }
        composable("add_patient") { AddPatientScreen(navController, viewModel) }
        composable("patient_history/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            PatientHistoryScreen(navController, viewModel, id)
        }

        // --- Clinical Analysis Flow ---
        composable("demographics") { DemographicsScreen(navController, viewModel) }
        composable("medical_history") { MedicalHistoryScreen(navController, viewModel) }
        composable("peridontal_status") { PeridontalStatusScreen(navController, viewModel) }
        composable("cbct") { CBCTScreen(navController, viewModel) }
        composable("upload_success") { UploadSuccessScreen(navController, viewModel) }
        
        // --- Prediction Flow ---
        composable("data_validation") { DataValidationScreen(navController) }
        composable("feature_selection") { FeatureSelectionScreen(navController) }
        composable("demographic_features") { DemographicFeaturesScreen(navController) }
        composable("preprocessing") { PreprocessingScreen(navController) }
        composable("select_ml") { SelectMLScreen(navController) }
        composable("random_forest") { RandomForestScreen(navController, viewModel) }
        composable("training_model") { TrainingModelScreen(navController, viewModel) }
        composable("prediction_ready") { PredictionReadyScreen(navController) }

        // --- Results & Recommendations ---
        composable("risk_assessment") { RiskAssessmentScreen(navController, viewModel) }
        composable("risk_explanation") { RiskExplanationScreen(navController, viewModel) }
        composable("bone_loss_visual") { BoneLossVisualScreen(navController) }
        composable("confidence_analysis") { ConfidenceAnalysisScreen(navController) }
        composable("result_summary") { ResultSummaryScreen(navController, viewModel) }
        composable("recommendations") { RecommendationsScreen(navController) }
        composable("preventive_measures") { PreventiveMeasuresScreen(navController) }
        composable("treatment_planning") { TreatmentPlanningScreen(navController) }

        // --- Reporting ---
        composable("generate_report") { GenerateReportScreen(navController) }
        composable("report_preview") { ReportPreviewScreen(navController) }
        composable("export_report") { ExportReportScreen(navController) }

        // --- Settings & Help ---
        composable("settings") { SettingsScreen(navController, viewModel) }
        composable("faq") { FaqScreen(navController) }
        composable("privacy") { PrivacyPolicyScreen(navController) }
        composable("terms") { TermsOfServiceScreen(navController) }
    }
}
