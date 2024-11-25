package com.jzheng.tinytimer.ui.navigation

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jzheng.tinytimer.R
import com.jzheng.tinytimer.data.Constants.DEFAULT_ICON_NUMBER
import com.jzheng.tinytimer.data.Constants.defaultPadding
import com.jzheng.tinytimer.receiver.LocalScreenTracker
import com.jzheng.tinytimer.ui.ButtonCard
import com.jzheng.tinytimer.ui.InputCard
import com.jzheng.tinytimer.ui.RadioButtonGroup
import com.jzheng.tinytimer.ui.theme.TimerTheme

@Composable
fun TutorialPageTemplate(
    title: String,
    description: String,
    isNextEnabled: Boolean,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    navController: NavController,
    sharedViewModel: SharedViewModel,
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

                    if (!sharedViewModel.isAllTested) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(defaultPadding * 2)
                        ) {
                            Button(
                                onClick = onPreviousClick,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Previous")
                            }

                            Button(
                                onClick = onNextClick,
                                enabled = isNextEnabled,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Next")
                            }
                        }
                    } else {
                        // Show Home button if `showNavigationButtons` is false
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
}


@Composable
fun IntroPage(
    navController: NavController
) {
    TutorialPageTemplate(
        title = "Threshold-based signals",
        description = stringResource(R.string.intro_features),
        isNextEnabled = true,
        onPreviousClick = { navController.navigate("home") },
        onNextClick = { navController.navigate("threshold") },
        navController = navController,
        sharedViewModel = viewModel(),
        content = {
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                bitmap = createIcon().asImageBitmap(),
                contentDescription = "Art icon",
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Spacer(modifier = Modifier.height(defaultPadding * 2))
            Text(
                text = stringResource(R.string.intro_features_2),
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(defaultPadding * 2))
            Text(
                text = stringResource(R.string.intro_features_3),
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(defaultPadding * 2))
            Text(
                text = stringResource(R.string.intro_features_4),
                style = MaterialTheme.typography.bodyLarge,
            )
        },
    )
}

fun createIcon(): Bitmap {
    val size = 192
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    paint.color = Color.White.toArgb()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = size * 0.05f

    // Draw rounded rectangle
    val rect = RectF(size * 0.05f, size * 0.05f, size * 0.95f, size * 0.95f)
    val cornerRadius = size * 0.3f
    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

    // Draw text
    paint.style = Paint.Style.FILL
    paint.textSize = size * 0.65f
    paint.textAlign = Paint.Align.CENTER
    paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

    val textX = size / 2f
    val textY = size / 2f - (paint.descent() + paint.ascent()) / 2
    canvas.drawText(DEFAULT_ICON_NUMBER.toString(), textX, textY, paint)

    return bitmap
}

