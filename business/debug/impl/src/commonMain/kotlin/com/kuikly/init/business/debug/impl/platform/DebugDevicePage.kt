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
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.common.widget.LocalContextProvider
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.DeviceInfo
import com.kuikly.init.common.base.platform.screen.provideScreenInfo
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_device")
public class DebugDevicePage : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {
            LocalContextProvider {
                DebugDeviceContent { closePage() }
            }
        }
    }

    private fun closePage() {
        acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebugDeviceContent(onClose: () -> Unit) {
    val placeholderText = stringResource(DebugImplMR.strings.debug_device_result_placeholder)
    var result by remember { mutableStateOf(placeholderText) }
    val deviceInfo = remember { DeviceInfo() }

    val pageTitle = stringResource(DebugImplMR.strings.debug_device_title)
    val btnClose = stringResource(DebugImplMR.strings.debug_close)
    val labelDeviceInfo = stringResource(DebugImplMR.strings.debug_device_label_device_info)
    val btnRefresh = stringResource(DebugImplMR.strings.debug_device_btn_refresh)
    val resultFormat = stringResource(DebugImplMR.strings.debug_device_result_format)

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
                DeviceInfoSection(
                    deviceInfo = deviceInfo,
                    labelDeviceInfo = labelDeviceInfo,
                    btnRefresh = btnRefresh,
                    resultFormat = resultFormat,
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
    labelDeviceInfo: String,
    btnRefresh: String,
    resultFormat: String,
    onResultChange: (String) -> Unit
) {
    Text(labelDeviceInfo, fontSize = 16.sp, color = Color(0xFF333333))
    DebugVSpacer(8.dp)
    DebugTestButton(btnRefresh) {
        val screen = provideScreenInfo()
        val density = screen.density
        val widthDp = (screen.widthPx / density).toInt()
        val heightDp = (screen.heightPx / density).toInt()
        onResultChange(String.format(
            resultFormat,
            deviceInfo.getDeviceId(),
            deviceInfo.getOSVersion(),
            deviceInfo.getDeviceModel(),
            widthDp,
            screen.widthPx,
            heightDp,
            screen.heightPx,
            screen.densityDpi,
            screen.density
        ))
    }
}
