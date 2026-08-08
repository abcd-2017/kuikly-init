package com.kuikly.init.business.debug.impl.platform

import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
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
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.NetworkMonitor
import com.kuikly.init.common.base.platform.NetworkType
import com.kuikly.init.common.base.platform.provideNetworkMonitor
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_network")
internal class DebugNetworkPage : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {
            DebugNetworkContent { closePage() }
        }
    }

    private fun closePage() {
        acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebugNetworkContent(onClose: () -> Unit) {
    val placeholderText = stringResource(DebugImplMR.strings.debug_network_result_placeholder)
    var result by remember { mutableStateOf(placeholderText) }
    val monitor = remember { provideNetworkMonitor() }

    val pageTitle = stringResource(DebugImplMR.strings.debug_network_title)
    val btnClose = stringResource(DebugImplMR.strings.debug_close)
    val labelNetworkStatus = stringResource(DebugImplMR.strings.debug_network_label_network_status)
    val btnRefresh = stringResource(DebugImplMR.strings.debug_network_btn_refresh)
    val resultFormat = stringResource(DebugImplMR.strings.debug_network_result_format)
    val statusConnected = stringResource(DebugImplMR.strings.debug_network_status_connected)
    val statusDisconnected = stringResource(DebugImplMR.strings.debug_network_status_disconnected)
    val typeWifi = stringResource(DebugImplMR.strings.debug_network_type_wifi)
    val typeCellular = stringResource(DebugImplMR.strings.debug_network_type_cellular)
    val typeNone = stringResource(DebugImplMR.strings.debug_network_type_none)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(pageTitle) },
                actions = {
                    Text(
                        text = btnClose,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(10.dp)
                            .clickable { onClose() }
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.fillMaxSize().padding(padding).padding(12.dp)
        ) {
            item {
                NetworkStatusSection(
                    monitor = monitor,
                    labelNetworkStatus = labelNetworkStatus,
                    btnRefresh = btnRefresh,
                    resultFormat = resultFormat,
                    statusConnected = statusConnected,
                    statusDisconnected = statusDisconnected,
                    typeWifi = typeWifi,
                    typeCellular = typeCellular,
                    typeNone = typeNone,
                    onResultChange = { result = it }
                )
            }
            item {
                DebugVSpacer(8.dp)
                DebugResultArea(result)
            }
        }
    }
}

@Composable
private fun NetworkStatusSection(
    monitor: NetworkMonitor,
    labelNetworkStatus: String,
    btnRefresh: String,
    resultFormat: String,
    statusConnected: String,
    statusDisconnected: String,
    typeWifi: String,
    typeCellular: String,
    typeNone: String,
    onResultChange: (String) -> Unit
) {
    Text(labelNetworkStatus, fontSize = 16.sp, color = Color(0xFF333333))
    DebugVSpacer(8.dp)
    DebugTestButton(btnRefresh) {
        val connected = monitor.isConnected()
        val type = monitor.getNetworkType()
        val typeLabel = when (type) {
            NetworkType.WIFI -> typeWifi
            NetworkType.CELLULAR -> typeCellular
            NetworkType.NONE -> typeNone
        }
        val ts = System.currentTimeMillis()
        onResultChange(String.format(
            resultFormat,
            ts,
            if (connected) statusConnected else statusDisconnected,
            typeLabel
        ))
    }
}
