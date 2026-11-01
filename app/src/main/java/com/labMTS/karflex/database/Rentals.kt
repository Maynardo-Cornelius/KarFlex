package com.labMTS.karflex.database

data class Rentals(
    val rentalId: Int,
    val vehicleId: Int,
    val userId: Int,
    val duration: Int,
    val startDate: String,
    val status: String
)
