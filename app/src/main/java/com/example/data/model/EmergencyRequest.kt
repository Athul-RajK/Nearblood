package com.example.data.model

data class EmergencyRequest(
    val requestId: String = "",
    val patientName: String = "",
    val hospitalName: String = "",
    val bloodGroup: String = "O+",
    val unitsNeeded: Int = 1,
    val contactPhone: String = "",
    val city: String = "New York",
    val status: String = "PENDING", // PENDING, IN_PROGRESS, RESOLVED, CANCELLED
    val urgency: String = "CRITICAL", // CRITICAL, HIGH, NORMAL
    val notes: String = "",
    val timestamp: String = "Just now"
)
