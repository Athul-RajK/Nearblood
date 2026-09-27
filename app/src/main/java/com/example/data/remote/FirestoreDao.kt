package com.example.data.remote

import android.util.Log
import com.example.data.model.Donation
import com.example.data.model.Donor
import com.example.data.model.DonorProfile
import com.example.data.model.EmergencyRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreDao {
    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("FirestoreDao", "Firebase not initialized; using Room local storage: ${e.message}")
            null
        }
    }

    // USERS / DONORS COLLECTION

    suspend fun saveUser(donor: Donor): Boolean {
        return try {
            firestore?.collection("users")?.document(donor.uid)?.set(donor, SetOptions.merge())?.await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreDao", "Error saving user to Firestore: ${e.message}")
            false
        }
    }

    fun observeUsers(): Flow<List<Donor>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("users").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirestoreDao", "Observe users error: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val donors = snapshot.documents.mapNotNull { doc ->
                    try { doc.toObject(Donor::class.java) } catch (e: Exception) { null }
                }
                trySend(donors)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateDonorApproval(uid: String, approved: Boolean): Boolean {
        return try {
            firestore?.collection("users")?.document(uid)?.update("approved", approved)?.await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreDao", "Error updating approval: ${e.message}")
            false
        }
    }

    suspend fun updateDonorStatus(uid: String, status: String): Boolean {
        return try {
            firestore?.collection("users")?.document(uid)?.update("status", status)?.await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreDao", "Error updating status: ${e.message}")
            false
        }
    }

    suspend fun updateDonorRole(uid: String, role: String): Boolean {
        return try {
            firestore?.collection("users")?.document(uid)?.update("role", role)?.await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreDao", "Error updating role: ${e.message}")
            false
        }
    }

    suspend fun updateDonorVerification(uid: String, verified: Boolean): Boolean {
        return try {
            firestore?.collection("users")?.document(uid)?.update("verified", verified)?.await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreDao", "Error updating verification: ${e.message}")
            false
        }
    }

    suspend fun updateDonorDetails(donor: Donor): Boolean {
        return saveUser(donor)
    }

    // DONATIONS COLLECTION

    suspend fun saveDonation(donation: Donation): Boolean {
        return try {
            firestore?.collection("donations")?.document(donation.donationId)?.set(donation, SetOptions.merge())?.await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreDao", "Error saving donation to Firestore: ${e.message}")
            false
        }
    }

    fun observeDonationsByDonor(donorId: String): Flow<List<Donation>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("donations")
            .whereEqualTo("donorId", donorId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirestoreDao", "Observe donations error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try { doc.toObject(Donation::class.java) } catch (e: Exception) { null }
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    fun observeAllDonations(): Flow<List<Donation>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("donations").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirestoreDao", "Observe all donations error: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    try { doc.toObject(Donation::class.java) } catch (e: Exception) { null }
                }
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    // EMERGENCY REQUESTS COLLECTION

    suspend fun saveEmergencyRequest(request: EmergencyRequest): Boolean {
        return try {
            firestore?.collection("emergency_requests")?.document(request.requestId)?.set(request, SetOptions.merge())?.await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreDao", "Error saving emergency request: ${e.message}")
            false
        }
    }

    fun observeEmergencyRequests(): Flow<List<EmergencyRequest>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("emergency_requests").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirestoreDao", "Observe emergency requests error: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    try { doc.toObject(EmergencyRequest::class.java) } catch (e: Exception) { null }
                }
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun updateEmergencyStatus(requestId: String, status: String): Boolean {
        return try {
            firestore?.collection("emergency_requests")?.document(requestId)?.update("status", status)?.await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreDao", "Error updating emergency status: ${e.message}")
            false
        }
    }

    // DONOR PROFILES COLLECTION (Firestore)

    suspend fun saveDonorProfile(profile: DonorProfile): Boolean {
        return try {
            firestore?.collection("donor_profiles")?.document(profile.userId)?.set(profile, SetOptions.merge())?.await()
            true
        } catch (e: Exception) {
            Log.e("FirestoreDao", "Error saving donor profile to Firestore: ${e.message}")
            false
        }
    }

    fun observeDonorProfile(userId: String): Flow<DonorProfile?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = db.collection("donor_profiles").document(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirestoreDao", "Observe donor profile error: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val profile = try { snapshot.toObject(DonorProfile::class.java) } catch (e: Exception) { null }
                trySend(profile)
            } else {
                trySend(null)
            }
        }
        awaitClose { listener.remove() }
    }

    fun observeAllDonorProfiles(): Flow<List<DonorProfile>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("donor_profiles").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("FirestoreDao", "Observe all donor profiles error: ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    try { doc.toObject(DonorProfile::class.java) } catch (e: Exception) { null }
                }
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }
}
