package com.example.data.model

data class Ambulance(
    val id: String,
    val serviceName: String,
    val phone: String,
    val city: String,
    val vehicleNo: String,
    val available: Boolean = true,
    val isEmergency24x7: Boolean = true
)
