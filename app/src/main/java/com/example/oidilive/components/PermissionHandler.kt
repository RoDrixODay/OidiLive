package com.example.oidilive.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionHandler(
    permissionState: PermissionState,
    permissionText: String,
    rationaleText: String
) {
    if (!permissionState.status.isGranted) {
        if (permissionState.status.shouldShowRationale) {
            AlertDialog(
                onDismissRequest = { },
                title = { Text("Permission Required") },
                text = { Text(rationaleText) },
                confirmButton = {
                    Button(onClick = { permissionState.launchPermissionRequest() }) {
                        Text("Grant Permission")
                    }
                }
            )
        } else {
            LaunchedEffect(Unit) {
                permissionState.launchPermissionRequest()
            }
        }
    }
} 