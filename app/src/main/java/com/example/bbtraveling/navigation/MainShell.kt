package com.example.bbtraveling.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Collections
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bbtraveling.ui.screens.AboutScreen
import com.example.bbtraveling.ui.screens.GalleryScreen
import com.example.bbtraveling.ui.screens.HomeScreen
import com.example.bbtraveling.ui.screens.PreferencesScreen
import com.example.bbtraveling.ui.screens.SettingsScreen
import com.example.bbtraveling.ui.screens.TripsScreen

private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun MainShell(rootNavController: NavHostController) {
    val mainNavController = rememberNavController()

    val items = listOf(
        BottomItem(Routes.Home, "Home", Icons.Rounded.Home),
        BottomItem(Routes.Trips, "Trips", Icons.Rounded.List),
        BottomItem(Routes.Gallery, "Gallery", Icons.Rounded.Collections),
        BottomItem(Routes.Settings, "Settings", Icons.Rounded.Settings)
    )

    val backStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            val scheme = MaterialTheme.colorScheme
            NavigationBar(
                containerColor = scheme.surface,
                contentColor = scheme.onSurface
            ) {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination
                            ?.hierarchy
                            ?.any { destination -> destination.route == item.route } == true,
                        onClick = {
                            mainNavController.navigateToTopLevel(item.route)
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = scheme.onPrimaryContainer,
                            selectedTextColor = scheme.onPrimaryContainer,
                            indicatorColor = scheme.primaryContainer,
                            unselectedIconColor = scheme.onSurfaceVariant,
                            unselectedTextColor = scheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = mainNavController,
            startDestination = Routes.Home,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.Home) {
                HomeScreen(
                    onTripClick = { tripId -> rootNavController.navigate(Routes.tripDetail(tripId)) },
                    onOpenTrips = { mainNavController.navigateToTopLevel(Routes.Trips) }
                )
            }
            composable(Routes.Trips) {
                TripsScreen(
                    onTripClick = { tripId -> rootNavController.navigate(Routes.tripDetail(tripId)) }
                )
            }
            composable(Routes.Gallery) {
                GalleryScreen(tripId = null, onBack = null)
            }
            composable(Routes.Settings) {
                SettingsScreen(
                    onOpenPreferences = { mainNavController.navigateSingleTop(Routes.Preferences) },
                    onOpenAbout = { mainNavController.navigateSingleTop(Routes.About) },
                    onOpenTerms = { rootNavController.navigate(Routes.Terms) }
                )
            }
            composable(Routes.Preferences) {
                PreferencesScreen(onBack = { mainNavController.popBackStack() })
            }
            composable(Routes.About) {
                AboutScreen(
                    onBack = { mainNavController.popBackStack() },
                    onOpenTerms = { rootNavController.navigate(Routes.Terms) }
                )
            }
        }
    }
}

private fun NavHostController.navigateToTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}
