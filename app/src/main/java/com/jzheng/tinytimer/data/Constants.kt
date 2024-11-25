package com.jzheng.tinytimer.data

import androidx.compose.ui.unit.dp

object Constants {
    const val IS_TESTING = false
    val DAYS_IN_WEEK = if (IS_TESTING) 3 else 7 // Number of days in a week, 7
    val UPDATE_INTERVAL: Int = if (IS_TESTING) 4 else 60
    const val HOURS_IN_DAY = 24L // Number of hours in a day, 24
    const val DEFAULT_ICON_NUMBER = 0
    val defaultPadding = 6.dp
    const val PASSWORD: String = "2358"

}