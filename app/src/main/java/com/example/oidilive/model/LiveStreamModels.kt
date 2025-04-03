package com.example.oidilive.model

import android.net.Uri

data class LiveComment(
    val id: Long,
    val username: String,
    val message: String,
    val timestamp: Long
)

data class HeartAnimation(
    val id: Long,
    val startPosition: Float
)

data class LiveStreamState(
    val isLive: Boolean = false,
    val hostUsername: String = "",
    val hostProfilePicture: Uri? = null,
    val viewerCount: Int = 0,
    val comments: List<LiveComment> = emptyList(),
    val recentJoiners: Set<String> = emptySet()
)

data class LiveViewer(
    val id: String,
    val username: String,
    val profilePicture: String? = null
) 