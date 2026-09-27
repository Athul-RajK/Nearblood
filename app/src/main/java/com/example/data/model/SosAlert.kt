package com.example.data.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class SosAlert(
    @get:PropertyName("alertId") @set:PropertyName("alertId")
    var alertId: String = "",

    @get:PropertyName("requestId") @set:PropertyName("requestId")
    var requestId: String = "",

    @get:PropertyName("ambulanceId") @set:PropertyName("ambulanceId")
    var ambulanceId: String = "",

    @get:PropertyName("ambulanceServiceName") @set:PropertyName("ambulanceServiceName")
    var ambulanceServiceName: String = "RapidCare Emergency Ambulance",

    @get:PropertyName("patientName") @set:PropertyName("patientName")
    var patientName: String = "",

    @get:PropertyName("hospitalName") @set:PropertyName("hospitalName")
    var hospitalName: String = "",

    @get:PropertyName("city") @set:PropertyName("city")
    var city: String = "New York",

    @get:PropertyName("pickupAddress") @set:PropertyName("pickupAddress")
    var pickupAddress: String = "",

    @get:PropertyName("bloodGroupNeeded") @set:PropertyName("bloodGroupNeeded")
    var bloodGroupNeeded: String = "O+",

    @get:PropertyName("unitsNeeded") @set:PropertyName("unitsNeeded")
    var unitsNeeded: Int = 1,

    @get:PropertyName("contactPhone") @set:PropertyName("contactPhone")
    var contactPhone: String = "",

    @get:PropertyName("driverPhone") @set:PropertyName("driverPhone")
    var driverPhone: String = "911",

    @get:PropertyName("urgency") @set:PropertyName("urgency")
    var urgency: String = "CRITICAL", // "CRITICAL", "HIGH", "EMERGENCY"

    @get:PropertyName("notes") @set:PropertyName("notes")
    var notes: String = "",

    @get:PropertyName("timestamp") @set:PropertyName("timestamp")
    var timestamp: Long = System.currentTimeMillis(),

    @get:PropertyName("formattedTime") @set:PropertyName("formattedTime")
    var formattedTime: String = "Just now",

    @get:PropertyName("targetNearbyDonorsCount") @set:PropertyName("targetNearbyDonorsCount")
    var targetNearbyDonorsCount: Int = 0,

    @get:PropertyName("targetDonorUids") @set:PropertyName("targetDonorUids")
    var targetDonorUids: List<String> = emptyList(),

    @get:PropertyName("responderDonorsCount") @set:PropertyName("responderDonorsCount")
    var responderDonorsCount: Int = 0,

    @get:PropertyName("responderDonorNames") @set:PropertyName("responderDonorNames")
    var responderDonorNames: List<String> = emptyList(),

    @get:PropertyName("status") @set:PropertyName("status")
    var status: String = "ACTIVE" // "ACTIVE", "RESPONDED", "RESOLVED", "CANCELLED"
)
