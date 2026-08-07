package com.kuikly.init.common.base.platform.screen

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

/**
 * OHOS 屏幕信息实现（通过 KNOI 调用 ETS 侧）
 *
 * 基于 @ohos.display，由 ArkTS 侧实现具体逻辑。
 */
actual fun provideScreenInfo(): ScreenInfo {
    return try {
        val service = getIOHOSPlatformServiceApi()
        ScreenInfo(
            widthPx = service?.getScreenWidth() ?: 0,
            heightPx = service?.getScreenHeight() ?: 0,
            densityDpi = service?.getScreenDensityDpi() ?: 160,
            density = (service?.getScreenDensity() ?: 1.0).toFloat(),
            scaledDensity = (service?.getScreenDensity() ?: 1.0).toFloat(),
            rotation = service?.getScreenRotation() ?: 0
        )
    } catch (e: Exception) {
        ScreenInfo(
            widthPx = 0,
            heightPx = 0,
            densityDpi = 160,
            density = 1.0f,
            scaledDensity = 1.0f,
            rotation = 0
        )
    }
}