@Composable
fun ThresholdPage(
    viewModel: SharedViewModel,
    navController: NavController
) {
    TutorialPageTemplate(
        title = "Threshold of session length",
        description = stringResource(R.string.threshold),
        isNextEnabled = true,
        onPreviousClick = { navController.navigate("intro") },
        onNextClick = { navController.navigate("threshold_2") },
        navController = navController,
        sharedViewModel = viewModel,
        content = {
            TableContent()
            Text(
                text = stringResource(R.string.table_desc),
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    )
}


@Composable
fun ThresholdPage2(
    viewModel: SharedViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val p80Duration = LocalScreenTracker.getPercentileDuration(context, 0.80)
    TutorialPageTemplate(
        title = "Threshold of session length",
        description = stringResource(R.string.threshold),
        isNextEnabled = true,
        onPreviousClick = { navController.navigate("threshold") },
        onNextClick = { navController.navigate("threshold_3") },
        navController = navController,
        sharedViewModel = viewModel,
        content = {
            TableContent(highlightRow = 5)
            Text(
                text = buildString {
                    append(stringResource(R.string.table_desc_2_1))
                    append(" ")
                    append(p80Duration.toString())
                    append(" ")
                    append(stringResource(R.string.table_desc_2_2))
                    append(" ")
                    append(p80Duration.toString())
                    append(" ")
                    append(stringResource(R.string.table_desc_2_3))
                },
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    )
}


@Composable
fun ThresholdPage3(
    viewModel: SharedViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val p90Duration = LocalScreenTracker.getPercentileDuration(context, 0.90)
    TutorialPageTemplate(
        title = "Threshold of session length",
        description = stringResource(R.string.threshold),
        isNextEnabled = true,
        onPreviousClick = { navController.navigate("threshold_2") },
        onNextClick = { navController.navigate("threshold_4") },
        navController = navController,
        sharedViewModel = viewModel,
        content = {
            TableContent(highlightRow = 3)
            Text(
                text = buildString {
                    append(stringResource(R.string.table_desc_3_1))
                    append(" ")
                    append(p90Duration.toString())
                    append(" ")
                    append(stringResource(R.string.table_desc_3_2))
                    append(" ")
                    append(p90Duration.toString())
                    append(" ")
                    append(stringResource(R.string.table_desc_3_3))
                },
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    )
}


@Composable
fun ThresholdPage4(
    viewModel: SharedViewModel,
    navController: NavController
) {
    TutorialPageTemplate(
        title = "Threshold of session length",
        description = stringResource(R.string.threshold),
        isNextEnabled = true,
        onPreviousClick = { navController.navigate("threshold_3") },
        onNextClick = { navController.navigate("content") },
        navController = navController,
        sharedViewModel = viewModel,
        content = {
            TableContent()
            Text(
                text = stringResource(R.string.table_desc_4),
                style = MaterialTheme.typography.bodyLarge,
            )
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

@Composable
fun ContentScreen(
    viewModel: SharedViewModel,
    navController: NavController
) {
    TutorialPageTemplate(
        title = "Attention signals",
        description = "You have set the threshold to ${viewModel.thresholdInMinute} minutes. When the session duration surpasses ${viewModel.thresholdInMinute} minutes, you will see the following signals.",
        isNextEnabled = true,
        onPreviousClick = { navController.navigate("threshold_4") },
        onNextClick = { navController.navigate("message") },
        navController = navController,
        sharedViewModel = viewModel(),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(4.dp))
//                ArtIconWithText(
//                    painter = painterResource(id = R.drawable.art),
//                    text = "Color change"
//                )
                ArtIconWithText(
                    painter = painterResource(id = R.drawable.chat),
                    text = "Toast message"
                )
                ArtIconWithText(
                    painter = painterResource(id = R.drawable.animate),
                    text = "Animation"
                )
                ArtIconWithText(
                    painter = painterResource(id = R.drawable.volume),
                    text = "Sound"
                )
                ArtIconWithText(
                    painter = painterResource(id = R.drawable.vibrate),
                    text = "Vibration"
                )
            }

        },
    )
}

@Composable
fun MessagePage(
    viewModel: SharedViewModel,
    navController: NavController
) {
    TutorialPageTemplate(
        title = "Toast message",
        description = stringResource(R.string.toast_message_test),
        isNextEnabled = viewModel.isMessageTested,

        onPreviousClick = { navController.navigate("content") },
        onNextClick = { navController.navigate("animation") },
        sharedViewModel = viewModel,
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
        isNextEnabled = viewModel.isAnimationTested,

        onPreviousClick = { navController.navigate("message") },
        onNextClick = { navController.navigate("sound") },
        sharedViewModel = viewModel,
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
        isNextEnabled = viewModel.isSoundTested,
        onPreviousClick = { navController.navigate("animation") },
        onNextClick = { navController.navigate("vibration") },
        sharedViewModel = viewModel,
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
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
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
        isNextEnabled = viewModel.isVibrationTested,
        onPreviousClick = { navController.navigate("sound") },
        onNextClick = { navController.navigate("review") },
        sharedViewModel = viewModel,
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
fun TableContent(
    highlightRow: Int? = null
) {
    val context = LocalContext.current
    val tableData = LocalScreenTracker.getTableData(context)
    // Sample data for the table
//    val tableData = listOf(
//        Pair("99", "0.5%"),
//        Pair("64", "1%"),
//        Pair("23", "5%"),
//        Pair("13", "10%"),
//        Pair("8", "15%"),
//        Pair("5", "20%"),
//        Pair("4", "25%"),
//        Pair("3", "30%"),
//        Pair("2", "40%"),
//        Pair("1", "50%")
//    )

    // Column to display table rows
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Table header
        TableRow(
            header = true,
            duration = "Duration (minute)",
            percentage = "% above the duration"
        )
        // Table rows
        tableData.forEachIndexed { index, (duration, percentage) ->
            TableRow(
                header = false,
                duration = duration,
                percentage = percentage,
                isHighlighted = index == highlightRow
            )
        }
    }
}

@Composable
fun TableRow(
    header: Boolean,
    duration: String,
    percentage: String,
    isHighlighted: Boolean = false
) {
    val borderColor = MaterialTheme.colorScheme.outline
    val backgroundColor = if (isHighlighted) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, borderColor))
            .background(backgroundColor),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .border(BorderStroke(1.dp, Color.Black))
                .padding(2.dp)
        ) {
            Text(
                text = duration,
                style = if (header) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = if (isHighlighted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterEnd)
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .border(BorderStroke(1.dp, borderColor))
                .padding(2.dp)
        ) {
            Text(
                text = percentage,
                style = if (header) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = if (isHighlighted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}


@Composable
fun ArtIconWithText(
    painter: Painter,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(8.dp)
    ) {
        Image(
            painter = painter,
            contentDescription = "Art icon",
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}
