package com.kuikly.init.common.base.platform.biometric

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import platform.Foundation.NSOperationQueue
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthentication
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics

/**
 * iOS 生物识别实现
 *
 * 基于 LocalAuthentication.LAContext。
 * isSupported 通过 canEvaluatePolicy 检查。
 * authenticate 通过 evaluatePolicy 发起认证。
 */
actual class Biometric {

    private val context = LAContext()

    actual fun isSupported(): Boolean {
        return try {
            context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
        } catch (e: Exception) {
            false
        }
    }

    actual fun getSupportedTypes(): Set<BiometricType> {
        return try {
            val types = mutableSetOf<BiometricType>()
            val canAuth = context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
            if (canAuth) {
                types.add(BiometricType.FINGERPRINT)
            }
            // 检查 PIN/密码
            val canAuthWithCredential = context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthentication, null)
            if (canAuthWithCredential) {
                types.add(BiometricType.PIN)
            }
            types
        } catch (e: Exception) {
            emptySet()
        }
    }

    actual fun authenticate(title: String, cancelText: String, callback: (String) -> Unit) {
        runBlocking {
            try {
                val canAuth = context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)
                if (!canAuth) {
                    callback("{\"result\":\"NOT_SUPPORTED\"}")
                    return@runBlocking
                }

                val result = suspendCancellableCoroutine { continuation ->
                    NSOperationQueue.mainQueue.addOperationWithBlock {
                        context.evaluatePolicy(
                            LAPolicyDeviceOwnerAuthenticationWithBiometrics,
                            title
                        ) { success, error ->
                            NSOperationQueue.mainQueue.addOperationWithBlock {
                                if (continuation.isActive) {
                                    val biometricResult = if (success) {
                                        BiometricResult.SUCCESS
                                    } else {
                                        val code = error?.code ?: 0
                                        // LAErrorUserCancel = -2, LAErrorSystemCancel = -4, LAErrorAppCancel = -9
                                        if (code == -2L || code == -4L || code == -9L) {
                                            BiometricResult.CANCELED
                                        } else if (code == -6L || code == -7L || code == -1L) {
                                            // LAErrorBiometryNotAvailable / NotEnlocked / AuthenticationFailed
                                            BiometricResult.NOT_SUPPORTED
                                        } else {
                                            BiometricResult.FAILED
                                        }
                                    }
                                    continuation.resume(biometricResult)
                                }
                            }
                        }
                    }
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

actual fun provideBiometric(): Biometric = Biometric()
