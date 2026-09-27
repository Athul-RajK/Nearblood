package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donor
import com.example.ui.theme.*

@Composable
fun DigitalIdCard(
    donor: Donor,
    modifier: Modifier = Modifier,
    onOpenQr: () -> Unit = {}
) {
    var showBackSide by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(230.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF5B3DE8),
                            Color(0xFF7044EB),
                            Color(0xFF8B5CF6)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            if (!showBackSide) {
                // FRONT SIDE OF ID CARD
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Header: NearBlood Network Logo & Verified Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🩸", fontSize = 16.sp)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "NEARBLOOD",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "DIGITAL DONOR PASS",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        if (donor.verified) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF10B981)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "VERIFIED",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF59E0B)
                            ) {
                                Text(
                                    text = "PENDING",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Body: Avatar, Name, ID, Blood Group Chip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar circle
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.22f))
                                    .border(1.5.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = donor.name.take(1).uppercase(),
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = donor.name,
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "ID: ${donor.uid}",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "📍 ${donor.city}, Kerala",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Blood Group Emblem
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = donor.bloodGroup,
                                        color = NearPurplePrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "GROUP",
                                        color = NearTextSecondary,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Footer: ICE Contact & Tap to Flip QR
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ICE Contact: ${donor.emergencyContactName.ifEmpty { "Family Contact" }}",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.sp
                            )
                            Text(
                                text = "Status: ${if (donor.available) "AVAILABLE TO DONATE" else "BUSY"}",
                                color = if (donor.available) Color(0xFFA7F3D0) else Color(0xFFFDE68A),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { showBackSide = true },
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCode,
                                contentDescription = "Show QR Pass",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else {
                // BACK SIDE OF ID CARD (QR CODE VERIFICATION PASS)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "OFFICIAL QR PASS",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Scan at hospital or verification counter to confirm eligibility.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Code: ${donor.qrCode}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { showBackSide = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Flip to Front",
                                color = NearPurplePrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    QrCodeView(
                        content = donor.qrCode,
                        size = 120.dp,
                        backgroundColor = Color.White
                    )
                }
            }
        }
    }
}
