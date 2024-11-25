package com.jzheng.tinytimer.data


data class SurveyQuestion(
    val text: String,
    val options: List<String> = SurveyTemplates.agreementOptions,
    var isExpanded: Boolean = false,
    var selectedOption: String? = null,
    val isOpenEnded: Boolean = false,  // Add this
    var textInput: String = ""
)
