package com.example.bonepredict

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.bonepredict.data.local.BonePredictDatabase
import com.example.bonepredict.data.remote.FirebaseService
import com.example.bonepredict.data.repository.BoneRepository
import com.example.bonepredict.ui.MainViewModel
import com.example.bonepredict.ui.MainViewModelFactory
import com.example.bonepredict.ui.navigation.AppNavigation
import com.example.bonepredict.ui.theme.BonePredictTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Core Components
        val database = BonePredictDatabase.getDatabase(this)
        val repository = BoneRepository(
            database.patientDao(),
            database.clinicalDataDao(),
            database.predictionDao(),
            FirebaseService()
        )
        val viewModelFactory = MainViewModelFactory(repository)

        setContent {
            BonePredictTheme {
                val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}