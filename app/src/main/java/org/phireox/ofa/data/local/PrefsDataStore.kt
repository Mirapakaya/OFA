package org.phireox.ofa.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ofa_preferences")

class PrefsDataStore(context: Context) {

    private val dataStore = context.dataStore

    val themeMode: Flow<ThemeMode> = dataStore.data.map { prefs ->
        ThemeMode.valueOf(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.name)
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    val dynamicColor: Flow<Boolean> = dataStore.data.map { it[Keys.DYNAMIC_COLOR] != false }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { it[Keys.DYNAMIC_COLOR] = enabled }
    }

    val favorites: Flow<Set<String>> = dataStore.data.map { prefs ->
        prefs[Keys.FAVORITES]?.toSet() ?: emptySet()
    }

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

    suspend fun clearRecents() {
        dataStore.edit { it.remove(Keys.RECENT_TOOLS) }
    }

    suspend fun clearFavorites() {
        dataStore.edit { it.remove(Keys.FAVORITES) }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        val FAVORITES = stringPreferencesKey("favorites")
        val RECENT_TOOLS = stringPreferencesKey("recent_tools")
    }
}

enum class ThemeMode { SYSTEM, LIGHT, DARK }
