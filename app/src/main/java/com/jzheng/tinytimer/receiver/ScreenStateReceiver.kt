package com.jzheng.tinytimer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.jzheng.tinytimer.data.HostManager
import com.jzheng.tinytimer.service.TimerService
import com.jzheng.tinytimer.tools.MyPreferenceManager
import java.util.Locale


class ScreenStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val serviceIntent = Intent(context, TimerService::class.java)
        val userId = MyPreferenceManager.getString(context, "UID", "")
        val uidValid = MyPreferenceManager.getBoolean(context, "UID_valid", false)

        val isScreenOn = when (intent.action) {
            Intent.ACTION_SCREEN_ON -> {
                LocalScreenTracker.recordScreenOn(context)
                serviceIntent.putExtra("isScreenOn", true)
                true
            }

            Intent.ACTION_SCREEN_OFF -> {
                LocalScreenTracker.recordScreenOff(context)
                serviceIntent.putExtra("isScreenOn", false)
                false
            }

            else -> return
        }

        context.startService(serviceIntent)

        // Save screen state to Firebase
        if (uidValid) {
            ScreenEventTracker.saveScreenStateToFirebase(isScreenOn, userId, context)
        }

    }

}

object ScreenEventTracker {
    private val database = FirebaseDatabase.getInstance()
    private val screenEventsRef = database.getReference("screen_events")
    private val eventCounterRef = database.getReference("screen_event_counters")
    private var lastScreenOnTime: Long? = null

    fun saveScreenStateToFirebase(isScreenOn: Boolean, userId: String, context: Context) {
        val currentTime = System.currentTimeMillis()
        val host = HostManager.getHost(context)

        if (isScreenOn) {
            lastScreenOnTime = currentTime
        } else if (lastScreenOnTime != null) {
            val userCounterRef = eventCounterRef.child(userId).child(host)

            userCounterRef.runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    // Get current count or initialize to 0 if not exists
                    val count = currentData.getValue(Int::class.java) ?: 0
                    // Increment count
                    currentData.value = count + 1
                    return Transaction.success(currentData)
                }

                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    // Add null checks
                    if (error != null || !committed || currentData == null) {
                        return
                    }

                    val count = currentData.getValue(Int::class.java) ?: return

                    val screenEvent = mapOf(
                        "startTime" to lastScreenOnTime!!,
                        "endTime" to currentTime
                    )

                    screenEventsRef.child(userId).child(host).child(count.toString()).setValue(screenEvent)
                    lastScreenOnTime = null
                }
            })
        }
    }
}

// New object for local tracking
object LocalScreenTracker {
    private const val PREFS_NAME = "screen_events"
    private const val KEY_LAST_ON = "last_screen_on"
    private const val KEY_DURATIONS = "session_durations"

    fun recordScreenOn(context: Context?) {
        context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putLong(KEY_LAST_ON, System.currentTimeMillis())
            ?.apply()
    }

    fun recordScreenOff(context: Context?) {
        val prefs = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) ?: return
        val lastOn = prefs.getLong(KEY_LAST_ON, 0)
        if (lastOn == 0L) return

        val duration = System.currentTimeMillis() - lastOn
        val durations = getDurations(prefs).toMutableList()
        durations.add(duration)

        prefs.edit()
            .putString(KEY_DURATIONS, durations.joinToString(","))
            .apply()
    }

    fun getTableData(context: Context): List<Pair<String, String>> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val durations = getDurations(prefs).sorted()

        if (durations.isEmpty()) return emptyList()

        val durationInMinutes = durations.map { it / (60 * 1000) }

        return listOf(
            // Special case for 99 minutes
            Pair(
                "99",
                "${
                    String.format(
                        Locale.US,
                        "%.1f",
                        100 - calculatePercentileRank(durationInMinutes, 99L)
                    )
                }%"
            ),
            Pair(percentile(durationInMinutes, 0.99).toString(), "1%"),
            Pair(percentile(durationInMinutes, 0.95).toString(), "5%"),
            Pair(percentile(durationInMinutes, 0.90).toString(), "10%"),
            Pair(percentile(durationInMinutes, 0.85).toString(), "15%"),
            Pair(percentile(durationInMinutes, 0.80).toString(), "20%"),
            Pair(percentile(durationInMinutes, 0.75).toString(), "25%"),
            Pair(percentile(durationInMinutes, 0.70).toString(), "30%"),
            Pair(percentile(durationInMinutes, 0.60).toString(), "40%"),
            Pair(percentile(durationInMinutes, 0.50).toString(), "50%")
        )
    }

    fun getPercentileDuration(context: Context, percentileRank: Double): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val durations = getDurations(prefs).sorted()
        if (durations.isEmpty()) return 0

        val durationInMinutes = durations.map { it / (60 * 1000) }
        return percentile(durationInMinutes, percentileRank)
    }

    private fun percentile(sortedList: List<Long>, percentile: Double): Long {
        if (sortedList.isEmpty()) return 0
        val index = (percentile * (sortedList.size - 1)).toInt()
        return sortedList[index]
    }

    private fun calculatePercentileRank(sortedList: List<Long>, value: Long): Double {
        val countBelow = sortedList.count { it <= value }
        return (countBelow.toDouble() / sortedList.size) * 100
    }

    private fun getDurations(prefs: SharedPreferences): List<Long> {
        return prefs.getString(KEY_DURATIONS, "")
            ?.split(",")
            ?.filter { it.isNotEmpty() }
            ?.map { it.toLong() }
            ?: emptyList()
    }
}