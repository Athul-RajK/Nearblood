package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Donation
import com.example.data.model.Donor
import com.example.data.model.EmergencyRequest
import com.example.ui.theme.*
import com.example.ui.viewmodel.BloodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(
    viewModel: BloodViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    AdminScreen(viewModel = viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: BloodViewModel
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val isSuperAdmin by viewModel.isSuperAdmin.collectAsState()
    val pendingDonors by viewModel.pendingDonors.collectAsState()
    val allDonors by viewModel.allDonors.collectAsState()
    val emergencyRequests by viewModel.emergencyRequests.collectAsState()
    val allDonations by viewModel.allDonations.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Dashboard, 1: Pending, 2: Donors, 3: Emergencies, 4: Certificates
    var donorToEdit by remember { mutableStateOf<Donor?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("ALL") } // ALL, active, suspended, rejected

    val filteredAdminDonors = remember(allDonors, searchQuery, selectedStatusFilter) {
        allDonors.filter { d ->
            val matchesQuery = searchQuery.isEmpty() ||
                    d.name.contains(searchQuery, ignoreCase = true) ||
                    d.email.contains(searchQuery, ignoreCase = true) ||
                    d.city.contains(searchQuery, ignoreCase = true) ||
                    d.phone.contains(searchQuery, ignoreCase = true)

            val matchesStatus = when (selectedStatusFilter) {
                "ACTIVE" -> d.status == "active"
                "SUSPENDED" -> d.status == "suspended"
                "REJECTED" -> d.status == "rejected"
                else -> true
            }

            matchesQuery && matchesStatus
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NearBgLight)
    ) {
        // TOP APP BAR
        Surface(color = Color.White, shadowElevation = 0.5.dp) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NearPurpleLight,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isSuperAdmin) Icons.Default.Shield else Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = NearPurplePrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isSuperAdmin) "Super Admin Hub" else "Admin Control Panel",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NearTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSuperAdmin) Color(0xFF7E22CE) else NearPurplePrimary
                                ) {
                                    Text(
                                        text = if (isSuperAdmin) "SUPER ADMIN" else "ADMIN",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Live stats, donor management & control",
                                fontSize = 11.sp,
                                color = NearTextSecondary
                            )
                        }
                    }

                    IconButton(onClick = { viewModel.exportDonorDataCsv(context) }) {
                        Icon(Icons.Default.Share, contentDescription = "Export CSV", tint = NearPurplePrimary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = NearPurplePrimary,
                    edgePadding = 0.dp
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("📊 Dashboard", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Pending (${pendingDonors.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                if (pendingDonors.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Badge(containerColor = NearPurplePrimary) {
                                        Text("${pendingDonors.size}", color = Color.White)
                                    }
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("👥 Donors (${allDonors.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("🚨 Emergencies (${emergencyRequests.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                    Tab(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        text = { Text("📜 Certificates", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                }
            }
        }

        // CONTENT BY TAB
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> AdminDashboardTab(
                    allDonors = allDonors,
                    pendingDonors = pendingDonors,
                    emergencyRequests = emergencyRequests,
                    allDonations = allDonations,
                    onNavigateToPending = { selectedTab = 1 },
                    onNavigateToEmergencies = { selectedTab = 3 }
                )
                1 -> PendingApprovalsTab(
                    pendingDonors = pendingDonors,
                    onApprove = { uid -> viewModel.approveDonor(uid) },
                    onReject = { uid -> viewModel.rejectDonor(uid) },
                    onEmail = { email -> viewModel.sendEmailNotification(context, email, "Nearblood Registration Status", "Hello, regarding your Nearblood donor profile application...") }
                )
                2 -> DonorsManagementTab(
                    donors = filteredAdminDonors,
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    selectedStatusFilter = selectedStatusFilter,
                    onStatusFilterChange = { selectedStatusFilter = it },
                    isSuperAdmin = isSuperAdmin,
                    onEdit = { donor -> donorToEdit = donor },
                    onToggleVerify = { uid, verified -> viewModel.toggleDonorVerification(uid, verified) },
                    onSuspend = { uid -> viewModel.suspendDonor(uid) },
                    onRestore = { uid -> viewModel.restoreDonor(uid) },
                    onPromote = { uid -> viewModel.promoteToAdmin(uid) },
                    onDemote = { uid -> viewModel.demoteToDonor(uid) },
                    onEmail = { email -> viewModel.sendEmailNotification(context, email, "Nearblood Network Notice", "Hello, message from Nearblood Admin team...") }
                )
                3 -> EmergenciesManagementTab(
                    requests = emergencyRequests,
                    onUpdateStatus = { reqId, status -> viewModel.updateEmergencyRequestStatus(reqId, status) }
                )
                4 -> CertificatesTab(
                    donations = allDonations,
                    donors = allDonors
                )
            }
        }
    }

    // EDIT DONOR DIALOG
    donorToEdit?.let { donor ->
        EditDonorDialog(
            donor = donor,
            onDismiss = { donorToEdit = null },
            onSave = { updated ->
                viewModel.editDonorDetails(updated)
                donorToEdit = null
            }
        )
    }
}

@Composable
private fun AdminDashboardTab(
    allDonors: List<Donor>,
    pendingDonors: List<Donor>,
    emergencyRequests: List<EmergencyRequest>,
    allDonations: List<Donation>,
    onNavigateToPending: () -> Unit,
    onNavigateToEmergencies: () -> Unit
) {
    val activeDonorsCount = allDonors.count { it.status == "active" && it.approved }
    val verifiedCount = allDonors.count { it.verified }
    val suspendedCount = allDonors.count { it.status == "suspended" }

    val bloodGroupCounts = remember(allDonors) {
        listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-").associateWith { group ->
            allDonors.count { it.bloodGroup.equals(group, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // OVERVIEW CARDS GRID
        item {
            Text("LIVE SYSTEM METRICS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF706767))
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Total Donors",
                    value = "${allDonors.size}",
                    subtitle = "$verifiedCount Verified Badges",
                    color = Color(0xFFB3261E),
                    icon = Icons.Default.People,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Pending Review",
                    value = "${pendingDonors.size}",
                    subtitle = "Action required",
                    color = Color(0xFFD97706),
                    icon = Icons.Default.HourglassEmpty,
                    onClick = onNavigateToPending,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    title = "Donations Logged",
                    value = "${allDonations.size}",
                    subtitle = "Total units recorded",
                    color = Color(0xFF15803D),
                    icon = Icons.Default.VolunteerActivism,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Emergencies",
                    value = "${emergencyRequests.size}",
                    subtitle = "${emergencyRequests.count { it.status == "PENDING" }} Pending",
                    color = Color(0xFFDC2626),
                    icon = Icons.Default.Emergency,
                    onClick = onNavigateToEmergencies,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // DONOR STATUS BREAKDOWN
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBFF)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF4DDDB))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Donor Status Breakdown", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1C1B1B))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatusMiniItem("Active", "$activeDonorsCount", Color(0xFF15803D))
                        StatusMiniItem("Pending", "${pendingDonors.size}", Color(0xFFD97706))
                        StatusMiniItem("Suspended", "$suspendedCount", Color(0xFFDC2626))
                        StatusMiniItem("Verified", "$verifiedCount", Color(0xFF2563EB))
                    }
                }
            }
        }

        // BLOOD GROUP DISTRIBUTION
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBFF)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF4DDDB))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Blood Group Distribution", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1C1B1B))
                    Text("Available blood supply inventory by group", fontSize = 12.sp, color = Color(0xFF706767))

                    Spacer(modifier = Modifier.height(12.dp))

                    val maxCount = bloodGroupCounts.values.maxOrNull()?.coerceAtLeast(1) ?: 1

                    bloodGroupCounts.forEach { (group, count) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(group, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.width(44.dp), color = Color(0xFFB3261E))
                            LinearProgressIndicator(
                                progress = { count.toFloat() / maxCount.toFloat() },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(10.dp)
                                    .clip(CircleShape),
                                color = Color(0xFFB3261E),
                                trackColor = Color(0xFFF9DEDC)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("$count donor(s)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C1B1B))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusMiniItem(label: String, count: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
        Text(label, fontSize = 11.sp, color = Color(0xFF706767))
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBFF)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF4DDDB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 12.sp, color = Color(0xFF706767), fontWeight = FontWeight.Medium)
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = color)
            Text(subtitle, fontSize = 10.sp, color = Color(0xFF706767))
        }
    }
}

