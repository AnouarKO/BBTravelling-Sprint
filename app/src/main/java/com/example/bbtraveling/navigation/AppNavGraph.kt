package com.example.bbtraveling.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bbtraveling.ui.screens.AuthScreen
import com.example.bbtraveling.ui.screens.GalleryScreen
import com.example.bbtraveling.ui.screens.SplashScreen
import com.example.bbtraveling.ui.screens.TermsScreen
import com.example.bbtraveling.ui.screens.TripDetailScreen
import com.example.bbtraveling.ui.viewmodel.AuthViewModel
import com.example.bbtraveling.ui.viewmodel.SettingsViewModel
import com.example.bbtraveling.ui.viewmodel.TripsViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    tripsViewModel: TripsViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash
    ) {
        composable(Routes.Splash) {
            SplashScreen(
                onFinished = {
                    val nextRoute = resolveStartRoute(settingsViewModel, authViewModel)
                    navController.navigate(nextRoute) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Auth) {
            AuthScreen(
                authViewModel = authViewModel,
                settingsViewModel = settingsViewModel,
                onAuthenticated = {
                    navController.navigate(Routes.Main) {
                        popUpTo(Routes.Auth) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.Main) {
            MainShell(
                rootNavController = navController,
                tripsViewModel = tripsViewModel,
                settingsViewModel = settingsViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.Auth) {
                        popUpTo(Routes.Main) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Routes.TripDetail,
            arguments = listOf(navArgument(Routes.ARG_TRIP_ID) { type = NavType.StringType })
        ) { entry ->
            val tripId = entry.arguments?.getString(Routes.ARG_TRIP_ID).orEmpty()
            TripDetailScreen(
                tripId = tripId,
                tripsViewModel = tripsViewModel,
                onBack = { navController.popBackStack() },
                onOpenGallery = { navController.navigate(Routes.galleryTrip(tripId)) }
            )
        }

        composable(
            route = Routes.GalleryTrip,
            arguments = listOf(navArgument(Routes.ARG_TRIP_ID) { type = NavType.StringType })
        ) { entry ->
            val tripId = entry.arguments?.getString(Routes.ARG_TRIP_ID)
            val trips by tripsViewModel.trips.collectAsState()
            GalleryScreen(
                tripId = tripId,
                trips = trips,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Terms) {
            TermsScreen(
                onAccept = { navController.popBackStack() },
                onReject = { navController.popBackStack() }
            )
        }

        composable(Routes.TermsOnboarding) {
            TermsScreen(
                onAccept = {
                    settingsViewModel.acceptTerms()
                    val nextRoute = if (authViewModel.isAuthenticated()) Routes.Main else Routes.Auth
                    navController.navigate(nextRoute) {
                        popUpTo(Routes.TermsOnboarding) { inclusive = true }
                    }
                },
                onReject = {
                    val nextRoute = if (authViewModel.isAuthenticated()) Routes.Main else Routes.Auth
                    navController.navigate(nextRoute) {
                        popUpTo(Routes.TermsOnboarding) { inclusive = true }
                    }
                }
            )
        }
    }
}

private fun resolveStartRoute(
    settingsViewModel: SettingsViewModel,
    authViewModel: AuthViewModel
): String {
    return when {
        !settingsViewModel.hasAcceptedTerms() -> Routes.TermsOnboarding
        authViewModel.isAuthenticated() -> Routes.Main
        else -> Routes.Auth
    }
}
