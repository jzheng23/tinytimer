package com.jzheng.tinytimer.data

import com.jzheng.tinytimer.data.Constants.IS_TESTING

object SurveyTemplates {
    val agreementOptions = listOf(
        "Strongly disagree",
        "Somewhat disagree",
        "Neither agree nor disagree",
        "Somewhat agree",
        "Strongly agree"
    )
    private val autonomySurvey = SurveyPage(
        questions = listOf(
            "I was free to decide how I wanted to control my phone usage.",
            "I could control my phone usage in my own way.",
            "I only control my phone usage because I have to.",
            "I control my phone usage because other people want me to.",
            "I will feel guilty if I don't control my phone usage."
        )
    )

    private val competenceSurvey = SurveyPage(
        questions = listOf(
            "My phone control abilities have improved in the last week.",
            "I am good at controlling my phone usage.",
            "I feel confident in my ability to control my phone usage.",
            "I find controlling my phone usage too challenging.",
            "I find it too difficult to control my phone usage regularly."
        )
    )

    private val motivationSurvey1 = SurveyPage(
        questions = listOf(
            "Other people will be upset if I don't control my phone usage.",
            "I control my phone usage because others will not be pleased with me if I don't.",
            "I feel under pressure from others to control my phone usage.",
            "Please select Strongly disagree.",
            "I would feel bad about myself if I quit trying to control my phone usage."
        )
    )

    private val motivationSurvey2 = SurveyPage(
        questions = listOf(
            "I would feel guilty if I stopped controlling my phone usage.",
            "I would feel like a failure if I gave up on controlling my phone usage.",
            "Controlling my phone usage is a sensible thing to do.",
            "The benefits of controlling my phone usage are important to me."
        )
    )

    private val motivationSurvey3 = SurveyPage(
        questions = listOf(
            "Controlling my phone usage is a good way to achieve what I need right now.",
            "I control my phone usage because it reflects the essence of who I am.",
            "Controlling my phone usage is consistent with my deepest principles.",
            "I control my phone usage because it expresses my values."
        )
    )

    private val ambientDisplayQuestions = listOf(
        "The app is useful for controlling my time spent on my phone.",
        "The app is easily perceivable.",
        "The app is easily noticeable during use.",
        "The app distracts me from my main task.",
        "The app is visually appealing."
    )

    private val ambientDisplaySurveyWeek2 = SurveyPage(
        title = "Please rate the following aspects of app's notification icon (i.e., the number in the status bar).",
        questions = ambientDisplayQuestions
    )

    private val ambientDisplaySurveyWeek3 = SurveyPage(
        title = "Please rate the following aspects of app's notification icon (i.e., the number in the status bar) and those attention signals (e.g., animation, message, sound, and vibration).",
        questions = ambientDisplayQuestions
    )

    private val feedbackSurvey = SurveyPage(
        questions = listOf(
            "What do you like about the app?",
            "What don't you like about the app?"
        ),
        options = emptyList(),
        isOpenEnded = true  // This will now be properly used
    )

    val endingMessages = mapOf(
        1 to "Thank you for completing Survey 1!",
        2 to "Thank you for completing Survey 2! In the next week, you will see the a number in the Timer's notification icon. That number is current session duration in minute, ranging from 0 to 99.",
        3 to "Thank you for completing Survey 3! Please return to the home page and complete the tutorial.",
        4 to "Congratulations! You have completed all surveys."
    )

    // Create the four surveys
    val survey1 = Survey(
        id = 1,
        pages = if (!IS_TESTING) listOf(
                autonomySurvey,
                competenceSurvey,
                motivationSurvey1,
                motivationSurvey2,
                motivationSurvey3,
                SurveyPage(questions = emptyList()) // Ending page
            ) else listOf(
            autonomySurvey,
            SurveyPage(questions = emptyList()) // Ending page
        )
    )

    val survey2 = Survey(
        id = 2,
        pages = if (!IS_TESTING) listOf(
            autonomySurvey,
            competenceSurvey,
            motivationSurvey1,
            motivationSurvey2,
            motivationSurvey3,
            SurveyPage(questions = emptyList()) // Ending page
        ) else listOf(
            autonomySurvey,
            SurveyPage(questions = emptyList())
        )
    )

    val survey3 = Survey(
        id = 3,
        pages = if (!IS_TESTING) listOf(
            autonomySurvey,
            competenceSurvey,
            motivationSurvey1,
            motivationSurvey2,
            motivationSurvey3,
            ambientDisplaySurveyWeek2,
            SurveyPage(questions = emptyList()) // Ending page
        ) else listOf(
            autonomySurvey,
            SurveyPage(questions = emptyList())
        )
    )

    val survey4 = Survey(
        id = 4,
        pages = if (!IS_TESTING) listOf(
            autonomySurvey,
            competenceSurvey,
            motivationSurvey1,
            motivationSurvey2,
            motivationSurvey3,
            ambientDisplaySurveyWeek3,
            feedbackSurvey,
            SurveyPage(questions = emptyList())
        ) else listOf(
            autonomySurvey,
            SurveyPage(questions = emptyList())
        )
    )
}