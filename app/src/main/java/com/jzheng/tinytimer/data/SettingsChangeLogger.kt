package com.jzheng.tinytimer.data

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.ServerValue
import com.google.firebase.database.Transaction

object SettingsChangeLogger {
    private val database = FirebaseDatabase.getInstance()
    private val logsRef = database.getReference("settings_change_logs")
    private val counterRef = database.getReference("settings_change_counters")

    fun logSettingChange(userId: String, settingName: String, newValue: Int, context: Context) {

        val host = HostManager.getHost(context)
        val userCounterRef = counterRef.child(userId).child(host)

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
                if (error == null && committed && currentData != null) {
                    val count = currentData.getValue(Int::class.java) ?: return

                    val changeLog = mapOf(
                        "timestamp" to ServerValue.TIMESTAMP,
                        "setting" to settingName,
                        "newValue" to newValue
                    )

                    logsRef.child(userId).child(host).child(count.toString()).setValue(changeLog)
                }
            }
        })
    }
}