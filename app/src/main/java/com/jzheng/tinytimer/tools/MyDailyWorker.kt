package com.jzheng.tinytimer.tools

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class MyDailyWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showNotification()
        return Result.success()
    }
}