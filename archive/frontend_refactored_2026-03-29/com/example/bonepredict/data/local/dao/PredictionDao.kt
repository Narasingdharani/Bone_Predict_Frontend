package com.example.bonepredict.data.local.dao

import androidx.room.*
import com.example.bonepredict.models.Prediction
import kotlinx.coroutines.flow.Flow

@Dao
interface PredictionDao {
    @Query("SELECT * FROM predictions WHERE clinicalDataId = :clinicalDataId")
    fun getPredictionsForData(clinicalDataId: String): Flow<List<Prediction>>

    @Query("SELECT p.* FROM predictions p INNER JOIN clinical_data d ON p.clinicalDataId = d.id WHERE d.patientId = :patientId")
    fun getPredictionsForPatient(patientId: String): Flow<List<Prediction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: Prediction)
}
