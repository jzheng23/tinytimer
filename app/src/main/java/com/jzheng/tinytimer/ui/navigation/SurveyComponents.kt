package com.jzheng.tinytimer.ui.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jzheng.tinytimer.R
import com.jzheng.tinytimer.data.Constants.defaultPadding
import com.jzheng.tinytimer.data.Survey
import com.jzheng.tinytimer.data.SurveyPage
import com.jzheng.tinytimer.data.SurveyQuestion
import com.jzheng.tinytimer.data.SurveyTemplates.endingMessages
import com.jzheng.tinytimer.tools.MyPreferenceManager
import com.jzheng.tinytimer.tools.NotificationHelper
import com.jzheng.tinytimer.tools.SurveyWorker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ExpandableSurveyQuestion(
    question: SurveyQuestion,
    questionIndex: Int,  // Add these parameters
    questions: SnapshotStateList<SurveyQuestion>,  // Add these parameters
    onExpandToggle: () -> Unit,
    onOptionSelected: (String) -> Unit,
    onTextInput: (String) -> Unit = {}, // Add this
    showDivider: Boolean
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.padding(vertical = defaultPadding / 2)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onExpandToggle)
                .padding(vertical = defaultPadding / 2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (question.isExpanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown,
                contentDescription = if (question.isExpanded) "Collapse" else "Expand"
            )
        }

        if (question.isOpenEnded) {
            OutlinedTextField(
                value = question.textInput,
                onValueChange = onTextInput,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                label = { Text("Your answer") },
                minLines = 3,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (questionIndex < questions.size - 1) {
                            questions[questionIndex] =
                                questions[questionIndex].copy(isExpanded = false)
                            questions[questionIndex + 1] =
                                questions[questionIndex + 1].copy(isExpanded = true)
                        }
                        focusManager.clearFocus()
                    }
                )
            )
        } else {
            if (!question.isExpanded && question.selectedOption != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = defaultPadding / 2),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(defaultPadding * 2),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = defaultPadding * 2)
                        )
                        Text(
                            text = question.selectedOption!!,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        // Show selected option when collapsed


        AnimatedVisibility(
            visible = question.isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                question.options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clickable { onOptionSelected(option) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = question.selectedOption == option,
                            onClick = { onOptionSelected(option) }
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = defaultPadding)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
        if (showDivider) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurveyPageScreen(
    surveyPage: SurveyPage,
    totalAnsweredQuestions: Int,
    totalQuestions: Int,
    onPreviousClick: (List<String?>) -> Unit = {}, // Modify to accept current answers
    onNextClick: (List<String?>) -> Unit = {},
    onComplete: (List<String?>) -> Unit = {},
    showPreviousButton: Boolean = true,
    onQuestionAnswered: () -> Unit = {},
    savedAnswers: List<String?> = emptyList(),
    surveyId: Int // Add this parameter

) {
    if (surveyPage.questions.isEmpty()) {
        EndingPageScreen(
            message = endingMessages[surveyId] ?: "Survey completed!",
            onComplete = { onComplete(emptyList()) },
            surveyId = surveyId
        )
    } else {
        val coroutineScope = rememberCoroutineScope()
        val scrollState = rememberScrollState()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        val questions = remember(surveyPage.questions, savedAnswers) {
            mutableStateListOf(
                *surveyPage.questions.mapIndexed { index, text ->
                    // If this is a new page (no saved answers), expand first question
                    // If returning to a page with some answers, expand first unanswered question
                    val firstUnansweredIndex = if (savedAnswers.isEmpty()) {
                        0 // New page - expand first question
                    } else {
                        savedAnswers.indexOfFirst { it == null } // Returning to page - expand first unanswered
                    }

                    SurveyQuestion(
                        text = text,
                        options = surveyPage.options,
                        isExpanded = index == firstUnansweredIndex,
                        selectedOption = savedAnswers.getOrNull(index),
                        isOpenEnded = surveyPage.isOpenEnded  // Add this line
                    )
                }.toTypedArray()
            )
        }

        val isNextEnabled by remember(questions) {
            derivedStateOf { questions.all { it.selectedOption != null } }
        }
        val progressPercent = ((totalAnsweredQuestions.toFloat() / totalQuestions) * 100).toInt()


        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), // Add this line
            containerColor = MaterialTheme.colorScheme.surface,
            topBar = {
                TopAppBar(
                    title = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = defaultPadding * 2)
                        ) {
                            LinearProgressIndicator(
                                progress = { totalAnsweredQuestions.toFloat() / totalQuestions },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                            )

                            Text(
                                text = "$progressPercent% Survey Completion",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = defaultPadding)
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior, // Add this line
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(paddingValues)
                    .padding(horizontal = defaultPadding * 2),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = surveyPage.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = defaultPadding)
                    )

                    questions.forEachIndexed { index, question ->
                        ExpandableSurveyQuestion(
                            question = question,
                            questionIndex = index,  // Add these
                            questions = questions,  // Add these
                            onExpandToggle = {
                                questions[index] = question.copy(isExpanded = !question.isExpanded)
                            },
                            onOptionSelected = { option ->
                                val isFirstAnswer = questions[index].selectedOption == null
                                questions[index] = question.copy(selectedOption = option)
                                if (isFirstAnswer) {
                                    onQuestionAnswered() // Only call when answering for the first time
                                }
                                coroutineScope.launch {
                                    delay(300)
                                    questions[index] = questions[index].copy(isExpanded = false)

                                    if (index < questions.size - 1 && questions[index + 1].selectedOption == null) {
                                        questions[index + 1] =
                                            questions[index + 1].copy(isExpanded = true)
                                    }
                                }
                            },
                            onTextInput = { text ->
                                questions[index] = question.copy(
                                    textInput = text,
                                    selectedOption = text  // Set the full text as selectedOption
                                )
                                if (text.isNotEmpty() && question.selectedOption == null) {
//                                    questions[index] = questions[index].copy(selectedOption = text)
                                    onQuestionAnswered()
                                }
                            },
                            showDivider = index < questions.size - 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(defaultPadding))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = defaultPadding),
                    horizontalArrangement = Arrangement.spacedBy(defaultPadding * 2)
                ) {
                    if (showPreviousButton) {
                        Button(
                            onClick = { onPreviousClick(questions.map { it.selectedOption }) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Previous")
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Button(
                        onClick = {
                            if (isNextEnabled) {
                                onNextClick(questions.map { it.selectedOption })
                            }
                        },
                        enabled = isNextEnabled,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Next")
                    }
                }
            }
        }
    }
}

