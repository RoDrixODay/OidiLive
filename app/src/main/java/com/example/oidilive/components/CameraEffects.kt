package com.example.oidilive.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class CameraEffect {
    NONE, WARM, COOL, GRAYSCALE, SEPIA
}

@Composable
fun CameraEffects(
    selectedEffect: CameraEffect,
    onEffectSelected: (CameraEffect) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(CameraEffect.values()) { effect ->
            FilterChip(
                selected = effect == selectedEffect,
                onClick = { onEffectSelected(effect) },
                label = { Text(effect.name) },
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .clip(CircleShape),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    containerColor = Color.DarkGray
                )
            )
        }
    }
} 