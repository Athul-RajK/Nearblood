package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donor
import com.example.ui.theme.*
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDialog(
    viewModel: BloodViewModel,
    onDismiss: () -> Unit
) {
    val allDonors by viewModel.allDonors.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Quick Switch Account, 1: New Registration

    // Registration Form Inputs
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var selectedBloodGroup by remember { mutableStateOf("O+") }
    var cityInput by remember { mutableStateOf("Kozhikode") }
    var phoneInput by remember { mutableStateOf("+91 98470 ") }
    var emergencyNameInput by remember { mutableStateOf("") }
    var emergencyPhoneInput by remember { mutableStateOf("") }

    val bloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("NearBlood Account", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NearTextPrimary)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = NearTextSecondary)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    contentColor = NearPurplePrimary,
                    containerColor = Color.White
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Select Profile", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Register Donor", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (selectedTab == 0) {
                    // QUICK SWITCH EXISTING USER
                    Text("Select a profile to switch session:", fontSize = 12.sp, color = NearTextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allDonors) { donor ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.switchUser(donor)
                                        onDismiss()
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = NearPurpleLight),
                                border = androidx.compose.foundation.BorderStroke(1.dp, NearPurpleBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(NearPurplePrimary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = donor.bloodGroup,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(donor.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NearTextPrimary)
                                            Text("${donor.city} • Role: ${donor.role}", fontSize = 11.sp, color = NearTextSecondary)
                                        }
                                    }

                                    Text("Switch →", fontSize = 12.sp, color = NearPurplePrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // NEW REGISTRATION
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                label = { Text("Full Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Email Address") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Password") },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        item {
                            Text("Select Blood Group", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NearTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(bloodGroups) { bg ->
                                    FilterChip(
                                        selected = selectedBloodGroup == bg,
                                        onClick = { selectedBloodGroup = bg },
                                        label = { Text(bg, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = NearPurplePrimary,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = cityInput,
                                onValueChange = { cityInput = it },
                                label = { Text("City") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                label = { Text("Phone Number") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = emergencyNameInput,
                                onValueChange = { emergencyNameInput = it },
                                label = { Text("Emergency Contact (ICE) Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = emergencyPhoneInput,
                                onValueChange = { emergencyPhoneInput = it },
                                label = { Text("Emergency Contact Phone") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (selectedTab == 1) {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank() && emailInput.isNotBlank()) {
                            viewModel.registerDonorAccount(
                                name = nameInput,
                                email = emailInput,
                                bloodGroup = selectedBloodGroup,
                                city = cityInput,
                                phone = phoneInput,
                                emergencyName = emergencyNameInput,
                                emergencyPhone = emergencyPhoneInput
                            )
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NearPurplePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Register Profile")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = NearTextSecondary)
            }
        }
    )
}
