package com.example.ui.components

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SosAlert
import com.example.ui.theme.*
import com.example.ui.viewmodel.BloodViewModel

@Composable
fun SosEmergencyDialog(
    alert: SosAlert,
    viewModel: BloodViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isAlreadyResponding = remember(alert, currentUser) {
        currentUser?.name?.let { alert.responderDonorNames.contains(it) } ?: false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(28.dp))
                .border(2.dp, NearRedAccent.copy(alpha = 0.5f), RoundedCornerShape(28.dp)),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Pulsing Siren & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NearRedLight,
                            border = BorderStroke(1.5.dp, NearRedAccent),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🚨", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NearRedAccent
                            ) {
                                Text(
                                    text = "LIVE SOS EMERGENCY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Ambulance Dispatched",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearTextPrimary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = NearTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Patient and Blood Group Highlight Card
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = NearBgLight,
                    border = BorderStroke(1.dp, NearCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "PATIENT IN TRANSIT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = alert.patientName.ifEmpty { "Emergency Patient" },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearTextPrimary
                            )
                            Text(
                                text = "Destination: ${alert.hospitalName} (${alert.city})",
                                fontSize = 12.sp,
                                color = NearTextSecondary
                            )
                        }

                        // Large Blood Group Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = NearRedAccent,
                            shadowElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = alert.bloodGroupNeeded,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "${alert.unitsNeeded} Unit(s)",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Ambulance & Location Details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Ambulance Service
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Emergency, contentDescription = null, tint = NearPurplePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ambulance: ${alert.ambulanceServiceName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = NearTextPrimary
                        )
                    }

                    // Pickup Address
                    if (alert.pickupAddress.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = NearTextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pickup: ${alert.pickupAddress}",
                                fontSize = 12.sp,
                                color = NearTextSecondary
                            )
                        }
                    }

                    // Realtime Alert Meta Info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CellTower, contentDescription = null, tint = NearGreenSuccess, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Alerted ${alert.targetNearbyDonorsCount} nearby registered donors in ${alert.city}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )
                    }

                    // Responder status
                    if (alert.responderDonorsCount > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = NearPurplePrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${alert.responderDonorsCount} donor(s) currently responding: ${alert.responderDonorNames.joinToString(", ")}",
                                fontSize = 12.sp,
                                color = NearPurplePrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (alert.notes.isNotEmpty()) {
                        Text(
                            text = "Note: ${alert.notes}",
                            fontSize = 12.sp,
                            color = NearTextSecondary,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary Action: Respond / Accept SOS
                Button(
                    onClick = {
                        viewModel.respondToSosAlert(alert.alertId)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAlreadyResponding) NearGreenSuccess else NearRedAccent
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("sos_respond_btn")
                ) {
                    Icon(
                        imageVector = if (isAlreadyResponding) Icons.Default.Check else Icons.Default.Bloodtype,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAlreadyResponding) "You Are Responding to This SOS" else "I Can Donate / Respond to SOS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Call Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val phone = alert.driverPhone.ifEmpty { "911" }
                            viewModel.makePhoneCall(context, phone)
                        },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call Driver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    if (alert.contactPhone.isNotEmpty()) {
                        OutlinedButton(
                            onClick = {
                                viewModel.makePhoneCall(context, alert.contactPhone)
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(Icons.Default.LocalHospital, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Hospital", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
