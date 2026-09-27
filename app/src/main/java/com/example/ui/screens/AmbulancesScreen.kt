package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ambulance
import com.example.data.model.SosAlert
import com.example.ui.components.RequestAmbulanceSosDialog
import com.example.ui.components.SosEmergencyDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.BloodViewModel

@Composable
fun AmbulancesScreen(
    viewModel: BloodViewModel
) {
    val context = LocalContext.current
    val ambulances by viewModel.ambulances.collectAsState()
    val sosAlerts by viewModel.sosAlerts.collectAsState()
    val isRealtimeDbConnected by viewModel.isRealtimeDbConnected.collectAsState()

    var showRequestAmbulanceDialog by remember { mutableStateOf(false) }
    var selectedAmbulanceForRequest by remember { mutableStateOf<Ambulance?>(null) }
    var selectedSosAlertForDetail by remember { mutableStateOf<SosAlert?>(null) }

    val activeSosAlerts = remember(sosAlerts) {
        sosAlerts.filter { it.status == "ACTIVE" }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearBgLight)
    ) {
        // TOP HEADER
        Surface(color = Color.White, shadowElevation = 0.5.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = NearRedLight,
                        modifier = Modifier.size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🚑", fontSize = 22.sp)
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Ambulances & SOS",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = NearTextPrimary
                        )
                        Text(
                            text = "24x7 Transit & Real-time Alerts",
                            fontSize = 12.sp,
                            color = NearTextSecondary
                        )
                    }
                }

                // Realtime Database connection badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isRealtimeDbConnected) NearGreenLight else NearAmberLight,
                    border = BorderStroke(1.dp, if (isRealtimeDbConnected) NearGreenSuccess.copy(alpha = 0.4f) else NearAmberWarning.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (isRealtimeDbConnected) NearGreenSuccess else NearAmberWarning)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isRealtimeDbConnected) "Firebase Live ⚡" else "Connecting...",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRealtimeDbConnected) Color(0xFF065F46) else Color(0xFF92400E)
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. DISPATCH AMBULANCE & BROADCAST SOS HERO CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, NearRedAccent.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NearRedAccent
                            ) {
                                Text(
                                    text = "EMERGENCY DISPATCH",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Realtime Broadcast",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NearRedAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Need an Emergency Ambulance?",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = NearTextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Dispatches the nearest ambulance unit and broadcasts an instant SOS alert to all registered donors in the area via Firebase Realtime Database.",
                            fontSize = 13.sp,
                            color = NearTextSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                selectedAmbulanceForRequest = ambulances.firstOrNull()
                                showRequestAmbulanceDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NearRedAccent),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("request_ambulance_sos_btn")
                        ) {
                            Icon(Icons.Default.EmergencyShare, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Request Ambulance & Send SOS Alert",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // 2. LIVE SOS ALERTS FEED SECTION
            if (activeSosAlerts.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(NearRedAccent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Live SOS Alerts Feed (${activeSosAlerts.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearTextPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NearRedLight
                        ) {
                            Text(
                                text = "Firebase Realtime",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearRedAccent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                items(activeSosAlerts, key = { it.alertId }) { alert ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSosAlertForDetail = alert }
                            .testTag("sos_alert_item_${alert.alertId}"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.5.dp, NearRedBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = NearRedAccent
                                    ) {
                                        Text(
                                            text = "SOS: ${alert.bloodGroupNeeded}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 12.sp,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = alert.patientName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = NearTextPrimary
                                    )
                                }

                                Text(
                                    text = alert.formattedTime,
                                    fontSize = 11.sp,
                                    color = NearTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Destination: ${alert.hospitalName} • ${alert.city}",
                                fontSize = 13.sp,
                                color = NearTextSecondary
                            )

                            if (alert.pickupAddress.isNotEmpty()) {
                                Text(
                                    text = "Pickup: ${alert.pickupAddress}",
                                    fontSize = 12.sp,
                                    color = NearTextSecondary
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.People, contentDescription = null, tint = NearPurplePrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${alert.targetNearbyDonorsCount} Donors Alerted",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NearPurplePrimary
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    FilledTonalButton(
                                        onClick = { selectedSosAlertForDetail = alert },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = NearRedLight, contentColor = NearRedAccent),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text("View & Respond", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. AVAILABLE AMBULANCE SERVICES DIRECTORY
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ambulance Units",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NearTextPrimary
                    )
                    Text(
                        text = "${ambulances.size} Available",
                        fontSize = 12.sp,
                        color = NearTextSecondary
                    )
                }
            }

            items(ambulances) { amb ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, NearCardBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(NearPurpleLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🚑", fontSize = 22.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = amb.serviceName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = NearTextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.LocationOn,
                                        contentDescription = null,
                                        tint = NearTextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${amb.city} • Plate: ${amb.vehicleNo}",
                                        fontSize = 12.sp,
                                        color = NearTextSecondary
                                    )
                                }
                                if (amb.isEmergency24x7) {
                                    Text(
                                        text = "24x7 Rapid Dispatch Available",
                                        fontSize = 11.sp,
                                        color = NearGreenSuccess,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Actions: Direct Call & Dispatch with SOS
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Dispatch + SOS Button
                            FilledTonalButton(
                                onClick = {
                                    selectedAmbulanceForRequest = amb
                                    showRequestAmbulanceDialog = true
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = NearRedLight,
                                    contentColor = NearRedAccent
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("SOS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Call Action Button
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${amb.phone}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, NearPurpleBorder, CircleShape)
                                    .background(NearPurpleLight)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Call",
                                    tint = NearPurplePrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // DISPATCH AMBULANCE & SOS DIALOG
    if (showRequestAmbulanceDialog) {
        RequestAmbulanceSosDialog(
            preselectedAmbulance = selectedAmbulanceForRequest,
            viewModel = viewModel,
            onDismiss = { showRequestAmbulanceDialog = false }
        )
    }

    // SOS DETAIL & RESPONSE DIALOG
    selectedSosAlertForDetail?.let { alert ->
        SosEmergencyDialog(
            alert = alert,
            viewModel = viewModel,
            onDismiss = { selectedSosAlertForDetail = null }
        )
    }
}
