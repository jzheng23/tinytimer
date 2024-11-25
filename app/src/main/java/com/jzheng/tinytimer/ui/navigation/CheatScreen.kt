package com.jzheng.tinytimer.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.jzheng.tinytimer.ui.theme.TimerTheme

@Composable
fun CheatScreen(
    viewModel: SharedPrefsViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val prefs = context.getSharedPreferences("MySharedPrefs", Context.MODE_PRIVATE)

    val survey1 by viewModel.survey1Completed.observeAsState(false)
    val survey2 by viewModel.survey2Completed.observeAsState(false)
    val survey3 by viewModel.survey3Completed.observeAsState(false)
    val survey4 by viewModel.survey4Completed.observeAsState(false)
    val tutorial by viewModel.tutorialCompleted.observeAsState(false)
    val dayCount by viewModel.dayCount.observeAsState(1)


    TimerTheme {
        Scaffold { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {

                    listOf(
                        "Survey 1 completed" to "survey1_completed" to survey1,
                        "Survey 2 completed" to "survey2_completed" to survey2,
                        "Survey 3 completed" to "survey3_completed" to survey3,
                        "Tutorial completed" to "tutorial_completed" to tutorial,
                        "Survey 4 completed" to "survey4_completed" to survey4,
                    ).forEach { (pair, checked) ->
                        val (label, key) = pair
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label)
                            Switch(
                                checked = checked,
                                onCheckedChange = {
                                    prefs.edit().putBoolean(key, it).apply()
                                }
                            )
                        }
                    }

                    var dayCountText by remember { mutableStateOf(dayCount.toString()) }
                    OutlinedTextField(
                        value = dayCountText,
                        onValueChange = { newValue ->
                            dayCountText = newValue
                            newValue.toIntOrNull()?.let { count ->
                                prefs.edit().putInt("day_count", count).apply()
                            }
                        },
                        label = { Text("Day Count") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { keyboardController?.hide() }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}