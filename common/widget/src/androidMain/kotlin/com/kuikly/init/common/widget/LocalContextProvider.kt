package com.kuikly.init.common.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.kuikly.init.common.base.platform.AppContext

/**
 * Android 平台 LocalContext 提供者
 *
 * 通过 CompositionLocalProvider 注入 LocalContext，
 * 使 stringResource() 等 Compose 扩展能正确访问资源。
 */
@Composable
actual fun LocalContextProvider(content: @Composable () -> Unit) {
    val context = AppContext.androidContext
    if (context != null) {
        CompositionLocalProvider(
            LocalContext provides (context as android.content.Context)
        ) {
            content()
        }
    } else {
        // AppContext 未初始化时直接透传（避免崩溃）
        content()
    }
}
