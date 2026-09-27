package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.components.QrCodeView
import com.example.ui.theme.*
import com.example.ui.viewmodel.BloodViewModel

@Composable
fun VerificationScreen(
    viewModel: BloodViewModel
) {
    val context = LocalContext.current
    val allDonors by viewModel.allDonors.collectAsState()

    var qrInput by remember { mutableStateOf("NEARBLOOD-DONOR-D101") }
    var verifiedDonorResult by remember { mutableStateOf<Donor?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    fun performVerification(code: String) {
        isVerifying = true
        val matched = allDonors.firstOrNull { it.qrCode.equals(code.trim(), ignoreCase = true) || it.uid.equals(code.trim(), ignoreCase = true) }
        verifiedDonorResult = matched
        isVerifying = false
        if (matched != null) {
            Toast.makeText(context, "Donor Verified: ${matched.name}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Invalid QR Code or Unrecognized Donor Pass", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        performVerification("NEARBLOOD-DONOR-D101")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearBgLight)
    ) {
        // TOP APP BAR
        Surface(color = Color.White, shadowElevation = 0.5.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = NearPurpleLight,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = NearPurplePrimary)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = "QR Donor Verification",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NearTextPrimary
                    )
                    Text(
                        text = "Verify authentic registration pass",
                        fontSize = 12.sp,
                        color = NearTextSecondary
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // SCANNER SIMULATOR / CODE INPUT CARD
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, NearCardBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Scan or Enter Donor Pass ID",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = NearTextPrimary
                    )

                    OutlinedTextField(
                        value = qrInput,
                        onValueChange = { qrInput = it },
                        label = { Text("Donor QR Code or ID") },
                        placeholder = { Text("e.g. NEARBLOOD-DONOR-D101") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null, tint = NearPurplePrimary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Button(
                        onClick = { performVerification(qrInput) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NearPurplePrimary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Verify Authenticity", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // SAMPLE PRESET CODES TO TEST QUICKLY
            Column {
                Text(
                    text = "Quick Demo Passes",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NearTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(allDonors) { donor ->
                        SuggestionChip(
                            onClick = {
                                qrInput = donor.qrCode
                                performVerification(donor.qrCode)
                            },
                            label = { Text("${donor.name} (${donor.bloodGroup})", fontSize = 11.sp) },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }

            // VERIFICATION RESULT CARD
            AnimatedVisibility(
                visible = verifiedDonorResult != null,
                enter = fadeIn() + slideInVertically()
            ) {
                verifiedDonorResult?.let { donor ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, NearGreenSuccess)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = NearGreenSuccess,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "PASS VERIFIED AUTHENTIC",
                                        fontWeight = FontWeight.Black,
                                        color = NearGreenSuccess,
                                        fontSize = 13.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = NearPurpleLight
                                ) {
                                    Text(
                                        text = donor.bloodGroup,
                                        fontWeight = FontWeight.Black,
                                        color = NearPurplePrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Divider(color = NearDivider)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Full Name", fontSize = 11.sp, color = NearTextSecondary)
                                    Text(donor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = NearTextPrimary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Location", fontSize = 11.sp, color = NearTextSecondary)
                                    Text(donor.city, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NearTextPrimary)
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Last Donation", fontSize = 11.sp, color = NearTextSecondary)
                                    Text(donor.lastDonationDate.ifBlank { "Never" }, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = NearTextPrimary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Eligible to Donate", fontSize = 11.sp, color = NearTextSecondary)
                                    Text(if (donor.available) "Yes (Ready)" else "Rest Period", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (donor.available) NearGreenSuccess else NearAmberWarning)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
