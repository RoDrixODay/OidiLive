package com.example.oidilive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.oidilive.ui.theme.OidiLiveTheme
import com.example.oidilive.viewmodel.LiveStreamViewModel
import com.example.oidilive.screens.SetupScreen
import com.example.oidilive.screens.LiveStreamScreen
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import com.example.oidilive.model.LiveViewer

class MainActivity : ComponentActivity() {
    private val viewModel: LiveStreamViewModel by viewModels()
    private val TAG = "MainActivity"
    
    // Use the new permission API instead of the deprecated onRequestPermissionsResult
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d(TAG, "Camera permission granted")
        } else {
            Log.d(TAG, "Camera permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Request camera permission using the new API
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            
            enableEdgeToEdge()
            setContent {
                OidiLiveTheme {
                    var isLiveStarted by remember { mutableStateOf(false) }

                    if (!isLiveStarted) {
                        SetupScreen { username, profilePicture ->
                            viewModel.startLive(username, profilePicture)
                            isLiveStarted = true
                        }
                    } else {
                        LiveStreamScreen(viewModel)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
        }
    }
}

// This is a simplified version of the LiveStreamScreen that's now in screens/LiveStreamScreen.kt
// We can remove this duplicate implementation
/*
@Composable
fun LiveStreamScreen(viewModel: LiveStreamViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val viewers by viewModel.viewers.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Fake video placeholder
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.DarkGray)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Viewer count
            Text(
                text = "${uiState.viewerCount} viewers",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Recent viewers
            ViewersList(viewers)

            Spacer(modifier = Modifier.weight(1f))

            // Comments section
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
        }
    }
}
*/

@Composable
fun ViewersList(viewers: List<LiveViewer>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(viewers) { viewer ->
            AsyncImage(
                model = viewer.profilePicture ?: "https://i.pravatar.cc/150?u=${viewer.id}",
                contentDescription = viewer.username,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun CommentItem(comment: com.example.oidilive.model.LiveComment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = comment.username,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = comment.message,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}