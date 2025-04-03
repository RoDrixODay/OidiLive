package com.example.oidilive.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oidilive.model.LiveComment
import com.example.oidilive.model.LiveStreamState
import com.example.oidilive.model.LiveViewer
import com.example.oidilive.model.HeartAnimation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlinx.coroutines.delay
import android.net.Uri
import com.example.oidilive.components.CameraEffect
import androidx.camera.core.Camera
import com.example.oidilive.components.CameraQuality
import android.util.Log

class LiveStreamViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LiveStreamState())
    val uiState: StateFlow<LiveStreamState> = _uiState.asStateFlow()

    private val kenyanUsernames = listOf(
        "kamau_254", "wanjiku_ke", "kipchoge_fan", "nairobi_finest", 
        "mkenya_halisi", "safari_lover", "simba_pride", "jambo_kenya",
        "maasai_morani", "tuko_pamoja", "hakuna_matata", "mombasa_bae",
        "kikuyu_princess", "kalenjin_warrior", "luo_finest", "samburu_queen"
    )
    
    private val internationalUsernames = listOf(
        "emma_uk", "john_usa", "tokyo_fan", "paris_lover", 
        "aussie_mate", "canadian_eh", "nigeria_prince", "south_africa_love"
    )

    private val kenyanMessages = listOf(
        "Wewe ni moto sana! 🔥", 
        "Nakupenda sana! ❤️",
        "Umetuletea Kenya kwenye ramani! 👏",
        "Sisi Wakenya tunakupenda sana!",
        "Unatufanya proud! 🇰🇪",
        "Mwanake wa Kenyan! 💯",
        "Ubarikiwe sana! 🙏",
        "Nairobi tunakupenda!",
        "Mombasa inakusalimia! 🌊",
        "Kisumu proud! 👑",
        "Eldoret tunakuwatch! 🏃‍♂️",
        "Nakuru inakupenda! ❤️",
        "Asante kwa kutuwakilisha! 🇰🇪",
        "Umechoma leo! 🔥",
        "Endelea kutufanya proud! 👏"
    )
    
    private val internationalMessages = listOf(
        "Hello from London! Love Kenya! 🇬🇧❤️🇰🇪",
        "Greetings from USA! Kenya is beautiful! 🇺🇸",
        "I visited Kenya last year, amazing country! 🌍",
        "Love your content from Australia! 🇦🇺",
        "Sending love from Nigeria! 🇳🇬",
        "Canada loves Kenya! 🇨🇦❤️🇰🇪",
        "Japan here! Kenya safari was the best! 🇯🇵",
        "South Africa loves our Kenyan neighbors! 🇿🇦",
        "I'm learning Swahili because of you! 🌍",
        "Kenya has the best wildlife! Planning my visit! 🦁"
    )

    private val _hearts = MutableStateFlow<Set<HeartAnimation>>(emptySet())
    val hearts: StateFlow<Set<HeartAnimation>> = _hearts.asStateFlow()

    private var _selectedEffect = MutableStateFlow(CameraEffect.NONE)
    val selectedEffect: StateFlow<CameraEffect> = _selectedEffect.asStateFlow()

    private var isAutoViewerIncrement = true
    private var manualViewerCount = 50

    private var isFlashEnabled = false

    private val _viewers = MutableStateFlow<List<LiveViewer>>(emptyList())
    val viewers: StateFlow<List<LiveViewer>> = _viewers.asStateFlow()

    init {
        startSimulation()
    }

    private fun startSimulation() {
        viewModelScope.launch {
            // Simulate increasing viewer count
            while (true) {
                _uiState.update { 
                    it.copy(viewerCount = it.viewerCount + Random.nextInt(1, 5))
                }
                kotlinx.coroutines.delay(2000)
            }
        }

        viewModelScope.launch {
            // Simulate new comments
            while (true) {
                val isKenyan = Random.nextDouble() < 0.85 // 85% chance of Kenyan comments
                
                val username = if (isKenyan) kenyanUsernames.random() else internationalUsernames.random()
                val message = if (isKenyan) kenyanMessages.random() else internationalMessages.random()
                
                addCommentInternal(username, message)
                kotlinx.coroutines.delay(Random.nextLong(500, 2000))
            }
        }

        viewModelScope.launch {
            // Simulate new viewers joining
            while (true) {
                val isKenyan = Random.nextDouble() < 0.8 // 80% chance of Kenyan viewers
                val username = if (isKenyan) kenyanUsernames.random() else internationalUsernames.random()
                
                _uiState.update {
                    it.copy(recentJoiners = it.recentJoiners + username)
                }
                
                // Add a welcome comment
                addCommentInternal(username, "Just joined! 👋")
                
                kotlinx.coroutines.delay(Random.nextLong(1000, 3000))
            }
        }
    }

    // Private method for internal use
    private fun addCommentInternal(username: String, message: String) {
        _uiState.update { 
            val newComment = LiveComment(
                id = System.currentTimeMillis(),
                username = username,
                message = message,
                timestamp = System.currentTimeMillis()
            )
            it.copy(comments = (it.comments + newComment).takeLast(50))
        }
    }

    // Public method for external use
    fun addComment(username: String, message: String) {
        _uiState.update { 
            val newComment = LiveComment(
                id = System.currentTimeMillis(),
                username = username,
                message = message,
                timestamp = System.currentTimeMillis()
            )
            it.copy(comments = (it.comments + newComment).takeLast(50))
        }
    }

    fun addHeart(xPosition: Float) {
        viewModelScope.launch {
            try {
                val newHeart = HeartAnimation(
                    id = System.currentTimeMillis(),
                    startPosition = xPosition
                )
                _hearts.update { it + newHeart }
                delay(2000) // Remove heart after animation
                _hearts.update { it - newHeart }
            } catch (e: Exception) {
                Log.e("LiveStreamViewModel", "Error adding heart: ${e.message}")
            }
        }
    }

    fun startLive(username: String, profilePicture: Uri?) {
        _uiState.update { 
            it.copy(
                hostUsername = username,
                hostProfilePicture = profilePicture,
                isLive = true
            )
        }
        generateRandomViewers()
        startSimulation()
    }

    fun removeJoiner(username: String) {
        _uiState.update { 
            it.copy(recentJoiners = it.recentJoiners - username)
        }
    }

    fun updateCameraEffect(effect: CameraEffect) {
        _selectedEffect.value = effect
    }

    fun endLive() {
        _uiState.update { it.copy(isLive = false) }
        // Additional cleanup if needed
    }

    fun toggleFlash(camera: Camera?, isEnabled: Boolean) {
        camera?.let {
            try {
                if (it.cameraInfo.hasFlashUnit()) {
                    it.cameraControl.enableTorch(isEnabled)
                }
            } catch (e: Exception) {
                // Log error but don't crash
                Log.e("LiveStreamViewModel", "Failed to toggle flash: ${e.message}")
            }
        }
    }

    fun incrementViewers(amount: Int = 10) {
        isAutoViewerIncrement = false
        _uiState.update { 
            it.copy(viewerCount = (it.viewerCount + amount).coerceAtLeast(0))
        }
    }

    fun decrementViewers(amount: Int = 10) {
        isAutoViewerIncrement = false
        _uiState.update { 
            it.copy(viewerCount = (it.viewerCount - amount).coerceAtLeast(0))
        }
    }

    fun updateStreamQuality(quality: CameraQuality) {
        viewModelScope.launch {
            when (quality) {
                CameraQuality.SD -> {
                    // Configure for 480p
                    // Example: 854x480, bitrate 1000000
                }
                CameraQuality.HD -> {
                    // Configure for 720p
                    // Example: 1280x720, bitrate 2500000
                }
            }
        }
    }

    private fun generateRandomViewers() {
        val randomViewers = List(10) { index ->
            LiveViewer(
                id = "viewer_$index",
                username = kenyanUsernames.random(),
                profilePicture = "https://i.pravatar.cc/150?u=viewer_$index"
            )
        }
        _viewers.value = randomViewers
    }
} 