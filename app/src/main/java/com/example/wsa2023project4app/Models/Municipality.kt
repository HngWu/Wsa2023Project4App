package com.example.wsa2023project4app.Models

import kotlinx.serialization.Serializable


@Serializable
data class Municipality (
    val id: Int,
    val name: String,
    val map: String,
    val logo: String?,
    val description: String,
    val touristSpot: String,
)