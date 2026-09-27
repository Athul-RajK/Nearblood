package com.example.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.ui.components.RequestAmbulanceSosDialog
import com.example.ui.components.SosEmergencyDialog
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.BloodViewModel

sealed class NavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : NavItem("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Requests : NavItem("requests", "Requests", Icons.Filled.WaterDrop, Icons.Outlined.WaterDrop)
    object Donors : NavItem("donors", "Donors", Icons.Filled.Groups, Icons.Outlined.Groups)
    object Profile : NavItem("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun MainAppNavigation(
    viewModel: BloodViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavItem.Home.route

    var showAuthDialog by remember { mutableStateOf(false) }
    var showEmergencyDialog by remember { mutableStateOf(false) }
    var showOnboarding by remember { mutableStateOf(false) }
    val activeSosAlert by viewModel.activeSosAlertForCurrentDonor.collectAsState()

    if (showOnboarding) {
        WelcomeScreen(
            onGetStarted = { showOnboarding = false },
            onImDonor = {
                showOnboarding = false
                showAuthDialog = true
            }
        )
        return
    }

    val isTopLevelDestination = currentRoute in listOf(
        NavItem.Home.route,
        NavItem.Requests.route,
        NavItem.Donors.route,
        NavItem.Profile.route
    )

    Scaffold(
        bottomBar = {
            if (isTopLevelDestination) {
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(64.dp)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // 1. HOME
                        BottomNavItem(
                            item = NavItem.Home,
                            isSelected = currentRoute == NavItem.Home.route,
                            onClick = {
                                if (currentRoute != NavItem.Home.route) {
                                    navController.navigate(NavItem.Home.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )

                        // 2. REQUESTS
                        BottomNavItem(
                            item = NavItem.Requests,
                            isSelected = currentRoute == NavItem.Requests.route,
                            onClick = {
                                if (currentRoute != NavItem.Requests.route) {
                                    navController.navigate(NavItem.Requests.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )

                        // 3. CENTER FLOATING PLUS ACTION
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(NearPurplePrimary)
                                .clickable { showEmergencyDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Emergency Broadcast",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // 4. DONORS
                        BottomNavItem(
                            item = NavItem.Donors,
                            isSelected = currentRoute == NavItem.Donors.route,
                            onClick = {
                                if (currentRoute != NavItem.Donors.route) {
                                    navController.navigate(NavItem.Donors.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )

                        // 5. PROFILE
                        BottomNavItem(
                            item = NavItem.Profile,
                            isSelected = currentRoute == NavItem.Profile.route,
                            onClick = {
                                if (currentRoute != NavItem.Profile.route) {
                                    navController.navigate(NavItem.Profile.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavItem.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToDonors = { navController.navigate(NavItem.Donors.route) },
                    onNavigateToAmbulances = { navController.navigate("ambulances") },
                    onNavigateToVerification = { navController.navigate("verification") },
                    onNavigateToAdmin = { navController.navigate("admin") },
                    onNavigateToProfile = { navController.navigate(NavItem.Profile.route) },
                    onOpenAuth = { showAuthDialog = true }
                )
            }

            composable(NavItem.Requests.route) {
                RequestsScreen(viewModel = viewModel)
            }

            composable(NavItem.Donors.route) {
                DonorsScreen(viewModel = viewModel)
            }

            composable("ambulances") {
                AmbulancesScreen(viewModel = viewModel)
            }

            composable("verification") {
                VerificationScreen(viewModel = viewModel)
            }

            composable(NavItem.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onOpenAuth = { showAuthDialog = true }
                )
            }

            composable("donor_profile") {
                DonorProfileScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenAuth = { showAuthDialog = true },
                    onViewPass = {
                        navController.navigate(NavItem.Profile.route)
                    }
                )
            }

            composable("admin") {
                AdminDashboard(viewModel = viewModel)
            }

            composable("welcome") {
                WelcomeScreen(
                    onGetStarted = { navController.popBackStack() },
                    onImDonor = {
                        navController.popBackStack()
                        showAuthDialog = true
                    }
                )
            }
        }

        if (showAuthDialog) {
            AuthDialog(
                viewModel = viewModel,
                onDismiss = { showAuthDialog = false }
            )
        }

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
                    showEmergencyDialog = false
                }
            )
        }

        activeSosAlert?.let { alert ->
            SosEmergencyDialog(
                alert = alert,
                viewModel = viewModel,
                onDismiss = { viewModel.dismissSosAlert(alert.alertId) }
            )
        }
    }
}

@Composable
private fun BottomNavItem(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(64.dp)
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.title,
            tint = if (isSelected) NearPurplePrimary else NearTextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) NearPurplePrimary else NearTextSecondary
        )
    }
}
