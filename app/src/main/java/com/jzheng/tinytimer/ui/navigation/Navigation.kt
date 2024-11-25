package com.jzheng.tinytimer.ui.navigation

import android.app.Application
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val context = LocalContext.current
    val context2 = LocalContext.current.applicationContext as Application
    val sharedPrefsViewModel: SharedPrefsViewModel = viewModel(
        factory = SharedPrefsViewModelFactory(context2)
    )
    val sharedViewModel: SharedViewModel = viewModel(
        factory = SharedViewModelFactory(context.applicationContext as Application)
    )
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        fadeComposable("home") {
            HomeScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }
        fadeComposable("review") {
            ReviewScreen(
                sharedViewModel = sharedViewModel,
                navController
            )
        }

        // Tutorial pages
        fadeComposable("intro") {
            IntroPage(
                navController
            )
        }
        fadeComposable("message") {
            MessagePage(
                viewModel = sharedViewModel,
                navController
            )
        }
        fadeComposable("animation") {
            AnimationPage(
                viewModel = sharedViewModel,
                navController
            )
        }
        fadeComposable("sound") {
            SoundPage(
                viewModel = sharedViewModel,
                navController
            )
        }
        fadeComposable("vibration") {
            VibrationPage(
                viewModel = sharedViewModel,
                navController
            )
        }
        fadeComposable("login") {
            LoginScreen(
                navController = navController,
                viewModel = sharedViewModel
            )
        }
    }
}

fun NavGraphBuilder.fadeComposable(
    route: String,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    val duration = 0
    composable(
        route = route,
        enterTransition = {
            fadeIn(animationSpec = tween(duration))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(duration))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(duration))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(duration))
        },
        content = content
    )
}