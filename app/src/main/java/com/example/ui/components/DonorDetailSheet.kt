package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Donor
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonorDetailDialog(
    donor: Donor,
    onDismiss: () -> Unit,
    onRequestBlood: (Donor) -> Unit,
    onMessage: (Donor) -> Unit
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
                                            "NearBlood Donor Profile: ${donor.name} (${donor.bloodGroup}) - Location: ${donor.city}. Contact: ${donor.phone}"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Share Donor Profile"))
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
                                    Toast.makeText(context, "Donor ID: ${donor.uid}", Toast.LENGTH_SHORT).show()
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

                        // Red Blood Drop inside Soft Glow Circle
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEDED)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(44.dp)) {
                                val w = size.width
                                val h = size.height
                                val dropPath = Path().apply {
                                    moveTo(w * 0.5f, 0f)
                                    cubicTo(w * 0.9f, h * 0.55f, w, h * 0.8f, w * 0.5f, h)
                                    cubicTo(0f, h * 0.8f, w * 0.1f, h * 0.55f, w * 0.5f, 0f)
                                    close()
                                }
                                drawPath(
                                    dropPath,
                                    brush = Brush.verticalGradient(
                                        listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "${donor.bloodGroup} Donor",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = NearTextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Status Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (donor.available) Color(0xFFE6F9F0) else Color(0xFFFEF2F2),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (donor.available) Color(0xFFB7F0D3) else Color(0xFFFECACA)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (donor.available) Color(0xFF10B981) else Color(0xFFEF4444))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (donor.available) "Available" else "Busy / Unavailable",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (donor.available) Color(0xFF059669) else Color(0xFFDC2626)
                                )
                            }
                        }
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
                                // 1. Name & Call Button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        DonorIconBox(icon = Icons.Outlined.Person)
                                        Spacer(modifier = Modifier.width(14.dp))
                                        Column {
                                            Text(
                                                text = "Name",
                                                fontSize = 12.sp,
                                                color = NearTextSecondary
                                            )
                                            Text(
                                                text = donor.name,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NearTextPrimary
                                            )
                                        }
                                    }

                                    // Purple Call Action Button
                                    IconButton(
                                        onClick = {
                                            if (donor.phone.isNotBlank()) {
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donor.phone}"))
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
                                            contentDescription = "Call Donor",
                                            tint = NearPurplePrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                Divider(color = NearDivider, thickness = 0.8.dp)

                                // 2. Location
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    DonorIconBox(icon = Icons.Outlined.LocationOn)
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "Location",
                                            fontSize = 12.sp,
                                            color = NearTextSecondary
                                        )
                                        Text(
                                            text = "${donor.city}, Kerala",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NearTextPrimary
                                        )
                                    }
                                }

                                Divider(color = NearDivider, thickness = 0.8.dp)

                                // 3. Distance
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    DonorIconBox(icon = Icons.Outlined.Explore)
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "Distance",
                                            fontSize = 12.sp,
                                            color = NearTextSecondary
                                        )
                                        Text(
                                            text = "2.4 km away",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NearTextPrimary
                                        )
                                    }
                                }

                                Divider(color = NearDivider, thickness = 0.8.dp)

                                // 4. Last Donated
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    DonorIconBox(icon = Icons.Outlined.CalendarToday)
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = "Last Donated",
                                            fontSize = 12.sp,
                                            color = NearTextSecondary
                                        )
                                        Text(
                                            text = if (donor.lastDonationDate.isNotBlank()) "${donor.lastDonationDate} (Eligible)" else "2 months ago",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NearTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ABOUT DONOR SECTION
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "About Donor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearTextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (donor.healthStatus.isNotBlank())
                                    "Healthy lifestyle, regular donor. Ready to help in emergencies."
                                else "Ready to donate in critical hospital emergencies.",
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = NearTextSecondary
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Metric pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DonorMetricPill(label = "Male")
                                DonorMetricPill(label = "25 Years")
                                DonorMetricPill(label = "60 kg")
                                DonorMetricPill(label = "Non-Smoker")
                            }
                        }
                    }
                }
            }

            // BOTTOM FLOATING BAR WITH REQUEST BLOOD & MESSAGE ACTIONS
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
                    // Primary Purple Pill Button "💧 Request Blood"
                    Button(
                        onClick = { onRequestBlood(donor) },
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
                                text = "Request Blood",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Circular / Rounded Chat Button
                    OutlinedButton(
                        onClick = { onMessage(donor) },
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
private fun DonorIconBox(icon: androidx.compose.ui.graphics.vector.ImageVector) {
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

@Composable
private fun DonorMetricPill(label: String) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = NearPurpleLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, NearPurpleBorder)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = NearPurpleText,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
