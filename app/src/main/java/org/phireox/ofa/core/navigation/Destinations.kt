package org.phireox.ofa.core.navigation

import org.phireox.ofa.data.model.Tool

sealed class Destination(val route: String) {
    data object Home : Destination("home")
    data object Search : Destination("search")
    data object Categories : Destination("categories")
    data object Favorites : Destination("favorites")
    data object Recent : Destination("recent")
    data object Settings : Destination("settings")
    data object About : Destination("about")
    data object Privacy : Destination("privacy")
    data object Subscription : Destination("subscription")
    data object Legal : Destination("legal")
    data object Diagnostics : Destination("diagnostics")
    data object Vault : Destination("vault")
    data object Notes : Destination("notes")
    data object NoteEditor : Destination("note/{noteId}") {
        fun createRoute(noteId: String) = "note/$noteId"
    }
    data object Emergency : Destination("emergency")
    data object Compass : Destination("compass")
    data object OfflineMaps : Destination("offline_maps")
    data object BackupRestore : Destination("backup_restore")
    data object Weather : Destination("weather")
    data object Messaging : Destination("messaging")
    data object Tool : Destination("tool/{toolId}") {
        fun createRoute(toolId: String) = "tool/$toolId"
    }
    data object Category : Destination("category/{categoryId}") {
        fun createRoute(categoryId: String) = "category/$categoryId"
    }
}
