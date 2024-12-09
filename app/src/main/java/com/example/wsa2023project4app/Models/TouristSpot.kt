package com.example.wsa2023project4app.Models

import kotlinx.serialization.Serializable

@Serializable
data class TouristSpot (
    val id: Int,
    val name: String,
    val address: String,
    val description: String,
    val picture: String?,
    val rating : String,
    val entranceFee: String,
)