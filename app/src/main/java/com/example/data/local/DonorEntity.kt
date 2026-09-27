package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Donor

@Entity(tableName = "donors")
data class DonorEntity(
    @PrimaryKey val uid: String,
    val name: String,
    val email: String,
    val role: String,
    val approved: Boolean,
    val status: String = "active",
    val photoUrl: String,
    val bloodGroup: String,
    val city: String,
    val phone: String,
    val verified: Boolean,
    val available: Boolean,
    val lastDonationDate: String,
    val healthStatus: String,
    val donationCount: Int,
    val emergencyContactName: String,
    val emergencyContactPhone: String,
    val emergencyRelation: String,
    val qrCode: String
) {
    fun toDonor(): Donor = Donor(
        uid = uid,
        name = name,
        email = email,
        role = role,
        approved = approved,
        status = status,
        photoUrl = photoUrl,
        bloodGroup = bloodGroup,
        city = city,
        phone = phone,
        verified = verified,
        available = available,
        lastDonationDate = lastDonationDate,
        healthStatus = healthStatus,
        donationCount = donationCount,
        emergencyContactName = emergencyContactName,
        emergencyContactPhone = emergencyContactPhone,
        emergencyRelation = emergencyRelation,
        qrCode = qrCode
    )

    companion object {
        fun fromDonor(donor: Donor): DonorEntity = DonorEntity(
            uid = donor.uid,
            name = donor.name,
            email = donor.email,
            role = donor.role,
            approved = donor.approved,
            status = donor.status,
            photoUrl = donor.photoUrl,
            bloodGroup = donor.bloodGroup,
            city = donor.city,
            phone = donor.phone,
            verified = donor.verified,
            available = donor.available,
            lastDonationDate = donor.lastDonationDate,
            healthStatus = donor.healthStatus,
            donationCount = donor.donationCount,
            emergencyContactName = donor.emergencyContactName,
            emergencyContactPhone = donor.emergencyContactPhone,
            emergencyRelation = donor.emergencyRelation,
            qrCode = donor.qrCode
        )
    }
}

