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
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

@Page("debug_report")
internal class DebugReportPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("上报测试") },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { padding ->
                ReportTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    bridgeModule = bridgeModule,
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
        }
    }
}

@Composable
private fun ReportTestContent(
    modifier: Modifier = Modifier,
    bridgeModule: com.kuikly.init.base.BridgeModule,
    onClose: () -> Unit
) {
    var eventCodeInput by remember { mutableStateOf("debug_event_test") }
    var dataInput by remember { mutableStateOf("{\"action\":\"click\",\"page\":\"debug_report\"}") }
    var log by remember { mutableStateOf("上报日志：\n") }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            ReportInputSection(
                eventCodeInput = eventCodeInput,
                dataInput = dataInput,
                onEventCodeChange = { eventCodeInput = it },
                onDataChange = { dataInput = it }
            )
        }
        item {
            ReportOpsSection(
                bridgeModule = bridgeModule,
                eventCodeInput = eventCodeInput,
                dataInput = dataInput,
                log = log,
                onLogChange = { log = it }
            )
        }
        item {
            ReportLogSection(log = log)
        }
        item {
            ReportCloseButton(onClose)
        }
    }
}

@Composable
private fun ReportInputSection(
    eventCodeInput: String,
    dataInput: String,
    onEventCodeChange: (String) -> Unit,
    onDataChange: (String) -> Unit
) {
    Text("EventCode:", fontSize = 14.sp, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    DebugTextField(
        value = eventCodeInput,
        placeholder = "输入 eventCode",
        onValueChange = onEventCodeChange
    )
    Spacer(Modifier.height(8.dp))
    Text("Data (JSON):", fontSize = 14.sp, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    DebugTextField(
        value = dataInput,
        placeholder = "输入 data JSON",
        onValueChange = onDataChange
    )
}

@Composable
private fun ReportOpsSection(
    bridgeModule: com.kuikly.init.base.BridgeModule,
    eventCodeInput: String,
    dataInput: String,
    log: String,
    onLogChange: (String) -> Unit
) {
    fun appendLog(msg: String) {
        onLogChange("[${bridgeModule.currentTimeStamp()}] $msg\n$log")
    }

    Spacer(Modifier.height(12.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                try {
                    val data = JSONObject(dataInput)
                    bridgeModule.reportDT(eventCodeInput, data)
                    appendLog("reportDT → eventCode=$eventCodeInput, data=$dataInput")
                } catch (e: Exception) {
                    appendLog("reportDT 失败: ${e.message}")
                }
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("reportDT 事件上报", color = MaterialTheme.colorScheme.onPrimary, fontSize = 15.sp)
    }
    Spacer(Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                try {
                    val data = JSONObject(dataInput)
                    bridgeModule.reportRealTime(eventCodeInput, data)
                    appendLog("reportRealTime → eventCode=$eventCodeInput, data=$dataInput")
                } catch (e: Exception) {
                    appendLog("reportRealTime 失败: ${e.message}")
                }
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("reportRealTime 实时上报", color = MaterialTheme.colorScheme.onPrimary, fontSize = 15.sp)
    }
    Spacer(Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .clickable {
                repeat(5) { i ->
                    try {
                        val data = JSONObject().apply {
                            put("index", i + 1)
                            put("batch", true)
                            put("timestamp", bridgeModule.currentTimeStamp())
                        }
                        bridgeModule.reportDT("debug_batch_event", data)
                        appendLog("批量上报 #${i + 1} → eventCode=debug_batch_event")
                    } catch (e: Exception) {
                        appendLog("批量上报 #${i + 1} 失败: ${e.message}")
                    }
                }
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("批量上报 5 条测试事件", color = MaterialTheme.colorScheme.onSecondary, fontSize = 15.sp)
    }
}

@Composable
private fun ReportLogSection(log: String) {
    Spacer(Modifier.height(16.dp))
    Text("上报日志", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
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
private fun ReportCloseButton(onClose: () -> Unit) {
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
