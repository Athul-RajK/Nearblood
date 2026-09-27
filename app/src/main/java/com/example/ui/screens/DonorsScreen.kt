package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.Donor
import com.example.ui.components.BloodDropBadge
import com.example.ui.components.DonorDetailDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorsScreen(
    viewModel: BloodViewModel
) {
    val context = LocalContext.current
    val filteredDonors by viewModel.filteredDonors.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedBloodGroup by viewModel.selectedBloodGroup.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val onlyAvailable by viewModel.onlyAvailable.collectAsState()

    var selectedDonorForDetail by remember { mutableStateOf<Donor?>(null) }

    val bloodGroups = listOf("ALL", "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")
    val cities = listOf("ALL", "Kozhikode", "Calicut", "New York", "San Francisco", "Chicago", "Houston")

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
                Text(
                    text = "Donors Directory",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NearTextPrimary
                )
                Text(
                    text = "Verified lifesavers ready to help in emergencies",
                    fontSize = 12.sp,
                    color = NearTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // SEARCH BAR
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search donor by name, city, phone...", color = NearTextMuted, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NearTextSecondary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = NearTextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF9F7FE),
                        unfocusedContainerColor = Color(0xFFF9F7FE),
                        focusedBorderColor = NearPurplePrimary,
                        unfocusedBorderColor = NearCardBorder
                    )
                )
            }
        }

        // FILTER CHIPS BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            // Blood Groups
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(bloodGroups) { group ->
                    val isSelected = selectedBloodGroup.equals(group, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedBloodGroup(group) },
                        label = {
                            Text(
                                text = group,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
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

            Spacer(modifier = Modifier.height(6.dp))

            // Cities and Availability Toggle
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Availability Toggle
                item {
                    FilterChip(
                        selected = onlyAvailable,
                        onClick = { viewModel.setOnlyAvailable(!onlyAvailable) },
                        label = { Text("● Available Now", fontSize = 11.sp) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFE6F9F0),
                            selectedLabelColor = Color(0xFF059669),
                            containerColor = Color.White,
                            labelColor = NearTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = onlyAvailable,
                            borderColor = if (onlyAvailable) Color(0xFF10B981) else NearCardBorder,
                            selectedBorderColor = Color(0xFF10B981)
                        )
                    )
                }

                items(cities) { city ->
                    val isSelected = selectedCity.equals(city, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedCity(city) },
                        label = { Text(city, fontSize = 11.sp) },
                        shape = RoundedCornerShape(20.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NearPurpleLight,
                            selectedLabelColor = NearPurpleDark,
                            containerColor = Color.White,
                            labelColor = NearTextSecondary
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
        }

        // DONOR CARDS LIST
        if (filteredDonors.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
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
                        Icon(
                            imageVector = Icons.Outlined.SearchOff,
                            contentDescription = null,
                            tint = NearPurplePrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No matching donors found",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = NearTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try adjusting your blood group or city filter.",
                        fontSize = 13.sp,
                        color = NearTextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredDonors, key = { it.uid }) { donor ->
                    DonorListItem(
                        donor = donor,
                        onClick = { selectedDonorForDetail = donor },
                        onCall = {
                            if (donor.phone.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donor.phone}"))
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    // DONOR DETAIL DIALOG
    selectedDonorForDetail?.let { donor ->
        DonorDetailDialog(
            donor = donor,
            onDismiss = { selectedDonorForDetail = null },
            onRequestBlood = {
                Toast.makeText(context, "Blood request sent to ${donor.name}!", Toast.LENGTH_SHORT).show()
                selectedDonorForDetail = null
            },
            onMessage = {
                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:${donor.phone}")
                    putExtra("sms_body", "Hi ${donor.name}, I found your profile on NearBlood. Are you available for blood donation?")
                }
                context.startActivity(smsIntent)
            }
        )
    }
}

@Composable
fun DonorListItem(
    donor: Donor,
    onClick: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, NearCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Blood Group Avatar
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(NearPurpleLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = donor.bloodGroup,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = NearPurplePrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = donor.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = NearTextPrimary
                            )
                            if (donor.verified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Donor",
                                    tint = NearGreenSuccess,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "${donor.city} • 2.4 km away",
                            fontSize = 12.sp,
                            color = NearTextSecondary
                        )
                    }
                }

                // Call Button
                IconButton(
                    onClick = onCall,
                    modifier = Modifier
                        .size(38.dp)
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

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = NearDivider, thickness = 0.8.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // Footer pills: Status & Last Donated
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (donor.available) Color(0xFFE6F9F0) else Color(0xFFFEF2F2)
                ) {
                    Text(
                        text = if (donor.available) "● Available" else "● Busy",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (donor.available) Color(0xFF059669) else Color(0xFFDC2626),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "Last: ${donor.lastDonationDate.ifBlank { "2 mos ago" }}",
                    fontSize = 11.sp,
                    color = NearTextSecondary
                )
            }
        }
    }
}
