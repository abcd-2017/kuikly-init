package com.kuikly.init.business.debug.bridge

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
import com.tencent.kuikly.compose.runtime.getValue
import com.tencent.kuikly.compose.runtime.mutableStateOf
import com.tencent.kuikly.compose.runtime.remember
import com.tencent.kuikly.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.platform.LocalActivity
import com.tencent.kuikly.compose.ui.text.font.FontFamily
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.base.bridgeModule
import com.kuikly.init.business.debug.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.pager.Pager

@Page("debug_cache")
internal class DebugCachePage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        val ctx = this
        setContent {
            val pager = LocalActivity.current.getPager()
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
                    bridgeModule = pager.bridgeModule,
                    onClose = { ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
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
    var log by remember { mutableStateOf("操作日志：\n") }

    LazyColumn(
        modifier = modifier.padding(16.dp)
    ) {
        item {
            CacheInputSection(
                keyInput = keyInput,
                valueInput = valueInput,
                onKeyChange = { keyInput = it },
                onValueChange = { valueInput = it }
            )
        }
        item {
            CacheOpsSection(
                bridgeModule = bridgeModule,
                keyInput = keyInput,
                valueInput = valueInput,
                log = log,
                onLogChange = { log = it }
            )
        }
        item {
            CacheLogSection(log = log)
        }
        item {
            CacheCloseButton(onClose)
        }
    }
}

@Composable
private fun CacheInputSection(
    keyInput: String,
    valueInput: String,
    onKeyChange: (String) -> Unit,
    onValueChange: (String) -> Unit
) {
    Text("Key:", fontSize = 14.sp, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    DebugTextField(
        value = keyInput,
        placeholder = "输入 key",
        onValueChange = onKeyChange
    )
    Spacer(Modifier.height(8.dp))
    Text("Value:", fontSize = 14.sp, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    DebugTextField(
        value = valueInput,
        placeholder = "输入 value",
        onValueChange = onValueChange
    )
}

@Composable
private fun CacheOpsSection(
    bridgeModule: com.kuikly.init.base.BridgeModule,
    keyInput: String,
    valueInput: String,
    log: String,
    onLogChange: (String) -> Unit
) {
    fun appendLog(msg: String) {
        onLogChange("$msg\n$log")
    }

    Spacer(Modifier.height(12.dp))
    CacheActionButton("setCache 写入缓存") {
        if (keyInput.isEmpty()) {
            appendLog("[setCache] 失败：key 为空")
        } else {
            bridgeModule.setCachedToNative(keyInput, valueInput) {
                appendLog("[setCache] key=$keyInput, value=$valueInput → 写入完成")
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    CacheActionButton("getCache 同步读取") {
        if (keyInput.isEmpty()) {
            appendLog("[getCache] 失败：key 为空")
        } else {
            val result = bridgeModule.getCachedFromNative(keyInput)
            appendLog("[getCache] key=$keyInput → ${if (result.isEmpty()) "(空)" else result}")
        }
    }
    Spacer(Modifier.height(8.dp))
    CacheActionButton("fetchCache 异步读取") {
        if (keyInput.isEmpty()) {
            appendLog("[fetchCache] 失败：key 为空")
        } else {
            bridgeModule.fetchCachedFromNative(keyInput) {
                appendLog("[fetchCache] key=$keyInput → ${it ?: "(null)"}")
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    CacheActionButton("批量写入 3 条测试数据") {
        listOf("test_key_a" to "value_1", "test_key_b" to "value_2", "test_key_c" to "value_3")
            .forEach { (k, v) ->
                bridgeModule.setCachedToNative(k, v) {
                    appendLog("[批量写入] key=$k, value=$v")
                }
            }
    }
}

@Composable
private fun CacheLogSection(log: String) {
    Spacer(Modifier.height(16.dp))
    Text("操作日志", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
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
private fun CacheCloseButton(onClose: () -> Unit) {
    Spacer(Modifier.height(16.dp))
    CacheActionButton(text = "关闭页面", isPrimary = false, onClick = onClose)
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
