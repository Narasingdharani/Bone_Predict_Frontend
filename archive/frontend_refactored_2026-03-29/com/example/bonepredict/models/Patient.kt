package com.example.bonepredict.models

import androidx.room.*
import java.util.UUID

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey
    val id: String = "P-${System.currentTimeMillis() % 1000000}",
    val firstName: String,
    val lastName: String,
    val dob: String,
    val gender: String,
    val contactNumber: String,
    val doctorId: String, // Link to User
    val createdAt: Long = System.currentTimeMillis()
) {
    val name: String @Ignore get() = "$firstName $lastName"
}
