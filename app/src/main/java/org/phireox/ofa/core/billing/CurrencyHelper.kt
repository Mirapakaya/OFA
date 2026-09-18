package org.phireox.ofa.core.billing

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import java.util.Currency
import java.util.Locale

object CurrencyHelper {

    data class Pricing(val currencyCode: String, val monthlyPrice: Long, val yearlyPrice: Long)

    fun detectPricing(context: Context): Pricing {
        val country = detectCountry(context)
        return when (country.uppercase(Locale.ROOT)) {
            "IN" -> Pricing("INR", 9900L, 99900L)
            "US" -> Pricing("USD", 299L, 2999L)
            "GB" -> Pricing("GBP", 199L, 1999L)
            "EU" -> Pricing("EUR", 199L, 1999L)
            "CA" -> Pricing("CAD", 399L, 3999L)
            "AU" -> Pricing("AUD", 399L, 3999L)
            else -> Pricing("USD", 299L, 2999L)
        }
    }

    private fun detectCountry(context: Context): String {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            val sim = tm?.simCountryIso
            if (!sim.isNullOrBlank()) return sim
        } catch (_: Exception) { }
        val locale = getLocale(context)
        return locale.country ?: "US"
    }

    private fun getLocale(context: Context): Locale {
        val configuration = context.resources.configuration
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.locales.get(0)
        } else {
            @Suppress("DEPRECATION")
            configuration.locale
        }
    }

    fun formatPrice(amount: Long, currencyCode: String): String {
        val currency = Currency.getInstance(currencyCode)
        val symbol = currency.symbol
        val major = amount / 100
        val minor = amount % 100
        return if (minor == 0L) "$symbol$major" else "$symbol$major.${String.format("%02d", minor)}"
    }
}
