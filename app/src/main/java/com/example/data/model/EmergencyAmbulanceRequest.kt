package com.example.data.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class EmergencyAmbulanceRequest(
    @get:PropertyName("requestId") @set:PropertyName("requestId")
    var requestId: String = "",

    @get:PropertyName("patientName") @set:PropertyName("patientName")
    var patientName: String = "",

    @get:PropertyName("pickupAddress") @set:PropertyName("pickupAddress")
    var pickupAddress: String = "",

    @get:PropertyName("destinationHospital") @set:PropertyName("destinationHospital")
    var destinationHospital: String = "",

    @get:PropertyName("city") @set:PropertyName("city")
    var city: String = "New York",

    @get:PropertyName("bloodGroupNeeded") @set:PropertyName("bloodGroupNeeded")
    var bloodGroupNeeded: String = "O+",

    @get:PropertyName("unitsNeeded") @set:PropertyName("unitsNeeded")
    var unitsNeeded: Int = 1,

    @get:PropertyName("patientCondition") @set:PropertyName("patientCondition")
    var patientCondition: String = "Emergency Transit / Critical",

    @get:PropertyName("requesterName") @set:PropertyName("requesterName")
    var requesterName: String = "",

    @get:PropertyName("contactPhone") @set:PropertyName("contactPhone")
    var contactPhone: String = "",

    @get:PropertyName("ambulanceAssigned") @set:PropertyName("ambulanceAssigned")
    var ambulanceAssigned: String = "",

    @get:PropertyName("ambulancePhone") @set:PropertyName("ambulancePhone")
    var ambulancePhone: String = "",

    @get:PropertyName("status") @set:PropertyName("status")
    var status: String = "DISPATCHED", // "PENDING", "DISPATCHED", "EN_ROUTE", "ARRIVED", "COMPLETED"

    @get:PropertyName("sosAlertSent") @set:PropertyName("sosAlertSent")
    var sosAlertSent: Boolean = true,

    @get:PropertyName("nearbyDonorsNotified") @set:PropertyName("nearbyDonorsNotified")
    var nearbyDonorsNotified: Int = 0,

    @get:PropertyName("timestamp") @set:PropertyName("timestamp")
    var timestamp: Long = System.currentTimeMillis()
)
