package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Donation

@Composable
fun DonationCertificateView(
    donation: Donation,
    donorName: String,
    modifier: Modifier = Modifier,
    onShareCertificate: () -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFDFB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFFD97706), RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Gold Seal Badge
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(27.dp))
                    .background(Color(0xFFFEF3C7))
                    .border(1.5.dp, Color(0xFFD97706), RoundedCornerShape(27.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Gold Seal",
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "CERTIFICATE OF APPRECIATION",
                color = Color(0xFF1E293B),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Text(
                text = "NEARBLOOD DONOR RECOGNITION",
                color = Color(0xFFDC2626),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "This certificate is proudly presented to",
                color = Color(0xFF64748B),
                fontSize = 12.sp
            )

            Text(
                text = donorName,
                color = Color(0xFF0F172A),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Text(
                text = "for selflessly donating ${donation.units} unit(s) of Blood (${donation.bloodGroup}) at ${donation.hospitalName} on ${donation.date}.",
                color = Color(0xFF334155),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "CERTIFICATE NO.", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(text = donation.certificateNumber, color = Color(0xFF1E293B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "VERIFIED BY", color = Color(0xFF94A3B8), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Nearblood Registry", color = Color(0xFF059669), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onShareCertificate,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Share Digital Certificate (PDF)", fontWeight = FontWeight.Bold)
            }
        }
    }
}
