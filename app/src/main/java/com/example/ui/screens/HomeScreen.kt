package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import com.example.data.model.SosAlert
import com.example.ui.components.BloodCampsDialog
import com.example.ui.components.BloodDropBadge
import com.example.ui.components.BloodRequestDetailDialog
import com.example.ui.components.DonorDetailDialog
import com.example.ui.components.RequestAmbulanceSosDialog
import com.example.ui.components.SosEmergencyDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: BloodViewModel,
    onNavigateToDonors: () -> Unit,
    onNavigateToAmbulances: () -> Unit,
    onNavigateToVerification: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onOpenAuth: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val filteredDonors by viewModel.filteredDonors.collectAsState()
    val allDonors by viewModel.allDonors.collectAsState()
    val emergencyRequests by viewModel.emergencyRequests.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()
    val activeSosAlertForCurrentDonor by viewModel.activeSosAlertForCurrentDonor.collectAsState()
    val isRealtimeDbConnected by viewModel.isRealtimeDbConnected.collectAsState()

    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showBloodCampsDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showRequestAmbulanceSosDialog by remember { mutableStateOf(false) }
    var selectedSosDialogForAlert by remember { mutableStateOf<SosAlert?>(null) }
    var selectedRequestForDetail by remember { mutableStateOf<EmergencyRequest?>(null) }
    var selectedDonorForDetail by remember { mutableStateOf<Donor?>(null) }

    // Mock recent requests matching design if list is empty
    val displayRequests = remember(emergencyRequests) {
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
            )
        )
    }

    // Blood groups summary count (matching screenshot)
    val bloodGroupCards = listOf(
        BloodGroupStat("A+", 12, GroupRed, GroupRedBg),
        BloodGroupStat("B+", 8, GroupBlue, GroupBlueBg),
        BloodGroupStat("O+", 15, GroupGreen, GroupGreenBg),
        BloodGroupStat("AB+", 5, GroupPurple, GroupPurpleBg),
        BloodGroupStat("A-", 4, GroupRed, GroupRedBg),
        BloodGroupStat("B-", 3, GroupBlue, GroupBlueBg),
        BloodGroupStat("O-", 6, GroupGreen, GroupGreenBg),
        BloodGroupStat("AB-", 2, GroupPurple, GroupPurpleBg)
    )

    val userName = currentUser?.name?.split(" ")?.firstOrNull() ?: "Athul"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearBgLight)
    ) {
        // TOP APP BAR / GREETING BAR
        Surface(
            color = Color.White,
            shadowElevation = 0.5.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Greeting text
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hi, $userName",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = NearTextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "👋", fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Every drop makes a difference",
                        fontSize = 12.sp,
                        color = NearTextSecondary
                    )
                }

                // Notification Bell with Red Badge & Profile/Auth Icon
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Firebase Realtime DB Live Indicator
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isRealtimeDbConnected) NearGreenLight else NearAmberLight,
                        border = BorderStroke(1.dp, if (isRealtimeDbConnected) NearGreenSuccess.copy(alpha = 0.3f) else NearAmberWarning.copy(alpha = 0.3f)),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isRealtimeDbConnected) NearGreenSuccess else NearAmberWarning)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isRealtimeDbConnected) "Live ⚡" else "Syncing",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isRealtimeDbConnected) Color(0xFF065F46) else Color(0xFF92400E)
                            )
                        }
                    }

                    if (isAdminMode) {
                        IconButton(
                            onClick = onNavigateToAdmin,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NearPurpleLight)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = NearPurplePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    // Notification Bell with unread dot
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.dp, NearCardBorder, CircleShape)
                            .background(Color.White)
                            .clickable { showNotificationsDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = NearTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        // Red Unread Dot
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 9.dp, end = 10.dp)
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                    }
                }
            }
        }

        // SCROLLABLE BODY
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp)
        ) {
            // INCOMING SOS EMERGENCY ALERT BANNER (FROM FIREBASE REALTIME DB)
            if (activeSosAlertForCurrentDonor != null) {
                item {
                    val alert = activeSosAlertForCurrentDonor!!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSosDialogForAlert = alert }
                            .testTag("home_incoming_sos_alert_card"),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.8.dp, NearRedAccent)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
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
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("🚨", fontSize = 18.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = NearRedAccent
                                            ) {
                                                Text(
                                                    text = "SOS EMERGENCY",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Nearby in ${alert.city}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NearRedAccent
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Ambulance Dispatched • Needs ${alert.bloodGroupNeeded}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NearTextPrimary
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.dismissSosAlert(alert.alertId) },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = NearTextSecondary, modifier = Modifier.size(16.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Patient ${alert.patientName} en route to ${alert.hospitalName}. ${alert.unitsNeeded} unit(s) of ${alert.bloodGroupNeeded} needed immediately upon arrival.",
                                fontSize = 12.sp,
                                color = NearTextSecondary,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.respondToSosAlert(alert.alertId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NearRedAccent),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Bloodtype, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("I Can Donate", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { selectedSosDialogForAlert = alert },
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("View SOS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NearTextPrimary)
                                }
                            }
                        }
                    }
                }
            }
            // 1. HERO GRADIENT BANNER CARD (Screen 2 matching)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NearPrimaryGradient)
                    ) {
                        // Subtle Watermark Blood Drop on right
                        Canvas(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                                .size(110.dp)
                        ) {
                            val w = size.width
                            val h = size.height
                            val dropPath = Path().apply {
                                moveTo(w * 0.5f, 0f)
                                cubicTo(w * 0.9f, h * 0.55f, w, h * 0.8f, w * 0.5f, h)
                                cubicTo(0f, h * 0.8f, w * 0.1f, h * 0.55f, w * 0.5f, 0f)
                                close()
                            }
                            drawPath(
                                path = dropPath,
                                color = Color.White.copy(alpha = 0.18f)
                            )
                        }

                        // Banner Content
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(22.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Need Blood?",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Find Donors\nNear You",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    lineHeight = 27.sp
                                )
                            }

                            // White Pill Button "Search Now →"
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.White,
                                shadowElevation = 1.dp,
                                modifier = Modifier.clickable { onNavigateToDonors() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Search Now",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NearPurpleDark
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = null,
                                        tint = NearPurpleDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. QUICK ACTION GRID (4 ROUNDED SQUARES)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionTile(
                        title = "Blood\nRequests",
                        icon = Icons.Outlined.WaterDrop,
                        onClick = { showEmergencyDialog = true }
                    )
                    QuickActionTile(
                        title = "Donors\nDirectory",
                        icon = Icons.Outlined.Groups,
                        onClick = onNavigateToDonors
                    )
                    QuickActionTile(
                        title = "Ambulance\nSOS",
                        icon = Icons.Outlined.Emergency,
                        onClick = { showRequestAmbulanceSosDialog = true }
                    )
                    QuickActionTile(
                        title = "My\nProfile",
                        icon = Icons.Outlined.Person,
                        onClick = onNavigateToProfile
                    )
                }
            }

            // 3. BLOOD GROUPS HORIZONTAL SECTION
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Blood Groups",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NearTextPrimary
                        )

                        TextButton(onClick = onNavigateToDonors) {
                            Text(
                                text = "View All",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NearPurplePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(bloodGroupCards) { stat ->
                            BloodGroupCard(
                                stat = stat,
                                onClick = {
                                    viewModel.setSelectedBloodGroup(stat.group)
                                    onNavigateToDonors()
                                }
                            )
                        }
                    }
                }
            }

            // 4. RECENT REQUESTS SECTION
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Requests",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NearTextPrimary
                        )

                        IconButton(onClick = { showEmergencyDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                contentDescription = "New Request",
                                tint = NearPurplePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        displayRequests.forEach { req ->
                            RecentRequestCard(
                                request = req,
                                onClick = { selectedRequestForDetail = req }
                            )
                        }
                    }
                }
            }

            // 5. FEATURED / VERIFIED DONORS NEARBY
            if (filteredDonors.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Available Donors",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NearTextPrimary
                            )

                            TextButton(onClick = onNavigateToDonors) {
                                Text("See More", color = NearPurplePrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            filteredDonors.take(3).forEach { donor ->
                                DonorMiniCard(
                                    donor = donor,
                                    onClick = { selectedDonorForDetail = donor }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // DETAIL DIALOG FOR BLOOD REQUEST (Matching Screen 4)
    selectedRequestForDetail?.let { req ->
        BloodRequestDetailDialog(
            request = req,
            onDismiss = { selectedRequestForDetail = null },
            onAcceptDonation = {
                Toast.makeText(context, "Thank you! Contacting ${req.patientName} at ${req.contactPhone}", Toast.LENGTH_LONG).show()
                selectedRequestForDetail = null
            },
            onMessageRequester = {
                val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:${req.contactPhone}")
                    putExtra("sms_body", "Hi ${req.patientName}, I can help donate ${req.bloodGroup} blood for your emergency at ${req.hospitalName}.")
                }
                context.startActivity(smsIntent)
            }
        )
    }

    // DETAIL DIALOG FOR DONOR (Matching Screen 3)
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
                    putExtra("sms_body", "Hi ${donor.name}, I found your profile on NearBlood. Are you available for ${donor.bloodGroup} blood donation?")
                }
                context.startActivity(smsIntent)
            }
        )
    }

    // BLOOD CAMPS DIALOG
    if (showBloodCampsDialog) {
        BloodCampsDialog(onDismiss = { showBloodCampsDialog = false })
    }

    // NOTIFICATIONS DIALOG
    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = NearPurplePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Notifications", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    notifications.forEach { item ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = NearPurpleLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = NearPurpleDark)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(item.message, fontSize = 12.sp, color = NearTextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(item.timestamp, fontSize = 10.sp, color = NearTextSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Close", color = NearPurplePrimary)
                }
            }
        )
    }

    // CREATE EMERGENCY REQUEST DIALOG
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

    if (showRequestAmbulanceSosDialog) {
        RequestAmbulanceSosDialog(
            viewModel = viewModel,
            onDismiss = { showRequestAmbulanceSosDialog = false }
        )
    }

    selectedSosDialogForAlert?.let { alert ->
        SosEmergencyDialog(
            alert = alert,
            viewModel = viewModel,
            onDismiss = { selectedSosDialogForAlert = null }
        )
    }
}

