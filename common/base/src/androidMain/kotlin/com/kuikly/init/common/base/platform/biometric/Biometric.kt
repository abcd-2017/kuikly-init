package com.kuikly.init.common.base.platform.biometric

import android.content.Context
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.kuikly.init.common.base.platform.AppContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Android 生物识别实现
 *
 * 基于 androidx.biometric.BiometricPrompt。
 * isSupported 通过 BiometricManager.canAuthenticate 检查。
 * authenticate 需要 FragmentActivity，通过 AppContext.currentActivity 获取。
 */
actual class Biometric(private val context: Context) {

    actual fun isSupported(): Boolean {
        return try {
            val manager = BiometricManager.from(context)
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            } else {
                @Suppress("DEPRECATION")
                manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            }
            result == BiometricManager.BIOMETRIC_SUCCESS
        } catch (e: Exception) {
            false
        }
    }

    actual fun getSupportedTypes(): Set<BiometricType> {
        return try {
            val manager = BiometricManager.from(context)
            val types = mutableSetOf<BiometricType>()

            // 检查生物识别（弱级别，包含指纹和人脸）
            val biometricResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            } else {
                @Suppress("DEPRECATION")
                manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            }
            if (biometricResult == BiometricManager.BIOMETRIC_SUCCESS) {
                // Android 不区分指纹和人脸，统一归为 FINGERPRINT
                types.add(BiometricType.FINGERPRINT)
            }

            // 检查 PIN/密码（DEVICE_CREDENTIAL）
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val credentialResult = manager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                if (credentialResult == BiometricManager.BIOMETRIC_SUCCESS) {
                    types.add(BiometricType.PIN)
                }
            }

            types
        } catch (e: Exception) {
            emptySet()
        }
    }

    actual fun authenticate(title: String, cancelText: String, callback: (String) -> Unit) {
        runBlocking {
            try {
                val activity = AppContext.currentActivity
                if (activity !is FragmentActivity) {
                    callback("{\"result\":\"NOT_SUPPORTED\"}")
                    return@runBlocking
                }

                val result = suspendCancellableCoroutine { continuation ->
                    val executor = ContextCompat.getMainExecutor(activity)
                    val biometricCallback = object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            if (continuation.isActive) {
                                continuation.resume(BiometricResult.SUCCESS)
                            }
                        }

                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            if (continuation.isActive) {
                                val result = when (errorCode) {
                                    BiometricPrompt.ERROR_USER_CANCELED,
                                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                                    BiometricPrompt.ERROR_CANCELED -> BiometricResult.CANCELED
                                    BiometricPrompt.ERROR_HW_NOT_PRESENT,
                                    BiometricPrompt.ERROR_HW_UNAVAILABLE,
                                    BiometricPrompt.ERROR_NO_BIOMETRICS -> BiometricResult.NOT_SUPPORTED
                                    else -> BiometricResult.FAILED
                                }
                                continuation.resume(result)
                            }
                        }

                        override fun onAuthenticationFailed() {
                            // 认证失败但不结束，等待最终结果
                        }
                    }

                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle(title)
                        .setNegativeButtonText(cancelText)
                        .build()

                    val prompt = BiometricPrompt(activity, executor, biometricCallback)
                    prompt.authenticate(promptInfo)
                }

                val jsonResult = when (result) {
                    BiometricResult.SUCCESS -> "{\"result\":\"SUCCESS\"}"
                    BiometricResult.FAILED -> "{\"result\":\"FAILED\"}"
                    BiometricResult.CANCELED -> "{\"result\":\"CANCELED\"}"
                    BiometricResult.NOT_SUPPORTED -> "{\"result\":\"NOT_SUPPORTED\"}"
                }
                callback(jsonResult)
            } catch (e: Exception) {
                callback("{\"result\":\"FAILED\"}")
            }
        }
    }
}

actual fun provideBiometric(): Biometric = Biometric(AppContext.application)
