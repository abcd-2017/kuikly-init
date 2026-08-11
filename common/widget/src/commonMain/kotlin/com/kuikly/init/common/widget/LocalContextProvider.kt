package com.kuikly.init.common.widget

import androidx.compose.runtime.Composable

/**
 * 平台特定的 LocalContext 提供者
 *
 * Android 平台需要提供 LocalContext（android.content.Context），
 * 以便 stringResource() 等 Compose 扩展能正确访问资源。
 * iOS/OHOS 平台无需此机制，直接透传 content。
 */
@Composable
expect fun LocalContextProvider(content: @Composable () -> Unit)

