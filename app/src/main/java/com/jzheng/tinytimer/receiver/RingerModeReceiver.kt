package com.jzheng.tinytimer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.jzheng.tinytimer.data.HostManager
import com.jzheng.tinytimer.tools.MyPreferenceManager

class RingerModeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        if (intent.action != AudioManager.RINGER_MODE_CHANGED_ACTION) return

        val userId = MyPreferenceManager.getString(context, "UID", "")
        val uidValid = MyPreferenceManager.getBoolean(context, "UID_valid", false)

        if (uidValid) {
            RingerEventTracker.saveRingerModeToFirebase(
                intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1),
                userId,
                context
            )
        }
    }
}

object RingerEventTracker {
    private val database = FirebaseDatabase.getInstance()
    private val ringerEventsRef = database.getReference("ringer_mode_events")
    private val eventCounterRef = database.getReference("ringer_event_counters")

    fun saveRingerModeToFirebase(mode: Int, userId: String, context: Context) {

        val host = HostManager.getHost(context)
        val userCounterRef = eventCounterRef.child(userId).child(host)

        userCounterRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                val count = currentData.getValue(Int::class.java) ?: 0
                currentData.value = count + 1
                return Transaction.success(currentData)
            }

            override fun onComplete(error: DatabaseError?, committed: Boolean, currentData: DataSnapshot?) {
                if (error == null && committed && currentData != null) {
                    val count = currentData.getValue(Int::class.java) ?: return

                    val ringerEvent = mapOf(
                        "timestamp" to System.currentTimeMillis(),
                        "mode" to mode
                    )

                    ringerEventsRef.child(userId).child(host).child(count.toString()).setValue(ringerEvent)
                }
            }
        })
    }
}