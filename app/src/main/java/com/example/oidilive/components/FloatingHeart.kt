package com.example.oidilive.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun FloatingHeartAnimation(
    startPosition: Float,
    onAnimationComplete: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    
    val xOffset = remember { startPosition }
    val yOffset = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val scale = remember { Animatable(0f) }
    
    val heartColor = remember {
        listOf(
            Color.Red,
            Color(0xFFE91E63),
            Color(0xFFFF4081)
        ).random()
    }
    
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = Random.nextFloat() * 0.5f + 0.8f,
            animationSpec = tween(200)
        )
        
        yOffset.animateTo(
            targetValue = -500f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = FastOutSlowInEasing
            )
        )
        
        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 1800,
                easing = LinearEasing
            )
        )
        
        delay(100)
        isVisible = false
        onAnimationComplete()
    }
    
    if (isVisible) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = heartColor,
            modifier = Modifier
                .offset(x = xOffset.dp, y = yOffset.value.dp)
                .alpha(alpha.value)
                .scale(scale.value)
                .size(40.dp)
        )
    }
} 