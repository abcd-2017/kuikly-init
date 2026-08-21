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
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

@Page("debug_report")
public class DebugReportPage : BasePager() {

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
    bridgeModule: com.kuikly.init.common.widget.BridgeModule,
    onClose: () -> Unit
) {
    var eventCodeInput by remember { mutableStateOf("debug_event_test") }
    var dataInput by remember { mutableStateOf("{\"action\":\"click\",\"page\":\"debug_report\"}") }
    var log by remember { mutableStateOf("上报日志：\n") }

    fun onLogChange(newLog: String) {
        log = newLog
    }

    fun appendLog(msg: String) {
        onLogChange("[\${bridgeModule.currentTimeStamp()}] \$msg\n\$log")
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            ReportInputSection(
                eventCodeInput = eventCodeInput,
                dataInput = dataInput,
                onEventCodeChange = { eventCodeInput = it },
                onDataChange = { dataInput = it },
                labelEventCode = "EventCode:",
                labelData = "Data (JSON):",
                placeholderEventCode = "输入 eventCode",
                placeholderData = "输入 data JSON"
            )
        }
        item {
            ReportOpsSection(
                bridgeModule = bridgeModule,
                eventCodeInput = eventCodeInput,
                dataInput = dataInput,
                btnReportDt = "reportDT 事件上报",
                btnReportRealTime = "reportRealTime 实时上报",
                btnBatchReport = "批量上报 5 条测试事件",
                logReportDtSuccess = "reportDT → eventCode=%1\$s, data=%2\$s",
                logReportDtFail = "reportDT 失败: %1\$s",
                logReportRealTimeSuccess = "reportRealTime → eventCode=%1\$s, data=%2\$s",
                logReportRealTimeFail = "reportRealTime 失败: %1\$s",
                logBatchReport = "批量上报 #%1\$d → eventCode=debug_batch_event",
                logBatchReportFail = "批量上报 #%1\$d 失败: %1\$s",
                onAppendLog = { appendLog(it) }
            )
        }
        item {
            ReportLogSection(log = log, reportLogTitle = "操作日志")
        }
        item {
            ReportCloseButton(btnText = "关闭页面", onClose = onClose)
        }
    }
}

@Composable
private fun ReportInputSection(
    eventCodeInput: String,
    dataInput: String,
    onEventCodeChange: (String) -> Unit,
    onDataChange: (String) -> Unit,
    labelEventCode: String,
    labelData: String,
    placeholderEventCode: String,
    placeholderData: String
) {
    Text(labelEventCode, fontSize = 14.sp, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    DebugTextField(
        value = eventCodeInput,
        placeholder = placeholderEventCode,
        onValueChange = onEventCodeChange
    )
    Spacer(Modifier.height(8.dp))
    Text(labelData, fontSize = 14.sp, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    DebugTextField(
        value = dataInput,
        placeholder = placeholderData,
        onValueChange = onDataChange
    )
}

@Composable
private fun ReportOpsSection(
    bridgeModule: com.kuikly.init.common.widget.BridgeModule,
    eventCodeInput: String,
    dataInput: String,
    btnReportDt: String,
    btnReportRealTime: String,
    btnBatchReport: String,
    logReportDtSuccess: String,
    logReportDtFail: String,
    logReportRealTimeSuccess: String,
    logReportRealTimeFail: String,
    logBatchReport: String,
    logBatchReportFail: String,
    onAppendLog: (String) -> Unit
) {
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
                    onAppendLog(String.format(logReportDtSuccess, eventCodeInput, dataInput))
                } catch (e: Exception) {
                    onAppendLog(String.format(logReportDtFail, e.message))
                }
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(btnReportDt, color = MaterialTheme.colorScheme.onPrimary, fontSize = 15.sp)
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
                    onAppendLog(String.format(logReportRealTimeSuccess, eventCodeInput, dataInput))
                } catch (e: Exception) {
                    onAppendLog(String.format(logReportRealTimeFail, e.message))
                }
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(btnReportRealTime, color = MaterialTheme.colorScheme.onPrimary, fontSize = 15.sp)
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
                        onAppendLog(String.format(logBatchReport, i + 1))
                    } catch (e: Exception) {
                        onAppendLog(String.format(logBatchReportFail, i + 1, e.message))
                    }
                }
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(btnBatchReport, color = MaterialTheme.colorScheme.onSecondary, fontSize = 15.sp)
    }
}

@Composable
private fun ReportLogSection(log: String, reportLogTitle: String) {
    Spacer(Modifier.height(16.dp))
    Text(reportLogTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
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
private fun ReportCloseButton(btnText: String, onClose: () -> Unit) {
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
        Text(btnText, color = Color.White, fontSize = 15.sp)
    }
    Spacer(Modifier.height(32.dp))
}
