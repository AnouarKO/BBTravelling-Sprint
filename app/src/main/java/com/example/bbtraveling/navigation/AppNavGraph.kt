package com.example.bbtraveling.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.bbtraveling.ui.screens.GalleryScreen
import com.example.bbtraveling.ui.screens.SplashScreen
import com.example.bbtraveling.ui.screens.TermsScreen
import com.example.bbtraveling.ui.screens.TripDetailScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash
    ) {
        composable(Routes.Splash) {
            SplashScreen(
                onFinished = {
                    navController.navigate(Routes.Main) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Main) {
            MainShell(rootNavController = navController)
        }

        composable(
            route = Routes.TripDetail,
            arguments = listOf(navArgument(Routes.ARG_TRIP_ID) { type = NavType.StringType })
        ) { entry ->
            val tripId = entry.arguments?.getString(Routes.ARG_TRIP_ID).orEmpty()
            TripDetailScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                onOpenGallery = { navController.navigate(Routes.galleryTrip(tripId)) }
            )
        }

        composable(
            route = Routes.GalleryTrip,
            arguments = listOf(navArgument(Routes.ARG_TRIP_ID) { type = NavType.StringType })
        ) { entry ->
            val tripId = entry.arguments?.getString(Routes.ARG_TRIP_ID)
            GalleryScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Terms) {
            TermsScreen(
                onAccept = { navController.popBackStack() },
                onReject = { navController.popBackStack() }
            )
        }
    }
}
