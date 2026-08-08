package com.kuikly.init.business.debug.impl.bridge

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.CenterAlignedTopAppBar
import com.tencent.kuikly.compose.material3.ExperimentalMaterial3Api
import com.tencent.kuikly.compose.material3.MaterialTheme
import com.tencent.kuikly.compose.material3.Scaffold
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.font.FontFamily
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.base.bridgeModule
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.pager.Pager

@Page("debug_cache")
internal class DebugCachePage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            val pageTitle = stringResource(DebugImplMR.strings.debug_cache_title)
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(pageTitle) },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { padding ->
                CacheTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    bridgeModule = bridgeModule,
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
        }
    }
}

@Composable
private fun CacheTestContent(
    modifier: Modifier = Modifier,
    bridgeModule: com.kuikly.init.base.BridgeModule,
    onClose: () -> Unit
) {
    var keyInput by remember { mutableStateOf("") }
    var valueInput by remember { mutableStateOf("") }
    val logPrefix = stringResource(DebugImplMR.strings.debug_cache_log_prefix)
    var log by remember { mutableStateOf(logPrefix) }

    val labelKey = stringResource(DebugImplMR.strings.debug_cache_label_key)
    val labelValue = stringResource(DebugImplMR.strings.debug_cache_label_value)
    val placeholderKey = stringResource(DebugImplMR.strings.debug_cache_placeholder_key)
    val placeholderValue = stringResource(DebugImplMR.strings.debug_cache_placeholder_value)
    val btnSetCache = stringResource(DebugImplMR.strings.debug_cache_btn_set_cache)
    val btnGetCacheSync = stringResource(DebugImplMR.strings.debug_cache_btn_get_cache_sync)
    val btnGetCacheAsync = stringResource(DebugImplMR.strings.debug_cache_btn_get_cache_async)
    val btnBatchWrite = stringResource(DebugImplMR.strings.debug_cache_btn_batch_write)
    val logSetCacheFail = stringResource(DebugImplMR.strings.debug_cache_log_set_cache_fail)
    val logSetCacheSuccess = stringResource(DebugImplMR.strings.debug_cache_log_set_cache_success)
    val logGetCacheFail = stringResource(DebugImplMR.strings.debug_cache_log_get_cache_fail)
    val logGetCacheSuccess = stringResource(DebugImplMR.strings.debug_cache_log_get_cache_success)
    val logFetchCacheFail = stringResource(DebugImplMR.strings.debug_cache_log_fetch_cache_fail)
    val logFetchCacheSuccess = stringResource(DebugImplMR.strings.debug_cache_log_fetch_cache_success)
    val logBatchWrite = stringResource(DebugImplMR.strings.debug_cache_log_batch_write)
    val valueEmpty = stringResource(DebugImplMR.strings.debug_cache_value_empty)
    val valueNull = stringResource(DebugImplMR.strings.debug_cache_value_null)
    val operationLog = stringResource(DebugImplMR.strings.debug_operation_log)
    val btnClose = stringResource(DebugImplMR.strings.debug_close_page)

    fun onLogChange(newLog: String) {
        log = newLog
    }

    fun appendLog(msg: String) {
        onLogChange("$msg\n$log")
    }

    LazyColumn(
        modifier = modifier.padding(16.dp)
    ) {
        item {
            CacheInputSection(
                keyInput = keyInput,
                valueInput = valueInput,
                onKeyChange = { keyInput = it },
                onValueChange = { valueInput = it },
                labelKey = labelKey,
                labelValue = labelValue,
                placeholderKey = placeholderKey,
                placeholderValue = placeholderValue
            )
        }
        item {
            CacheOpsSection(
                bridgeModule = bridgeModule,
                keyInput = keyInput,
                valueInput = valueInput,
                btnSetCache = btnSetCache,
                btnGetCacheSync = btnGetCacheSync,
                btnGetCacheAsync = btnGetCacheAsync,
                btnBatchWrite = btnBatchWrite,
                logSetCacheFail = logSetCacheFail,
                logSetCacheSuccess = logSetCacheSuccess,
                logGetCacheFail = logGetCacheFail,
                logGetCacheSuccess = logGetCacheSuccess,
                logFetchCacheFail = logFetchCacheFail,
                logFetchCacheSuccess = logFetchCacheSuccess,
                logBatchWrite = logBatchWrite,
                valueEmpty = valueEmpty,
                valueNull = valueNull,
                onAppendLog = { appendLog(it) }
            )
        }
        item {
            CacheLogSection(log = log, operationLog = operationLog)
        }
        item {
            CacheCloseButton(btnText = btnClose, onClose = onClose)
        }
    }
}

