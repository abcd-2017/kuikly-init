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
import com.kuikly.init.common.base.platform.DeviceInfo
import com.kuikly.init.common.base.platform.screen.provideScreenInfo
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_device")
internal class DebugDevicePage : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {
            DebugDeviceContent { closePage() }
        }
    }

    private fun closePage() {
        acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebugDeviceContent(onClose: () -> Unit) {
    var result by remember { mutableStateOf("点击刷新按钮获取设备信息") }
    val deviceInfo = remember { DeviceInfo() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Device 测试") },
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
                DeviceInfoSection(
                    deviceInfo = deviceInfo,
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
private fun DeviceInfoSection(
    deviceInfo: DeviceInfo,
    onResultChange: (String) -> Unit
) {
    Text("设备信息", fontSize = 16.sp, color = Color(0xFF333333))
    DebugVSpacer(8.dp)
    DebugTestButton("刷新全部信息") {
        val screen = provideScreenInfo()
        val density = screen.density
        val widthDp = (screen.widthPx / density).toInt()
        val heightDp = (screen.heightPx / density).toInt()
        onResultChange(
            buildString {
                appendLine("设备 ID：${deviceInfo.getDeviceId()}")
                appendLine("OS 版本：${deviceInfo.getOSVersion()}")
                appendLine("设备型号：${deviceInfo.getDeviceModel()}")
                appendLine("屏幕宽度：${widthDp} dp (${screen.widthPx} px)")
                appendLine("屏幕高度：${heightDp} dp (${screen.heightPx} px)")
                appendLine("屏幕密度 DPI：${screen.densityDpi}")
                appendLine("密度比例：${screen.density}")
            }
        )
    }
}
