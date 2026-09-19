package org.phireox.ofa.data.local

import android.content.Context
import android.content.SharedPreferences

class ProviderConfig(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun get(key: String): String = prefs.getString(key, "") ?: ""
    fun set(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    companion object {
        private const val PREFS_NAME = "ofa_provider_config"
        const val KEY_AI_BASE_URL = "ai_base_url"
        const val KEY_WEATHER_PROVIDER = "weather_provider"
        const val KEY_TEMP_MAIL_PROVIDER = "temp_mail_provider"
        const val KEY_TEMP_PHONE_PROVIDER = "temp_phone_provider"
        const val KEY_MEETING_PROVIDER = "meeting_provider"
    }
}
