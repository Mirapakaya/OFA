package org.phireox.ofa.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ofa_preferences")

class PrefsDataStore(context: Context) {

    private val appContext = context.applicationContext
    private val dataStore = appContext.dataStore

    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    val dynamicColor: Flow<Boolean> = dataStore.data.map { it[Keys.DYNAMIC_COLOR] == true }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    val favorites: Flow<Set<String>> = dataStore.data.map { it[Keys.FAVORITES] ?: emptySet() }

    suspend fun toggleFavorite(toolId: String) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.FAVORITES]?.toMutableSet() ?: mutableSetOf()
            if (current.contains(toolId)) current.remove(toolId) else current.add(toolId)
            prefs[Keys.FAVORITES] = current
        }
    }

    val recentTools: Flow<List<String>> = dataStore.data.map { prefs ->
        (prefs[Keys.RECENT_TOOLS]?.split(",") ?: emptyList()).filter { it.isNotBlank() }
    }

    suspend fun addRecentTool(toolId: String) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.RECENT_TOOLS]?.split(",")?.toMutableList() ?: mutableListOf()
            current.remove(toolId)
            current.add(0, toolId)
            prefs[Keys.RECENT_TOOLS] = current.take(50).joinToString(",")
        }
    }

    val isPremium: Flow<Boolean> = dataStore.data.map { it[Keys.IS_PREMIUM] == true }

    val onboardingComplete: Flow<Boolean> = dataStore.data.map { it[Keys.ONBOARDING_COMPLETE] == true }

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    }

    suspend fun setPremium(premium: Boolean) {
        dataStore.edit { it[Keys.IS_PREMIUM] = premium }
    }

    suspend fun clearRecents() {
        dataStore.edit { it.remove(Keys.RECENT_TOOLS) }
    }

    suspend fun clearFavorites() {
        dataStore.edit { it.remove(Keys.FAVORITES) }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
        appContext.cacheDir?.deleteRecursivelyOrIgnore()
        appContext.filesDir?.deleteRecursivelyOrIgnore()
    }

    suspend fun export(): Map<String, Any> = dataStore.data.first().let { prefs ->
        mapOf(
            Keys.THEME_MODE.name to (prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name),
            Keys.DYNAMIC_COLOR.name to (prefs[Keys.DYNAMIC_COLOR] == true),
            Keys.FAVORITES.name to (prefs[Keys.FAVORITES] ?: emptySet<String>()),
            Keys.RECENT_TOOLS.name to (prefs[Keys.RECENT_TOOLS] ?: ""),
            Keys.IS_PREMIUM.name to (prefs[Keys.IS_PREMIUM] == true),
            Keys.ONBOARDING_COMPLETE.name to (prefs[Keys.ONBOARDING_COMPLETE] == true)
        )
    }

    suspend fun import(map: Map<String, Any>) {
        dataStore.edit { prefs ->
            (map[Keys.THEME_MODE.name] as? String)?.let { prefs[Keys.THEME_MODE] = it }
            (map[Keys.DYNAMIC_COLOR.name] as? Boolean)?.let { prefs[Keys.DYNAMIC_COLOR] = it }
            (map[Keys.FAVORITES.name] as? Set<*>)?.filterIsInstance<String>()?.toSet()?.let { prefs[Keys.FAVORITES] = it }
            (map[Keys.RECENT_TOOLS.name] as? String)?.let { prefs[Keys.RECENT_TOOLS] = it }
            (map[Keys.IS_PREMIUM.name] as? Boolean)?.let { prefs[Keys.IS_PREMIUM] = it }
            (map[Keys.ONBOARDING_COMPLETE.name] as? Boolean)?.let { prefs[Keys.ONBOARDING_COMPLETE] = it }
        }
    }

    private fun java.io.File.deleteRecursivelyOrIgnore() {
        runCatching { listFiles()?.forEach { it.deleteRecursivelyOrIgnore() } }
        runCatching { delete() }
    }

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val FAVORITES = stringSetPreferencesKey("favorites")
        val RECENT_TOOLS = stringPreferencesKey("recent_tools")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    }
}

enum class ThemeMode { SYSTEM, LIGHT, DARK }
