package com.jzheng.tinytimer.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.jzheng.tinytimer.R
import com.jzheng.tinytimer.data.Constants.UPDATE_INTERVAL
import com.jzheng.tinytimer.receiver.ScreenStateReceiver
import com.jzheng.tinytimer.tools.MyPreferenceManager
import com.jzheng.tinytimer.tools.NotificationHelper
import com.jzheng.tinytimer.tools.showMessage

class TimerService : Service() {
    private lateinit var notificationHelper: NotificationHelper
    private val handler = Handler(Looper.getMainLooper())
    private var startTime: Long = 0
    private val screenStateReceiver = ScreenStateReceiver()
    private val updatePeriod = 1000L * UPDATE_INTERVAL

    private val runnable = object : Runnable {
        override fun run() {
            val context = this@TimerService
            val currentTime = System.currentTimeMillis()
            val minutesPassed = ((currentTime - startTime) / updatePeriod).toInt()
            val threshold = MyPreferenceManager.getInt(context, getString(R.string.threshold_in_minute), 99)
            val soundSetting = MyPreferenceManager.getInt(context, getString(R.string.sound_setting), 2)
            val vibrationSetting = MyPreferenceManager.getInt(context, getString(R.string.vibration_setting), 2)

            // Show notification based on settings and threshold
            when {
                minutesPassed < threshold ->
                    notificationHelper.showNotification(minutesPassed)
                minutesPassed == threshold ->
                    notificationHelper.showNotification(
                        minutesPassed,
                        soundEnabled = soundSetting != 2,
                        vibrationEnabled = vibrationSetting != 2
                    )
                else ->
                    notificationHelper.showNotification(
                        minutesPassed,
                        soundEnabled = soundSetting == 1,
                        vibrationEnabled = vibrationSetting == 1
                    )
            }

            when (MyPreferenceManager.getInt(context, getString(R.string.animation_setting), 2)) {
                0 -> if (minutesPassed == threshold) notificationHelper.showAnimation(minutesPassed)
                1 -> if (minutesPassed >= threshold) notificationHelper.showAnimation(minutesPassed)
            }
            when (MyPreferenceManager.getInt(context, getString(R.string.toast_setting), 2)) {
                0 -> if (minutesPassed == threshold) showMessage(context, minutesPassed)
                1 -> if (minutesPassed >= threshold) showMessage(context, minutesPassed)
            }

            handler.postDelayed(this, updatePeriod)
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)
        startForeground(1, notificationHelper.createForegroundNotification())

        registerReceiver(screenStateReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val isScreenOn: Boolean = intent?.getBooleanExtra("isScreenOn", false) ?: false
        updateNotification(isScreenOn)
        return START_STICKY
    }

    private fun updateNotification(isScreenOn: Boolean) {
        // Your notification update logic here
        if (isScreenOn) {
//            handler.removeCallbacks(runnableLock)
            if (MyPreferenceManager.getBoolean(this, getString(R.string.is_timer_enabled))) {
                startTime = System.currentTimeMillis()
                handler.post(runnable)
            } else {
                notificationHelper.showStaticNotification()
            }
        } else {
            handler.removeCallbacks(runnable)
            notificationHelper.showStaticNotification()
        }
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

}