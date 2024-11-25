package com.jzheng.tinytimer.ui.navigation

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
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
import com.jzheng.tinytimer.R
import com.jzheng.tinytimer.data.Constants.DEFAULT_ICON_NUMBER
import com.jzheng.tinytimer.data.SettingsChangeLogger
import com.jzheng.tinytimer.tools.MyPreferenceManager
import com.jzheng.tinytimer.tools.NotificationHelper
import kotlin.reflect.KProperty

class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()
    private val notificationHelper = NotificationHelper(getApplication())
    private val userId = MyPreferenceManager.getString(context, "UID", "")
    private val uidValid = MyPreferenceManager.getBoolean(context, "UID_valid", false)

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

    private fun createState(
        settingName: String,
        initialValue: Int
    ) = if (uidValid) {
        ObservedMutableIntState(userId, settingName, initialValue, context)
    } else {
        RegularMutableIntState(initialValue)
    }

    var messageFrequency by createState(
        "messageFrequency",
        MyPreferenceManager.getInt(
            context,
            context.getString(R.string.toast_setting),
            0
        )
    )
    var animationFrequency by createState(
        "animationFrequency",
        MyPreferenceManager.getInt(
            context,
            context.getString(R.string.animation_setting),
            0
        )
    )
    var soundFrequency by createState(
        "soundFrequency",
        MyPreferenceManager.getInt(
            context,
            context.getString(R.string.sound_setting),
            0
        )
    )
    var vibrationFrequency by createState(
        "vibrationFrequency",
        MyPreferenceManager.getInt(
            context,
            context.getString(R.string.vibration_setting),
            0
        )
    )

    var thresholdInMinute by createState(
        "thresholdInMinute",
        MyPreferenceManager.getInt(
            context,
            context.getString(R.string.threshold_in_minute),
            5
        )
    )

    var selectedAnimation by createState(
        "selectedAnimation",
        MyPreferenceManager.getInt(
            context,
            context.getString(R.string.selected_animation),
            0
        )
    )


    fun showStaticNotification() {
        notificationHelper.showStaticNotification()
    }

    fun testAnimation() {
        if (MyPreferenceManager.getInt(
                context,
                context.getString(R.string.selected_animation)
            ) == 1
        ) {
            notificationHelper.showCirclingAnimation(DEFAULT_ICON_NUMBER)
        } else {
            notificationHelper.showBlinkingAnimation(DEFAULT_ICON_NUMBER)
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
//        val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val ringtone = RingtoneManager.getRingtone(context, notificationSound)
//        ringtone.play()
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
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
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

interface IntStateDelegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int)
}

class ObservedMutableIntState(
    private val userId: String,
    private val settingName: String,
    initialValue: Int,
    private val context: Context
) : IntStateDelegate {
    private val state = mutableIntStateOf(initialValue)

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return state.intValue
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        if (state.intValue != value) {
            state.intValue = value
            SettingsChangeLogger.logSettingChange(userId, settingName, value, context)

        }
    }
}

class RegularMutableIntState(initialValue: Int) : IntStateDelegate {
    private val state = mutableIntStateOf(initialValue)

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return state.intValue
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        state.intValue = value
    }
}