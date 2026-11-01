package com.labMTS.karflex.database

data class Vehicle(
    val vehicleId: Int,
    val modelName: String,
    val type: String,
    val brand: String,
    val pricePerDay: Int,
    val transmission: String,
    val image: String,
    val description: String
)