// ---------------------- SUB-COMPONENTS ----------------------

data class BloodGroupStat(
    val group: String,
    val count: Int,
    val color: Color,
    val bgColor: Color
)

@Composable
fun QuickActionTile(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(76.dp)
            .height(86.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, NearCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
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
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = NearTextPrimary,
                lineHeight = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun BloodGroupCard(
    stat: BloodGroupStat,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(82.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, NearCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drop badge
            BloodDropBadge(
                bloodGroup = stat.group,
                color = stat.color,
                bgColor = stat.bgColor,
                size = 36.dp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stat.group,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = NearTextPrimary
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${stat.count} Donors",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = NearTextSecondary
            )
        }
    }
}

@Composable
fun RecentRequestCard(
    request: EmergencyRequest,
    onClick: () -> Unit
) {
    val dropColor = when (request.bloodGroup) {
        "A+" -> GroupRed
        "O+" -> GroupGreen
        "B+" -> GroupBlue
        else -> GroupPurple
    }
    val dropBg = when (request.bloodGroup) {
        "A+" -> GroupRedBg
        "O+" -> GroupGreenBg
        "B+" -> GroupBlueBg
        else -> GroupPurpleBg
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, NearCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Blood drop icon
                BloodDropBadge(
                    bloodGroup = request.bloodGroup,
                    color = dropColor,
                    bgColor = dropBg,
                    size = 40.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${request.bloodGroup} Required",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NearTextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${request.unitsNeeded} Units",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${request.hospitalName}, ${request.city}",
                        fontSize = 12.sp,
                        color = NearTextSecondary,
                        maxLines = 1
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = request.timestamp.ifBlank { "10m ago" },
                    fontSize = 11.sp,
                    color = NearTextMuted
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = NearTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun DonorMiniCard(
    donor: Donor,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, NearCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NearPurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = donor.bloodGroup,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = NearPurplePrimary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = donor.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NearTextPrimary
                    )
                    Text(
                        text = "${donor.city} • 2.4 km away",
                        fontSize = 12.sp,
                        color = NearTextSecondary
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (donor.available) Color(0xFFE6F9F0) else Color(0xFFFEF2F2)
            ) {
                Text(
                    text = if (donor.available) "Available" else "Busy",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (donor.available) Color(0xFF059669) else Color(0xFFDC2626),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun CreateEmergencyDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String, Int, String, String, String) -> Unit
) {
    var patientName by remember { mutableStateOf("") }
    var hospital by remember { mutableStateOf("Medical College, Kozhikode") }
    var bloodGroup by remember { mutableStateOf("A+") }
    var units by remember { mutableStateOf("2") }
    var phone by remember { mutableStateOf("+91 ") }
    var city by remember { mutableStateOf("Kozhikode") }
    var notes by remember { mutableStateOf("Patient is in emergency care. Immediate help is appreciated.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🚨", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Broadcast Emergency Need", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NearTextPrimary)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("Patient / Requester Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = hospital,
                    onValueChange = { hospital = it },
                    label = { Text("Hospital / Medical Center") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = { bloodGroup = it },
                        label = { Text("Blood Group") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = units,
                        onValueChange = { units = it },
                        label = { Text("Units") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Emergency Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (patientName.isNotBlank() && hospital.isNotBlank()) {
                        onSubmit(patientName, hospital, bloodGroup, units.toIntOrNull() ?: 1, phone, city, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NearPurplePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Broadcast Alert", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = NearTextSecondary)
            }
        }
    )
}
