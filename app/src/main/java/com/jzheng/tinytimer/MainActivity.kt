package com.jzheng.tinytimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.jzheng.tinytimer.ui.navigation.Navigation
import com.jzheng.tinytimer.ui.theme.TimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerTheme {
                Navigation()
            }
        }
    }
}










