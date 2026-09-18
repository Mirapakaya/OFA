package org.phireox.ofa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.phireox.ofa.core.billing.RazorpayResultBus
import org.phireox.ofa.core.theme.OFATheme
import org.phireox.ofa.data.local.PrefsDataStore
import org.phireox.ofa.data.local.ThemeMode
import org.phireox.ofa.feature.main.OFAApp
import org.phireox.ofa.feature.onboarding.OnboardingScreen

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()
        val prefs = PrefsDataStore(this)
        setContent {
            val mode = prefs.themeMode.collectAsState(initial = ThemeMode.SYSTEM).value
            val dynamic = prefs.dynamicColor.collectAsState(initial = false).value
            val darkTheme = when (mode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }
            val onboardingComplete = prefs.onboardingComplete.collectAsState(initial = false).value
            OFATheme(darkTheme = darkTheme, dynamicColor = dynamic) {
                if (onboardingComplete) {
                    OFAApp()
                } else {
                    OnboardingScreen(prefs = prefs, onFinished = {})
                }
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
