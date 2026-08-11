package com.kuikly.init.business.debug.impl.hardware

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
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.scan.ScanResult
import com.kuikly.init.common.base.platform.scan.provideScanner
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_scanner")
public class DebugScannerPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            LocalContextProvider {
                val pageTitle = stringResource(DebugImplMR.strings.debug_scanner_title)
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
                ScannerTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
            }
        }
    }
}

@Composable
private fun ScannerTestContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    val resultPlaceholder = stringResource(DebugImplMR.strings.debug_scanner_result_placeholder)
    var result by remember { mutableStateOf(resultPlaceholder) }
    val scanner = remember { provideScanner() }

    val btnOpenScan = stringResource(DebugImplMR.strings.debug_scanner_btn_open_scan)
    val btnMockScan = stringResource(DebugImplMR.strings.debug_scanner_btn_mock_scan)
    val resultTitle = stringResource(DebugImplMR.strings.debug_scanner_result_title)
    val logStartScan = stringResource(DebugImplMR.strings.debug_scanner_log_start_scan)
    val logScanSuccess = stringResource(DebugImplMR.strings.debug_scanner_log_scan_success)
    val logScanFail = stringResource(DebugImplMR.strings.debug_scanner_log_scan_fail)
    val logScanException = stringResource(DebugImplMR.strings.debug_scanner_log_scan_exception)
    val logMockScan = stringResource(DebugImplMR.strings.debug_scanner_log_mock_scan)
    val resultFormat = stringResource(DebugImplMR.strings.debug_scanner_result_format)
    val btnClose = stringResource(DebugImplMR.strings.debug_close_page)

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            ScannerActionSection(
                scanner = scanner,
                btnOpenScan = btnOpenScan,
                btnMockScan = btnMockScan,
                logStartScan = logStartScan,
                logScanSuccess = logScanSuccess,
                logScanFail = logScanFail,
                logScanException = logScanException,
                logMockScan = logMockScan,
                resultFormat = resultFormat,
                onResultChange = { result = it }
            )
        }
        item {
            ScannerResultSection(result = result, resultTitle = resultTitle)
        }
        item {
            ScannerCloseButton(btnText = btnClose, onClose = onClose)
        }
    }
}

@Composable
private fun ScannerActionSection(
    scanner: com.kuikly.init.common.base.platform.scan.Scanner,
    btnOpenScan: String,
    btnMockScan: String,
    logStartScan: String,
    logScanSuccess: String,
    logScanFail: String,
    logScanException: String,
    logMockScan: String,
    resultFormat: String,
    onResultChange: (String) -> Unit
) {
    HardwareActionButtonPrimary(btnOpenScan) {
        onResultChange(logStartScan)
        try {
            scanner.startScan { scanResult ->
                onResultChange(
                    if (scanResult != null) {
                        String.format(logScanSuccess, formatResult(scanResult, resultFormat))
                    } else {
                        logScanFail
                    }
                )
            }
        } catch (e: Exception) {
            onResultChange(String.format(logScanException, e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonSecondary(btnMockScan) {
        val mock = ScanResult(
            content = "https://github.com/kuikly",
            format = "QR_CODE"
        )
        onResultChange(String.format(logMockScan, formatResult(mock, resultFormat)))
    }
}

@Composable
private fun ScannerResultSection(result: String, resultTitle: String) {
    Spacer(Modifier.height(16.dp))
    Text(resultTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
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
            text = result,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF333333)
        )
    }
}

@Composable
private fun ScannerCloseButton(btnText: String, onClose: () -> Unit) {
    Spacer(Modifier.height(16.dp))
    HardwareActionButtonSecondary(btnText, onClick = onClose)
    Spacer(Modifier.height(32.dp))
}

@Composable
private fun HardwareActionButtonPrimary(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = MaterialTheme.colorScheme.onPrimary, fontSize = 15.sp)
    }
}

@Composable
private fun HardwareActionButtonSecondary(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSecondary, fontSize = 15.sp)
    }
}

private fun formatResult(r: ScanResult, format: String): String {
    return String.format(format, r.content, r.format)
}
