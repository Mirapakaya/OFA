package org.phireox.ofa.core.billing

import android.app.Activity
import com.razorpay.Checkout
import org.json.JSONObject

class RazorpayBillingManager(private val key: String) {

    fun startCheckout(activity: Activity, orderId: String, amountPaise: Long, currency: String, description: String) {
        val checkout = Checkout()
        checkout.setKeyID(key)
        val options = JSONObject().apply {
            put("name", "OFA")
            put("description", description)
            put("order_id", orderId)
            put("amount", amountPaise)
            put("currency", currency)
            put("prefill", JSONObject().apply {
                put("email", "")
                put("contact", "")
            })
        }
        checkout.open(activity, options)
    }
}
