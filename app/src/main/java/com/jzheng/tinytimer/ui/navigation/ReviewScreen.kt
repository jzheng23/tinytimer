package com.jzheng.tinytimer.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jzheng.tinytimer.tools.MyPreferenceManager
import com.jzheng.tinytimer.ui.InputCard
import com.jzheng.tinytimer.ui.RadioButtonGroup
import com.jzheng.tinytimer.ui.ReviewCard
import com.jzheng.tinytimer.ui.theme.TimerTheme

@Composable
fun ReviewScreen(
    sharedViewModel: SharedViewModel,
    navController: NavController
) {
    sharedViewModel.isAllTested = true
    val context = LocalContext.current
    TimerTheme {
        Scaffold { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            )
            {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        with(sharedViewModel) {
                            Text(
                                text = "Attention signals",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 16.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            ReviewCard(
                                title = "Threshold",
                                onAuthClick = { navController.navigate("threshold") }
                            ) {
                                InputCard(
                                    desc = "Set your threshold",
                                    value = thresholdInMinute.toString(),
                                    label = "The threshold (in minutes)",
                                    onValueChange = {
                                        thresholdInMinute = it.toIntOrNull() ?: 0
                                        saveThresholdInMinute()
                                    },
                                )
                            }
                            ReviewCard(
                                title = "Toast message",
                                onAuthClick = { navController.navigate("message") }
                            ) {
                                RadioButtonGroup(
                                    desc = "When to show the toast message?",
                                    options = options,
                                    selectedOption = sharedViewModel.options[sharedViewModel.messageFrequency],
                                    onOptionSelected = {
                                        sharedViewModel.saveMessageFrequency(it)
                                    }
                                )
                            }
                            ReviewCard(
                                title = "Animation",
                                onAuthClick = { navController.navigate("animation") }
                            ) {
                                RadioButtonGroup(
                                    desc = "When to show the animation?",
                                    options = options,
                                    selectedOption = options[animationFrequency],
                                    onOptionSelected = {
                                        saveAnimationFrequency(it)
                                    }
                                )
                                RadioButtonGroup(
                                    desc = "Which animation to show?",
                                    options = listOf("Blinking", "Circling"),
                                    selectedOption = if (selectedAnimation == 0) "Blinking" else "Circling",
                                    isEnabled = (animationFrequency != 2),
                                    onOptionSelected = {
                                        saveSelectedAnimation(it)
                                    }
                                )
                            }
                            ReviewCard(
                                title = "Sound",
                                onAuthClick = { navController.navigate("sound") }
                            ) {
                                RadioButtonGroup(
                                    desc = "When to play the sound?",
                                    options = options,
                                    selectedOption = options[soundFrequency],
                                    isEnabled = (checkRingerMode() == 2),
                                    onOptionSelected = {
                                        saveSoundFrequency(it)
                                    }
                                )
                            }
                            ReviewCard(
                                title = "Vibration",
                                onAuthClick = { navController.navigate("vibration") }
                            ) {
                                RadioButtonGroup(
                                    desc = "When to play the vibration?",
                                    options = options,
                                    selectedOption = if (checkRingerMode() != 0) {
                                        options[vibrationFrequency]
                                    } else options[2],
                                    isEnabled = (sharedViewModel.checkRingerMode() != 0),
                                    onOptionSelected = {
                                        sharedViewModel.saveVibrationFrequency(it)
                                    }
                                )
                            }
                        }

                    }

                    Button(
                        onClick = {
                            sharedViewModel.isConfirmed = true
                            MyPreferenceManager.setBoolean(context, "tutorial_completed", true)
                            navController.navigate("home")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }

    }
}