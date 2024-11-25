package com.jzheng.tinytimer.data

import androidx.compose.ui.unit.dp

object Constants {
    const val IS_TESTING = false
    val UPDATE_INTERVAL: Int = if (IS_TESTING) 4 else 60
    val defaultPadding = 6.dp
}