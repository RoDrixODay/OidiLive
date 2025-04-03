package com.example.oidilive.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun FloatingHeart(
    startPosition: Float,
    onAnimationComplete: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    val random = Random.Default

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(1000),
        finishedListener = { onAnimationComplete() }
    )

    val offsetY by animateFloatAsState(
        targetValue = if (isVisible) -200f else 0f,
        animationSpec = tween(2000, easing = LinearEasing)
    )

    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1.5f else 0.5f,
        animationSpec = tween(2000)
    )

    val rotation by animateFloatAsState(
        targetValue = if (isVisible) random.nextFloat() * 360f else 0f,
        animationSpec = tween(2000)
    )

    LaunchedEffect(key1 = true) {
        delay(100)
        isVisible = false
    }

    Icon(
        imageVector = Icons.Default.Favorite,
        contentDescription = null,
        tint = Color(0xFFE91E63),
        modifier = Modifier
            .offset(
                x = (startPosition + random.nextFloat() * 40 - 20).dp,
                y = offsetY.dp
            )
            .size(24.dp)
            .graphicsLayer(
                alpha = animatedAlpha,
                scaleX = scale,
                scaleY = scale,
                rotationZ = rotation
            )
    )
} 