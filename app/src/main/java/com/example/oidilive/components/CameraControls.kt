package com.example.oidilive.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.oidilive.R

@Composable
fun CameraControls(
    onSwitchCamera: () -> Unit,
    onToggleFlash: () -> Unit,
    isFlashEnabled: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Camera flip button
        IconButton(
            onClick = onSwitchCamera,
            modifier = Modifier
                .shadow(4.dp, CircleShape)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                .size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_flip_camera),
                contentDescription = "Switch Camera",
                tint = Color.White
            )
        }
        
        // Flash toggle button
        IconButton(
            onClick = onToggleFlash,
            modifier = Modifier
                .shadow(4.dp, CircleShape)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                .size(40.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (isFlashEnabled) R.drawable.ic_flash_on else R.drawable.ic_flash_off
                ),
                contentDescription = "Toggle Flash",
                tint = if (isFlashEnabled) Color(0xFFFFD600) else Color.White
            )
        }
    }
} 