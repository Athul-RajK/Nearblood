package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

/**
 * Custom QR Code Renderer for Jetpack Compose Canvas.
 * Generates a deterministic 2D QR matrix based on the content string.
 */
@Composable
fun QrCodeView(
    content: String,
    modifier: Modifier = Modifier,
    size: Dp = 160.dp,
    qrColor: Color = Color(0xFF1E293B),
    backgroundColor: Color = Color.White
) {
    val matrixSize = 21 // Standard QR Version 1 size (21x21)
    
    // Generate deterministic boolean matrix from string content
    val qrMatrix = remember(content) {
        val matrix = Array(matrixSize) { BooleanArray(matrixSize) }
        val hash = abs(content.hashCode())

        // 1. Finder patterns (Top-Left, Top-Right, Bottom-Left)
        drawFinderPattern(matrix, 0, 0)
        drawFinderPattern(matrix, matrixSize - 7, 0)
        drawFinderPattern(matrix, 0, matrixSize - 7)

        // 2. Timing patterns
        for (i in 7 until matrixSize - 7) {
            matrix[6][i] = (i % 2 == 0)
            matrix[i][6] = (i % 2 == 0)
        }

        // 3. Fill data modules based on string bytes and hash seed
        val bytes = content.toByteArray()
        var byteIdx = 0
        var bitIdx = 0
        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                // Skip finder pattern zones
                if (isFinderZone(r, c, matrixSize)) continue
                if (r == 6 || c == 6) continue // Skip timing lines

                val byteVal = if (bytes.isNotEmpty()) bytes[byteIdx % bytes.size].toInt() else hash
                val bitSet = ((byteVal shr (bitIdx % 8)) and 1) == 1
                matrix[r][c] = bitSet xor ((r + c + hash) % 3 == 0)

                bitIdx++
                if (bitIdx % 8 == 0) byteIdx++
            }
        }
        matrix
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellWidth = this.size.width / matrixSize
            val cellHeight = this.size.height / matrixSize

            for (r in 0 until matrixSize) {
                for (c in 0 until matrixSize) {
                    if (qrMatrix[r][c]) {
                        drawRect(
                            color = qrColor,
                            topLeft = Offset(c * cellWidth, r * cellHeight),
                            size = Size(cellWidth * 0.92f, cellHeight * 0.92f)
                        )
                    }
                }
            }
        }

        // Center Brand Emblem
        Box(
            modifier = Modifier
                .size(size * 0.22f)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFDC2626)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🩸",
                fontSize = 12.sp
            )
        }
    }
}

private fun drawFinderPattern(matrix: Array<BooleanArray>, row: Int, col: Int) {
    for (r in 0..6) {
        for (c in 0..6) {
            val isOuterBorder = r == 0 || r == 6 || c == 0 || c == 6
            val isInnerCore = r in 2..4 && c in 2..4
            matrix[row + r][col + c] = isOuterBorder || isInnerCore
        }
    }
}

private fun isFinderZone(r: Int, c: Int, matrixSize: Int): Boolean {
    val inTopLeft = r < 8 && c < 8
    val inTopRight = r < 8 && c >= matrixSize - 8
    val inBottomLeft = r >= matrixSize - 8 && c < 8
    return inTopLeft || inTopRight || inBottomLeft
}
