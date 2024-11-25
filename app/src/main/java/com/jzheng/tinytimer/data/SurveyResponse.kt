package com.jzheng.tinytimer.data

data class SurveyResponse(
    val timestamp: Long,
    val responses: Map<String, java.io.Serializable>  // Can hold both Int and String
)