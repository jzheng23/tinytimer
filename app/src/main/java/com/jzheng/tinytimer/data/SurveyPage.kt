package com.jzheng.tinytimer.data

// Define a data class for a survey page
data class SurveyPage(
    val title: String = "Reflect on your experience with controlling your phone usage in the last week, and rate your agreement with the following statements.",
    val questions: List<String>,
    val options: List<String> = SurveyTemplates.agreementOptions,
    val isOpenEnded: Boolean = false
)