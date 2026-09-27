package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.theme.*

data class BloodCamp(
    val id: String,
    val title: String,
    val organizer: String,
    val location: String,
    val date: String,
    val time: String,
    val donorsRegistered: Int,
    val targetUnits: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodCampsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val camps = remember {
        listOf(
            BloodCamp(
                id = "CAMP-1",
                title = "Mega Blood Donation Drive 2026",
                organizer = "Medical College & Red Cross Society",
                location = "Medical College Campus, Kozhikode",
                date = "Sunday, Aug 30, 2026",
                time = "9:00 AM - 4:00 PM",
                donorsRegistered = 142,
                targetUnits = 250
            ),
            BloodCamp(
                id = "CAMP-2",
                title = "Youth LifeSaver Camp",
                organizer = "Rotary Club Calicut Central",
                location = "Town Hall, Mananchira, Calicut",
                date = "Friday, Sep 04, 2026",
                time = "10:00 AM - 5:00 PM",
                donorsRegistered = 88,
                targetUnits = 150
            ),
            BloodCamp(
                id = "CAMP-3",
                title = "Corporate Emergency Blood Camp",
                organizer = "Cyberpark Tech Community",
                location = "Cyberpark Clubhouse, Nellikode",
                date = "Wednesday, Sep 09, 2026",
                time = "9:30 AM - 3:30 PM",
                donorsRegistered = 65,
                targetUnits = 100
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(NearPurpleLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Event,
                        contentDescription = null,
                        tint = NearPurplePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Upcoming Blood Camps", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = NearTextPrimary)
                    Text("Donation drives in your region", fontSize = 12.sp, color = NearTextSecondary)
                }
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(camps) { camp ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NearCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(camp.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = NearTextPrimary)
                            Text(camp.organizer, fontSize = 12.sp, color = NearPurplePrimary, fontWeight = FontWeight.Medium)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = NearTextSecondary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(camp.location, fontSize = 12.sp, color = NearTextSecondary)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Schedule, contentDescription = null, tint = NearTextSecondary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${camp.date} • ${camp.time}", fontSize = 12.sp, color = NearTextSecondary)
                            }

                            // Progress
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Donors: ${camp.donorsRegistered}/${camp.targetUnits}", fontSize = 11.sp, color = NearTextSecondary)
                                    Text("${(camp.donorsRegistered * 100) / camp.targetUnits}% filled", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NearGreenSuccess)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { camp.donorsRegistered.toFloat() / camp.targetUnits },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = NearPurplePrimary,
                                    trackColor = NearPurpleLight,
                                )
                            }

                            Button(
                                onClick = {
                                    Toast.makeText(context, "Registered for ${camp.title}!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NearPurpleLight, contentColor = NearPurplePrimary)
                            ) {
                                Text("Register to Donate", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = NearPurplePrimary, fontWeight = FontWeight.Bold)
            }
        }
    )
}
