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
import com.tencent.kuikly.compose.runtime.rememberCoroutineScope
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
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import kotlinx.coroutines.launch

@Page("debug_network_request")
internal class DebugNetworkRequestPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        val ctx = this
        setContent {
            val pager = LocalActivity.current.getPager()
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("网络请求测试") },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { padding ->
                NetworkTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    bridgeModule = pager.bridgeModule,
                    onClose = { ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
        }
    }
}

@Composable
private fun NetworkTestContent(
    modifier: Modifier = Modifier,
    bridgeModule: com.kuikly.init.base.BridgeModule,
    onClose: () -> Unit
) {
    var cmdInput by remember { mutableStateOf("test.cmd") }
    var paramsInput by remember { mutableStateOf("{\"userId\":\"12345\"}") }
    var result by remember { mutableStateOf("响应结果将在此显示…") }
    val scope = rememberCoroutineScope()

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            NetworkInputSection(
                cmdInput = cmdInput,
                paramsInput = paramsInput,
                onCmdChange = { cmdInput = it },
                onParamsChange = { paramsInput = it }
            )
        }
        item {
            NetworkRequestSection(
                bridgeModule = bridgeModule,
                cmdInput = cmdInput,
                paramsInput = paramsInput,
                scope = scope,
                onResultChange = { result = it }
            )
        }
        item {
            NetworkPresetSection(
                onSelect = { cmd, params ->
                    cmdInput = cmd
                    paramsInput = params
                }
            )
        }
        item {
            NetworkResultSection(result = result)
        }
        item {
            NetworkCloseButton(onClose)
        }
    }
}

@Composable
private fun NetworkInputSection(
    cmdInput: String,
    paramsInput: String,
    onCmdChange: (String) -> Unit,
    onParamsChange: (String) -> Unit
) {
    Text("CMD:", fontSize = 14.sp, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    DebugTextField(
        value = cmdInput,
        placeholder = "输入 cmd",
        onValueChange = onCmdChange
    )
    Spacer(Modifier.height(8.dp))
    Text("请求参数 (JSON):", fontSize = 14.sp, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    DebugTextField(
        value = paramsInput,
        placeholder = "输入 reqParams JSON",
        onValueChange = onParamsChange
    )
}

@Composable
private fun NetworkRequestSection(
    bridgeModule: com.kuikly.init.base.BridgeModule,
    cmdInput: String,
    paramsInput: String,
    scope: kotlinx.coroutines.CoroutineScope,
    onResultChange: (String) -> Unit
) {
    Spacer(Modifier.height(12.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                scope.launch {
                    try {
                        val reqParams = JSONObject(paramsInput)
                        val startTime = bridgeModule.currentTimeStamp()
                        onResultChange("请求中…")
                        val resp = bridgeModule.ssoRequest(cmdInput, reqParams)
                        val elapsed = bridgeModule.currentTimeStamp() - startTime
                        onResultChange(
                            if (resp != null) {
                                "耗时: ${elapsed}ms\n\n${formatJson(resp.toString())}"
                            } else {
                                "耗时: ${elapsed}ms\n\n返回 null"
                            }
                        )
                    } catch (e: Exception) {
                        onResultChange("请求失败: ${e.message}")
                    }
                }
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("发起 ssoRequest", color = MaterialTheme.colorScheme.onPrimary, fontSize = 15.sp)
    }
}

@Composable
private fun NetworkPresetSection(
    onSelect: (String, String) -> Unit
) {
    val presetCases = listOf(
        "测试用例 A" to ("test.cmd" to "{\"userId\":\"12345\"}"),
        "测试用例 B" to ("user.info" to "{\"userId\":\"999\"}"),
        "测试用例 C" to ("app.config" to "{}")
    )

    Spacer(Modifier.height(16.dp))
    Text("预设测试用例", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(8.dp))
    presetCases.forEach { (label, caseData) ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable { onSelect(caseData.first, caseData.second) }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontSize = 14.sp)
        }
    }
}

@Composable
private fun NetworkResultSection(result: String) {
    Spacer(Modifier.height(16.dp))
    Text("响应结果", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .padding(8.dp)
    ) {
        Text(
            text = result,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF333333)
        )
    }
}

@Composable
private fun NetworkCloseButton(onClose: () -> Unit) {
    Spacer(Modifier.height(16.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.outline)
            .clickable(onClick = onClose)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("关闭页面", color = Color.White, fontSize = 15.sp)
    }
    Spacer(Modifier.height(32.dp))
}

private fun formatJson(raw: String): String {
    return try {
        val obj = JSONObject(raw)
        obj.toString()
    } catch (e: Exception) {
        raw
    }
}
