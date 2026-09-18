package org.phireox.ofa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.phireox.ofa.core.billing.RazorpayResultBus
import org.phireox.ofa.core.theme.OFATheme
import org.phireox.ofa.feature.main.OFAApp

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OFATheme {
                OFAApp()
            }
        }
    }

    override fun onPaymentSuccess(paymentId: String?, data: PaymentData?) {
        RazorpayResultBus.emit(RazorpayResultBus.Event.Success(paymentId ?: "", data))
    }

    override fun onPaymentError(errorCode: Int, description: String?, data: PaymentData?) {
        RazorpayResultBus.emit(RazorpayResultBus.Event.Error(errorCode, description, data))
    }
}
