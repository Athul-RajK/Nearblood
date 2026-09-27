package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun NearBloodLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    Box(
        modifier = modifier
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Gradient for the ribbons
            val primaryGrad = Brush.linearGradient(
                colors = listOf(Color(0xFF5B3DE8), Color(0xFF8B5CF6), Color(0xFF9F75FF)),
                start = Offset(0f, 0f),
                end = Offset(w, h)
            )

            val secondaryGrad = Brush.linearGradient(
                colors = listOf(Color(0xFF8B5CF6), Color(0xFF6342E8), Color(0xFF4C28CB)),
                start = Offset(w, 0f),
                end = Offset(0f, h)
            )

            val strokeWidth = w * 0.22f
            val cornerRadius = w * 0.18f

            // Left stem with rounded top and bottom turn
            val leftPath = Path().apply {
                moveTo(w * 0.24f, h * 0.85f)
                lineTo(w * 0.24f, h * 0.22f)
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(w * 0.24f - strokeWidth/2, h * 0.22f - strokeWidth/2, w * 0.24f + strokeWidth/2, h * 0.22f + strokeWidth/2),
                    startAngleDegrees = 180f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(w * 0.50f, h * 0.11f)
                arcTo(
                    rect = androidx.compose.ui.geometry.Rect(w * 0.38f, h * 0.11f, w * 0.62f, h * 0.35f),
                    startAngleDegrees = 270f,
                    sweepAngleDegrees = 90f,
                    forceMoveTo = false
                )
                lineTo(w * 0.24f, h * 0.75f)
            }

            // Diagonal ribbon crossing
            val ribbonPath1 = Path().apply {
                moveTo(w * 0.22f, h * 0.32f)
                cubicTo(
                    w * 0.22f, h * 0.15f,
                    w * 0.45f, h * 0.15f,
                    w * 0.55f, h * 0.35f
                )
                lineTo(w * 0.78f, h * 0.78f)
                cubicTo(
                    w * 0.82f, h * 0.85f,
                    w * 0.75f, h * 0.90f,
                    w * 0.65f, h * 0.85f
                )
                lineTo(w * 0.35f, h * 0.38f)
                close()
            }

            // Stylized 3D modern geometric "N" ribbon
            val barWidth = w * 0.24f
            val rad = CornerRadius(w * 0.12f, w * 0.12f)

            // Left vertical rounded pillar
            drawRoundRect(
                brush = primaryGrad,
                topLeft = Offset(w * 0.15f, h * 0.15f),
                size = Size(barWidth, h * 0.70f),
                cornerRadius = rad
            )

            // Right vertical rounded pillar
            drawRoundRect(
                brush = secondaryGrad,
                topLeft = Offset(w * 0.61f, h * 0.15f),
                size = Size(barWidth, h * 0.70f),
                cornerRadius = rad
            )

            // Connecting diagonal ribbon with shadow/glow
            val diagPath = Path().apply {
                moveTo(w * 0.15f, h * 0.28f)
                lineTo(w * 0.39f, h * 0.15f)
                lineTo(w * 0.85f, h * 0.72f)
                lineTo(w * 0.61f, h * 0.85f)
                close()
            }

            drawPath(
                path = diagPath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF8B5CF6), Color(0xFF6342E8)),
                    start = Offset(w * 0.2f, h * 0.2f),
                    end = Offset(w * 0.8f, h * 0.8f)
                )
            )

            // Center subtle inner accent highlight
            drawCircle(
                color = Color.White.copy(alpha = 0.25f),
                radius = w * 0.08f,
                center = Offset(w * 0.50f, h * 0.50f)
            )
        }
    }
}

@Composable
fun BloodDropBadge(
    bloodGroup: String,
    modifier: Modifier = Modifier,
    color: Color = GroupRed,
    bgColor: Color = GroupRedBg,
    size: Dp = 42.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.52f)) {
            val w = this.size.width
            val h = this.size.height

            val dropPath = Path().apply {
                moveTo(w * 0.5f, 0f)
                cubicTo(w * 0.85f, h * 0.55f, w, h * 0.75f, w * 0.5f, h)
                cubicTo(0f, h * 0.75f, w * 0.15f, h * 0.55f, w * 0.5f, 0f)
                close()
            }
            drawPath(dropPath, color = color)
        }
    }
}
