package com.example.data.remote

import android.util.Log
import com.example.data.model.EmergencyAmbulanceRequest
import com.example.data.model.SosAlert
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRealtimeDao {
    private val rtdbUrl = "https://athulmusic-b6ea1-default-rtdb.firebaseio.com"

    private val database: FirebaseDatabase? by lazy {
        try {
            val db = FirebaseDatabase.getInstance(rtdbUrl)
            try {
                db.setPersistenceEnabled(true)
            } catch (e: Exception) {
                // Persistence can only be set once
            }
            db
        } catch (e: Exception) {
            Log.w("FirebaseRealtimeDao", "Default URL failed, trying generic instance: ${e.message}")
            try {
                FirebaseDatabase.getInstance()
            } catch (err: Exception) {
                Log.e("FirebaseRealtimeDao", "Firebase Realtime Database unavailable: ${err.message}")
                null
            }
        }
    }

    /**
     * Broadcasts an SOS Alert to all connected donors in the Realtime Database.
     */
    suspend fun broadcastSosAlert(alert: SosAlert): Boolean {
        return try {
            val db = database ?: return false
            val ref = db.getReference("sos_alerts").child(alert.alertId)
            ref.setValue(alert).await()
            Log.d("FirebaseRealtimeDao", "SOS alert ${alert.alertId} broadcasted to Realtime Database")
            true
        } catch (e: Exception) {
            Log.e("FirebaseRealtimeDao", "Failed to broadcast SOS alert: ${e.message}")
            false
        }
    }

    /**
     * Records an Emergency Ambulance Request with live dispatch tracking in Realtime Database.
     */
    suspend fun createAmbulanceRequest(request: EmergencyAmbulanceRequest): Boolean {
        return try {
            val db = database ?: return false
            val ref = db.getReference("emergency_ambulance_requests").child(request.requestId)
            ref.setValue(request).await()
            Log.d("FirebaseRealtimeDao", "Ambulance request ${request.requestId} created in Realtime Database")
            true
        } catch (e: Exception) {
            Log.e("FirebaseRealtimeDao", "Failed to create ambulance request in Realtime DB: ${e.message}")
            false
        }
    }

    /**
     * Observes live SOS alerts in real time.
     */
    fun observeSosAlerts(): Flow<List<SosAlert>> = callbackFlow {
        val db = database
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ref = db.getReference("sos_alerts")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val alerts = mutableListOf<SosAlert>()
                for (child in snapshot.children) {
                    try {
                        val alert = child.getValue(SosAlert::class.java)
                        if (alert != null) {
                            alerts.add(alert)
                        }
                    } catch (e: Exception) {
                        Log.w("FirebaseRealtimeDao", "Failed to deserialize SosAlert: ${e.message}")
                    }
                }
                // Sort with most recent alerts first
                alerts.sortByDescending { it.timestamp }
                trySend(alerts)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseRealtimeDao", "observeSosAlerts cancelled: ${error.message}")
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    /**
     * Observes active ambulance dispatch requests in real time.
     */
    fun observeAmbulanceRequests(): Flow<List<EmergencyAmbulanceRequest>> = callbackFlow {
        val db = database
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val ref = db.getReference("emergency_ambulance_requests")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requests = mutableListOf<EmergencyAmbulanceRequest>()
                for (child in snapshot.children) {
                    try {
                        val req = child.getValue(EmergencyAmbulanceRequest::class.java)
                        if (req != null) {
                            requests.add(req)
                        }
                    } catch (e: Exception) {
                        Log.w("FirebaseRealtimeDao", "Failed to deserialize AmbulanceRequest: ${e.message}")
                    }
                }
                requests.sortByDescending { it.timestamp }
                trySend(requests)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseRealtimeDao", "observeAmbulanceRequests cancelled: ${error.message}")
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    /**
     * Allows a registered donor to accept and respond to an emergency SOS alert.
     */
    suspend fun respondToSosAlert(alertId: String, donorName: String, donorUid: String): Boolean {
        return try {
            val db = database ?: return false
            val alertRef = db.getReference("sos_alerts").child(alertId)
            val snapshot = alertRef.get().await()
            val alert = snapshot.getValue(SosAlert::class.java) ?: return false

            val updatedResponders = (alert.responderDonorNames + donorName).distinct()
            val updates = mapOf<String, Any>(
                "status" to "RESPONDED",
                "responderDonorsCount" to updatedResponders.size,
                "responderDonorNames" to updatedResponders
            )
            alertRef.updateChildren(updates).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRealtimeDao", "Error responding to SOS alert: ${e.message}")
            false
        }
    }

    /**
     * Updates an SOS alert status (e.g. RESOLVED or DISMISSED).
     */
    suspend fun updateSosAlertStatus(alertId: String, status: String): Boolean {
        return try {
            val db = database ?: return false
            db.getReference("sos_alerts").child(alertId).child("status").setValue(status).await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseRealtimeDao", "Error updating SOS alert status: ${e.message}")
            false
        }
    }

    /**
     * Observes real-time Firebase connection health (.info/connected).
     */
    fun observeConnectionStatus(): Flow<Boolean> = callbackFlow {
        val db = database
        if (db == null) {
            trySend(false)
            close()
            return@callbackFlow
        }

        val connectedRef = db.getReference(".info/connected")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                trySend(connected)
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(false)
            }
        }

        connectedRef.addValueEventListener(listener)
        awaitClose { connectedRef.removeEventListener(listener) }
    }
}
