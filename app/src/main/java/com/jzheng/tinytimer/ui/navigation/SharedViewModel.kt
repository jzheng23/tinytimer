package com.jzheng.tinytimer.ui.navigation

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jzheng.tinytimer.R
import com.jzheng.tinytimer.tools.MyPreferenceManager
import com.jzheng.tinytimer.tools.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import androidx.core.net.toUri

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()
    private val notificationHelper = NotificationHelper(getApplication())
    private val _isTimerEnabled = MutableStateFlow(false)
    val isTimerEnabled = _isTimerEnabled.asStateFlow()

    fun updateTimerEnabled(newValue: Boolean) {
        viewModelScope.launch {
            _isTimerEnabled.value = newValue
            MyPreferenceManager.setBoolean(
                context,
                context.getString(R.string.is_timer_enabled),
                newValue
            )
            if (newValue) showStaticNotification()
            else cancelStaticNotification()
        }
    }

    init {
        viewModelScope.launch {
            _isTimerEnabled.value = MyPreferenceManager.getBoolean(
                context,
                context.getString(R.string.is_timer_enabled)
            )
        }
    }

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    val options = listOf(
        "Only once at the threshold",
        "Every minute after the threshold",
        "Never/disabled"
    )

    var isAnimationTested by mutableStateOf(false)
    var isMessageTested by mutableStateOf(false)
    var isSoundTested by mutableStateOf(false)
    var isVibrationTested by mutableStateOf(false)
    var isAllTested by mutableStateOf(false)
    var isConfirmed by mutableStateOf(false)


    var messageFrequency by mutableIntStateOf(
        MyPreferenceManager.getInt(
            context,
            context.getString(R.string.toast_setting),
            0
        )
    )
    var animationFrequency by mutableIntStateOf(
        MyPreferenceManager.getInt(
            context,
            context.getString(R.string.animation_setting),
            0
        )
    )
    var soundFrequency by mutableIntStateOf(
        MyPreferenceManager.getInt(
            context,
            context.getString(R.string.sound_setting),
            0
        )
    )
    var vibrationFrequency by mutableIntStateOf(
        MyPreferenceManager.getInt(
            context,
            context.getString(R.string.vibration_setting),
            0
        )
    )

    var thresholdInMinute by mutableIntStateOf(
        MyPreferenceManager.getInt(
            context,
            context.getString(R.string.threshold_in_minute),
            5
        )
    )

    var selectedAnimation by mutableIntStateOf(
        MyPreferenceManager.getInt(
            context,
            context.getString(R.string.selected_animation),
            0
        )
    )


    private fun showStaticNotification() {
        notificationHelper.showNotification()
    }

    fun testAnimation() {
        if (MyPreferenceManager.getInt(
                context,
                context.getString(R.string.selected_animation)
            ) == 1
        ) {
            notificationHelper.showCirclingAnimation(0)
        } else {
            notificationHelper.showBlinkingAnimation(0)
        }
    }

    fun saveSelectedAnimation(animation: String) {
        selectedAnimation = when (animation) {
            "Blinking" -> 0
            else -> 1
        }
        MyPreferenceManager.setInt(
            context,
            context.getString(R.string.selected_animation),
            selectedAnimation
        )
    }

    fun testMessage() {
        Toast.makeText(
            context,
            "Hey, you have been using your phone for $thresholdInMinute minutes!",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    fun saveMessageFrequency(selectedOption: String) {
        messageFrequency = getOptionIndex(selectedOption)
        MyPreferenceManager.setInt(
            context,
            context.getString(R.string.toast_setting),
            messageFrequency
        )
    }

    fun saveAnimationFrequency(selectedOption: String) {
        animationFrequency = getOptionIndex(selectedOption)
        MyPreferenceManager.setInt(
            context,
            context.getString(R.string.animation_setting),
            animationFrequency
        )
    }

    fun saveSoundFrequency(selectedOption: String) {
        soundFrequency = getOptionIndex(selectedOption)
        MyPreferenceManager.setInt(
            context,
            context.getString(R.string.sound_setting),
            soundFrequency
        )
    }

    fun saveVibrationFrequency(selectedOption: String) {
        vibrationFrequency = getOptionIndex(selectedOption)
        MyPreferenceManager.setInt(
            context,
            context.getString(R.string.vibration_setting),
            vibrationFrequency
        )
    }

    fun checkRingerMode(): Int {
        return audioManager.ringerMode
    }

    fun testSound() {
        notificationHelper.showNotification(
            0,
            soundEnabled = true,
            vibrationEnabled = false
        )
    }

    @Suppress("DEPRECATION")
    fun testVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
            val effect = VibrationEffect.createWaveform(longArrayOf(0, 100, 200, 300), -1)
            val combinedVibration = CombinedVibration.createParallel(effect)
            vibratorManager.vibrate(combinedVibration)
        } else {
            val vibrator =
                context.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
            vibrator.vibrate(longArrayOf(0, 100, 200, 300), -1)
        }
    }

    fun saveThresholdInMinute() {
        val minutes = thresholdInMinute
        MyPreferenceManager.setInt(
            context,
            context.getString(R.string.threshold_in_minute),
            minutes
        )
    }

    fun checkSoundProfile(): String {
        return when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_SILENT -> "Phone is in silent mode"
            AudioManager.RINGER_MODE_VIBRATE -> "Phone is in vibrate mode"
            AudioManager.RINGER_MODE_NORMAL -> "Phone is in normal mode"
            else -> "Unknown mode"
        }
    }

    fun openVideo() {
        val url = context.getString(R.string.url_ringer_mode)
        try {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle the exception when no Activity can handle the Intent
        }
    }

    private fun getOptionIndex(selectedOption: String): Int {

        return when (selectedOption) {
            options[0] -> 0
            options[1] -> 1
            options[2] -> 2
            else -> 0
        }
    }

    private fun cancelStaticNotification() {
        notificationHelper.clearNotification()
    }

}


class SharedViewModelFactory(private val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