@Composable
private fun PendingApprovalsTab(
    pendingDonors: List<Donor>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit,
    onEmail: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (pendingDonors.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBFF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF4DDDB))
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF15803D), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No Pending Registrations", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1C1B1B))
                        Text("All donor applications have been thoroughly reviewed.", fontSize = 12.sp, color = Color(0xFF706767))
                    }
                }
            }
        } else {
            items(pendingDonors) { donor ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBFF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFEF3C7))
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
                                    color = Color(0xFFF9DEDC),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(donor.bloodGroup, color = Color(0xFFB3261E), fontWeight = FontWeight.Black, fontSize = 16.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(donor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1C1B1B))
                                    Text("${donor.city} • ${donor.phone}", fontSize = 12.sp, color = Color(0xFF706767))
                                    Text(donor.email, fontSize = 11.sp, color = Color(0xFF706767))
                                }
                            }

                            IconButton(onClick = { onEmail(donor.email) }) {
                                Icon(Icons.Default.Email, contentDescription = "Email", tint = Color(0xFFB3261E))
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onReject(donor.uid) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB3261E))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Reject")
                            }

                            Button(
                                onClick = { onApprove(donor.uid) },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Approve")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DonorsManagementTab(
    donors: List<Donor>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedStatusFilter: String,
    onStatusFilterChange: (String) -> Unit,
    isSuperAdmin: Boolean,
    onEdit: (Donor) -> Unit,
    onToggleVerify: (String, Boolean) -> Unit,
    onSuspend: (String) -> Unit,
    onRestore: (String) -> Unit,
    onPromote: (String) -> Unit,
    onDemote: (String) -> Unit,
    onEmail: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // SEARCH & FILTER HEADER
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search donor by name, city, email...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFFFFBFF),
                    unfocusedContainerColor = Color(0xFFFFFBFF),
                    focusedBorderColor = Color(0xFFB3261E)
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(listOf("ALL", "ACTIVE", "SUSPENDED", "REJECTED")) { status ->
                    FilterChip(
                        selected = selectedStatusFilter == status,
                        onClick = { onStatusFilterChange(status) },
                        label = { Text(status, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFB3261E),
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(donors) { donor ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBFF)),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (donor.status) {
                            "suspended" -> Color(0xFFFCA5A5)
                            "rejected" -> Color(0xFFE2E8F0)
                            else -> Color(0xFFF4DDDB)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (donor.photoUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = donor.photoUrl,
                                        contentDescription = donor.name,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFFF9DEDC),
                                        modifier = Modifier.size(40.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                donor.name.take(1).uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFB3261E)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(donor.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1C1B1B))
                                        if (donor.verified) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(Icons.Default.Verified, contentDescription = "Verified", tint = Color(0xFF15803D), modifier = Modifier.size(16.dp))
                                        }
                                    }
                                    Text("${donor.bloodGroup} • ${donor.city} • Role: ${donor.role.uppercase()}", fontSize = 11.sp, color = Color(0xFF706767))
                                    Text("Phone: ${donor.phone}", fontSize = 11.sp, color = Color(0xFF706767))
                                }
                            }

                            // Status Tag
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = when (donor.status) {
                                    "suspended" -> Color(0xFFFEF2F2)
                                    "rejected" -> Color(0xFFF3F4F6)
                                    else -> Color(0xFFDCFCE7)
                                }
                            ) {
                                Text(
                                    text = donor.status.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (donor.status) {
                                        "suspended" -> Color(0xFFB3261E)
                                        "rejected" -> Color(0xFF6B7280)
                                        else -> Color(0xFF15803D)
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // ACTION BUTTONS
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onEdit(donor) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("Edit", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = { onToggleVerify(donor.uid, donor.verified) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Text(
                                    if (donor.verified) "Unverify" else "Badge ✓",
                                    fontSize = 11.sp,
                                    color = if (donor.verified) Color(0xFFB3261E) else Color(0xFF15803D)
                                )
                            }

                            if (donor.status == "suspended") {
                                Button(
                                    onClick = { onRestore(donor.uid) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
                                    modifier = Modifier.weight(1.1f)
                                ) {
                                    Text("Restore", fontSize = 11.sp)
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { onSuspend(donor.uid) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFB3261E)),
                                    modifier = Modifier.weight(1.1f)
                                ) {
                                    Text("Suspend", fontSize = 11.sp)
                                }
                            }

                            if (isSuperAdmin && donor.role != "super_admin") {
                                IconButton(
                                    onClick = {
                                        if (donor.role == "admin") onDemote(donor.uid) else onPromote(donor.uid)
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (donor.role == "admin") Icons.Default.RemoveModerator else Icons.Default.AddModerator,
                                        contentDescription = "Change Role",
                                        tint = Color(0xFF7E22CE)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmergenciesManagementTab(
    requests: List<EmergencyRequest>,
    onUpdateStatus: (String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (requests.isEmpty()) {
            item {
                Text("No active emergency blood requests.", color = Color(0xFF706767), fontSize = 14.sp)
            }
        } else {
            items(requests) { req ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBFF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF4DDDB))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Patient: ${req.patientName}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1C1B1B))
                                Text("Hospital: ${req.hospitalName} • ${req.city}", fontSize = 12.sp, color = Color(0xFF706767))
                                Text("Contact: ${req.contactPhone}", fontSize = 12.sp, color = Color(0xFF706767))
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFF9DEDC),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(req.bloodGroup, color = Color(0xFFB3261E), fontWeight = FontWeight.Black, fontSize = 18.sp)
                                }
                            }
                        }

                        if (req.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Note: ${req.notes}", fontSize = 11.sp, color = Color(0xFF706767))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Status: ${req.status}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFFB3261E))

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (req.status != "IN_PROGRESS") {
                                    OutlinedButton(
                                        onClick = { onUpdateStatus(req.requestId, "IN_PROGRESS") },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("In Progress", fontSize = 11.sp)
                                    }
                                }
                                if (req.status != "RESOLVED") {
                                    Button(
                                        onClick = { onUpdateStatus(req.requestId, "RESOLVED") },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                                    ) {
                                        Text("Mark Resolved ✓", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CertificatesTab(
    donations: List<Donation>,
    donors: List<Donor>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (donations.isEmpty()) {
            item {
                Text("No donations recorded in system history.", color = Color(0xFF706767), fontSize = 14.sp)
            }
        } else {
            items(donations) { don ->
                val donor = donors.firstOrNull { it.uid == don.donorId }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBFF)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF4DDDB))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Certificate #: ${don.certificateNumber}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFFB3261E))
                                Text("Donor: ${donor?.name ?: don.donorId}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("Hospital: ${don.hospitalName}", fontSize = 12.sp, color = Color(0xFF706767))
                                Text("Date: ${don.date} • Units: ${don.units} (${don.bloodGroup})", fontSize = 12.sp, color = Color(0xFF706767))
                            }

                            Icon(Icons.Default.CardMembership, contentDescription = "Certificate", tint = Color(0xFF15803D), modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditDonorDialog(
    donor: Donor,
    onDismiss: () -> Unit,
    onSave: (Donor) -> Unit
) {
    var name by remember { mutableStateOf(donor.name) }
    var email by remember { mutableStateOf(donor.email) }
    var phone by remember { mutableStateOf(donor.phone) }
    var city by remember { mutableStateOf(donor.city) }
    var bloodGroup by remember { mutableStateOf(donor.bloodGroup) }
    var photoUrl by remember { mutableStateOf(donor.photoUrl) }
    var healthStatus by remember { mutableStateOf(donor.healthStatus) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Donor Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bloodGroup,
                    onValueChange = { bloodGroup = it },
                    label = { Text("Blood Group (e.g. O+, A-)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = photoUrl,
                    onValueChange = { photoUrl = it },
                    label = { Text("Profile Photo URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = healthStatus,
                    onValueChange = { healthStatus = it },
                    label = { Text("Health Status") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        donor.copy(
                            name = name,
                            email = email,
                            phone = phone,
                            city = city,
                            bloodGroup = bloodGroup,
                            photoUrl = photoUrl,
                            healthStatus = healthStatus
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E))
            ) {
                Text("Save Changes")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
