package com.jzheng.tinytimer.tools

import android.content.Context
import android.content.SharedPreferences

object MyPreferenceManager {
    private const val PREFS_NAME = "MySharedPrefs"
    private lateinit var prefs: SharedPreferences

    private fun getPrefs(context: Context): SharedPreferences {
        if (!::prefs.isInitialized) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
        return prefs
    }

    fun getInt(context: Context, key: String, defaultValue: Int = 0): Int =
        getPrefs(context).getInt(key, defaultValue)

    fun getString(context: Context, key: String, defaultValue: String = ""): String =
        getPrefs(context).getString(key, defaultValue) ?: ""

    fun getBoolean(context: Context, key: String, defaultValue: Boolean = false): Boolean =
        getPrefs(context).getBoolean(key, defaultValue)

    fun setInt(context: Context, key: String, value: Int) {
        getPrefs(context)
            .edit()
            .putInt(key, value)
            .apply()
    }

    fun setString(context: Context, key: String, value: String) {
        getPrefs(context)
            .edit()
            .putString(key, value)
            .apply()
    }

    fun setBoolean(context: Context, key: String, value: Boolean) {
        getPrefs(context)
            .edit()
            .putBoolean(key, value)
            .apply()
    }
}