package com.fersaiyan.cyanbridge.devices.metarayban

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Compatibility facade for builds that intentionally omit the Meta Wearables DAT SDK.
 *
 * The Meta device integration is not compiled into this build. The class remains so the rest of
 * CyanBridge can keep its existing device-handling code paths without pulling in Meta artifacts.
 */
class MetaRaybanManager private constructor(private val context: Context) {

    companion object {
        private const val UNAVAILABLE =
            "Meta Wearables support is not included in this build"

        @Volatile
        private var instance: MetaRaybanManager? = null

        fun getInstance(context: Context): MetaRaybanManager =
            instance ?: synchronized(this) {
                instance ?: MetaRaybanManager(context.applicationContext).also { instance = it }
            }
    }

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    private val _registrationState = MutableStateFlow(RegistrationState.UNAVAILABLE)
    val registrationState: StateFlow<RegistrationState> = _registrationState.asStateFlow()

    private val _availableDeviceCount = MutableStateFlow(0)
    val availableDeviceCount: StateFlow<Int> = _availableDeviceCount.asStateFlow()

    private val _selectedDeviceName = MutableStateFlow<String?>(null)
    val selectedDeviceName: StateFlow<String?> = _selectedDeviceName.asStateFlow()

    private val _selectedDeviceIsDisplayCapable = MutableStateFlow(false)
    val selectedDeviceIsDisplayCapable: StateFlow<Boolean> =
        _selectedDeviceIsDisplayCapable.asStateFlow()

    private val _deviceSessionState = MutableStateFlow(DeviceSessionState.IDLE)
    val deviceSessionState: StateFlow<DeviceSessionState> = _deviceSessionState.asStateFlow()

    private val _streamState = MutableStateFlow(StreamState.STOPPED)
    val streamState: StateFlow<StreamState> = _streamState.asStateFlow()

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _lastCapturedPhoto = MutableStateFlow<CapturedPhoto?>(null)
    val lastCapturedPhoto: StateFlow<CapturedPhoto?> = _lastCapturedPhoto.asStateFlow()

    private val _isDisplayActive = MutableStateFlow(false)
    val isDisplayActive: StateFlow<Boolean> = _isDisplayActive.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    private val _debugMockEnabled = MutableStateFlow(false)
    val debugMockEnabled: StateFlow<Boolean> = _debugMockEnabled.asStateFlow()

    fun isDebugMockEnabled(): Boolean = false

    fun setDebugMockEnabled(enabled: Boolean) {
        _debugMockEnabled.value = enabled
    }

    fun initialize() {
        _isInitialized.value = false
        _registrationState.value = RegistrationState.UNAVAILABLE
        _lastError.value = UNAVAILABLE
    }

    fun startRegistration(activity: Activity) {
        _lastError.value = UNAVAILABLE
    }

    fun startUnregistration(activity: Activity) {
        _lastError.value = UNAVAILABLE
    }

    fun isRegistered(): Boolean = false

    fun isCameraReady(): Boolean = false

    fun awaitCameraReady(timeoutMs: Long = 5_000L): Boolean = false

    fun refreshRegistrationState() {
        _registrationState.value = RegistrationState.UNAVAILABLE
    }

    fun handleRegistrationCallback(intent: Intent): Boolean = false

    fun checkCameraPermission(
        onGranted: () -> Unit,
        onRequestNeeded: () -> Unit,
        onError: (String) -> Unit,
    ) {
        onError(UNAVAILABLE)
    }

    fun startSession(onSuccess: () -> Unit, onError: (String) -> Unit) {
        onError(UNAVAILABLE)
    }

    fun stopSession() {
        _deviceSessionState.value = DeviceSessionState.IDLE
    }

    fun startStreaming(
        onFrame: (Bitmap) -> Unit,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        onError(UNAVAILABLE)
    }

    fun stopStreaming() {
        _isStreaming.value = false
        _streamState.value = StreamState.STOPPED
    }

    fun capturePhoto(onSuccess: (CapturedPhoto) -> Unit, onError: (String) -> Unit) {
        onError(UNAVAILABLE)
    }

    fun capturePhotoOnce(): CapturedPhoto = throw IllegalStateException(UNAVAILABLE)

    fun savePhotoForProcessing(photo: CapturedPhoto, namePrefix: String): File =
        throw IllegalStateException(UNAVAILABLE)

    fun startDisplay(onSuccess: () -> Unit, onError: (String) -> Unit) {
        onError(UNAVAILABLE)
    }

    fun stopDisplay() {
        _isDisplayActive.value = false
    }

    fun reportExternalError(operation: String, message: String): String =
        "$operation: ${message.ifBlank { UNAVAILABLE }}"

    fun diagnosticsSnapshot(): String = UNAVAILABLE

    fun registrationGuidance(): String? = UNAVAILABLE

    fun installedMetaAiPackageName(): String? = null

    fun isMetaAiInstalled(): Boolean = false

    fun destroy() {
        instance = null
    }

    data class CapturedPhoto(
        val bytes: ByteArray,
        val mimeType: String,
        val uri: Uri?,
    )

    enum class RegistrationState {
        UNAVAILABLE,
        AVAILABLE,
        REGISTERED,
        REGISTERING,
        UNREGISTERING,
    }

    enum class DeviceSessionState {
        IDLE,
        STARTING,
        STARTED,
        PAUSED,
        STOPPING,
        STOPPED,
    }

    enum class StreamState {
        STOPPED,
        STARTING,
        STARTED,
        STREAMING,
        STOPPING,
        PAUSED,
        CLOSED,
    }
}
