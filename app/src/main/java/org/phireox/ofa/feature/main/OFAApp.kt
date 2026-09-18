package org.phireox.ofa.feature.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.phireox.ofa.core.navigation.Destination
import org.phireox.ofa.core.navigation.OFANavHost

@Composable
fun OFAApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomRoutes = listOf(Destination.Home.route, Destination.Search.route, Destination.Categories.route, Destination.Settings.route)
    val showBottomBar = bottomRoutes.contains(currentDestination?.route)

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = currentDestination?.hierarchy?.any { it.route == Destination.Home.route } == true,
                        onClick = { navController.navigate(Destination.Home.route) { popUpTo(navController.graph.findStartDestination().id) { saveState = true } launchSingleTop = true restoreState = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("Search") },
                        selected = currentDestination?.route == Destination.Search.route,
                        onClick = { navController.navigate(Destination.Search.route) { launchSingleTop = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
                        label = { Text("Categories") },
                        selected = currentDestination?.route == Destination.Categories.route,
                        onClick = { navController.navigate(Destination.Categories.route) { launchSingleTop = true } }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        selected = currentDestination?.route == Destination.Settings.route,
                        onClick = { navController.navigate(Destination.Settings.route) { launchSingleTop = true } }
                    )
                }
            }
        }
    ) { padding ->
        OFANavHost(navController = navController)
    }
}