// Composable to handle multi-page surveys
@Composable
fun MultiPageSurvey(
    survey: Survey,
    responses: Map<Int, List<String?>>,
    currentPageIndex: Int,
    surveyId: Int,
    onPageComplete: (Int, List<String?>) -> Unit,
    onSurveyComplete: (List<List<String?>>) -> Unit,
) {
    var answeredQuestions by remember { mutableIntStateOf(0) }
    val totalQuestions = survey.pages.sumOf { it.questions.size }

    LaunchedEffect(responses) {
        answeredQuestions = responses.values.sumOf { pageResponses ->
            pageResponses.count { it != null }
        }
    }
    AnimatedContent(
        targetState = currentPageIndex,
        transitionSpec = {
            // You can adjust the animation duration by changing the milliseconds
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "Page Transition"
    ) { targetPage ->
        SurveyPageScreen(
            surveyPage = survey.pages[targetPage],
            totalAnsweredQuestions = answeredQuestions,
            totalQuestions = totalQuestions,
            onPreviousClick = { currentAnswers ->
                if (targetPage > 0) {
                    onPageComplete(targetPage - 1, currentAnswers)
                }
            },
            onNextClick = { currentAnswers ->
                if (targetPage < survey.pages.size - 1) {
                    onPageComplete(targetPage + 1, currentAnswers)
                } else {
                    onSurveyComplete(responses.values.toList())
                }
            },
            onQuestionAnswered = {
                answeredQuestions++
            },
            onComplete = { onSurveyComplete(responses.values.toList()) },
            showPreviousButton = targetPage > 0,
            savedAnswers = responses[targetPage] ?: emptyList(),
            surveyId = surveyId
        )
    }
}

@Composable
fun EndingPageScreen(
    message: String = "GG",
    onComplete: () -> Unit = {},
    surveyId: Int = 0 // Add surveyId parameter
) {
    val context = LocalContext.current
    val notificationHelper = NotificationHelper(context)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(defaultPadding * 2),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Left
        )
        Spacer(modifier = Modifier.height(defaultPadding * 2))
        Button(
            onClick = {
                when (surveyId) {
                    1 -> exFun1(context)
                    2 -> exFun2(notificationHelper, context)
                    3 -> exFun3(context) // notification should be scheduled after the tutorial
                    4 -> exFun4(context)
                }
                onComplete()
            }
        ) {
            Text("Finish")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EndingPagePreview() {
    EndingPageScreen()
}

// Add functions for different survey endings
private fun exFun1(
    context: Context
) {
    // Specific actions for survey 1
    MyPreferenceManager.setBoolean(context, context.getString(R.string.survey1_completed), true)
    SurveyWorker.scheduleNotification(context,)
}

private fun exFun2(
    notificationHelper: NotificationHelper,
    context: Context
) {
    // Specific actions for survey 2
    MyPreferenceManager.setBoolean(context, context.getString(R.string.survey2_completed), true)
    MyPreferenceManager.setBoolean(context, context.getString(R.string.is_timer_enabled), true)
    notificationHelper.showNotification()
    SurveyWorker.scheduleNotification(context)
}

private fun exFun3(context: Context) {
    // Specific actions for survey 3
    MyPreferenceManager.setBoolean(context, context.getString(R.string.survey3_completed), true)
}

private fun exFun4(
    context: Context
) {
    // Specific actions for survey 4
    MyPreferenceManager.setBoolean(context, context.getString(R.string.survey4_completed), true)
    SurveyWorker.cancelScheduledNotification(context)
}