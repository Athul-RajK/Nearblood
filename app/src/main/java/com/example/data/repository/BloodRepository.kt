package com.example.data.repository

import com.example.data.local.AmbulanceDao
import com.example.data.local.AmbulanceEntity
import com.example.data.local.DonationDao
import com.example.data.local.DonationEntity
import com.example.data.local.DonorDao
import com.example.data.local.DonorEntity
import com.example.data.model.Ambulance
import com.example.data.model.Donation
import com.example.data.model.Donor
import com.example.data.model.DonorProfile
import com.example.data.model.EmergencyAmbulanceRequest
import com.example.data.model.EmergencyRequest
import com.example.data.model.SosAlert
import com.example.data.remote.FirebaseRealtimeDao
import com.example.data.remote.FirestoreDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BloodRepository(
    private val donorDao: DonorDao,
    private val donationDao: DonationDao,
    private val ambulanceDao: AmbulanceDao,
    private val firestoreDao: FirestoreDao = FirestoreDao(),
    private val realtimeDao: FirebaseRealtimeDao = FirebaseRealtimeDao()
) {
    val allDonors: Flow<List<Donor>> = donorDao.getAllDonors().map { list ->
        list.map { it.toDonor() }
    }

    val allAmbulances: Flow<List<Ambulance>> = ambulanceDao.getAllAmbulances().map { list ->
        list.map { it.toAmbulance() }
    }

    private val _emergencyRequests = MutableStateFlow<List<EmergencyRequest>>(emptyList())
    val emergencyRequests: Flow<List<EmergencyRequest>> = _emergencyRequests.asStateFlow()

    // Realtime Firebase Flows
    val sosAlerts: Flow<List<SosAlert>> = realtimeDao.observeSosAlerts()
    val liveAmbulanceRequests: Flow<List<EmergencyAmbulanceRequest>> = realtimeDao.observeAmbulanceRequests()
    val isRealtimeDbConnected: Flow<Boolean> = realtimeDao.observeConnectionStatus()

    fun getDonationsForDonor(donorId: String): Flow<List<Donation>> {
        return donationDao.getDonationsByDonor(donorId).map { list ->
            list.map { it.toDonation() }
        }
    }

    val allDonations: Flow<List<Donation>> = donationDao.getDonationsByDonor("%").map { list ->
        list.map { it.toDonation() }
    }

    suspend fun saveDonor(donor: Donor) = withContext(Dispatchers.IO) {
        donorDao.insertDonor(DonorEntity.fromDonor(donor))
        firestoreDao.saveUser(donor)
    }

    suspend fun updateDonorApproval(uid: String, approved: Boolean) = withContext(Dispatchers.IO) {
        val existing = donorDao.getDonorById(uid)
        if (existing != null) {
            val updated = existing.copy(approved = approved)
            donorDao.updateDonor(updated)
            firestoreDao.updateDonorApproval(uid, approved)
        }
    }

    suspend fun updateDonorVerification(uid: String, verified: Boolean) = withContext(Dispatchers.IO) {
        val existing = donorDao.getDonorById(uid)
        if (existing != null) {
            val updated = existing.copy(verified = verified)
            donorDao.updateDonor(updated)
            firestoreDao.updateDonorVerification(uid, verified)
        }
    }

    suspend fun updateDonorAvailability(uid: String, available: Boolean) = withContext(Dispatchers.IO) {
        val existing = donorDao.getDonorById(uid)
        if (existing != null) {
            val updated = existing.copy(available = available)
            donorDao.updateDonor(updated)
            firestoreDao.saveUser(updated.toDonor())
        }
    }

    suspend fun updateDonorStatus(uid: String, status: String) = withContext(Dispatchers.IO) {
        val existing = donorDao.getDonorById(uid)
        if (existing != null) {
            val updated = existing.copy(status = status)
            donorDao.updateDonor(updated)
            firestoreDao.updateDonorStatus(uid, status)
        }
    }

    suspend fun updateDonorRole(uid: String, role: String) = withContext(Dispatchers.IO) {
        val existing = donorDao.getDonorById(uid)
        if (existing != null) {
            val updated = existing.copy(role = role)
            donorDao.updateDonor(updated)
            firestoreDao.updateDonorRole(uid, role)
        }
    }

    suspend fun updateDonorDetails(donor: Donor) = withContext(Dispatchers.IO) {
        donorDao.updateDonor(DonorEntity.fromDonor(donor))
        firestoreDao.updateDonorDetails(donor)
        firestoreDao.saveDonorProfile(DonorProfile.fromDonor(donor))
    }

    suspend fun saveDonorProfile(profile: DonorProfile): Boolean = withContext(Dispatchers.IO) {
        val donor = profile.toDonor()
        donorDao.insertDonor(DonorEntity.fromDonor(donor))
        val userSaved = firestoreDao.saveUser(donor)
        val profileSaved = firestoreDao.saveDonorProfile(profile)
        userSaved || profileSaved
    }

    fun observeDonorProfile(userId: String): Flow<DonorProfile?> {
        return firestoreDao.observeDonorProfile(userId)
    }

    suspend fun addDonation(donation: Donation) = withContext(Dispatchers.IO) {
        donationDao.insertDonation(DonationEntity.fromDonation(donation))
        firestoreDao.saveDonation(donation)
        val existing = donorDao.getDonorById(donation.donorId)
        if (existing != null) {
            val updated = existing.copy(
                donationCount = existing.donationCount + 1,
                lastDonationDate = donation.date
            )
            donorDao.updateDonor(updated)
            firestoreDao.saveUser(updated.toDonor())
        }
    }

    // Emergency Request & SOS Handlers
    suspend fun createEmergencyRequest(request: EmergencyRequest) = withContext(Dispatchers.IO) {
        _emergencyRequests.value = listOf(request) + _emergencyRequests.value
        firestoreDao.saveEmergencyRequest(request)
    }

    suspend fun updateEmergencyStatus(requestId: String, status: String) = withContext(Dispatchers.IO) {
        _emergencyRequests.value = _emergencyRequests.value.map {
            if (it.requestId == requestId) it.copy(status = status) else it
        }
        firestoreDao.updateEmergencyStatus(requestId, status)
    }

    /**
     * Dispatches an Emergency Ambulance and broadcasts an SOS alert to nearby registered donors in Realtime DB.
     */
    suspend fun requestAmbulanceWithSos(
        ambulanceRequest: EmergencyAmbulanceRequest,
        sosAlert: SosAlert
    ): Boolean = withContext(Dispatchers.IO) {
        // 1. Save to Firebase Realtime Database
        val ambSaved = realtimeDao.createAmbulanceRequest(ambulanceRequest)
        val sosSaved = realtimeDao.broadcastSosAlert(sosAlert)

        // 2. Also register in Firestore / Local Emergency Request list
        val emRequest = EmergencyRequest(
            requestId = ambulanceRequest.requestId,
            patientName = ambulanceRequest.patientName,
            hospitalName = ambulanceRequest.destinationHospital,
            bloodGroup = ambulanceRequest.bloodGroupNeeded,
            unitsNeeded = ambulanceRequest.unitsNeeded,
            contactPhone = ambulanceRequest.contactPhone,
            city = ambulanceRequest.city,
            status = "PENDING",
            urgency = "CRITICAL",
            notes = "🚨 SOS AMBULANCE DISPATCHED: ${ambulanceRequest.ambulanceAssigned}. ${sosAlert.notes}",
            timestamp = "Just now"
        )
        createEmergencyRequest(emRequest)

        ambSaved || sosSaved
    }

    suspend fun respondToSosAlert(alertId: String, donorName: String, donorUid: String): Boolean = withContext(Dispatchers.IO) {
        realtimeDao.respondToSosAlert(alertId, donorName, donorUid)
    }

    suspend fun updateSosAlertStatus(alertId: String, status: String): Boolean = withContext(Dispatchers.IO) {
        realtimeDao.updateSosAlertStatus(alertId, status)
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val sampleDonors = listOf(
            Donor(
                uid = "D101",
                name = "Dr. Marcus Vance",
                email = "marcus.vance@example.com",
                role = "donor",
                approved = true,
                status = "active",
                bloodGroup = "O+",
                city = "New York",
                phone = "+1 555-0192",
                verified = true,
                available = true,
                lastDonationDate = "2026-03-10",
                healthStatus = "Fit & Eligible",
                donationCount = 8,
                emergencyContactName = "Sarah Vance",
                emergencyContactPhone = "+1 555-0199",
                emergencyRelation = "Spouse"
            ),
            Donor(
                uid = "D102",
                name = "Elena Rostova",
                email = "elena.r@example.com",
                role = "donor",
                approved = true,
                status = "active",
                bloodGroup = "A-",
                city = "San Francisco",
                phone = "+1 555-0184",
                verified = true,
                available = true,
                lastDonationDate = "2026-06-20",
                healthStatus = "Eligible Next Month",
                donationCount = 4,
                emergencyContactName = "Dmitri Rostov",
                emergencyContactPhone = "+1 555-0180",
                emergencyRelation = "Brother"
            ),
            Donor(
                uid = "D103",
                name = "David Chen",
                email = "david.chen@example.com",
                role = "donor",
                approved = true,
                status = "active",
                bloodGroup = "B+",
                city = "Chicago",
                phone = "+1 555-0145",
                verified = true,
                available = false,
                lastDonationDate = "2026-01-12",
                healthStatus = "Fit & Eligible",
                donationCount = 12,
                emergencyContactName = "Linda Chen",
                emergencyContactPhone = "+1 555-0146",
                emergencyRelation = "Mother"
            ),
            Donor(
                uid = "D104",
                name = "Aaliyah Patel",
                email = "aaliyah.p@example.com",
                role = "donor",
                approved = true,
                status = "active",
                bloodGroup = "AB-",
                city = "Houston",
                phone = "+1 555-0123",
                verified = false,
                available = true,
                lastDonationDate = "2025-11-05",
                healthStatus = "Fit & Eligible",
                donationCount = 2,
                emergencyContactName = "Rohan Patel",
                emergencyContactPhone = "+1 555-0124",
                emergencyRelation = "Father"
            ),
            Donor(
                uid = "D105",
                name = "Samuel Jackson",
                email = "sam.j@example.com",
                role = "donor",
                approved = false, // Pending Admin Approval
                status = "active",
                bloodGroup = "O-",
                city = "San Francisco",
                phone = "+1 555-0177",
                verified = false,
                available = true,
                lastDonationDate = "2026-02-14",
                healthStatus = "Fit & Eligible",
                donationCount = 1,
                emergencyContactName = "Maria Jackson",
                emergencyContactPhone = "+1 555-0178",
                emergencyRelation = "Sister"
            ),
            Donor(
                uid = "D106",
                name = "Sophia Martinez",
                email = "sophia.m@example.com",
                role = "donor",
                approved = true,
                status = "active",
                bloodGroup = "AB+",
                city = "Los Angeles",
                phone = "+1 555-0111",
                verified = true,
                available = true,
                lastDonationDate = "2026-04-01",
                healthStatus = "Fit & Eligible",
                donationCount = 6,
                emergencyContactName = "Carlos Martinez",
                emergencyContactPhone = "+1 555-0112",
                emergencyRelation = "Spouse"
            ),
            Donor(
                uid = "ADMIN101",
                name = "Sarah Miller (Regional Admin)",
                email = "admin@nearblood.org",
                role = "admin",
                approved = true,
                status = "active",
                bloodGroup = "O+",
                city = "Central HQ",
                phone = "+1 800-BLOOD-HQ",
                verified = true,
                available = true,
                lastDonationDate = "2026-01-01",
                healthStatus = "Administrator",
                donationCount = 15,
                emergencyContactName = "HQ Control Desk",
                emergencyContactPhone = "+1 800-555-9999",
                emergencyRelation = "HQ"
            ),
            Donor(
                uid = "SUPER101",
                name = "Chief Executive (Super Admin)",
                email = "superadmin@nearblood.org",
                role = "super_admin",
                approved = true,
                status = "active",
                bloodGroup = "O-",
                city = "Global HQ",
                phone = "+1 800-SUPER-ADM",
                verified = true,
                available = true,
                lastDonationDate = "2026-01-01",
                healthStatus = "Super Administrator",
                donationCount = 20,
                emergencyContactName = "Exec Ops",
                emergencyContactPhone = "+1 800-555-0000",
                emergencyRelation = "HQ"
            )
        )
        donorDao.insertAllDonors(sampleDonors.map { DonorEntity.fromDonor(it) })
        try { 
            sampleDonors.forEach { 
                firestoreDao.saveUser(it)
                firestoreDao.saveDonorProfile(DonorProfile.fromDonor(it))
            } 
        } catch (_: Exception) {}

        val sampleDonations = listOf(
            Donation("DON-881", "D101", "Metropolitan City Hospital", "2026-03-10", 1, "O+"),
            Donation("DON-724", "D101", "St. Jude Medical Center", "2025-11-15", 1, "O+"),
            Donation("DON-612", "D101", "Red Cross Central Clinic", "2025-07-02", 1, "O+"),
            Donation("DON-419", "D102", "UCSF Health Center", "2026-06-20", 1, "A-"),
            Donation("DON-301", "D103", "Chicago General Hospital", "2026-01-12", 2, "B+")
        )
        donationDao.insertAllDonations(sampleDonations.map { DonationEntity.fromDonation(it) })
        try { sampleDonations.forEach { firestoreDao.saveDonation(it) } } catch (_: Exception) {}

        val sampleAmbulances = listOf(
            Ambulance("AMB-1", "RapidCare Emergency Ambulance", "911", "New York", "NY-MED-91", true, true),
            Ambulance("AMB-2", "Red Cross Critical Response", "+1 800-555-0191", "San Francisco", "SF-EMG-04", true, true),
            Ambulance("AMB-3", "LifeLine ICU Transit Unit", "+1 800-555-0144", "Chicago", "CH-ICU-12", true, true),
            Ambulance("AMB-4", "Metro Rescue Trauma Unit", "+1 800-555-0188", "Houston", "TX-AMB-55", false, true)
        )
        ambulanceDao.insertAllAmbulances(sampleAmbulances.map { AmbulanceEntity.fromAmbulance(it) })

        val sampleEmergencyRequests = listOf(
            EmergencyRequest(
                requestId = "REQ-101",
                patientName = "Robert Taylor",
                hospitalName = "Presbyterian Emergency Ward",
                bloodGroup = "O-",
                unitsNeeded = 3,
                contactPhone = "+1 555-0190",
                city = "New York",
                status = "PENDING",
                urgency = "CRITICAL",
                notes = "Urgent trauma surgery requiring O- negative blood",
                timestamp = "10 mins ago"
            ),
            EmergencyRequest(
                requestId = "REQ-102",
                patientName = "Maria Garcia",
                hospitalName = "Memorial Care Hospital",
                bloodGroup = "B+",
                unitsNeeded = 2,
                contactPhone = "+1 555-0165",
                city = "Chicago",
                status = "IN_PROGRESS",
                urgency = "HIGH",
                notes = "Scheduled cardiac procedure",
                timestamp = "1 hour ago"
            )
        )
        _emergencyRequests.value = sampleEmergencyRequests
        try { sampleEmergencyRequests.forEach { firestoreDao.saveEmergencyRequest(it) } } catch (_: Exception) {}

        val sampleSos = SosAlert(
            alertId = "SOS-911",
            requestId = "REQ-101",
            ambulanceId = "AMB-1",
            ambulanceServiceName = "RapidCare Emergency Ambulance",
            patientName = "Robert Taylor",
            hospitalName = "Presbyterian Emergency Ward",
            city = "New York",
            pickupAddress = "350 5th Ave, New York, NY",
            bloodGroupNeeded = "O-",
            unitsNeeded = 3,
            contactPhone = "+1 555-0190",
            driverPhone = "911",
            urgency = "CRITICAL",
            notes = "Emergency trauma surgery in transit. Immediate O- donors needed at ER entrance.",
            timestamp = System.currentTimeMillis() - 1000 * 60 * 5,
            formattedTime = "5 mins ago",
            targetNearbyDonorsCount = 4,
            targetDonorUids = listOf("D101", "D104"),
            responderDonorsCount = 1,
            responderDonorNames = listOf("Dr. Marcus Vance"),
            status = "ACTIVE"
        )
        try { realtimeDao.broadcastSosAlert(sampleSos) } catch (_: Exception) {}
    }
}

