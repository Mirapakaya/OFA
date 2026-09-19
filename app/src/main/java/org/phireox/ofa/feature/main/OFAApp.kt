package org.phireox.ofa.feature.main

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
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
    val bottomRoutes = setOf(Destination.Home.route, Destination.Search.route, Destination.Categories.route, Destination.Notes.route, Destination.Settings.route)
    val showBottomBar = currentDestination?.route in bottomRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FloatingBottomBar(
                    items = listOf(
                        BarItem("Home", Icons.Default.Home, Destination.Home.route),
                        BarItem("Search", Icons.Default.Search, Destination.Search.route),
                        BarItem("Categories", Icons.Default.Category, Destination.Categories.route),
                        BarItem("Notes", Icons.Default.Description, Destination.Notes.route),
                        BarItem("Settings", Icons.Default.Settings, Destination.Settings.route)
                    ),
                    currentRoute = currentDestination?.route,
                    onItemClick = { route ->
                        if (currentDestination?.route == route) return@FloatingBottomBar
                        if (route == Destination.Home.route) {
                            navController.navigate(Destination.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } else {
                            navController.navigate(route) { launchSingleTop = true }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        OFANavHost(navController = navController, modifier = Modifier.padding(paddingValues))
    }
}

private data class BarItem(val label: String, val icon: ImageVector, val route: String)

@Composable
private fun FloatingBottomBar(items: List<BarItem>, currentRoute: String?, onItemClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 4.dp,
            shadowElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                items.forEach { item ->
                    val selected = item.route == currentRoute
                    val iconColor by animateColorAsState(
                        targetValue = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        label = "iconColor"
                    )
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label, tint = iconColor) },
                        label = { Text(item.label) },
                        selected = selected,
                        onClick = { onItemClick(item.route) }
                    )
                }
            }
        }
    }
}
