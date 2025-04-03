package com.example.oidilive.utils

fun formatViewerNumber(count: Int): String {
    return when {
        count < 1000 -> count.toString()
        count < 10000 -> String.format("%.1fK", count / 1000.0)
        count < 1000000 -> String.format("%dK", count / 1000)
        else -> String.format("%.1fM", count / 1000000.0)
    }
} 