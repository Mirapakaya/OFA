package org.phireox.ofa.feature.ai

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object AiChatClient {

    suspend fun chat(provider: String, apiKey: String, baseUrl: String, history: List<Pair<String, String>>): String {
        return try {
            val request = buildRequest(provider, apiKey, baseUrl, history)
            val response = post(request)
            extractText(provider, response)
        } catch (e: Exception) {
            "Error: ${e.localizedMessage ?: "Request failed"}"
        }
    }

    private fun buildRequest(provider: String, apiKey: String, baseUrl: String, history: List<Pair<String, String>>): Request {
        val messages = JSONArray()
        history.forEach { (role, content) ->
            messages.put(JSONObject().put("role", if (role == "user") "user" else "assistant").put("content", content))
        }
        return when (provider) {
            "openai" -> Request(
                url = "https://api.openai.com/v1/chat/completions",
                headers = mapOf("Authorization" to "Bearer $apiKey"),
                body = JSONObject().put("model", "gpt-4o-mini").put("messages", messages).toString()
            )
            "anthropic" -> Request(
                url = "https://api.anthropic.com/v1/messages",
                headers = mapOf("x-api-key" to apiKey, "anthropic-version" to "2023-06-01"),
                body = JSONObject().put("model", "claude-3-haiku-20240307").put("max_tokens", 1024).put("messages", messages).toString()
            )
            "gemini" -> {
                val parts = JSONArray()
                history.forEach { (_, content) -> parts.put(JSONObject().put("text", content)) }
                Request(
                    url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey",
                    headers = emptyMap(),
                    body = JSONObject().put("contents", JSONArray().put(JSONObject().put("parts", parts))).toString()
                )
            }
            "custom" -> Request(
                url = baseUrl.trimEnd('/') + "/v1/chat/completions",
                headers = mapOf("Authorization" to "Bearer $apiKey"),
                body = JSONObject().put("messages", messages).toString()
            )
            else -> throw IllegalArgumentException("Unknown provider")
        }
    }

    private fun extractText(provider: String, response: String): String {
        return try {
            val json = JSONObject(response)
            when (provider) {
                "gemini" -> {
                    val candidates = json.getJSONArray("candidates")
                    val parts = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts")
                    (0 until parts.length()).joinToString("\n") { parts.getJSONObject(it).getString("text") }
                }
                else -> {
                    json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                }
            }
        } catch (e: Exception) {
            "Could not parse response: $response"
        }
    }

    private fun post(request: Request): String {
        val url = URL(request.url)
        return (url.openConnection() as HttpURLConnection).use { conn ->
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            request.headers.forEach { (k, v) -> conn.setRequestProperty(k, v) }
            conn.doOutput = true
            conn.outputStream.use { it.write(request.body.toByteArray()) }
            conn.inputStream.bufferedReader().use { it.readText() }
        }
    }

    private data class Request(val url: String, val headers: Map<String, String>, val body: String)
}
