package com.jzheng.tinytimer.ui.navigation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.navigation.NavHostController
import com.jzheng.tinytimer.R
import com.jzheng.tinytimer.data.Constants.defaultPadding
import com.jzheng.tinytimer.service.TimerService
import com.jzheng.tinytimer.tools.MyPermissionManager
import com.jzheng.tinytimer.ui.ArrowCard
import com.jzheng.tinytimer.ui.SwitchCard
import com.jzheng.tinytimer.ui.usePollState
import androidx.core.net.toUri
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.jzheng.tinytimer.tools.MyDailyWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.random.Random


@Composable
fun HomeScreen(
    navController: NavHostController,
    sharedViewModel: SharedViewModel
) {
//    val isTester = false
    val context = LocalContext.current

    val isTimerEnabled by sharedViewModel.isTimerEnabled.collectAsState()
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
                    desc = "Please grant notification permission to TinyTimer",
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


                        SwitchCard(
                            desc = "Show current session length",
                            isChecked = isTimerEnabled,
                            onCheckedChange = {
                                sharedViewModel.updateTimerEnabled(it)
                                if (it) {
                                    scheduleDailyMaintenance(context)
                                }
                            }
                        )

                        Button(
                            onClick = { navController.navigate("review") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Attention signals")
                        }
                    }

                    LogoAndContact()
                }
            }
        }
    }
}


@Composable
fun LogoAndContact(

) {
    val context = LocalContext.current
    val url = stringResource(id = R.string.url_privacy_policy)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(vertical = defaultPadding * 2)
    ) {
        Image(
            painterResource(id = if (androidx.compose.foundation.isSystemInDarkTheme()) R.drawable.logos_wordmark_light_text else R.drawable.logos_wordmark_dark_text),
            contentDescription = null,
            modifier = Modifier
                .weight(1f) // The weight is a ratio, so both use 1f to split space equally
                .fillMaxWidth()
        )
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .weight(1f) // The weight is a ratio, so both use 1f to split space equally
                .fillMaxWidth()
                .padding(end = defaultPadding / 2)
        ) {
            val versionName = getVersionName(context)
            Text(
                text = stringResource(id = R.string.app_name) + " " + versionName,
                style = MaterialTheme.typography.bodyMedium,
            )

            Row {
                Text("By", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.width(defaultPadding / 2))
                Text(
                    text = "Jian Zheng",
                    style = TextStyle(textDecoration = TextDecoration.Underline),
                    modifier = Modifier
                        .clickable {
                            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "message/rfc822" // MIME type for email
                                putExtra(Intent.EXTRA_EMAIL, arrayOf("jzheng23@umd.edu"))
                                putExtra(Intent.EXTRA_SUBJECT, "From Timer")
                            }

                            if (emailIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(
                                    Intent.createChooser(
                                        emailIntent,
                                        "Choose an email client"
                                    )
                                )
                                Log.d("email", "can do")
                            } else {
                                Log.d("email", "cannot do")
                            }
                        }
                        .padding(end = defaultPadding / 3)
                )
            }

            Text(
                text = "Privacy Policy ",
                style = TextStyle(textDecoration = TextDecoration.Underline),
                textAlign = TextAlign.End,
                modifier = Modifier
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Handle the exception when no Activity can handle the Intent
                        }
                    }
            )
        }
    }
}

fun getVersionName(context: Context): String {
    return try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        // Handle the exception if the package name is not found
        "Not Found"
    }
}

fun scheduleDailyMaintenance(context: Context, initialDelayInput: Long = 0L) {
    val myWorkManager = WorkManager.getInstance(context)

    val initialDelay = if (initialDelayInput == 0L) {
        getInitialDelay()
    } else {
        initialDelayInput
    }
    val workRequest = PeriodicWorkRequestBuilder<MyDailyWorker>(24, TimeUnit.HOURS)
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .build()
    myWorkManager.enqueueUniquePeriodicWork(
        "daily_maintenance",
        ExistingPeriodicWorkPolicy.UPDATE, // KEEP or UPDATE
        workRequest
    )
//    val workInfo = myWorkManager.getWorkInfoById(workRequest.id)
//    val workState = workInfo.get()?.state
//    Log.d("workManager", "scheduled! $workState id ${workRequest.id}")
}

fun getInitialDelay(): Long {
    val currentDate = Calendar.getInstance()
    val hourInt = 3
    val randomInt = Random.nextInt(0, 60)
    val dueDate = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hourInt)
        set(Calendar.MINUTE, randomInt)
        set(Calendar.SECOND, 0)
        // If it's after or equal 3 AM, schedule for the next day
        if (before(currentDate)) {
            add(Calendar.HOUR_OF_DAY, 24)
        }
    }
//    Log.d("workManager", "Set at $hourInt:$randomInt")
    return dueDate.timeInMillis - currentDate.timeInMillis
}