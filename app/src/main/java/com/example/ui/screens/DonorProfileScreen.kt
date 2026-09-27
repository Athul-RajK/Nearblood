package com.example.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DonorProfile
import com.example.ui.theme.*
import com.example.ui.viewmodel.BloodViewModel
import com.example.ui.viewmodel.FirestoreSyncStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorProfileScreen(
    viewModel: BloodViewModel,
    onNavigateBack: () -> Unit = {},
    onOpenAuth: () -> Unit = {},
    onViewPass: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val syncStatus by viewModel.firestoreSyncStatus.collectAsState()

    var isEditMode by remember { mutableStateOf(false) }

    // Intercept hardware back button when in edit mode
    BackHandler(enabled = isEditMode) {
        isEditMode = false
    }

    if (currentUser == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NearBgLight)
                .statusBarsPadding()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(NearPurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonSearch,
                        contentDescription = "No Profile",
                        tint = NearPurplePrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Donor Profile Found",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NearTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign in or register a profile to manage blood type, donation dates, and contact details in Firestore.",
                    fontSize = 14.sp,
                    color = NearTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onOpenAuth,
                    colors = ButtonDefaults.buttonColors(containerColor = NearPurplePrimary),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp)
                        .testTag("signin_register_button")
                ) {
                    Text("Sign In / Register Donor", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    val donor = currentUser!!
    val currentProfile = remember(donor) { DonorProfile.fromDonor(donor) }

    // Editable state holders
    var editedFullName by remember(donor) { mutableStateOf(donor.name) }
    var editedBloodType by remember(donor) { mutableStateOf(donor.bloodGroup) }
    var editedLastDonationDate by remember(donor) { mutableStateOf(donor.lastDonationDate) }
    var editedPhone by remember(donor) { mutableStateOf(donor.phone) }
    var editedEmail by remember(donor) { mutableStateOf(donor.email) }
    var editedCity by remember(donor) { mutableStateOf(donor.city) }
    var editedAddress by remember(donor) { mutableStateOf("${donor.city}, United States") }
    var editedEmergencyName by remember(donor) { mutableStateOf(donor.emergencyContactName) }
    var editedEmergencyPhone by remember(donor) { mutableStateOf(donor.emergencyContactPhone) }
    var editedEmergencyRelation by remember(donor) { mutableStateOf(donor.emergencyRelation.ifEmpty { "Family" }) }
    var editedIsAvailable by remember(donor) { mutableStateOf(donor.available) }
    var editedWeightKg by remember(donor) { mutableStateOf("68.0") }
    var editedAge by remember(donor) { mutableStateOf("28") }
    var editedHealthNotes by remember(donor) { mutableStateOf(donor.healthStatus) }

    val eligibilityInfo = remember(editedLastDonationDate) {
        viewModel.calculateEligibility(editedLastDonationDate)
    }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
    val relationOptions = listOf("Spouse", "Parent", "Sibling", "Child", "Friend", "Family", "Guardian")

    // Date Picker Dialog setup
    val calendar = Calendar.getInstance()
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                editedLastDonationDate = formatted
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis() // Donation cannot be in the future
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                if (isEditMode) isEditMode = false else onNavigateBack()
                            },
                            modifier = Modifier.testTag("donor_profile_back_btn")
                        ) {
                            Icon(
                                imageVector = if (isEditMode) Icons.Default.Close else Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = NearTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = if (isEditMode) "Edit Donor Profile" else "Donor Profile",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearTextPrimary
                            )
                            Text(
                                text = "Synced with Firestore",
                                fontSize = 11.sp,
                                color = NearPurplePrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Action Button: Edit or Save
                    if (!isEditMode) {
                        FilledTonalButton(
                            onClick = { isEditMode = true },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = NearPurpleLight,
                                contentColor = NearPurplePrimary
                            ),
                            shape = RoundedCornerShape(18.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("edit_profile_button")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                val updated = DonorProfile(
                                    userId = donor.uid,
                                    fullName = editedFullName.trim().ifEmpty { donor.name },
                                    bloodType = editedBloodType,
                                    lastDonationDate = editedLastDonationDate,
                                    phoneNumber = editedPhone.trim(),
                                    email = editedEmail.trim(),
                                    city = editedCity.trim().ifEmpty { donor.city },
                                    residentialAddress = editedAddress.trim(),
                                    emergencyContactName = editedEmergencyName.trim(),
                                    emergencyContactPhone = editedEmergencyPhone.trim(),
                                    emergencyContactRelation = editedEmergencyRelation,
                                    isAvailable = editedIsAvailable,
                                    weightKg = editedWeightKg.toDoubleOrNull() ?: 68.0,
                                    age = editedAge.toIntOrNull() ?: 28,
                                    healthNotes = editedHealthNotes.trim(),
                                    donationCount = donor.donationCount,
                                    verifiedBadge = donor.verified,
                                    updatedAt = System.currentTimeMillis()
                                )
                                viewModel.saveDonorProfile(updated) { success ->
                                    if (success) {
                                        isEditMode = false
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NearPurplePrimary),
                            shape = RoundedCornerShape(18.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("save_profile_button")
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        containerColor = NearBgLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // FIRESTORE CLOUD STATUS BANNER
            item {
                FirestoreStatusBanner(
                    syncStatus = syncStatus,
                    userId = donor.uid,
                    onForceSync = { viewModel.syncDonorProfileWithCloud() }
                )
            }

            // PROFILE HERO CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, NearCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            Box(
                                modifier = Modifier
                                    .size(86.dp)
                                    .clip(CircleShape)
                                    .background(NearPurpleLight)
                                    .border(2.dp, NearPurpleBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = donor.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifEmpty { "DR" },
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NearPurplePrimary
                                )
                            }
                            // Blood Group badge on avatar
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(NearRedAccent)
                                    .border(2.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isEditMode) editedBloodType else donor.bloodGroup,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (isEditMode) {
                            OutlinedTextField(
                                value = editedFullName,
                                onValueChange = { editedFullName = it },
                                label = { Text("Full Name") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_donor_name"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = donor.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NearTextPrimary
                                )
                                if (donor.verified) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified Donor",
                                        tint = NearPurplePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Donor ID: ${donor.uid} • ${donor.city}",
                                fontSize = 13.sp,
                                color = NearTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Badges Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (donor.available) NearGreenLight else Color(0xFFFEE2E2),
                                border = BorderStroke(1.dp, if (donor.available) NearGreenSuccess.copy(alpha = 0.3f) else NearRedAccent.copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (donor.available) NearGreenSuccess else NearRedAccent)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (donor.available) "Available to Donate" else "Resting / Busy",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (donor.available) Color(0xFF065F46) else Color(0xFF991B1B)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = NearPurpleLight,
                                border = BorderStroke(1.dp, NearPurpleBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolunteerActivism,
                                        contentDescription = null,
                                        tint = NearPurplePrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${donor.donationCount} Donations",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NearPurplePrimary
                                    )
                                }
                            }
                        }

                        if (!isEditMode) {
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedButton(
                                onClick = onViewPass,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, NearPurpleBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .testTag("view_digital_pass_btn")
                            ) {
                                Icon(Icons.Default.Badge, contentDescription = null, tint = NearPurplePrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("View Digital Pass & Certificates", color = NearPurplePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 1. BLOOD TYPE & COMPATIBILITY SECTION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, NearCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NearRedLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WaterDrop,
                                        contentDescription = null,
                                        tint = NearRedAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Blood Group & Compatibility",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NearTextPrimary
                                    )
                                    Text(
                                        text = "Stored as Firestore field: bloodType",
                                        fontSize = 11.sp,
                                        color = NearTextSecondary
                                    )
                                }
                            }

                            // Current Blood Group Pill
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = NearRedAccent
                            ) {
                                Text(
                                    text = if (isEditMode) editedBloodType else donor.bloodGroup,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isEditMode) {
                            Text(
                                text = "Select Blood Group:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NearTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // 4x2 Grid of Blood Groups
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    bloodGroups.take(4).forEach { bg ->
                                        BloodGroupSelectChip(
                                            bloodGroup = bg,
                                            isSelected = editedBloodType == bg,
                                            onSelect = { editedBloodType = bg },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    bloodGroups.drop(4).forEach { bg ->
                                        BloodGroupSelectChip(
                                            bloodGroup = bg,
                                            isSelected = editedBloodType == bg,
                                            onSelect = { editedBloodType = bg },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        } else {
                            // Read-only Compatibility Highlights
                            val bg = donor.bloodGroup
                            val compatibilityText = when (bg) {
                                "O-" -> "Universal Red Cell Donor: Can donate red blood cells to any blood group (O-, O+, A-, A+, B-, B+, AB-, AB+)."
                                "O+" -> "Can donate to O+, A+, B+, AB+. Can receive red cells from O+, O-."
                                "A+" -> "Can donate to A+, AB+. Can receive red cells from A+, A-, O+, O-."
                                "A-" -> "Can donate to A-, A+, AB-, AB+. Can receive red cells from A-, O-."
                                "B+" -> "Can donate to B+, AB+. Can receive red cells from B+, B-, O+, O-."
                                "B-" -> "Can donate to B-, B+, AB-, AB+. Can receive red cells from B-, O-."
                                "AB+" -> "Universal Recipient: Can receive red blood cells of any type. Can donate to AB+."
                                "AB-" -> "Can donate to AB-, AB+. Can receive from AB-, A-, B-, O-."
                                else -> "Compatible across matching blood groups."
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NearBgLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = NearPurplePrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = compatibilityText,
                                        fontSize = 12.sp,
                                        color = NearTextPrimary,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. LAST DONATION DATE & ELIGIBILITY SECTION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, NearCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NearPurpleLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Event,
                                        contentDescription = null,
                                        tint = NearPurplePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Last Donation Date",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NearTextPrimary
                                    )
                                    Text(
                                        text = "Stored as Firestore field: lastDonationDate",
                                        fontSize = 11.sp,
                                        color = NearTextSecondary
                                    )
                                }
                            }

                            Text(
                                text = if (isEditMode) editedLastDonationDate else donor.lastDonationDate,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearPurplePrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isEditMode) {
                            // Date Picker Trigger Row
                            OutlinedButton(
                                onClick = { datePickerDialog.show() },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("date_picker_button")
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Select Date: $editedLastDonationDate",
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Quick Date Presets:",
                                fontSize = 12.sp,
                                color = NearTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            // Quick Presets Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                QuickPresetChip(
                                    label = "Today",
                                    onClick = {
                                        editedLastDonationDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                QuickPresetChip(
                                    label = "1 Mo Ago",
                                    onClick = {
                                        val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
                                        editedLastDonationDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                QuickPresetChip(
                                    label = "3 Mo Ago",
                                    onClick = {
                                        val cal = Calendar.getInstance().apply { add(Calendar.MONTH, -3) }
                                        editedLastDonationDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamic Eligibility Progress Card
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (eligibilityInfo.isEligible) NearGreenLight.copy(alpha = 0.5f) else NearAmberLight.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, if (eligibilityInfo.isEligible) NearGreenSuccess.copy(alpha = 0.3f) else NearAmberWarning.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = eligibilityInfo.statusText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (eligibilityInfo.isEligible) Color(0xFF065F46) else Color(0xFF92400E)
                                    )
                                    Text(
                                        text = "${eligibilityInfo.daysPassed} days since last donation",
                                        fontSize = 11.sp,
                                        color = NearTextSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { eligibilityInfo.progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (eligibilityInfo.isEligible) NearGreenSuccess else NearAmberWarning,
                                    trackColor = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Standard recovery interval is 56 days between whole blood donations.",
                                    fontSize = 11.sp,
                                    color = NearTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 3. CONTACT DETAILS SECTION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, NearCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(NearPurpleLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    tint = NearPurplePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Contact Details",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NearTextPrimary
                                )
                                Text(
                                    text = "Phone, email, address stored in Firestore",
                                    fontSize = 11.sp,
                                    color = NearTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isEditMode) {
                            OutlinedTextField(
                                value = editedPhone,
                                onValueChange = { editedPhone = it },
                                label = { Text("Primary Phone Number") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_donor_phone"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = editedEmail,
                                onValueChange = { editedEmail = it },
                                label = { Text("Email Address") },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_donor_email"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = editedCity,
                                onValueChange = { editedCity = it },
                                label = { Text("City") },
                                leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_donor_city"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = editedAddress,
                                onValueChange = { editedAddress = it },
                                label = { Text("Residential Address") },
                                leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_donor_address"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            // View Mode: Contact Rows with Action Triggers
                            ContactItemRow(
                                icon = Icons.Default.Phone,
                                label = "Phone",
                                value = donor.phone.ifEmpty { "Not specified" },
                                onAction = {
                                    if (donor.phone.isNotEmpty()) viewModel.makePhoneCall(context, donor.phone)
                                },
                                actionIcon = Icons.Default.Call,
                                actionTooltip = "Call"
                            )

                            Divider(color = NearDivider, modifier = Modifier.padding(vertical = 8.dp))

                            ContactItemRow(
                                icon = Icons.Default.Email,
                                label = "Email",
                                value = donor.email.ifEmpty { "Not specified" },
                                onAction = {
                                    if (donor.email.isNotEmpty()) {
                                        viewModel.sendEmailNotification(context, donor.email, "Nearblood Donor Communication", "Hello ${donor.name},\n")
                                    }
                                },
                                actionIcon = Icons.Default.Send,
                                actionTooltip = "Email"
                            )

                            Divider(color = NearDivider, modifier = Modifier.padding(vertical = 8.dp))

                            ContactItemRow(
                                icon = Icons.Default.LocationOn,
                                label = "City & Area",
                                value = "${donor.city}, United States",
                                onAction = null,
                                actionIcon = null,
                                actionTooltip = null
                            )
                        }
                    }
                }
            }

            // 4. EMERGENCY (ICE) CONTACT SECTION
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, NearCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NearRedLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContactEmergency,
                                        contentDescription = null,
                                        tint = NearRedAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Emergency Contact (ICE)",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NearTextPrimary
                                    )
                                    Text(
                                        text = "Notified during medical or transit events",
                                        fontSize = 11.sp,
                                        color = NearTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isEditMode) {
                            OutlinedTextField(
                                value = editedEmergencyName,
                                onValueChange = { editedEmergencyName = it },
                                label = { Text("Emergency Contact Name") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_ice_name"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = editedEmergencyPhone,
                                onValueChange = { editedEmergencyPhone = it },
                                label = { Text("Emergency Contact Phone") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_ice_phone"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = editedEmergencyRelation,
                                onValueChange = { editedEmergencyRelation = it },
                                label = { Text("Relationship (e.g. Spouse, Parent)") },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_ice_relation"),
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = donor.emergencyContactName.ifEmpty { "Family Member / Emergency Contact" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = NearTextPrimary
                                    )
                                    Text(
                                        text = "Relation: ${donor.emergencyRelation.ifEmpty { "Family" }}",
                                        fontSize = 12.sp,
                                        color = NearTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = donor.emergencyContactPhone.ifEmpty { donor.phone },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = NearPurplePrimary
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val targetPhone = donor.emergencyContactPhone.ifEmpty { donor.phone }
                                        if (targetPhone.isNotEmpty()) {
                                            viewModel.makePhoneCall(context, targetPhone)
                                        }
                                    },
                                    modifier = Modifier
                                        .background(NearPurpleLight, CircleShape)
                                        .size(44.dp)
                                        .testTag("call_ice_btn")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Call ICE Contact",
                                        tint = NearPurplePrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. AVAILABILITY & HEALTH CRITERIA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, NearCardBorder)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ready to Donate (Availability)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = NearTextPrimary
                                )
                                Text(
                                    text = if (if (isEditMode) editedIsAvailable else donor.available)
                                        "Visible to patients in emergency requests"
                                    else
                                        "Hidden from instant search (Resting/Busy)",
                                    fontSize = 12.sp,
                                    color = NearTextSecondary
                                )
                            }

                            Switch(
                                checked = if (isEditMode) editedIsAvailable else donor.available,
                                onCheckedChange = {
                                    if (isEditMode) {
                                        editedIsAvailable = it
                                    } else {
                                        viewModel.toggleAvailability(it)
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = NearPurplePrimary,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFD6CEF2)
                                ),
                                modifier = Modifier.testTag("availability_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isEditMode) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = editedWeightKg,
                                    onValueChange = { editedWeightKg = it },
                                    label = { Text("Weight (kg)") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )

                                OutlinedTextField(
                                    value = editedAge,
                                    onValueChange = { editedAge = it },
                                    label = { Text("Age (yrs)") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = editedHealthNotes,
                                onValueChange = { editedHealthNotes = it },
                                label = { Text("Health & Medical Notes") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NearBgLight,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = NearGreenSuccess, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Health Status: ${donor.healthStatus}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NearTextPrimary)
                                        Text("Weight & Age Criteria: ≥50kg • 18-65 years", fontSize = 11.sp, color = NearTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SAVE TO FIRESTORE ACTION (in Edit Mode)
            if (isEditMode) {
                item {
                    Button(
                        onClick = {
                            val updated = DonorProfile(
                                userId = donor.uid,
                                fullName = editedFullName.trim().ifEmpty { donor.name },
                                bloodType = editedBloodType,
                                lastDonationDate = editedLastDonationDate,
                                phoneNumber = editedPhone.trim(),
                                email = editedEmail.trim(),
                                city = editedCity.trim().ifEmpty { donor.city },
                                residentialAddress = editedAddress.trim(),
                                emergencyContactName = editedEmergencyName.trim(),
                                emergencyContactPhone = editedEmergencyPhone.trim(),
                                emergencyContactRelation = editedEmergencyRelation,
                                isAvailable = editedIsAvailable,
                                weightKg = editedWeightKg.toDoubleOrNull() ?: 68.0,
                                age = editedAge.toIntOrNull() ?: 28,
                                healthNotes = editedHealthNotes.trim(),
                                donationCount = donor.donationCount,
                                verifiedBadge = donor.verified,
                                updatedAt = System.currentTimeMillis()
                            )
                            viewModel.saveDonorProfile(updated) { success ->
                                if (success) {
                                    isEditMode = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NearPurplePrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_firestore_save_btn")
                    ) {
                        Icon(Icons.Default.CloudDone, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save Profile to Firestore",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ---------------------- SUB-COMPONENTS ----------------------

@Composable
private fun FirestoreStatusBanner(
    syncStatus: FirestoreSyncStatus,
    userId: String,
    onForceSync: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = when (syncStatus) {
            is FirestoreSyncStatus.Syncing -> NearAmberLight
            is FirestoreSyncStatus.Error -> Color(0xFFFEE2E2)
            else -> NearPurpleLight
        },
        border = BorderStroke(
            1.dp,
            when (syncStatus) {
                is FirestoreSyncStatus.Syncing -> NearAmberWarning.copy(alpha = 0.4f)
                is FirestoreSyncStatus.Error -> NearRedAccent.copy(alpha = 0.4f)
                else -> NearPurpleBorder
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                when (syncStatus) {
                    is FirestoreSyncStatus.Syncing -> {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            color = NearAmberWarning,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    is FirestoreSyncStatus.Error -> {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = "Sync Error",
                            tint = NearRedAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    else -> {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Cloud Connected",
                            tint = NearPurplePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = when (syncStatus) {
                            is FirestoreSyncStatus.Syncing -> "Syncing with Firestore..."
                            is FirestoreSyncStatus.Error -> "Offline Cache (Room Sync)"
                            is FirestoreSyncStatus.Synced -> "Firestore Collection: donor_profiles"
                            else -> "Cloud Synchronized"
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NearTextPrimary
                    )
                    Text(
                        text = "Document ID: $userId",
                        fontSize = 10.sp,
                        color = NearTextSecondary
                    )
                }
            }

            TextButton(
                onClick = onForceSync,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.testTag("force_cloud_sync_btn")
            ) {
                Text(
                    text = "Sync Now",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NearPurplePrimary
                )
            }
        }
    }
}

@Composable
private fun BloodGroupSelectChip(
    bloodGroup: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) NearPurplePrimary else Color.White,
        border = BorderStroke(1.dp, if (isSelected) NearPurplePrimary else NearCardBorder),
        modifier = modifier.height(44.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = bloodGroup,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isSelected) Color.White else NearTextPrimary
            )
        }
    }
}

@Composable
private fun QuickPresetChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = NearBgLight,
        border = BorderStroke(1.dp, NearCardBorder),
        modifier = modifier.height(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = NearTextPrimary
            )
        }
    }
}

@Composable
private fun ContactItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onAction: (() -> Unit)?,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector?,
    actionTooltip: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(NearBgLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = NearPurplePrimary, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = label, fontSize = 11.sp, color = NearTextSecondary)
                Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = NearTextPrimary)
            }
        }

        if (onAction != null && actionIcon != null) {
            IconButton(
                onClick = onAction,
                modifier = Modifier
                    .size(36.dp)
                    .background(NearPurpleLight, CircleShape)
            ) {
                Icon(
                    imageVector = actionIcon,
                    contentDescription = actionTooltip,
                    tint = NearPurplePrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
