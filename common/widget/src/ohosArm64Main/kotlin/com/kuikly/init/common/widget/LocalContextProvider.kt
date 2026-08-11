package com.kuikly.init.common.widget

import androidx.compose.runtime.Composable

/**
 * OHOS 平台 LocalContext 提供者
 *
 * OHOS 平台无需 LocalContext，直接透传 content。
 */
@Composable
actual fun LocalContextProvider(content: @Composable () -> Unit) {
    content()
}
