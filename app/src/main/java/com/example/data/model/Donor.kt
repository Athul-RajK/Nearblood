package com.example.data.model

data class Donor(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "donor", // "donor", "admin", "super_admin"
    val approved: Boolean = true, // Admin approval flag
    val status: String = "active", // "active", "suspended", "rejected"
    val photoUrl: String = "",
    val bloodGroup: String = "O+", // "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"
    val city: String = "New York",
    val phone: String = "",
    val verified: Boolean = false, // Verified donor badge
    val available: Boolean = true, // Availability status (Available / Busy)
    val lastDonationDate: String = "2026-03-15", // YYYY-MM-DD
    val healthStatus: String = "Fit & Eligible",
    val donationCount: Int = 3,
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val emergencyRelation: String = "Family",
    val qrCode: String = "NEARBLOOD-DONOR-$uid"
)

