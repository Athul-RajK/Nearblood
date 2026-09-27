package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Ambulance
import com.example.data.model.Donation
import com.example.data.model.Donor
import com.example.data.model.DonorProfile
import com.example.data.model.EmergencyAmbulanceRequest
import com.example.data.model.EmergencyRequest
import com.example.data.model.NotificationItem
import com.example.data.model.SosAlert
import com.example.data.repository.BloodRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BloodViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = BloodRepository(db.donorDao(), db.donationDao(), db.ambulanceDao())

    // Filter States
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedBloodGroup = MutableStateFlow("ALL") // "ALL", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"
    val selectedBloodGroup: StateFlow<String> = _selectedBloodGroup.asStateFlow()

    private val _selectedCity = MutableStateFlow("ALL")
    val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

    private val _onlyAvailable = MutableStateFlow(false)
    val onlyAvailable: StateFlow<Boolean> = _onlyAvailable.asStateFlow()

    // Active User / Session State
    private val _currentUser = MutableStateFlow<Donor?>(null)
    val currentUser: StateFlow<Donor?> = _currentUser.asStateFlow()

    // Firestore Sync Status
    private val _firestoreSyncStatus = MutableStateFlow<FirestoreSyncStatus>(FirestoreSyncStatus.Synced("Ready"))
    val firestoreSyncStatus: StateFlow<FirestoreSyncStatus> = _firestoreSyncStatus.asStateFlow()

    // Active User Donor Profile Flow (reactive to currentUser & Firestore)
    val currentDonorProfile: StateFlow<DonorProfile?> = currentUser.map { user ->
        user?.let { DonorProfile.fromDonor(it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isAdminMode = MutableStateFlow(false)
    val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

    val isSuperAdmin: StateFlow<Boolean> = currentUser.map {
        it?.role == "super_admin"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Emergency Requests
    val emergencyRequests: StateFlow<List<EmergencyRequest>> = repository.emergencyRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All System Donations
    val allDonations: StateFlow<List<Donation>> = repository.allDonations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // System Notifications
    private val _notifications = MutableStateFlow<List<NotificationItem>>(
        listOf(
            NotificationItem("N1", "Welcome to Nearblood", "Thank you for joining the blood donation network!", "Just now", "info"),
            NotificationItem("N2", "Eligibility Reminder", "You are eligible to donate blood today!", "Today", "eligibility")
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // All Donors from Room Flow
    val allDonors: StateFlow<List<Donor>> = repository.allDonors.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filtered Donors List
    val filteredDonors: StateFlow<List<Donor>> = combine(
        allDonors,
        searchQuery,
        selectedBloodGroup,
        selectedCity,
        onlyAvailable
    ) { donors, query, group, city, availableOnly ->
        donors.filter { donor ->
            val matchesQuery = query.isEmpty() ||
                    donor.name.contains(query, ignoreCase = true) ||
                    donor.city.contains(query, ignoreCase = true) ||
                    donor.phone.contains(query, ignoreCase = true)

            val matchesGroup = group == "ALL" || donor.bloodGroup.equals(group, ignoreCase = true)
            val matchesCity = city == "ALL" || donor.city.equals(city, ignoreCase = true)
            val matchesAvailable = !availableOnly || donor.available

            // Regular donors must be approved by Admin to show in public directory
            val isPubliclyVisible = donor.approved || donor.role == "admin"

            matchesQuery && matchesGroup && matchesCity && matchesAvailable && isPubliclyVisible
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pending Donors for Admin Approval Screen
    val pendingDonors: StateFlow<List<Donor>> = allDonors.map { list ->
        list.filter { !it.approved && it.role != "admin" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Ambulances Flow
    val ambulances: StateFlow<List<Ambulance>> = repository.allAmbulances.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Firebase Realtime Database SOS Alerts & Ambulance Requests
    val sosAlerts: StateFlow<List<SosAlert>> = repository.sosAlerts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val liveAmbulanceRequests: StateFlow<List<EmergencyAmbulanceRequest>> = repository.liveAmbulanceRequests.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val isRealtimeDbConnected: StateFlow<Boolean> = repository.isRealtimeDbConnected.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    private val _dismissedAlertIds = MutableStateFlow<Set<String>>(emptySet())
    val dismissedAlertIds: StateFlow<Set<String>> = _dismissedAlertIds.asStateFlow()

    // Alert specifically targeted to the current active donor based on city / registered area
    val activeSosAlertForCurrentDonor: StateFlow<SosAlert?> = combine(
        sosAlerts,
        currentUser,
        _dismissedAlertIds
    ) { alerts, user, dismissed ->
        alerts.firstOrNull { alert ->
            alert.status == "ACTIVE" &&
            !dismissed.contains(alert.alertId) &&
            (user == null || user.city.isBlank() || alert.city.equals(user.city, ignoreCase = true) || alert.targetDonorUids.contains(user.uid))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Active User Donations Flow
    val activeUserDonations: StateFlow<List<Donation>> = currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getDonationsForDonor(user.uid)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            repository.seedInitialDataIfEmpty()
        }
        viewModelScope.launch {
            allDonors.collect { list ->
                if (list.isNotEmpty() && _currentUser.value == null) {
                    _currentUser.value = list.firstOrNull { it.uid == "D101" } ?: list.first()
                }
            }
        }
    }

    // Filter Handlers
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setSelectedBloodGroup(group: String) { _selectedBloodGroup.value = group }
    fun setSelectedCity(city: String) { _selectedCity.value = city }
    fun setOnlyAvailable(available: Boolean) { _onlyAvailable.value = available }

    // User Session Handlers
    fun switchUser(donor: Donor) {
        _currentUser.value = donor
        _isAdminMode.value = (donor.role == "admin" || donor.role == "super_admin")
        Toast.makeText(getApplication(), "Logged in as ${donor.name} (${donor.role.replace('_', ' ').uppercase()})", Toast.LENGTH_SHORT).show()
    }

    fun registerDonorAccount(
        name: String,
        email: String,
        bloodGroup: String,
        city: String,
        phone: String,
        emergencyName: String,
        emergencyPhone: String,
        isGoogleSignIn: Boolean = false
    ) {
        val newUid = "D${System.currentTimeMillis().toString().takeLast(5)}"
        val newDonor = Donor(
            uid = newUid,
            name = name,
            email = email,
            role = "donor",
            approved = isGoogleSignIn, // Auto-approve Google sign-in demo or require admin
            photoUrl = "",
            bloodGroup = bloodGroup,
            city = city,
            phone = phone,
            verified = false,
            available = true,
            lastDonationDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
            healthStatus = "Fit & Eligible",
            donationCount = 0,
            emergencyContactName = emergencyName,
            emergencyContactPhone = emergencyPhone,
            emergencyRelation = "Family",
            qrCode = "NEARBLOOD-DONOR-$newUid"
        )

        viewModelScope.launch {
            repository.saveDonor(newDonor)
            _currentUser.value = newDonor
            addNotification("Account Registered", "Welcome $name! Your profile is ${if (newDonor.approved) "active" else "submitted for admin approval"}.", "approval")
            Toast.makeText(getApplication(), "Registration successful! ${if (!newDonor.approved) "Pending Admin Approval." else ""}", Toast.LENGTH_LONG).show()
        }
    }

    // Toggle Availability
    fun toggleAvailability(available: Boolean) {
        val current = _currentUser.value ?: return
        val updated = current.copy(available = available)
        _currentUser.value = updated
        viewModelScope.launch {
            repository.updateDonorAvailability(current.uid, available)
            Toast.makeText(getApplication(), "Status set to ${if (available) "AVAILABLE" else "BUSY"}", Toast.LENGTH_SHORT).show()
        }
    }

    // Admin & Super Admin Operations
    fun approveDonor(uid: String) {
        viewModelScope.launch {
            repository.updateDonorApproval(uid, true)
            repository.updateDonorStatus(uid, "active")
            addNotification("Donor Approved", "Donor $uid has been approved and is now active.", "approval")
            Toast.makeText(getApplication(), "Donor approved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun rejectDonor(uid: String) {
        viewModelScope.launch {
            repository.updateDonorApproval(uid, false)
            repository.updateDonorStatus(uid, "rejected")
            addNotification("Donor Rejected", "Donor $uid application was rejected.", "approval")
            Toast.makeText(getApplication(), "Donor registration rejected.", Toast.LENGTH_SHORT).show()
        }
    }

    fun suspendDonor(uid: String) {
        viewModelScope.launch {
            repository.updateDonorStatus(uid, "suspended")
            addNotification("Donor Suspended", "Donor $uid has been temporarily suspended.", "warning")
            Toast.makeText(getApplication(), "Donor account suspended.", Toast.LENGTH_SHORT).show()
        }
    }

    fun restoreDonor(uid: String) {
        viewModelScope.launch {
            repository.updateDonorStatus(uid, "active")
            repository.updateDonorApproval(uid, true)
            addNotification("Donor Restored", "Donor $uid account restored to active status.", "approval")
            Toast.makeText(getApplication(), "Donor account restored to Active status.", Toast.LENGTH_SHORT).show()
        }
    }

    fun editDonorDetails(updatedDonor: Donor) {
        viewModelScope.launch {
            repository.updateDonorDetails(updatedDonor)
            if (_currentUser.value?.uid == updatedDonor.uid) {
                _currentUser.value = updatedDonor
            }
            Toast.makeText(getApplication(), "Donor details updated successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun promoteToAdmin(uid: String) {
        viewModelScope.launch {
            repository.updateDonorRole(uid, "admin")
            Toast.makeText(getApplication(), "Promoted donor to Administrator!", Toast.LENGTH_SHORT).show()
        }
    }

    fun demoteToDonor(uid: String) {
        viewModelScope.launch {
            repository.updateDonorRole(uid, "donor")
            Toast.makeText(getApplication(), "Role updated to standard Donor.", Toast.LENGTH_SHORT).show()
        }
    }

    fun toggleDonorVerification(uid: String, currentVerified: Boolean) {
        viewModelScope.launch {
            repository.updateDonorVerification(uid, !currentVerified)
            Toast.makeText(getApplication(), "Updated donor verification status", Toast.LENGTH_SHORT).show()
        }
    }

    // Emergency Request Management
    fun submitEmergencyRequest(
        patientName: String,
        hospitalName: String,
        bloodGroup: String,
        unitsNeeded: Int,
        phone: String,
        city: String,
        urgency: String,
        notes: String
    ) {
        val reqId = "REQ-${System.currentTimeMillis().toString().takeLast(5)}"
        val request = com.example.data.model.EmergencyRequest(
            requestId = reqId,
            patientName = patientName,
            hospitalName = hospitalName,
            bloodGroup = bloodGroup,
            unitsNeeded = unitsNeeded,
            contactPhone = phone,
            city = city,
            status = "PENDING",
            urgency = urgency,
            notes = notes,
            timestamp = "Just now"
        )
        viewModelScope.launch {
            repository.createEmergencyRequest(request)
            addNotification("Emergency Request Broadcasted", "Urgent request for $unitsNeeded unit(s) of $bloodGroup at $hospitalName", "emergency")
            Toast.makeText(getApplication(), "Emergency request created & broadcasted!", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Dispatches an Emergency Ambulance and broadcasts a live SOS Alert to registered nearby donors in Realtime DB.
     */
    fun sendEmergencyAmbulanceRequestWithSos(
        patientName: String,
        pickupAddress: String,
        destinationHospital: String,
        city: String,
        bloodGroupNeeded: String,
        unitsNeeded: Int,
        contactPhone: String,
        ambulance: Ambulance?,
        patientCondition: String = "Critical Emergency Transit",
        notes: String = "Immediate blood donors needed upon arrival.",
        broadcastSosToDonors: Boolean = true
    ) {
        viewModelScope.launch {
            val reqId = "AMB-${System.currentTimeMillis().toString().takeLast(5)}"
            val alertId = "SOS-${System.currentTimeMillis().toString().takeLast(4)}"

            // Find all registered donors in the same city who are available
            val nearbyDonors = allDonors.value.filter { donor ->
                donor.city.equals(city, ignoreCase = true) && donor.available
            }
            val targetUids = nearbyDonors.map { it.uid }

            val ambRequest = EmergencyAmbulanceRequest(
                requestId = reqId,
                patientName = patientName,
                pickupAddress = pickupAddress,
                destinationHospital = destinationHospital,
                city = city,
                bloodGroupNeeded = bloodGroupNeeded,
                unitsNeeded = unitsNeeded,
                patientCondition = patientCondition,
                requesterName = currentUser.value?.name ?: "Emergency Dispatcher",
                contactPhone = contactPhone,
                ambulanceAssigned = ambulance?.serviceName ?: "RapidCare Emergency Ambulance",
                ambulancePhone = ambulance?.phone ?: "911",
                status = "DISPATCHED",
                sosAlertSent = broadcastSosToDonors,
                nearbyDonorsNotified = nearbyDonors.size,
                timestamp = System.currentTimeMillis()
            )

            val sosAlert = SosAlert(
                alertId = alertId,
                requestId = reqId,
                ambulanceId = ambulance?.id ?: "AMB-1",
                ambulanceServiceName = ambulance?.serviceName ?: "RapidCare Emergency Ambulance",
                patientName = patientName,
                hospitalName = destinationHospital,
                city = city,
                pickupAddress = pickupAddress,
                bloodGroupNeeded = bloodGroupNeeded,
                unitsNeeded = unitsNeeded,
                contactPhone = contactPhone,
                driverPhone = ambulance?.phone ?: "911",
                urgency = "CRITICAL",
                notes = notes,
                timestamp = System.currentTimeMillis(),
                formattedTime = "Just now",
                targetNearbyDonorsCount = nearbyDonors.size,
                targetDonorUids = targetUids,
                responderDonorsCount = 0,
                responderDonorNames = emptyList(),
                status = "ACTIVE"
            )

            repository.requestAmbulanceWithSos(ambRequest, sosAlert)

            addNotification(
                title = "🚨 Emergency Ambulance Dispatched",
                message = "Ambulance en route to $destinationHospital ($city). SOS alert sent to ${nearbyDonors.size} nearby donors!",
                type = "emergency"
            )

            Toast.makeText(
                getApplication(),
                "Ambulance Dispatched! Live SOS sent to ${nearbyDonors.size} nearby donors in $city.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun respondToSosAlert(alertId: String) {
        val donorName = currentUser.value?.name ?: "Registered Donor"
        val donorUid = currentUser.value?.uid ?: ""
        viewModelScope.launch {
            repository.respondToSosAlert(alertId, donorName, donorUid)
            addNotification(
                title = "SOS Response Acknowledged",
                message = "Thank you $donorName! Hospital & ambulance driver have been notified of your response.",
                type = "eligibility"
            )
            Toast.makeText(
                getApplication(),
                "Response Sent! Ambulance team & hospital notified that you are on your way.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun dismissSosAlert(alertId: String) {
        _dismissedAlertIds.value = _dismissedAlertIds.value + alertId
    }

    fun resolveSosAlert(alertId: String) {
        viewModelScope.launch {
            repository.updateSosAlertStatus(alertId, "RESOLVED")
            Toast.makeText(getApplication(), "SOS alert marked as resolved.", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateEmergencyRequestStatus(requestId: String, status: String) {
        viewModelScope.launch {
            repository.updateEmergencyStatus(requestId, status)
            Toast.makeText(getApplication(), "Emergency request status changed to $status", Toast.LENGTH_SHORT).show()
        }
    }

    // Send Email Notification Intent
    fun sendEmailNotification(context: Context, email: String, subject: String, body: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Email client unavailable for $email", Toast.LENGTH_SHORT).show()
        }
    }

    // Export Donor Data CSV / Share
    fun exportDonorDataCsv(context: Context) {
        val donors = allDonors.value
        val sb = StringBuilder()
        sb.append("UID,Name,Email,Role,Approved,Status,BloodGroup,City,Phone,Verified,DonationCount,LastDonationDate\n")
        donors.forEach { d ->
            sb.append("${d.uid},\"${d.name}\",\"${d.email}\",${d.role},${d.approved},${d.status},${d.bloodGroup},\"${d.city}\",\"${d.phone}\",${d.verified},${d.donationCount},${d.lastDonationDate}\n")
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "Nearblood_Donor_Directory_Export.csv")
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(Intent.createChooser(shareIntent, "Export Donor Data (CSV)"))
        } catch (e: Exception) {
            Toast.makeText(context, "Export ready (${donors.size} records)", Toast.LENGTH_SHORT).show()
        }
    }

    // Add New Donation Log
    fun logNewDonation(hospitalName: String, units: Int) {
        val current = _currentUser.value ?: return
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val donationId = "DON-${(100..999).random()}"
        val newDonation = Donation(
            donationId = donationId,
            donorId = current.uid,
            hospitalName = hospitalName,
            date = todayStr,
            units = units,
            bloodGroup = current.bloodGroup
        )

        viewModelScope.launch {
            repository.addDonation(newDonation)
            val updatedUser = current.copy(
                donationCount = current.donationCount + 1,
                lastDonationDate = todayStr
            )
            _currentUser.value = updatedUser
            addNotification("Donation Recorded", "Thank you for donating $units unit(s) at $hospitalName!", "eligibility")
            Toast.makeText(getApplication(), "Donation recorded & certificate generated!", Toast.LENGTH_LONG).show()
        }
    }

    // Emergency Quick Call / WhatsApp Intent Triggers
    fun makePhoneCall(context: Context, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Call action unavailable: $phoneNumber", Toast.LENGTH_SHORT).show()
        }
    }

    fun openWhatsApp(context: Context, phoneNumber: String) {
        val cleanPhone = phoneNumber.replace(Regex("[^0-9+]"), "")
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://api.whatsapp.com/send?phone=$cleanPhone&text=Hello%20from%20Nearblood%20App,%20I%20am%20reaching%20out%20regarding%20a%20blood%20donation%20request.")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Opening browser for WhatsApp...", Toast.LENGTH_SHORT).show()
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$cleanPhone"))
            webIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(webIntent)
        }
    }

    // Eligibility Calculation helper
    fun calculateEligibility(lastDonationDateStr: String): EligibilityInfo {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val lastDate = sdf.parse(lastDonationDateStr) ?: Date()
            val diffMs = Date().time - lastDate.time
            val daysPassed = (diffMs / (1000 * 60 * 60 * 24)).toInt()
            val requiredGap = 56 // 56 days mandatory gap between donations

            if (daysPassed >= requiredGap) {
                EligibilityInfo(
                    isEligible = true,
                    daysRemaining = 0,
                    progress = 1.0f,
                    statusText = "Eligible to Donate Now! 🎉",
                    daysPassed = daysPassed
                )
            } else {
                val remaining = requiredGap - daysPassed
                val prog = daysPassed.toFloat() / requiredGap.toFloat()
                EligibilityInfo(
                    isEligible = false,
                    daysRemaining = remaining,
                    progress = prog.coerceIn(0f, 1f),
                    statusText = "Eligible in $remaining day(s)",
                    daysPassed = daysPassed
                )
            }
        } catch (e: Exception) {
            EligibilityInfo(true, 0, 1.0f, "Eligible to Donate", 60)
        }
    }

    fun saveDonorProfile(profile: DonorProfile, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            _firestoreSyncStatus.value = FirestoreSyncStatus.Syncing
            try {
                repository.saveDonorProfile(profile)
                _currentUser.value = profile.toDonor()
                _firestoreSyncStatus.value = FirestoreSyncStatus.Synced("Stored in Firestore (donor_profiles/${profile.userId})")
                addNotification("Profile Updated", "Donor profile saved and synchronized with Firestore cloud.", "info")
                Toast.makeText(getApplication(), "Donor profile successfully stored in Firestore!", Toast.LENGTH_SHORT).show()
                onComplete?.invoke(true)
            } catch (e: Exception) {
                _firestoreSyncStatus.value = FirestoreSyncStatus.Error(e.message ?: "Failed to sync with Firestore")
                Toast.makeText(getApplication(), "Saved locally (Firestore offline: ${e.message})", Toast.LENGTH_SHORT).show()
                onComplete?.invoke(false)
            }
        }
    }

    fun syncDonorProfileWithCloud() {
        val current = _currentUser.value ?: return
        val profile = DonorProfile.fromDonor(current)
        saveDonorProfile(profile)
    }

    private fun addNotification(title: String, message: String, type: String) {
        val newItem = NotificationItem(
            id = "N${System.currentTimeMillis().toString().takeLast(4)}",
            title = title,
            message = message,
            timestamp = "Just now",
            type = type
        )
        _notifications.value = listOf(newItem) + _notifications.value
    }
}

sealed class FirestoreSyncStatus {
    object Idle : FirestoreSyncStatus()
    object Syncing : FirestoreSyncStatus()
    data class Synced(val message: String = "Synchronized with Firestore") : FirestoreSyncStatus()
    data class Error(val error: String) : FirestoreSyncStatus()
}

data class EligibilityInfo(
    val isEligible: Boolean,
    val daysRemaining: Int,
    val progress: Float,
    val statusText: String,
    val daysPassed: Int
)
