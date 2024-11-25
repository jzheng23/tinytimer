package com.jzheng.tinytimer.ui.navigation

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.jzheng.tinytimer.R
import com.jzheng.tinytimer.data.Constants.DAYS_IN_WEEK
import com.jzheng.tinytimer.data.Constants.PASSWORD
import com.jzheng.tinytimer.data.Constants.defaultPadding
import com.jzheng.tinytimer.service.TimerService
import com.jzheng.tinytimer.tools.MyPermissionManager
import com.jzheng.tinytimer.tools.MyPreferenceManager
import com.jzheng.tinytimer.tools.NotificationHelper
import com.jzheng.tinytimer.tools.dayCountPlusPlus
import com.jzheng.tinytimer.ui.ArrowCard
import com.jzheng.tinytimer.ui.usePollState


@Composable
fun HomeScreen(
    navController: NavHostController
) {
//    val isTester = false
    var showTutorialDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val context2 = LocalContext.current.applicationContext as Application
    val sharedPrefsViewModel: SharedPrefsViewModel = viewModel(
        factory = SharedPrefsViewModelFactory(context2)
    )
    val notificationAllowed by usePollState {
        MyPermissionManager.checkNotificationPermission(context)
    }

    val userId by sharedPrefsViewModel.userId.observeAsState("")
    val isTester by sharedPrefsViewModel.isTester.observeAsState(false)
    val isSurvey1Completed by sharedPrefsViewModel.survey1Completed.observeAsState(false)
    val isSurvey2Completed by sharedPrefsViewModel.survey2Completed.observeAsState(false)
    val isSurvey3Completed by sharedPrefsViewModel.survey3Completed.observeAsState(false)
    val isSurvey4Completed by sharedPrefsViewModel.survey4Completed.observeAsState(false)
    val isTutorialCompleted by sharedPrefsViewModel.tutorialCompleted.observeAsState(false)
    val dayCount by sharedPrefsViewModel.dayCount.observeAsState(1)

    val instructionText by remember {
        derivedStateOf {
            when {
                !validateUid(userId) -> "Participant ID is invalid. Data collection is disabled."
                isSurvey4Completed -> "Congratulations! You've completed the study. The researcher will contact you shortly."
                dayCount > (DAYS_IN_WEEK * 3) && !isSurvey4Completed -> "Please complete Survey 4."
                isSurvey3Completed && !isTutorialCompleted -> "Please complete Transition Tutorial."
                dayCount > (DAYS_IN_WEEK * 2) && !isSurvey3Completed -> "Please complete Survey 3."
                dayCount > DAYS_IN_WEEK && !isSurvey2Completed -> "Please complete Survey 2."
                !isSurvey1Completed -> "Please complete Survey 1."
                else -> "Everything is set."
            }
        }
    }

    Log.d("prefs", "Is survey1 completed is $isSurvey1Completed")
    if (userId == "") navController.navigate("login")

    if (isSurvey1Completed) {
        context.startForegroundService(
            Intent(
                context,
                TimerService::class.java
            )
        )
    }

    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {

            if (showTutorialDialog) {
                AlertDialog(
                    onDismissRequest = { showTutorialDialog = false },
                    title = {
                        Text("About the tutorial")
                    },
                    text = {
                        Text(stringResource(R.string.tutorial_confirm))
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showTutorialDialog = false
                                navController.navigate("intro")
                            }
                        ) {
                            Text("I'm ready")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showTutorialDialog = false
                            }
                        ) {
                            Text("Not yet")
                        }
                    }
                )
            }
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
                        if (!isSurvey4Completed) {
                            Text(
                                text = "Day " + calculateDay(dayCount).toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = defaultPadding * 2),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "Week " + calculateWeek(dayCount).toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = defaultPadding * 2),
                                textAlign = TextAlign.Center
                            )
                        }

                        Text(
                            text = instructionText,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = defaultPadding * 2),
                            textAlign = TextAlign.Center
                        )

                        if (validateUid(userId)) {
                            Column(
                                horizontalAlignment = Alignment.Start,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = defaultPadding)

                            ) {
                                if (!isSurvey1Completed) {
                                    Button(
                                        onClick = {
                                            navController.navigate("survey1")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                    ) {
                                        Text(text = "Survey 1")
                                    }
                                }
                                WeekRow(
                                    text = "Week 1",
                                    checkedItems = generateWeekData(dayCount, 0)
                                )
                                if (!isSurvey2Completed) {
                                    Button(
                                        onClick = {
                                            navController.navigate("survey2")
                                        },
                                        enabled = dayCount > DAYS_IN_WEEK - 1,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                    ) {
                                        Text(text = "Survey 2")
                                    }
                                }

                                WeekRow(
                                    text = "Week 2",
                                    isActive = dayCount > DAYS_IN_WEEK,
                                    checkedItems = generateWeekData(dayCount, 1)
                                )
                                if (!isTutorialCompleted) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (!isSurvey3Completed) {
                                            Button(
                                                onClick = { navController.navigate("survey3") },
                                                enabled = dayCount > (DAYS_IN_WEEK * 2 - 1),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(end = defaultPadding / 2),
                                            ) {
                                                Text(text = "Survey 3")
                                            }
                                        }

                                        Button(
                                            onClick = { showTutorialDialog = true },
                                            enabled = isSurvey3Completed,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(start = defaultPadding / 2),
                                        ) {
                                            Text(text = "Transition Tutorial")
                                        }
                                    }
                                }
                                WeekRow(
                                    text = "Week 3",
                                    isActive = dayCount > (DAYS_IN_WEEK * 2),
                                    checkedItems = generateWeekData(
                                        dayCount,
                                        2
                                    )
                                )
                                if (!isSurvey4Completed) {
                                    Button(
                                        onClick = {
                                            navController.navigate("survey4")
                                        },
                                        enabled = dayCount > (DAYS_IN_WEEK * 3 - 1),
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                    ) {
                                        Text(text = "Survey 4")
                                    }
                                }
                            }
                        }


                        if (isTutorialCompleted && !isSurvey4Completed) {
                            Button(
                                onClick = { navController.navigate("review") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "Settings")
                            }
                        }

                        if (isTester) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = defaultPadding)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(defaultPadding)
                                ) {
                                    Text(
                                        text = "Debug (only visible to testers)",
                                        style = MaterialTheme.typography.titleMedium,
                                    )

                                    Button(
                                        onClick = {
                                            dayCountPlusPlus(context)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Day++")
                                    }
                                    Button(
                                        onClick = {
                                            MyPreferenceManager.setInt(context, "day_count", 1)
                                            MyPreferenceManager.setBoolean(
                                                context,
                                                context.getString(R.string.survey1_completed),
                                                false
                                            )
                                            MyPreferenceManager.setBoolean(
                                                context,
                                                context.getString(R.string.survey2_completed),
                                                false
                                            )
                                            MyPreferenceManager.setBoolean(
                                                context,
                                                context.getString(R.string.survey3_completed),
                                                false
                                            )
                                            MyPreferenceManager.setBoolean(
                                                context,
                                                context.getString(R.string.survey4_completed),
                                                false
                                            )
                                            MyPreferenceManager.setBoolean(
                                                context,
                                                context.getString(R.string.tutorial_completed),
                                                false
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Reset Day")
                                    }
                                    Button(
                                        onClick = { navController.navigate("review") },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = "Settings")
                                    }
                                    Button(
                                        onClick = {
                                            NotificationHelper(context).showSurveyNotification()
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = "Second notification")
                                    }
                                }

                            }

                        }


                    }

                    LogoAndContact(navController)
                }
            }

        }
    }
}


