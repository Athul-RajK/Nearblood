package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Ambulance
import com.example.ui.theme.*
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestAmbulanceSosDialog(
    preselectedAmbulance: Ambulance? = null,
    viewModel: BloodViewModel,
    onDismiss: () -> Unit
) {
    val ambulances by viewModel.ambulances.collectAsState()
    val allDonors by viewModel.allDonors.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var patientName by remember { mutableStateOf("") }
    var destinationHospital by remember { mutableStateOf("") }
    var pickupAddress by remember { mutableStateOf("") }
    var city by remember { mutableStateOf(currentUser?.city ?: "New York") }
    var selectedBloodGroup by remember { mutableStateOf("O+") }
    var unitsNeededText by remember { mutableStateOf("2") }
    var contactPhone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var patientCondition by remember { mutableStateOf("Trauma / Acute Hemorrhage") }
    var notes by remember { mutableStateOf("") }
    var broadcastSosToDonors by remember { mutableStateOf(true) }

    var selectedAmbulance by remember {
        mutableStateOf(preselectedAmbulance ?: ambulances.firstOrNull())
    }

    val bloodGroups = listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")
    val popularCities = listOf("New York", "Kozhikode", "Chicago", "San Francisco", "Houston")

    // Count nearby donors registered in the app for the selected city
    val nearbyDonorsCount = remember(city, allDonors) {
        allDonors.count { it.city.equals(city, ignoreCase = true) && it.available }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(28.dp))
                .border(1.5.dp, NearRedAccent.copy(alpha = 0.4f), RoundedCornerShape(28.dp)),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NearRedLight,
                            border = BorderStroke(1.dp, NearRedAccent),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🚑", fontSize = 22.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Emergency Ambulance & SOS",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearTextPrimary
                            )
                            Text(
                                text = "Dispatches vehicle & alerts nearby donors",
                                fontSize = 11.sp,
                                color = NearTextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = NearTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Form
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // SOS BROADCAST HIGHLIGHT BANNER
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NearRedLight,
                        border = BorderStroke(1.dp, NearRedAccent.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CellTower, contentDescription = null, tint = NearRedAccent, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Broadcast SOS Alert to Donors",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = NearTextPrimary
                                    )
                                    Text(
                                        text = if (broadcastSosToDonors)
                                            "Will alert $nearbyDonorsCount registered donors in $city in real-time!"
                                        else
                                            "SOS disabled (only ambulance will be dispatched)",
                                        fontSize = 11.sp,
                                        color = if (broadcastSosToDonors) Color(0xFF991B1B) else NearTextSecondary
                                    )
                                }
                            }

                            Switch(
                                checked = broadcastSosToDonors,
                                onCheckedChange = { broadcastSosToDonors = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = NearRedAccent
                                ),
                                modifier = Modifier.testTag("broadcast_sos_switch")
                            )
                        }
                    }

                    // PATIENT & PICKUP INFO
                    OutlinedTextField(
                        value = patientName,
                        onValueChange = { patientName = it },
                        label = { Text("Patient Name *") },
                        placeholder = { Text("e.g. John Doe / Robert Taylor") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_patient_name"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = destinationHospital,
                        onValueChange = { destinationHospital = it },
                        label = { Text("Destination Hospital *") },
                        placeholder = { Text("e.g. Metropolitan City Hospital") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_hospital_name"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = pickupAddress,
                        onValueChange = { pickupAddress = it },
                        label = { Text("Pickup Location / Street Address *") },
                        placeholder = { Text("e.g. 5th Avenue & 42nd St, NY") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_pickup_address"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // CITY SELECTION
                    Column {
                        Text(
                            text = "City / Area (Nearby Donors Target):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NearTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            popularCities.take(3).forEach { c ->
                                FilterChip(
                                    selected = city.equals(c, ignoreCase = true),
                                    onClick = { city = c },
                                    label = { Text(c, fontSize = 11.sp) },
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }

                    // BLOOD GROUP NEEDED & UNITS
                    Column {
                        Text(
                            text = "Required Blood Group for Emergency:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NearTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            bloodGroups.take(4).forEach { bg ->
                                Surface(
                                    onClick = { selectedBloodGroup = bg },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selectedBloodGroup == bg) NearRedAccent else NearBgLight,
                                    border = BorderStroke(1.dp, if (selectedBloodGroup == bg) NearRedAccent else NearCardBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = bg,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (selectedBloodGroup == bg) Color.White else NearTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            bloodGroups.drop(4).forEach { bg ->
                                Surface(
                                    onClick = { selectedBloodGroup = bg },
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (selectedBloodGroup == bg) NearRedAccent else NearBgLight,
                                    border = BorderStroke(1.dp, if (selectedBloodGroup == bg) NearRedAccent else NearCardBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = bg,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (selectedBloodGroup == bg) Color.White else NearTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = unitsNeededText,
                            onValueChange = { unitsNeededText = it },
                            label = { Text("Units Needed") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text("Contact Phone *") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    // AMBULANCE PROVIDER SELECTION
                    if (ambulances.isNotEmpty()) {
                        Column {
                            Text(
                                text = "Select Ambulance Provider:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NearTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            ambulances.take(3).forEach { amb ->
                                Surface(
                                    onClick = { selectedAmbulance = amb },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedAmbulance?.id == amb.id) NearPurpleLight else Color.White,
                                    border = BorderStroke(1.dp, if (selectedAmbulance?.id == amb.id) NearPurplePrimary else NearCardBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("🚑", fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(amb.serviceName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = NearTextPrimary)
                                                Text("Phone: ${amb.phone} • ${amb.city}", fontSize = 11.sp, color = NearTextSecondary)
                                            }
                                        }
                                        if (selectedAmbulance?.id == amb.id) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NearPurplePrimary, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Emergency Notes for Donors & Crew") },
                        placeholder = { Text("e.g. Critical trauma in ER entrance, immediate O- required") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SUBMIT BUTTON
                Button(
                    onClick = {
                        val units = unitsNeededText.toIntOrNull() ?: 2
                        viewModel.sendEmergencyAmbulanceRequestWithSos(
                            patientName = patientName.trim().ifEmpty { "Emergency Patient" },
                            pickupAddress = pickupAddress.trim().ifEmpty { "Current Location" },
                            destinationHospital = destinationHospital.trim().ifEmpty { "City General Hospital" },
                            city = city.trim(),
                            bloodGroupNeeded = selectedBloodGroup,
                            unitsNeeded = units,
                            contactPhone = contactPhone.trim().ifEmpty { "911" },
                            ambulance = selectedAmbulance,
                            patientCondition = patientCondition,
                            notes = notes.trim(),
                            broadcastSosToDonors = broadcastSosToDonors
                        )
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NearRedAccent),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_ambulance_sos_btn")
                ) {
                    Icon(Icons.Default.EmergencyShare, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (broadcastSosToDonors)
                            "Dispatch Ambulance & Send SOS ($nearbyDonorsCount Donors)"
                        else
                            "Dispatch Emergency Ambulance",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
