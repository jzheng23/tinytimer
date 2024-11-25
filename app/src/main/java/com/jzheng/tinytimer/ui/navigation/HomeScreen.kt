package com.jzheng.tinytimer.ui.navigation

import android.app.Application
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jzheng.tinytimer.R
import com.jzheng.tinytimer.data.Constants.defaultPadding
import com.jzheng.tinytimer.service.TimerService
import com.jzheng.tinytimer.tools.MyPermissionManager
import com.jzheng.tinytimer.tools.MyPreferenceManager
import com.jzheng.tinytimer.ui.ArrowCard
import com.jzheng.tinytimer.ui.usePollState


@Composable
fun HomeScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
//    val isTester = false
    val context = LocalContext.current
    val context2 = LocalContext.current.applicationContext as Application
    val sharedPrefsViewModel: SharedPrefsViewModel = viewModel(
        factory = SharedPrefsViewModelFactory(context2)
    )
    val notificationAllowed by usePollState {
        MyPermissionManager.checkNotificationPermission(context)
    }
    context.startForegroundService(
        Intent(
            context,
            TimerService::class.java
        )
    )

    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (!notificationAllowed) {
                ArrowCard(
                    title = "Permission",
                    desc = "Please grant notification permission to Timer",
                    onAuthClick = {
                        MyPermissionManager.requestNotificationPermission(context)
                        context.startForegroundService(
                            Intent(
                                context,
                                TimerService::class.java
                            )
                        )
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                        .padding(defaultPadding * 2),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Button(
                            onClick = {
                                MyPreferenceManager.setBoolean(
                                    context,
                                    context.getString(R.string.is_timer_enabled),
                                    true
                                )
                                sharedViewModel.showStaticNotification()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Start Timer")
                        }
                        Button(
                            onClick = { navController.navigate("review") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Settings")
                        }
                    }
                }
            }
        }
    }
}
