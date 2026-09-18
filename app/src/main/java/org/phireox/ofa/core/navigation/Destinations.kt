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
    data object Tool : Destination("tool/{toolId}") {
        fun createRoute(toolId: String) = "tool/$toolId"
    }
    data object Category : Destination("category/{categoryId}") {
        fun createRoute(categoryId: String) = "category/$categoryId"
    }
}
