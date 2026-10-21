package com.fersaiyan.cyanbridge.devices.meizumyvu

import android.content.Context
import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MeizuMyvuFailure(
    val stage: String,
    val reason: String,
)

/**
 * Compatibility facade for builds that intentionally omit the Meizu MYVU transport.
 *
 * The upstream MYVU client is not compiled into this build. This no-op implementation keeps the
 * existing dashboard code compiling without adding any Meizu dependency.
 */
class MeizuMyvuManager private constructor(private val context: Context) {

    companion object {
        private const val UNAVAILABLE =
            "Meizu MYVU support is not included in this build"

        @Volatile
        private var instance: MeizuMyvuManager? = null

        fun getInstance(context: Context): MeizuMyvuManager =
            instance ?: synchronized(this) {
                instance ?: MeizuMyvuManager(context.applicationContext).also { instance = it }
            }
    }

    private val _state = MutableStateFlow(MeizuMyvuUiState(lastError = UNAVAILABLE))
    val state: StateFlow<MeizuMyvuUiState> = _state.asStateFlow()

    private val _failurePrompt = MutableStateFlow<MeizuMyvuFailure?>(null)
    val failurePrompt: StateFlow<MeizuMyvuFailure?> = _failurePrompt.asStateFlow()

    fun connect(
        macAddress: String,
        requestContext: Context = context,
        userInitiated: Boolean = false,
    ) {
        _state.value = _state.value.copy(lastError = UNAVAILABLE)
    }

    fun disconnect() {
        _state.value = _state.value.copy(
            connectionLabel = "Disconnected",
            relayReady = false,
            lastError = null,
        )
    }

    fun isReady(): Boolean = false

    fun shouldRequestConnection(nowElapsedMs: Long = SystemClock.elapsedRealtime()): Boolean = false

    fun consumeFailurePrompt() {
        _failurePrompt.value = null
    }

    fun sendTestNotification() = Unit

    fun syncClock() = Unit

    fun setBrightness(levelPercent: Int) = Unit

    fun showTeleprompter(title: String, text: String) = Unit

    fun queryBattery() = Unit

    fun diagnosticsSnapshot(): String = UNAVAILABLE

    data class MeizuMyvuUiState(
        val connectionLabel: String = "Unsupported",
        val protocolState: String = "IDLE",
        val selectedAddress: String? = null,
        val deviceName: String? = null,
        val batteryPercent: Int? = null,
        val lastError: String? = null,
        val relayReady: Boolean = false,
    )
}
