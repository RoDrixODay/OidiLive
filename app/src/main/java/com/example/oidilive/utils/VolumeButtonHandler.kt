package com.example.oidilive.utils

import android.view.KeyEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.activity.ComponentActivity

@Composable
fun VolumeButtonHandler(
    onVolumeUp: () -> Unit,
    onVolumeDown: () -> Unit
) {
    val view = LocalView.current
    
    DisposableEffect(view) {
        val activity = view.context as ComponentActivity
        val keyListener = { keyCode: Int, _: KeyEvent ->
            when (keyCode) {
                KeyEvent.KEYCODE_VOLUME_UP -> {
                    onVolumeUp()
                    true
                }
                KeyEvent.KEYCODE_VOLUME_DOWN -> {
                    onVolumeDown()
                    true
                }
                else -> false
            }
        }
        
        activity.addOnKeyListener(keyListener)
        
        onDispose {
            activity.removeOnKeyListener(keyListener)
        }
    }
}

private fun ComponentActivity.addOnKeyListener(listener: (Int, KeyEvent) -> Boolean) {
    this.window.callback = object : android.view.Window.Callback by this.window.callback {
        override fun dispatchKeyEvent(event: KeyEvent): Boolean {
            return if (event.action == KeyEvent.ACTION_DOWN) {
                listener(event.keyCode, event) || super.dispatchKeyEvent(event)
            } else {
                super.dispatchKeyEvent(event)
            }
        }
    }
}

private fun ComponentActivity.removeOnKeyListener(listener: (Int, KeyEvent) -> Boolean) {
    // Reset to default callback
    this.window.callback = this
} 