@Composable
fun LogoAndContact(
    navController: NavHostController
) {
    val context = LocalContext.current
    var showCheatDialog by remember { mutableStateOf(false) }
    val url = stringResource(id = R.string.url_privacy_policy)
    var password by remember { mutableStateOf("") }

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
                modifier = Modifier.clickable { showCheatDialog = true }
            )
            if (showCheatDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showCheatDialog = false
                        password = ""
                    },
                    title = { Text("Enter Password") },
                    text = {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { if (password == PASSWORD) navController.navigate("cheat") }
                            )
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (password == PASSWORD) {
                                navController.navigate("cheat")
                                showCheatDialog = false
                            }
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showCheatDialog = false
                            password = ""
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
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
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
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

@Composable
fun CircleWithCheck(status: Int) {
    Box(
        modifier = Modifier
            .size((defaultPadding * 4)) // Circle size
            .clip(CircleShape)
            .alpha((status + 1).toFloat() / 3f)// Make the shape circular
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        if (status == 2) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = "Completed",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        } else if (status == 1) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Ongoing",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.rotate(90f)
            )
        }
    }
}


@Composable
fun WeekRow(
    text: String,
    isActive: Boolean = true,
    checkedItems: List<Int>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = defaultPadding)
            .alpha(if (isActive) 1f else 0.4f),

        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = defaultPadding)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
            )
            LazyRow(
                horizontalArrangement = Arrangement.SpaceEvenly, // Space between circles
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(defaultPadding)
            ) {
                items(checkedItems.size) { index ->
                    if (isActive) {
                        CircleWithCheck(status = checkedItems[index])
                    } else {
                        CircleWithCheck(status = 0)
                    }

                }
            }
        }
    }


}

fun generateWeekData(dayCount: Int, weekOffset: Int): List<Int> {
    val currentWeekStart = weekOffset * DAYS_IN_WEEK + 1
//    val currentWeekEnd = currentWeekStart + daysInWeek - 1

    return List(DAYS_IN_WEEK) { dayIndex ->
        val dayOfWeek = currentWeekStart + dayIndex
        when {
            dayOfWeek < dayCount -> 2  // Completed
            dayOfWeek == dayCount -> 1 // Ongoing
            else -> 0 // Not started
        }
    }
}

fun calculateWeek(input: Int): Int {
    return when (input) {
        in 1..DAYS_IN_WEEK -> 1
        in DAYS_IN_WEEK + 1..DAYS_IN_WEEK * 2 -> 2
        in DAYS_IN_WEEK * 2 + 1..DAYS_IN_WEEK * 3 + 1 -> 3
        else -> 0
    }
}

fun calculateDay(number: Int): Int {
    val residual = number % DAYS_IN_WEEK
    return if (residual == 0) DAYS_IN_WEEK else residual
}