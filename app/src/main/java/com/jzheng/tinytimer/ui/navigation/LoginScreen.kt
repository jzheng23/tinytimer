package com.jzheng.tinytimer.ui.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase
import com.jzheng.tinytimer.data.Constants.defaultPadding
import com.jzheng.tinytimer.data.DeviceInfo
import com.jzheng.tinytimer.data.HostManager
import com.jzheng.tinytimer.service.TimerService
import com.jzheng.tinytimer.tools.DailyWorker
import com.jzheng.tinytimer.tools.MyPermissionManager
import com.jzheng.tinytimer.ui.ArrowCard
import com.jzheng.tinytimer.ui.usePollState
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: SharedViewModel
) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val notificationAllowed by usePollState {
        MyPermissionManager.checkNotificationPermission(context)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Welcome!") },
            )
        },
        content = { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)

            ) {
                Column(
                    modifier = Modifier
//                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = defaultPadding)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
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
                        ArrowCard(
                            title = "Login",
                            desc = "Please login with the assigned participant ID. If you cannot find your ID, please contact jzheng23@umd.edu.",
                            onAuthClick = {
                                showDialog = true
                            }
                        )
                    }
                }
                if (showDialog) {
                    Dialog(
                        onDismissRequest = {
                            showDialog = false
                        },
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                var userIdInput by remember { mutableStateOf("") }
                                OutlinedTextField(
                                    value = userIdInput,
                                    onValueChange = {
                                        userIdInput = it
                                    },
                                    label = { Text("Participant ID") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next
                                    ),
                                    modifier = Modifier.semantics {
                                        contentDescription = "Participant ID"
                                    }
                                )
                                Button(
                                    onClick = {
                                        saveUserId(
                                            userIdInput,
                                            context
                                        )
                                        showDialog = false
                                        viewModel.showStaticNotification()
                                        navController.navigate("home")
                                    },
                                    enabled = (userIdInput != ""),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .semantics { contentDescription = "Confirm login" }
                                ) {
                                    Text("Confirm")
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}


fun saveUserId(
    userId: String,
    context: Context
) {
    val userIdIsValidate: Boolean = validateUid(userId)
    val isTester = isTester(userId)
    val sharedPreferences =
        context.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("UID", userId)
        .putBoolean("UID_valid", userIdIsValidate)
        .putBoolean("is_tester", isTester)
        .apply()
    if (userIdIsValidate) {
        DeviceInfoTracker.saveTimezoneToFirebase(userId, context)
        DailyWorker.schedule(context)
    }
}

fun isTester(userId: String?): Boolean {
    return userId?.startsWith("te", ignoreCase = true) ?: false
}

fun validateUid(userId: String?): Boolean {
    val pattern = "^[A-Za-z]{2}\\d{3}$"
    val regex = pattern.toRegex()
    return if (userId == null) {
        false
    } else {
        regex.matches(userId)
    }
}

object DeviceInfoTracker {
    private val database = FirebaseDatabase.getInstance()
    private val timezoneRef = database.getReference("timezones")
    private val deviceInfoRef = database.getReference("device_info")

    fun saveTimezoneToFirebase(userId: String, context: Context) {
        val host = HostManager.getHost(context)
        timezoneRef.child(userId).child(host).setValue(TimeZone.getDefault().id)
        deviceInfoRef.child(userId).child(host).setValue(DeviceInfo.getAllInfo())
    }
}
