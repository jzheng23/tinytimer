package com.jzheng.tinytimer.tools

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jzheng.tinytimer.R
import com.jzheng.tinytimer.data.Constants.DAYS_IN_WEEK
import com.jzheng.tinytimer.data.Constants.HOURS_IN_DAY
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DailyWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        dayCountPlusPlus(applicationContext)
        return Result.success()
    }

    companion object {
        fun schedule(context: Context) {
            // Calculate initial delay until 4am
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                if (HOURS_IN_DAY == 24L) set(Calendar.HOUR_OF_DAY, 4)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (before(now)) add(Calendar.HOUR_OF_DAY, HOURS_IN_DAY.toInt())
            }
            val initialDelay = target.timeInMillis - now.timeInMillis

            val request = PeriodicWorkRequestBuilder<DailyWorker>(HOURS_IN_DAY, TimeUnit.HOURS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    "DayCountUpdate",
                    ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                    request
                )
        }
    }
}

fun dayCountPlusPlus(context: Context) {
    when (val currentCount = MyPreferenceManager.getInt(context,
        context.getString(R.string.day_count), 1)) {
        1 -> if (MyPreferenceManager.getBoolean(context, context.getString(R.string.survey1_completed), false)) {
            MyPreferenceManager.setInt(context, context.getString(R.string.day_count), 2)
        }

        DAYS_IN_WEEK + 1 -> if (MyPreferenceManager.getBoolean(
                context,
                context.getString(R.string.survey2_completed),
                false
            )
        ) {
            MyPreferenceManager.setInt(context, context.getString(R.string.day_count), currentCount + 1)
        }

        DAYS_IN_WEEK * 2 + 1 -> if (MyPreferenceManager.getBoolean(
                context,
                context.getString(R.string.tutorial_completed),
                false
            )
        ) {
            MyPreferenceManager.setInt(context, context.getString(R.string.day_count), currentCount + 1)
        }

        else -> MyPreferenceManager.setInt(
            context,
            context.getString(R.string.day_count),
            minOf(currentCount + 1, DAYS_IN_WEEK * 3 + 1)
        )
    }
}