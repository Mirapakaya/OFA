package org.phireox.ofa.data.local

import android.content.Context
import android.content.SharedPreferences
import android.os.Build

class AiProviderConfig(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getProvider(): String = prefs.getString(KEY_PROVIDER, "openai") ?: "openai"
    fun getApiKey(): String = prefs.getString(KEY_API_KEY, "") ?: ""
    fun getBaseUrl(): String = prefs.getString(KEY_BASE_URL, "") ?: ""

    fun save(provider: String, apiKey: String, baseUrl: String) {
        prefs.edit().apply {
            putString(KEY_PROVIDER, provider)
            putString(KEY_API_KEY, apiKey)
            putString(KEY_BASE_URL, baseUrl)
            apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "ofa_ai_provider"
        private const val KEY_PROVIDER = "provider"
        private const val KEY_API_KEY = "api_key"
        private const val KEY_BASE_URL = "base_url"
    }
}
