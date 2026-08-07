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
    var result by remember { mutableStateOf("点击刷新按钮检测网络状态") }
    val monitor = remember { provideNetworkMonitor() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Network 测试") },
                actions = {
                    Text(
                        text = "关闭",
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
    onResultChange: (String) -> Unit
) {
    Text("网络状态检测", fontSize = 16.sp, color = Color(0xFF333333))
    DebugVSpacer(8.dp)
    DebugTestButton("刷新网络状态") {
        val connected = monitor.isConnected()
        val type = monitor.getNetworkType()
        val typeLabel = when (type) {
            NetworkType.WIFI -> "WIFI"
            NetworkType.CELLULAR -> "CELLULAR"
            NetworkType.NONE -> "NONE"
        }
        val ts = System.currentTimeMillis()
        onResultChange(
            buildString {
                appendLine("检测时间：$ts")
                appendLine("网络连接状态：${if (connected) "已连接" else "未连接"}")
                appendLine("网络类型：$typeLabel")
            }
        )
    }
}
