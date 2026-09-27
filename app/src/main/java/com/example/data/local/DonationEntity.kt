package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Donation

@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey val donationId: String,
    val donorId: String,
    val hospitalName: String,
    val date: String,
    val units: Int,
    val bloodGroup: String,
    val certificateNumber: String
) {
    fun toDonation(): Donation = Donation(
        donationId = donationId,
        donorId = donorId,
        hospitalName = hospitalName,
        date = date,
        units = units,
        bloodGroup = bloodGroup,
        certificateNumber = certificateNumber
    )

    companion object {
        fun fromDonation(donation: Donation): DonationEntity = DonationEntity(
            donationId = donation.donationId,
            donorId = donation.donorId,
            hospitalName = donation.hospitalName,
            date = donation.date,
            units = donation.units,
            bloodGroup = donation.bloodGroup,
            certificateNumber = donation.certificateNumber
        )
    }
}
