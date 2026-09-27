package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.data.model.EmergencyRequest
import com.example.ui.components.BloodDropBadge
import com.example.ui.components.BloodRequestDetailDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(
    viewModel: BloodViewModel
) {
    val context = LocalContext.current
    val emergencyRequests by viewModel.emergencyRequests.collectAsState()
    var selectedFilterGroup by remember { mutableStateOf("ALL") }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var selectedRequestForDetail by remember { mutableStateOf<EmergencyRequest?>(null) }

    val bloodGroups = listOf("ALL", "A+", "B+", "O+", "AB+", "A-", "B-", "O-", "AB-")

    val sampleRequests = remember(emergencyRequests) {
        if (emergencyRequests.isNotEmpty()) emergencyRequests else listOf(
            EmergencyRequest(
                requestId = "REQ-1",
                patientName = "Sreelakshmi",
                hospitalName = "Medical College, Kozhikode",
                bloodGroup = "A+",
                unitsNeeded = 2,
                contactPhone = "+91 98471 23456",
                city = "Kozhikode",
                urgency = "CRITICAL",
                notes = "Patient is in emergency care. Immediate help is appreciated.",
                timestamp = "10m ago"
            ),
            EmergencyRequest(
                requestId = "REQ-2",
                patientName = "Muhammed Rashid",
                hospitalName = "Makaraparamba Clinic",
                bloodGroup = "O+",
                unitsNeeded = 1,
                contactPhone = "+91 94472 88776",
                city = "Calicut",
                urgency = "HIGH",
                notes = "Required for scheduled minor surgery.",
                timestamp = "25m ago"
            ),
            EmergencyRequest(
                requestId = "REQ-3",
                patientName = "Deepak Menon",
                hospitalName = "Aster MIMS Hospital",
                bloodGroup = "B+",
                unitsNeeded = 3,
                contactPhone = "+91 98950 11223",
                city = "Kozhikode",
                urgency = "CRITICAL",
                notes = "Urgent requirement for emergency cardiac ICU.",
                timestamp = "45m ago"
            ),
            EmergencyRequest(
                requestId = "REQ-4",
                patientName = "Ananya Nair",
                hospitalName = "Baby Memorial Hospital",
                bloodGroup = "AB+",
                unitsNeeded = 1,
                contactPhone = "+91 94460 33445",
                city = "Kozhikode",
                urgency = "HIGH",
                notes = "Platelet / whole blood requirement.",
                timestamp = "2h ago"
            )
        )
    }

    val filteredList = remember(sampleRequests, selectedFilterGroup) {
        if (selectedFilterGroup == "ALL") sampleRequests
        else sampleRequests.filter { it.bloodGroup.equals(selectedFilterGroup, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearBgLight)
    ) {
        // TOP HEADER
        Surface(color = Color.White, shadowElevation = 0.5.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Blood Requests",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = NearTextPrimary
                        )
                        Text(
                            text = "Active emergency blood requirements",
                            fontSize = 12.sp,
                            color = NearTextSecondary
                        )
                    }

                    Button(
                        onClick = { showEmergencyDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NearPurplePrimary),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Post Need", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // FILTER CHIPS
        LazyRow(
            modifier = Modifier.padding(vertical = 10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(bloodGroups) { group ->
                val isSelected = selectedFilterGroup == group
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilterGroup = group },
                    label = { Text(group, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NearPurplePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = NearTextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) NearPurplePrimary else NearCardBorder,
                        selectedBorderColor = NearPurplePrimary
                    )
                )
            }
        }

        // REQUESTS LIST
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredList) { req ->
                RecentRequestCard(
                    request = req,
                    onClick = { selectedRequestForDetail = req }
                )
            }
        }
    }

    // DETAIL DIALOG
    selectedRequestForDetail?.let { req ->
        BloodRequestDetailDialog(
            request = req,
            onDismiss = { selectedRequestForDetail = null },
            onAcceptDonation = {
                Toast.makeText(context, "Thank you! Contacting ${req.patientName}", Toast.LENGTH_LONG).show()
                selectedRequestForDetail = null
            },
            onMessageRequester = {
                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:${req.contactPhone}")
                    putExtra("sms_body", "Hi ${req.patientName}, I can help donate ${req.bloodGroup} blood at ${req.hospitalName}.")
                }
                context.startActivity(smsIntent)
            }
        )
    }

    // CREATE EMERGENCY DIALOG
    if (showEmergencyDialog) {
        CreateEmergencyDialog(
            onDismiss = { showEmergencyDialog = false },
            onSubmit = { patientName, hospital, bg, units, phone, city, notes ->
                viewModel.submitEmergencyRequest(
                    patientName = patientName,
                    hospitalName = hospital,
                    bloodGroup = bg,
                    unitsNeeded = units,
                    phone = phone,
                    city = city,
                    urgency = "CRITICAL",
                    notes = notes
                )
                Toast.makeText(context, "Emergency Broadcast Sent!", Toast.LENGTH_SHORT).show()
                showEmergencyDialog = false
            }
        )
    }
}
