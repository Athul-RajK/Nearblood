package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donation
import com.example.ui.components.DigitalIdCard
import com.example.ui.components.DonationCertificateView
import com.example.ui.theme.*
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: BloodViewModel,
    onOpenAuth: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val userDonations by viewModel.activeUserDonations.collectAsState()

    var showDigitalPassView by remember { mutableStateOf(false) }
    var showLogDonationDialog by remember { mutableStateOf(false) }
    var selectedCertificateDonation by remember { mutableStateOf<Donation?>(null) }

    var hospitalInput by remember { mutableStateOf("") }
    var unitsInput by remember { mutableStateOf("1") }

    androidx.activity.compose.BackHandler(enabled = showDigitalPassView) {
        showDigitalPassView = false
    }

    if (currentUser == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NearBgLight)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(NearPurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🩸", fontSize = 32.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Active Account",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NearTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Sign in or create a donor profile to view your digital pass and history.",
                    fontSize = 13.sp,
                    color = NearTextSecondary,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onOpenAuth,
                    colors = ButtonDefaults.buttonColors(containerColor = NearPurplePrimary),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("Sign In / Register", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    // Default: Show rich DonorProfileScreen with Firestore integration
    if (!showDigitalPassView) {
        DonorProfileScreen(
            viewModel = viewModel,
            onNavigateBack = { showDigitalPassView = true },
            onOpenAuth = onOpenAuth,
            onViewPass = { showDigitalPassView = true }
        )
        return
    }

    val donor = currentUser!!

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NearBgLight),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // TOP HEADER
        item {
            Surface(color = Color.White, shadowElevation = 0.5.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { showDigitalPassView = false }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back to Donor Profile", tint = NearTextPrimary)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "Digital Pass & ID",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearTextPrimary
                            )
                            Text(
                                text = "Interactive pass card & history",
                                fontSize = 11.sp,
                                color = NearTextSecondary
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = onOpenAuth,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NearPurpleBorder)
                    ) {
                        Text("Switch User", fontSize = 12.sp, color = NearPurplePrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // QUICK LINK BACK TO DONOR PROFILE (FIRESTORE)
        item {
            Card(
                onClick = { showDigitalPassView = false },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NearPurpleLight),
                border = androidx.compose.foundation.BorderStroke(1.dp, NearPurpleBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = NearPurplePrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Donor Profile (Firestore)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NearTextPrimary)
                        Text("Blood Type: ${donor.bloodGroup} • Last Donated: ${donor.lastDonationDate}", fontSize = 11.sp, color = NearTextSecondary)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NearPurplePrimary, modifier = Modifier.size(18.dp))
                }
            }
        }

        // DIGITAL DONOR ID CARD
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                Text(
                    text = "Digital Donor Pass",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NearTextPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                DigitalIdCard(donor = donor)
            }
        }

        // AVAILABILITY TOGGLE CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, NearCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Availability for Donation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NearTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (donor.available) "Visible to searchers for blood requests" else "Hidden from emergency search",
                            fontSize = 12.sp,
                            color = NearTextSecondary
                        )
                    }

                    Switch(
                        checked = donor.available,
                        onCheckedChange = { viewModel.toggleAvailability(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NearPurplePrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFD6CEF2)
                        )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
        }

        // EMERGENCY CONTACT (ICE) CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, NearCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ContactPhone, contentDescription = null, tint = NearPurplePrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Emergency Contact (ICE)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = NearTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${donor.emergencyContactName.ifEmpty { "Family Contact" }} (${donor.emergencyRelation})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = NearTextPrimary
                        )
                        Text(
                            text = donor.emergencyContactPhone.ifEmpty { donor.phone },
                            fontSize = 12.sp,
                            color = NearTextSecondary
                        )
                    }

                    IconButton(
                        onClick = {
                            val phoneToCall = donor.emergencyContactPhone.ifEmpty { donor.phone }
                            viewModel.makePhoneCall(context, phoneToCall)
                        },
                        modifier = Modifier
                            .background(NearPurpleLight, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Call ICE", tint = NearPurplePrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // DONATION HISTORY HEADER & LOG BUTTON
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Donation Records",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = NearTextPrimary
                )

                Button(
                    onClick = { showLogDonationDialog = true },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NearPurplePrimary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Donation", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // DONATION HISTORY LIST
        if (userDonations.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NearCardBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📜", fontSize = 32.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No donation records yet", fontWeight = FontWeight.Bold, color = NearTextPrimary)
                        Text("Tap 'Log Donation' after your blood donation to earn badges.", fontSize = 12.sp, color = NearTextSecondary)
                    }
                }
            }
        } else {
            items(userDonations, key = { it.donationId }) { donation ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NearCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(donation.hospitalName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NearTextPrimary)
                            Text("Date: ${donation.date} • ${donation.units} Unit(s)", fontSize = 12.sp, color = NearTextSecondary)
                        }

                        TextButton(
                            onClick = { selectedCertificateDonation = donation }
                        ) {
                            Text("Certificate", color = NearPurplePrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // LOG DONATION DIALOG
    if (showLogDonationDialog) {
        AlertDialog(
            onDismissRequest = { showLogDonationDialog = false },
            title = { Text("Log New Donation", fontWeight = FontWeight.Bold, color = NearTextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = hospitalInput,
                        onValueChange = { hospitalInput = it },
                        label = { Text("Hospital / Camp Name") },
                        placeholder = { Text("e.g. Kozhikode Medical College") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = unitsInput,
                        onValueChange = { unitsInput = it },
                        label = { Text("Units Donated") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (hospitalInput.isNotBlank()) {
                            val units = unitsInput.toIntOrNull() ?: 1
                            viewModel.logNewDonation(hospitalInput, units)
                            Toast.makeText(context, "Donation logged successfully!", Toast.LENGTH_SHORT).show()
                            showLogDonationDialog = false
                            hospitalInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NearPurplePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Record")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogDonationDialog = false }) {
                    Text("Cancel", color = NearTextSecondary)
                }
            }
        )
    }

    // CERTIFICATE VIEW DIALOG
    selectedCertificateDonation?.let { donation ->
        AlertDialog(
            onDismissRequest = { selectedCertificateDonation = null },
            confirmButton = {
                TextButton(onClick = { selectedCertificateDonation = null }) {
                    Text("Close", color = NearPurplePrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                DonationCertificateView(
                    donation = donation,
                    donorName = donor.name
                )
            }
        )
    }
}
