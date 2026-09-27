package com.example.data.model

import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

/**
 * Data model for User Donor Profiles stored in Firestore.
 * Contains blood type, donation history/dates, personal & emergency contact details,
 * and donor eligibility metadata.
 */
@IgnoreExtraProperties
data class DonorProfile(
    @get:PropertyName("userId") @set:PropertyName("userId")
    var userId: String = "",

    @get:PropertyName("fullName") @set:PropertyName("fullName")
    var fullName: String = "",

    @get:PropertyName("bloodType") @set:PropertyName("bloodType")
    var bloodType: String = "O+", // "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"

    @get:PropertyName("lastDonationDate") @set:PropertyName("lastDonationDate")
    var lastDonationDate: String = "2026-03-15", // ISO format "YYYY-MM-DD"

    // Contact Details
    @get:PropertyName("phoneNumber") @set:PropertyName("phoneNumber")
    var phoneNumber: String = "+1 (555) 234-5678",

    @get:PropertyName("email") @set:PropertyName("email")
    var email: String = "",

    @get:PropertyName("city") @set:PropertyName("city")
    var city: String = "New York",

    @get:PropertyName("residentialAddress") @set:PropertyName("residentialAddress")
    var residentialAddress: String = "742 Evergreen Terrace, New York, NY",

    // Emergency (ICE) Contact Details
    @get:PropertyName("emergencyContactName") @set:PropertyName("emergencyContactName")
    var emergencyContactName: String = "Sarah Vance",

    @get:PropertyName("emergencyContactPhone") @set:PropertyName("emergencyContactPhone")
    var emergencyContactPhone: String = "+1 (555) 987-6543",

    @get:PropertyName("emergencyContactRelation") @set:PropertyName("emergencyContactRelation")
    var emergencyContactRelation: String = "Spouse",

    // Donor Status & Health Criteria
    @get:PropertyName("isAvailable") @set:PropertyName("isAvailable")
    var isAvailable: Boolean = true,

    @get:PropertyName("weightKg") @set:PropertyName("weightKg")
    var weightKg: Double = 68.0,

    @get:PropertyName("age") @set:PropertyName("age")
    var age: Int = 28,

    @get:PropertyName("healthNotes") @set:PropertyName("healthNotes")
    var healthNotes: String = "Fit & Eligible - Hemoglobin 14.5 g/dL",

    @get:PropertyName("donationCount") @set:PropertyName("donationCount")
    var donationCount: Int = 4,

    @get:PropertyName("verifiedBadge") @set:PropertyName("verifiedBadge")
    var verifiedBadge: Boolean = true,

    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt")
    var updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Converts to the internal Donor model used by local Room cache & directory.
     */
    fun toDonor(): Donor {
        return Donor(
            uid = userId,
            name = fullName,
            email = email,
            role = "donor",
            approved = true,
            status = "active",
            photoUrl = "",
            bloodGroup = bloodType,
            city = city,
            phone = phoneNumber,
            verified = verifiedBadge,
            available = isAvailable,
            lastDonationDate = lastDonationDate,
            healthStatus = healthNotes,
            donationCount = donationCount,
            emergencyContactName = emergencyContactName,
            emergencyContactPhone = emergencyContactPhone,
            emergencyRelation = emergencyContactRelation,
            qrCode = "NEARBLOOD-DONOR-$userId"
        )
    }

    companion object {
        /**
         * Factory function to instantiate DonorProfile from an existing Donor.
         */
        fun fromDonor(donor: Donor): DonorProfile {
            return DonorProfile(
                userId = donor.uid,
                fullName = donor.name,
                bloodType = donor.bloodGroup,
                lastDonationDate = donor.lastDonationDate,
                phoneNumber = donor.phone,
                email = donor.email,
                city = donor.city,
                residentialAddress = "${donor.city}, United States",
                emergencyContactName = donor.emergencyContactName.ifEmpty { "Emergency Contact" },
                emergencyContactPhone = donor.emergencyContactPhone.ifEmpty { donor.phone },
                emergencyContactRelation = donor.emergencyRelation.ifEmpty { "Family" },
                isAvailable = donor.available,
                weightKg = 68.0,
                age = 28,
                healthNotes = donor.healthStatus,
                donationCount = donor.donationCount,
                verifiedBadge = donor.verified,
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}
