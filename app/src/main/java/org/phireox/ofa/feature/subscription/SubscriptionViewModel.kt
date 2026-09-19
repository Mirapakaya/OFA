package org.phireox.ofa.feature.subscription

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.phireox.ofa.BuildConfig
import org.phireox.ofa.core.billing.CurrencyHelper
import org.phireox.ofa.core.billing.ExternalOrderProvider
import org.phireox.ofa.core.billing.RazorpayBillingManager
import org.phireox.ofa.core.billing.RazorpayResultBus
import org.phireox.ofa.data.local.PrefsDataStore

class SubscriptionViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = PrefsDataStore(app)
    private val orderProvider = ExternalOrderProvider(BuildConfig.RAZORPAY_ORDER_PROVIDER_URL)
    private val billingManager = RazorpayBillingManager(BuildConfig.RAZORPAY_KEY)
    private val pricing = CurrencyHelper.detectPricing(app)

    private val _uiState = MutableStateFlow(
        SubscriptionUiState(
            currency = pricing.currencyCode,
            monthlyPrice = CurrencyHelper.formatPrice(pricing.monthlyPrice, pricing.currencyCode),
            yearlyPrice = CurrencyHelper.formatPrice(pricing.yearlyPrice, pricing.currencyCode)
        )
    )
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            RazorpayResultBus.events.collect { event ->
                when (event) {
                    is RazorpayResultBus.Event.Success -> {
                        prefs.setPremium(true)
                        _uiState.value = _uiState.value.copy(isPremium = true, message = "Payment successful: ${event.paymentId}", loading = false)
                    }
                    is RazorpayResultBus.Event.Error -> {
                        _uiState.value = _uiState.value.copy(message = "Payment failed: ${event.description}", loading = false)
                    }
                }
            }
        }
        viewModelScope.launch {
            prefs.isPremium.collect { premium ->
                _uiState.value = _uiState.value.copy(isPremium = premium)
            }
        }
    }

    fun purchase(activity: Activity, plan: Plan) {
        val amount = when (plan) {
            Plan.MONTHLY -> pricing.monthlyPrice
            Plan.YEARLY -> pricing.yearlyPrice
        }
        _uiState.value = _uiState.value.copy(loading = true, message = null)
        viewModelScope.launch {
            val result = orderProvider.createOrder(amount, pricing.currencyCode)
            result.onSuccess { orderId ->
                billingManager.startCheckout(activity, orderId, amount, pricing.currencyCode, "OFA Premium $plan")
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(message = "Order creation failed: ${error.message}. Configure an external order provider.", loading = false)
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            prefs.setPremium(true)
            _uiState.value = _uiState.value.copy(isPremium = true, message = "Purchases restored (local preview).", loading = false)
        }
    }

    enum class Plan { MONTHLY, YEARLY }

    data class SubscriptionUiState(
        val isPremium: Boolean = false,
        val currency: String = "",
        val monthlyPrice: String = "",
        val yearlyPrice: String = "",
        val loading: Boolean = false,
        val message: String? = null
    )
}
