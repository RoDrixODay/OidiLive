package com.example.oidilive.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import coil.compose.AsyncImage
import com.example.oidilive.components.*
import com.example.oidilive.model.LiveComment
import com.example.oidilive.model.LiveViewer
import com.example.oidilive.utils.formatViewerNumber
import com.example.oidilive.viewmodel.LiveStreamViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Instagram-inspired colors
private val instagramGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFE1306C), // Instagram pink
        Color(0xFFFF9800), // Instagram orange
        Color(0xFFFFD600)  // Instagram yellow
    )
)

private val instagramBlue = Color(0xFF0095F6)
private val instagramDarkGray = Color(0xFF121212)
private val instagramLightGray = Color(0xFF262626)
private val instagramTextGray = Color(0xFF8E8E8E)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LiveStreamScreen(viewModel: LiveStreamViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val hearts by viewModel.hearts.collectAsState()
    val viewers by viewModel.viewers.collectAsState()
    
    var cameraSelector by remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }
    var isFlashEnabled by remember { mutableStateOf(false) }
    var showEndLiveDialog by remember { mutableStateOf(false) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var commentText by remember { mutableStateOf("") }
    var showViewerTooltip by remember { mutableStateOf(false) }
    
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val scope = rememberCoroutineScope()

    var currentQuality by remember { mutableStateOf(CameraQuality.HD) }
    var isBackCamera by remember { mutableStateOf(false) }

    // Request camera permission immediately
    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // Show viewer tooltip briefly
    LaunchedEffect(Unit) {
        showViewerTooltip = true
        delay(5000)
        showViewerTooltip = false
    }

    // Handle permissions
    PermissionHandler(
        permissionState = cameraPermissionState,
        permissionText = "Camera permission is required for live streaming",
        rationaleText = "The app needs camera access to stream your video. Please grant the permission to continue."
    )

    // End live dialog
    if (showEndLiveDialog) {
        EndLiveDialog(
            onConfirm = {
                viewModel.endLive()
                showEndLiveDialog = false
            },
            onDismiss = {
                showEndLiveDialog = false
            }
        )
    }

    // Main content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(instagramDarkGray)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { offset ->
                        viewModel.addHeart(offset.x)
                    }
                )
            }
    ) {
        // Camera preview
        if (cameraPermissionState.status.isGranted) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                cameraSelector = cameraSelector,
                onCameraReady = { 
                    camera = it
                }
            )
        } else {
            // Camera permission not granted
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(instagramDarkGray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Camera permission required",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Semi-transparent gradient overlay at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Semi-transparent gradient overlay at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Floating hearts
        hearts.forEach { heart ->
            key(heart.id) {
                FloatingHeartAnimation(
                    startPosition = heart.startPosition,
                    onAnimationComplete = { /* Heart will be removed by ViewModel */ }
                )
            }
        }

        // Join notifications
        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 70.dp, start = 16.dp)
        ) {
            uiState.recentJoiners.forEach { username ->
                key(username) {
                    JoinNotification(
                        username = username,
                        onDismiss = {
                            viewModel.removeJoiner(username)
                        }
                    )
                }
            }
        }

        // UI Controls
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Top row with controls and info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Live indicator
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Red)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Host info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = uiState.hostProfilePicture ?: "https://i.pravatar.cc/150?u=${uiState.hostUsername}",
                        contentDescription = "Host",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(2.dp, instagramBlue, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = uiState.hostUsername,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Camera controls
                Row(
                    horizontalArrangement = Arrangement.End
                ) {
                    CameraControls(
                        onSwitchCamera = { 
                            isBackCamera = !isBackCamera
                            cameraSelector = if (isBackCamera) {
                                CameraSelector.DEFAULT_BACK_CAMERA
                            } else {
                                CameraSelector.DEFAULT_FRONT_CAMERA
                            }
                        },
                        onToggleFlash = { 
                            isFlashEnabled = !isFlashEnabled
                            viewModel.toggleFlash(camera, isFlashEnabled)
                        },
                        isFlashEnabled = isFlashEnabled
                    )
                }
            }

            // Top right controls - aligned horizontally
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Viewer count
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Viewers",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = formatViewerNumber(uiState.viewerCount),
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // End live button
                IconButton(
                    onClick = { showEndLiveDialog = true },
                    modifier = Modifier
                        .shadow(4.dp, CircleShape)
                        .background(Color.Red, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "End Live",
                        tint = Color.White
                    )
                }
            }

            // Viewer tooltip
            AnimatedVisibility(
                visible = showViewerTooltip,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Double-tap to send hearts",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }

            // Viewers row
            if (viewers.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy((-8).dp)
                ) {
                    items(viewers) { viewer ->
                        AsyncImage(
                            model = viewer.profilePicture ?: "https://i.pravatar.cc/150?u=${viewer.id}",
                            contentDescription = viewer.username,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            // Spacer to push comments to bottom
            Spacer(modifier = Modifier.weight(1f))

            // Comments section - at the bottom quarter of the screen
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                reverseLayout = true
            ) {
                items(uiState.comments) { comment ->
                    CommentItem(comment)
                }
            }

            // Comment input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { 
                        Text(
                            "Add a comment...", 
                            color = Color.White.copy(alpha = 0.6f)
                        ) 
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = instagramLightGray,
                        unfocusedContainerColor = instagramLightGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            scope.launch {
                                viewModel.addComment(uiState.hostUsername, commentText)
                                commentText = ""
                            }
                        }
                    },
                    modifier = Modifier
                        .background(instagramBlue, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(comment: LiveComment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = "https://i.pravatar.cc/150?u=${comment.username}",
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = comment.username,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = comment.message,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }
    }
} 