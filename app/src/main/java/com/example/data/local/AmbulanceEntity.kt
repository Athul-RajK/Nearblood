package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Ambulance

@Entity(tableName = "ambulances")
data class AmbulanceEntity(
    @PrimaryKey val id: String,
    val serviceName: String,
    val phone: String,
    val city: String,
    val vehicleNo: String,
    val available: Boolean,
    val isEmergency24x7: Boolean
) {
    fun toAmbulance(): Ambulance = Ambulance(
        id = id,
        serviceName = serviceName,
        phone = phone,
        city = city,
        vehicleNo = vehicleNo,
        available = available,
        isEmergency24x7 = isEmergency24x7
    )

    companion object {
        fun fromAmbulance(a: Ambulance): AmbulanceEntity = AmbulanceEntity(
            id = a.id,
            serviceName = a.serviceName,
            phone = a.phone,
            city = a.city,
            vehicleNo = a.vehicleNo,
            available = a.available,
            isEmergency24x7 = a.isEmergency24x7
        )
    }
}
