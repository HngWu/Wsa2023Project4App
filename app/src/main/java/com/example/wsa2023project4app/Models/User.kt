package com.example.wsa2023project4app.Models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val password: String
)