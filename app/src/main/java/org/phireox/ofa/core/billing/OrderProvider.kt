package org.phireox.ofa.core.billing

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

interface OrderProvider {
    suspend fun createOrder(amountPaise: Long, currency: String): Result<String>
}

class ExternalOrderProvider(private val endpoint: String) : OrderProvider {
    override suspend fun createOrder(amountPaise: Long, currency: String): Result<String> {
        if (endpoint.isBlank()) return Result.failure(IllegalStateException("Order provider URL not configured"))
        return try {
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true
            val body = JSONObject().apply {
                put("amount", amountPaise)
                put("currency", currency)
                put("receipt", "ofa_${System.currentTimeMillis()}")
            }.toString()
            conn.outputStream.use { it.write(body.toByteArray()) }
            val response = conn.inputStream.bufferedReader().use { it.readText() }
            val orderId = JSONObject(response).getString("id")
            Result.success(orderId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
