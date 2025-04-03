package com.example.oidilive.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun ViewerCountTooltip() {
    var showTooltip by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(5000) // Hide after 5 seconds
        showTooltip = false
    }

    if (showTooltip) {
        Box(
            modifier = Modifier
                .background(
                    Color.Black.copy(alpha = 0.7f),
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = "Use volume buttons to adjust viewer count",
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
} 