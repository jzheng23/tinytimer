package com.jzheng.tinytimer.data


// First, let's create a data structure to represent a multi-page survey
data class Survey(
    val id: Int,
    val pages: List<SurveyPage>
)

