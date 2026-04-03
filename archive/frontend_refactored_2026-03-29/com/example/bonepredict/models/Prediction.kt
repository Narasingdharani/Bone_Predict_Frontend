package com.example.bonepredict.models

import androidx.room.*
import java.util.UUID

@Entity(
    tableName = "predictions",
    foreignKeys = [
        ForeignKey(
            entity = ClinicalData::class,
            parentColumns = ["id"],
            childColumns = ["clinicalDataId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["clinicalDataId"])]
)
data class Prediction(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val clinicalDataId: String,
    val riskScore: Float,
    val riskCategory: String, // Low, Moderate, High
    val modelUsed: String = "Random Forest",
    val confidenceScore: Float,
    val resultsSummary: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    val riskLevel: String get() = riskCategory
    val date: String get() = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(createdAt))
}
