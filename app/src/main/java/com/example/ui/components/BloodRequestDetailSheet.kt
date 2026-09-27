package com.example.ui.components

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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.EmergencyRequest
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodRequestDetailDialog(
    request: EmergencyRequest,
    onDismiss: () -> Unit,
    onAcceptDonation: (EmergencyRequest) -> Unit,
    onMessageRequester: (EmergencyRequest) -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAF9FE))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // TOP BAR WITH PEACH/LAVENDER GRADIENT BACKGROUND
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NearHeaderPeachGradient)
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.8f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = NearTextPrimary
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "🚨 Emergency Blood Request: ${request.bloodGroup} needed (${request.unitsNeeded} units) at ${request.hospitalName}, ${request.city}. Patient: ${request.patientName}. Contact: ${request.contactPhone}"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Blood Request"))
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.8f))
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = "Share",
                                    tint = NearTextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Request ID: ${request.requestId}", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.8f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MoreVert,
                                    contentDescription = "More",
                                    tint = NearTextPrimary
                                )
                            }
                        }
                    }
                }

                // SCROLLABLE CONTENT
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // HERO BADGE & NAME
                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Emergency Kit / Briefcase Icon inside Peach Circle
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFE8E4)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0xFFEF4444)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "${request.bloodGroup} Blood Required",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = NearTextPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${request.unitsNeeded} Units",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }

                    // MAIN INFO CARD
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NearCardBorder),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // 1. Requested By & Call Button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        RequestIconBox(icon = Icons.Outlined.Person)
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column {
                                            Text(
                                                text = "Requested By",
                                                fontSize = 12.sp,
                                                color = NearTextSecondary
                                            )
                                            Text(
                                                text = request.patientName,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NearTextPrimary
                                            )
                                        }
                                    }

                                    // Purple Call Action Button
                                    IconButton(
                                        onClick = {
                                            if (request.contactPhone.isNotBlank()) {
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${request.contactPhone}"))
                                                context.startActivity(intent)
                                            } else {
                                                Toast.makeText(context, "No phone number available", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .border(1.5.dp, NearPurpleBorder, CircleShape)
                                            .background(NearPurpleLight)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Phone,
                                            contentDescription = "Call Requester",
                                            tint = NearPurplePrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Divider(color = NearDivider, thickness = 0.8.dp)

                                // 2. Hospital
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RequestIconBox(icon = Icons.Outlined.LocalHospital)
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "Hospital",
                                            fontSize = 12.sp,
                                            color = NearTextSecondary
                                        )
                                        Text(
                                            text = "${request.hospitalName}, ${request.city}",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NearTextPrimary
                                        )
                                    }
                                }

                                Divider(color = NearDivider, thickness = 0.8.dp)

                                // 3. Need By
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RequestIconBox(icon = Icons.Outlined.Schedule)
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "Need By",
                                            fontSize = 12.sp,
                                            color = NearTextSecondary
                                        )
                                        Text(
                                            text = "Today, 6:00 PM (Critical)",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NearTextPrimary
                                        )
                                    }
                                }

                                Divider(color = NearDivider, thickness = 0.8.dp)

                                // 4. Posted
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RequestIconBox(icon = Icons.Outlined.CalendarMonth)
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "Posted",
                                            fontSize = 12.sp,
                                            color = NearTextSecondary
                                        )
                                        Text(
                                            text = request.timestamp.ifBlank { "10 minutes ago" },
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NearTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ADDITIONAL DETAILS SECTION
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Additional Details",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (request.notes.isNotBlank())
                                    request.notes
                                else
                                    "Patient is in emergency care. Immediate blood donation assistance is appreciated.",
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = NearTextSecondary
                            )
                        }
                    }

                    // CAUTION / NOTICE BANNER (Soft red warning with shield icon)
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFEDED),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFED7D7))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = Color(0xFFEF4444),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Please contact the requester before donating.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFEF4444),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }

            // BOTTOM FLOATING BAR WITH I CAN DONATE & MESSAGE ACTIONS
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Primary Purple Pill Button "💧 I Can Donate"
                    Button(
                        onClick = { onAcceptDonation(request) },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NearPurplePrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("💧", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "I Can Donate",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Circular / Rounded Chat Button
                    OutlinedButton(
                        onClick = { onMessageRequester(request) },
                        modifier = Modifier.size(54.dp),
                        shape = RoundedCornerShape(27.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, NearPurpleBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NearPurplePrimary,
                            containerColor = NearPurpleLight
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Message",
                            tint = NearPurplePrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RequestIconBox(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(NearPurpleLight),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NearPurplePrimary,
            modifier = Modifier.size(18.dp)
        )
    }
}
