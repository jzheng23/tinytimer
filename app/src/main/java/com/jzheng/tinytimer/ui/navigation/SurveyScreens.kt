package com.jzheng.tinytimer.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import com.jzheng.tinytimer.data.HostManager
import com.jzheng.tinytimer.data.Survey
import com.jzheng.tinytimer.data.SurveyResponse
import com.jzheng.tinytimer.data.SurveyTemplates.survey1
import com.jzheng.tinytimer.data.SurveyTemplates.survey2
import com.jzheng.tinytimer.data.SurveyTemplates.survey3
import com.jzheng.tinytimer.data.SurveyTemplates.survey4
import com.jzheng.tinytimer.tools.MyPreferenceManager
import com.jzheng.tinytimer.ui.theme.TimerTheme


// Example usage in your app:
@Composable
fun SurveyContainer(
    survey: Survey,
    navController: NavHostController
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val responses = remember {
        mutableStateMapOf<Int, List<String?>>() // Store responses per page
    }
    val context = LocalContext.current
    val userId = MyPreferenceManager.getString(context, "UID", "")
    val uidValid = MyPreferenceManager.getBoolean(context, "UID_valid", false)

    MultiPageSurvey(
        survey = survey,
        responses = responses,
        currentPageIndex = currentPage,
        surveyId = survey.id,
        onPageComplete = { newPageIndex, pageResponses ->
            // Store responses for current page
            responses[currentPage] = pageResponses
            // Update current page
            currentPage = newPageIndex
        },
        onSurveyComplete = { allResponses ->
            // Get all responses in order
            if (uidValid) {
                val responsesToFirebase = allResponses.flatten()
                    .mapIndexed { questionId, answer ->
                        val pageIndex = questionId / survey.pages[0].questions.size
                        questionId.toString() to if (survey.pages[pageIndex].isOpenEnded) {
                            answer as java.io.Serializable  // String is Serializable
                        } else {
                            survey.pages[0].options.indexOf(answer)  // Int is Serializable
                        }
                    }.toMap()

                val surveyResponse = SurveyResponse(
                    timestamp = System.currentTimeMillis(),
                    responses = responsesToFirebase
                )

                val host = HostManager.getHost(context)

                FirebaseDatabase.getInstance().reference
                    .child("responses")
                    .child(userId).child(host)
                    .child(survey.id.toString())
                    .setValue(surveyResponse)
            }

            navController.navigate("home")
        },
    )
}


@Composable
fun SurveyScreen1(
    navController: NavHostController,
) {
    TimerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            SurveyContainer(
                survey = survey1,
                navController = navController
            )
        }
    }
}

@Composable
fun SurveyScreen2(
    navController: NavHostController,
) {
    TimerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            SurveyContainer(
                survey = survey2,
                navController = navController
            )
        }
    }
}

@Composable
fun SurveyScreen3(
    navController: NavHostController,
) {
    TimerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            SurveyContainer(
                survey = survey3,
                navController = navController
            )
        }
    }
}

@Composable
fun SurveyScreen4(
    navController: NavHostController,
) {
    TimerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            SurveyContainer(
                survey = survey4,
                navController = navController
            )
        }
    }
}



