package com.example.data.model

data class Donation(
    val donationId: String,
    val donorId: String,
    val hospitalName: String,
    val date: String, // YYYY-MM-DD
    val units: Int = 1,
    val bloodGroup: String = "O+",
    val certificateNumber: String = "CERT-${donationId.take(6).uppercase()}"
)
