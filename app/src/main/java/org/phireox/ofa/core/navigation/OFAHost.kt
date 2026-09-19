package org.phireox.ofa.core.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.phireox.ofa.feature.about.AboutScreen
import org.phireox.ofa.feature.categories.CategoriesScreen
import org.phireox.ofa.feature.categories.CategoryDetailScreen
import org.phireox.ofa.feature.diagnostics.DiagnosticsScreen
import org.phireox.ofa.feature.home.HomeScreen
import org.phireox.ofa.feature.legal.LegalScreen
import org.phireox.ofa.feature.notes.NoteEditorScreen
import org.phireox.ofa.feature.emergency.EmergencyScreen
import org.phireox.ofa.feature.notes.NotesScreen
import org.phireox.ofa.feature.privacy.PrivacyDashboardScreen
import org.phireox.ofa.feature.search.SearchScreen
import org.phireox.ofa.feature.settings.SettingsScreen
import org.phireox.ofa.feature.subscription.SubscriptionScreen
import org.phireox.ofa.feature.tool.ToolDetailScreen
import org.phireox.ofa.feature.vault.VaultScreen
import org.phireox.ofa.feature.compass.CompassScreen
import org.phireox.ofa.feature.maps.OfflineMapsScreen
import org.phireox.ofa.feature.backup.BackupRestoreScreen
import org.phireox.ofa.feature.weather.WeatherScreen
import org.phireox.ofa.feature.messaging.MessagingScreen
import org.phireox.ofa.feature.ai.AiAssistantScreen
import org.phireox.ofa.feature.settings.ProvidersScreen

@Composable
fun OFANavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Destination.Home.route,
        modifier = modifier,
        enterTransition = { slideInHorizontally { it } + fadeIn() },
        exitTransition = { slideOutHorizontally { -it / 2 } + fadeOut() },
        popEnterTransition = { slideInHorizontally { -it } + fadeIn() },
        popExitTransition = { slideOutHorizontally { it } + fadeOut() }
    ) {
        composable(Destination.Home.route) {
            HomeScreen(
                onToolClick = { id -> navController.navigate(Destination.Tool.createRoute(id)) },
                onCategoryClick = { key -> navController.navigate(Destination.Category.createRoute(key)) },
                onSearchClick = { navController.navigate(Destination.Search.route) },
                onSettingsClick = { navController.navigate(Destination.Settings.route) },
                onNotesClick = { navController.navigate(Destination.Notes.route) },
                onEmergencyClick = { navController.navigate(Destination.Emergency.route) },
                onCompassClick = { navController.navigate(Destination.Compass.route) },
                onOfflineMapsClick = { navController.navigate(Destination.OfflineMaps.route) },
                onWeatherClick = { navController.navigate(Destination.Weather.route) },
                onMessagingClick = { navController.navigate(Destination.Messaging.route) },
                onAiAssistantClick = { navController.navigate(Destination.AiAssistant.route) }
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
                onSubscriptionClick = { navController.navigate(Destination.Subscription.route) },
                onLegalClick = { navController.navigate(Destination.Legal.route) },
                onDiagnosticsClick = { navController.navigate(Destination.Diagnostics.route) },
                onVaultClick = { navController.navigate(Destination.Vault.route) },
                onBackupRestoreClick = { navController.navigate(Destination.BackupRestore.route) },
                onProvidersClick = { navController.navigate(Destination.Providers.route) }
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
        composable(Destination.Legal.route) {
            LegalScreen { navController.popBackStack() }
        }
        composable(Destination.Diagnostics.route) {
            DiagnosticsScreen { navController.popBackStack() }
        }
        composable(Destination.Vault.route) {
            VaultScreen { navController.popBackStack() }
        }
        composable(Destination.Notes.route) {
            NotesScreen(
                onBack = { navController.popBackStack() },
                onNoteClick = { noteId -> navController.navigate(Destination.NoteEditor.createRoute(noteId)) }
            )
        }
        composable(Destination.NoteEditor.route) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            NoteEditorScreen(noteId = noteId) { navController.popBackStack() }
        }
        composable(Destination.Emergency.route) {
            EmergencyScreen { navController.popBackStack() }
        }
        composable(Destination.Compass.route) {
            CompassScreen { navController.popBackStack() }
        }
        composable(Destination.OfflineMaps.route) {
            OfflineMapsScreen { navController.popBackStack() }
        }
        composable(Destination.BackupRestore.route) {
            BackupRestoreScreen { navController.popBackStack() }
        }
        composable(Destination.Weather.route) {
            WeatherScreen { navController.popBackStack() }
        }
        composable(Destination.Messaging.route) {
            MessagingScreen { navController.popBackStack() }
        }
        composable(Destination.AiAssistant.route) {
            AiAssistantScreen { navController.popBackStack() }
        }
        composable(Destination.Providers.route) {
            ProvidersScreen { navController.popBackStack() }
        }
    }
}
