package org.phireox.ofa.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.phireox.ofa.feature.about.AboutScreen
import org.phireox.ofa.feature.categories.CategoriesScreen
import org.phireox.ofa.feature.categories.CategoryDetailScreen
import org.phireox.ofa.feature.home.HomeScreen
import org.phireox.ofa.feature.privacy.PrivacyDashboardScreen
import org.phireox.ofa.feature.search.SearchScreen
import org.phireox.ofa.feature.settings.SettingsScreen
import org.phireox.ofa.feature.subscription.SubscriptionScreen
import org.phireox.ofa.feature.tool.ToolDetailScreen

@Composable
fun OFANavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = Destination.Home.route, modifier = modifier) {
        composable(Destination.Home.route) {
            HomeScreen(
                onToolClick = { id -> navController.navigate(Destination.Tool.createRoute(id)) },
                onSearchClick = { navController.navigate(Destination.Search.route) },
                onSettingsClick = { navController.navigate(Destination.Settings.route) }
            )
        }
        composable(Destination.Search.route) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onToolClick = { id -> navController.navigate(Destination.Tool.createRoute(id)) }
            )
        }
        composable(Destination.Categories.route) {
            CategoriesScreen(
                onBack = { navController.popBackStack() },
                onCategoryClick = { key -> navController.navigate(Destination.Category.createRoute(key)) }
            )
        }
        composable(Destination.Category.route) { backStackEntry ->
            val key = backStackEntry.arguments?.getString("categoryId") ?: return@composable
            CategoryDetailScreen(
                categoryKey = key,
                onBack = { navController.popBackStack() },
                onToolClick = { id -> navController.navigate(Destination.Tool.createRoute(id)) }
            )
        }
        composable(Destination.Tool.route) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("toolId") ?: return@composable
            ToolDetailScreen(toolId = id) { navController.popBackStack() }
        }
        composable(Destination.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onSubscriptionClick = { navController.navigate(Destination.Subscription.route) }
            )
        }
        composable(Destination.Subscription.route) {
            SubscriptionScreen { navController.popBackStack() }
        }
        composable(Destination.About.route) {
            AboutScreen { navController.popBackStack() }
        }
        composable(Destination.Privacy.route) {
            PrivacyDashboardScreen { navController.popBackStack() }
        }
    }
}
