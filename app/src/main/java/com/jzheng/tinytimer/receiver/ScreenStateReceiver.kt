package com.jzheng.tinytimer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jzheng.tinytimer.service.TimerService


class ScreenStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val serviceIntent = Intent(context, TimerService::class.java)

        when (intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                serviceIntent.putExtra("isScreenOn", true)
            }

            Intent.ACTION_SCREEN_OFF -> {
                serviceIntent.putExtra("isScreenOn", false)
            }

            else -> return
        }
        context.startService(serviceIntent)

    }

}