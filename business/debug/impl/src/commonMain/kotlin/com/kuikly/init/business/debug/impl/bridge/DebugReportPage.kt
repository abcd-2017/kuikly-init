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
import com.kuikly.init.common.widget.LocalContextProvider
import com.kuikly.init.common.widget.bridgeModule
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

@Page("debug_report")
public class DebugReportPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            LocalContextProvider {
                val pageTitle = stringResource(DebugImplMR.strings.debug_report_title)
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
                ReportTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    bridgeModule = bridgeModule,
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
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
    val logPrefix = stringResource(DebugImplMR.strings.debug_report_log_prefix)
    var log by remember { mutableStateOf(logPrefix) }

    val labelEventCode = stringResource(DebugImplMR.strings.debug_report_label_event_code)
    val labelData = stringResource(DebugImplMR.strings.debug_report_label_data)
    val placeholderEventCode = stringResource(DebugImplMR.strings.debug_report_placeholder_event_code)
    val placeholderData = stringResource(DebugImplMR.strings.debug_report_placeholder_data)
    val btnReportDt = stringResource(DebugImplMR.strings.debug_report_btn_report_dt)
    val btnReportRealTime = stringResource(DebugImplMR.strings.debug_report_btn_report_real_time)
    val btnBatchReport = stringResource(DebugImplMR.strings.debug_report_btn_batch_report)
    val logReportDtSuccess = stringResource(DebugImplMR.strings.debug_report_log_report_dt_success)
    val logReportDtFail = stringResource(DebugImplMR.strings.debug_report_log_report_dt_fail)
    val logReportRealTimeSuccess = stringResource(DebugImplMR.strings.debug_report_log_report_real_time_success)
    val logReportRealTimeFail = stringResource(DebugImplMR.strings.debug_report_log_report_real_time_fail)
    val logBatchReport = stringResource(DebugImplMR.strings.debug_report_log_batch_report)
    val logBatchReportFail = stringResource(DebugImplMR.strings.debug_report_log_batch_report_fail)
    val reportLogTitle = stringResource(DebugImplMR.strings.debug_operation_log)
    val btnClose = stringResource(DebugImplMR.strings.debug_close_page)

    fun onLogChange(newLog: String) {
        log = newLog
    }

    fun appendLog(msg: String) {
        onLogChange("[${bridgeModule.currentTimeStamp()}] $msg\n$log")
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            ReportInputSection(
                eventCodeInput = eventCodeInput,
                dataInput = dataInput,
                onEventCodeChange = { eventCodeInput = it },
                onDataChange = { dataInput = it },
                labelEventCode = labelEventCode,
                labelData = labelData,
                placeholderEventCode = placeholderEventCode,
                placeholderData = placeholderData
            )
        }
        item {
            ReportOpsSection(
                bridgeModule = bridgeModule,
                eventCodeInput = eventCodeInput,
                dataInput = dataInput,
                btnReportDt = btnReportDt,
                btnReportRealTime = btnReportRealTime,
                btnBatchReport = btnBatchReport,
                logReportDtSuccess = logReportDtSuccess,
                logReportDtFail = logReportDtFail,
                logReportRealTimeSuccess = logReportRealTimeSuccess,
                logReportRealTimeFail = logReportRealTimeFail,
                logBatchReport = logBatchReport,
                logBatchReportFail = logBatchReportFail,
                onAppendLog = { appendLog(it) }
            )
        }
        item {
            ReportLogSection(log = log, reportLogTitle = reportLogTitle)
        }
        item {
            ReportCloseButton(btnText = btnClose, onClose = onClose)
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
