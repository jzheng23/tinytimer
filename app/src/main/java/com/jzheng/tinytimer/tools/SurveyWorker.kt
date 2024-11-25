package com.jzheng.tinytimer.tools

import android.content.Context
import android.util.Log
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jzheng.tinytimer.data.Constants.DAYS_IN_WEEK
import java.util.Calendar
import java.util.concurrent.TimeUnit

// Existing daily worker class can be reused
class SurveyWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        NotificationHelper(applicationContext).showSurveyNotification()
        return Result.success()
    }

    companion object {
        fun scheduleNotification(
            context: Context,
            isTest: Boolean = false
        ) {
            val workRequest = if (isTest) {
                // Test mode: 1 min initial delay, repeat every 5 mins
                OneTimeWorkRequestBuilder<SurveyWorker>()
                    .setInitialDelay(10, TimeUnit.SECONDS)
                    .build()
            } else {
                // Normal mode: schedule for next survey date
                val dayCount = MyPreferenceManager.getInt(context, "day_count", 1)
                val dayDelay = if (dayCount % DAYS_IN_WEEK == 0) DAYS_IN_WEEK else DAYS_IN_WEEK - 1

                val calendar = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, dayDelay)
                    set(Calendar.HOUR_OF_DAY, 21)
                    set(Calendar.MINUTE, 0)
                }

                val initialDelay = calendar.timeInMillis - System.currentTimeMillis()

                PeriodicWorkRequestBuilder<SurveyWorker>(
                    24, TimeUnit.HOURS,
                    PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS, TimeUnit.MILLISECONDS
                )
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .build()
            }


            WorkManager.getInstance(context).apply {
                if (isTest) {
                    enqueueUniqueWork(
                        "testNotification",
                        ExistingWorkPolicy.REPLACE,
                        workRequest as OneTimeWorkRequest
                    )
                } else {
                    enqueueUniquePeriodicWork(
                        "dailyNotification",
                        ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                        workRequest as PeriodicWorkRequest
                    )
                }
            }

            Log.d(
                "SurveyWorker", "Next notification scheduled for: ${
                    if (isTest) {
                        Calendar.getInstance().apply {
                            timeInMillis = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1)
                        }.time
                    } else {
                        "next survey date at 21:00"
                    }
                }"
            )
        }

        fun cancelScheduledNotification(context: Context) {
            WorkManager.getInstance(context).apply {
                cancelUniqueWork("dailyNotification")
                cancelUniqueWork("testNotification")
                Log.d("SurveyWorker", "Scheduled notifications cancelled")
            }
        }
    }
}



