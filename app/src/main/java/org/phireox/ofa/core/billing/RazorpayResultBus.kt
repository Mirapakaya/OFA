package org.phireox.ofa.core.billing

import com.razorpay.PaymentData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object RazorpayResultBus {
    sealed class Event {
        data class Success(val paymentId: String, val data: PaymentData?) : Event()
        data class Error(val code: Int, val description: String?, val data: PaymentData?) : Event()
    }

    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> = _events.asSharedFlow()

    fun emit(event: Event) {
        _events.tryEmit(event)
    }
}
