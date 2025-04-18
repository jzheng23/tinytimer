package com.jzheng.tinytimer.ui.navigation

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.jzheng.tinytimer.R
import com.jzheng.tinytimer.data.Constants.defaultPadding
import com.jzheng.tinytimer.ui.ButtonCard
import com.jzheng.tinytimer.ui.InputCard
import com.jzheng.tinytimer.ui.RadioButtonGroup
import com.jzheng.tinytimer.ui.theme.TimerTheme

@Composable
fun TutorialPageTemplate(
    title: String,
    description: String,
//    isNextEnabled: Boolean,
//    onPreviousClick: () -> Unit,
//    onNextClick: () -> Unit,
    navController: NavController,
//    sharedViewModel: SharedViewModel,
    content: @Composable () -> Unit,
) {
    TimerTheme {
        Scaffold { paddingValues ->
            Surface(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
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
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = defaultPadding * 2),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        content()
                    }

                    Button(
                        onClick = { navController.navigate("review") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Back to review")
                    }
                }
            }
        }

    }
}

@Composable
fun MessagePage(
    viewModel: SharedViewModel,
    navController: NavController
) {
    TutorialPageTemplate(
        title = "Toast message",
        description = stringResource(R.string.toast_message_test),
//        sharedViewModel = viewModel,
        navController = navController
    ) {
        ButtonCard(
            desc = "Show a toast message",
            onAuthClick = {
                viewModel.testMessage()
                viewModel.isMessageTested = true
            },
            buttonText = "Test"
        )
        RadioButtonGroup(
            desc = "When to show the toast message?",
            options = viewModel.options,
            selectedOption = viewModel.options[viewModel.messageFrequency],
            onOptionSelected = {
                viewModel.saveMessageFrequency(it)
            }
        )
    }
}

@Composable
fun AnimationPage(
    viewModel: SharedViewModel,
    navController: NavController
) {
    TutorialPageTemplate(
        title = "Animation",
        description = stringResource(R.string.animation),
//        sharedViewModel = viewModel,
        navController = navController,
    ) {
        with(viewModel) {
            ButtonCard(
                desc = "Play an animation",
                onAuthClick = {
                    testAnimation()
                    isAnimationTested = true
                },
                buttonText = "Test"
            )
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

    }
}

@Composable
fun SoundPage(
    viewModel: SharedViewModel,
    navController: NavController
) {
    TutorialPageTemplate(
        title = "Sound",
        description = stringResource(R.string.play_sound),
//        sharedViewModel = viewModel,
        navController = navController
    ) {
        var showDialog by remember { mutableStateOf(false) }

        val context = LocalContext.current
        ButtonCard(
            desc = "Play a sound",
            onAuthClick = {
                if (viewModel.checkRingerMode() == 2) {
                    viewModel.testSound()
                    viewModel.isSoundTested = true
                } else {
                    showDialog = true
                }
            },
            buttonText = "Test"
        )
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text("Phone is muted")
                },
                text = {
                    Text("${viewModel.checkSoundProfile()}. " + stringResource(R.string.sound_permission))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.openVideo()
                            showDialog = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = { openMode(context) }) {
                        Text("Show me how")

                    }
                }
            )
        }
        RadioButtonGroup(
            desc = "When to play the sound?",
            options = viewModel.options,
            selectedOption = if (viewModel.checkRingerMode() == 2) {
                viewModel.options[viewModel.soundFrequency]
            } else viewModel.options[2],
            isEnabled = (viewModel.checkRingerMode() == 2),
            onOptionSelected = {
                viewModel.saveSoundFrequency(it)
            }
        )
    }
}


fun openMode(
    context: Context
) {
    val url = context.getString(R.string.url_ringer_mode)
    try {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle the exception when no Activity can handle the Intent
    }
}

@Composable
fun VibrationPage(
    viewModel: SharedViewModel,
    navController: NavController
) {
    TutorialPageTemplate(
        title = "Vibration",
        description = stringResource(R.string.play_vib),
//        sharedViewModel = viewModel,
        navController = navController
    ) {
        val context = LocalContext.current
        var showDialog by remember { mutableStateOf(false) }
        ButtonCard(
            desc = "Play a vibration",
            onAuthClick = {
                if (viewModel.checkRingerMode() != 0) {
                    viewModel.testVibration()
                    viewModel.isVibrationTested = true
                } else {
                    showDialog = true
                }
            },
            buttonText = "Test"
        )
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text("Vibration is disabled")
                },
                text = {
                    Text("${viewModel.checkSoundProfile()}. " + stringResource(R.string.vibration_permission))
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.openVideo()
                            showDialog = false
                        }
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = { openMode(context) }) {
                        Text("Show me how")

                    }
                }
            )
        }
        RadioButtonGroup(
            desc = "When to play the vibration?",
            options = viewModel.options,
            selectedOption = if (viewModel.checkRingerMode() != 0) {
                viewModel.options[viewModel.vibrationFrequency]
            } else viewModel.options[2],
            isEnabled = (viewModel.checkRingerMode() != 0),
            onOptionSelected = {
                viewModel.saveVibrationFrequency(it)
            }
        )
    }
}


@Composable
fun ThresholdPage(
    viewModel: SharedViewModel,
    navController: NavController
) {
    TutorialPageTemplate(
        title = "Threshold of session length",
        description = stringResource(R.string.threshold),
        navController = navController,
//        sharedViewModel = viewModel,
        content = {

            Spacer(modifier = Modifier.height(10.dp))
            InputCard(
                desc = "Set your threshold",
                value = viewModel.thresholdInMinute.toString(),
                label = "The threshold (in minutes)",
                onValueChange = {
                    viewModel.thresholdInMinute = it.toIntOrNull() ?: 0
                    viewModel.saveThresholdInMinute()
                },
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    )
}
