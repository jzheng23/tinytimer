package com.jzheng.tinytimer.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.jzheng.tinytimer.data.Constants.defaultPadding
import com.jzheng.tinytimer.ui.theme.TimerTheme

@Composable
fun FinishScreen() {
    TimerTheme {
        Scaffold { paddingValues ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier.padding(defaultPadding)

                ) {
                    Text(
                        text = "Congratulations!",
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(defaultPadding))
                    Text(
                        text = "You've completed the study! Please don't uninstall the app until you receive the payment.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Left
                    )
                }

            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun FinishScreenPreview() {
    FinishScreen()
}