@Composable
private fun CacheInputSection(
    keyInput: String,
    valueInput: String,
    onKeyChange: (String) -> Unit,
    onValueChange: (String) -> Unit,
    labelKey: String,
    labelValue: String,
    placeholderKey: String,
    placeholderValue: String
) {
    Text(labelKey, fontSize = 14.sp, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    DebugTextField(
        value = keyInput,
        placeholder = placeholderKey,
        onValueChange = onKeyChange
    )
    Spacer(Modifier.height(8.dp))
    Text(labelValue, fontSize = 14.sp, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    DebugTextField(
        value = valueInput,
        placeholder = placeholderValue,
        onValueChange = onValueChange
    )
}

@Composable
private fun CacheOpsSection(
    bridgeModule: com.kuikly.init.base.BridgeModule,
    keyInput: String,
    valueInput: String,
    btnSetCache: String,
    btnGetCacheSync: String,
    btnGetCacheAsync: String,
    btnBatchWrite: String,
    logSetCacheFail: String,
    logSetCacheSuccess: String,
    logGetCacheFail: String,
    logGetCacheSuccess: String,
    logFetchCacheFail: String,
    logFetchCacheSuccess: String,
    logBatchWrite: String,
    valueEmpty: String,
    valueNull: String,
    onAppendLog: (String) -> Unit
) {
    Spacer(Modifier.height(12.dp))
    CacheActionButton(btnSetCache) {
        if (keyInput.isEmpty()) {
            onAppendLog(logSetCacheFail)
        } else {
            bridgeModule.setCachedToNative(keyInput, valueInput) {
                onAppendLog(String.format(logSetCacheSuccess, keyInput, valueInput))
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    CacheActionButton(btnGetCacheSync) {
        if (keyInput.isEmpty()) {
            onAppendLog(logGetCacheFail)
        } else {
            val result = bridgeModule.getCachedFromNative(keyInput)
            onAppendLog(String.format(logGetCacheSuccess, keyInput, if (result.isEmpty()) valueEmpty else result))
        }
    }
    Spacer(Modifier.height(8.dp))
    CacheActionButton(btnGetCacheAsync) {
        if (keyInput.isEmpty()) {
            onAppendLog(logFetchCacheFail)
        } else {
            bridgeModule.fetchCachedFromNative(keyInput) {
                onAppendLog(String.format(logFetchCacheSuccess, keyInput, it ?: valueNull))
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    CacheActionButton(btnBatchWrite) {
        listOf("test_key_a" to "value_1", "test_key_b" to "value_2", "test_key_c" to "value_3")
            .forEach { (k, v) ->
                bridgeModule.setCachedToNative(k, v) {
                    onAppendLog(String.format(logBatchWrite, k, v))
                }
            }
    }
}

@Composable
private fun CacheLogSection(log: String, operationLog: String) {
    Spacer(Modifier.height(16.dp))
    Text(operationLog, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .padding(8.dp)
    ) {
        Text(
            text = log,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF333333)
        )
    }
}

@Composable
private fun CacheCloseButton(btnText: String, onClose: () -> Unit) {
    Spacer(Modifier.height(16.dp))
    CacheActionButton(text = btnText, isPrimary = false, onClick = onClose)
    Spacer(Modifier.height(32.dp))
}

@Composable
private fun CacheActionButton(
    text: String,
    isPrimary: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isPrimary) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isPrimary) MaterialTheme.colorScheme.onPrimary else Color.White,
            fontSize = 15.sp
        )
    }
}
