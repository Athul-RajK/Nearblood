package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NearBloodLogoIcon
import com.example.ui.theme.*

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onImDonor: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NearOnboardingGradient)
    ) {
        // Decorative background elements (soft glowing circles and dot pattern)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Top right soft glowing purple radial
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFE9E2FC).copy(alpha = 0.6f), Color.Transparent),
                    center = Offset(w * 0.85f, h * 0.20f),
                    radius = w * 0.45f
                )
            )

            // Bottom left soft glowing radial
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFEDE5FD).copy(alpha = 0.5f), Color.Transparent),
                    center = Offset(w * 0.15f, h * 0.65f),
                    radius = w * 0.5f
                )
            )

            // Top-right subtle dot matrix
            val startX = w * 0.78f
            val startY = h * 0.12f
            val dotSpacing = 16f
            for (row in 0..5) {
                for (col in 0..4) {
                    drawCircle(
                        color = Color(0xFFCFC7E8).copy(alpha = 0.45f),
                        radius = 2.5f,
                        center = Offset(startX + (col * dotSpacing), startY + (row * dotSpacing))
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP STATUS SPACING
            Spacer(modifier = Modifier.height(10.dp))

            // CENTER BRANDING & HEADLINE
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // ICONIC 'N' GRADIENT LOGO
                NearBloodLogoIcon(
                    size = 110.dp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // APP TITLE
                Text(
                    text = "NearBlood",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = NearTextPrimary,
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // HEART ICON
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = NearPurplePrimary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // SLOGAN
                Text(
                    text = "Someone needs you.\nBe the reason they live.",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = NearTextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // SUBTITLE DESCRIPTION
                Text(
                    text = "NearBlood helps you find blood donors near you, when every second counts.",
                    fontSize = 14.sp,
                    color = NearTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }

            // BOTTOM ACTIONS & CAROUSEL INDICATOR
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // PRIMARY PILL BUTTON "Get Started →"
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NearPurplePrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Get Started",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // SECONDARY PILL BUTTON "I'm a Donor"
                OutlinedButton(
                    onClick = onImDonor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NearPurpleBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.8f),
                        contentColor = NearPurplePrimary
                    )
                ) {
                    Text(
                        text = "I'm a Donor",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NearPurplePrimary
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 3-DOT CAROUSEL INDICATOR (Active elongated capsule + 2 dots)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(NearPurplePrimary)
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD6CEF2))
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD6CEF2))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
