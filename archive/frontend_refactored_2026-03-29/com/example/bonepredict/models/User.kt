package com.example.bonepredict.models

import androidx.room.*
import java.util.UUID

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val email: String,
    val role: String = "Doctor",
    val institution: String? = null,
    val licenseId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
