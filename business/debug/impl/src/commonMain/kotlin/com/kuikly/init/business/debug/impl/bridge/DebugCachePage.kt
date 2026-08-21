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
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.common.widget.bridgeModule
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.pager.Pager

@Page("debug_cache")
public class DebugCachePage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("缓存测试") },
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
    bridgeModule: com.kuikly.init.common.widget.BridgeModule,
    onClose: () -> Unit
) {
    var keyInput by remember { mutableStateOf("") }
    var valueInput by remember { mutableStateOf("") }
    var log by remember { mutableStateOf("操作日志：\n") }

    fun onLogChange(newLog: String) {
        log = newLog
    }

    fun appendLog(msg: String) {
        onLogChange("\$msg\n\$log")
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
                labelKey = "Key:",
                labelValue = "Value:",
                placeholderKey = "输入 key",
                placeholderValue = "输入 value"
            )
        }
        item {
            CacheOpsSection(
                bridgeModule = bridgeModule,
                keyInput = keyInput,
                valueInput = valueInput,
                btnSetCache = "setCache 写入缓存",
                btnGetCacheSync = "getCache 同步读取",
                btnGetCacheAsync = "fetchCache 异步读取",
                btnBatchWrite = "批量写入 3 条测试数据",
                logSetCacheFail = "[setCache] 失败：key 为空",
                logSetCacheSuccess = "[setCache] key=%1\$s, value=%2\$s → 写入完成",
                logGetCacheFail = "[getCache] 失败：key 为空",
                logGetCacheSuccess = "[getCache] key=%1\$s → %2\$s",
                logFetchCacheFail = "[fetchCache] 失败：key 为空",
                logFetchCacheSuccess = "[fetchCache] key=%1\$s → %2\$s",
                logBatchWrite = "[批量写入] key=%1\$s, value=%2\$s",
                valueEmpty = "(空)",
                valueNull = "(null)",
                onAppendLog = { appendLog(it) }
            )
        }
        item {
            CacheLogSection(log = log, operationLog = "操作日志")
        }
        item {
            CacheCloseButton(btnText = "关闭页面", onClose = onClose)
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
    bridgeModule: com.kuikly.init.common.widget.BridgeModule,
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
