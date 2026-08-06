package com.kuikly.init.business.debug.platform

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
import com.tencent.kuikly.compose.runtime.getValue
import com.tencent.kuikly.compose.runtime.mutableStateOf
import com.tencent.kuikly.compose.runtime.remember
import com.tencent.kuikly.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.haptic.HapticNotification
import com.kuikly.init.common.base.platform.haptic.HapticStyle
import com.kuikly.init.common.base.platform.haptic.provideHaptic
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_haptic")
internal class DebugHapticPage : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {
            DebugHapticContent { closePage() }
        }
    }

    private fun closePage() {
        acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebugHapticContent(onClose: () -> Unit) {
    var result by remember { mutableStateOf("点击按钮触发震动") }
    val haptic = remember { provideHaptic() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Haptic 测试") },
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
                HapticImpactSection(haptic) { result = it }
            }
            item {
                HapticNotificationSection(haptic) { result = it }
            }
            item {
                HapticOtherSection(haptic) { result = it }
            }
            item {
                DebugVSpacer(8.dp)
                DebugResultArea(result)
            }
        }
    }
}

@Composable
private fun HapticImpactSection(
    haptic: com.kuikly.init.common.base.platform.haptic.Haptic,
    onResultChange: (String) -> Unit
) {
    Text("冲击反馈（Impact）", fontSize = 16.sp, color = Color(0xFF333333))
    DebugVSpacer(4.dp)
    DebugTestButton("Impact Light") {
        haptic.impact(HapticStyle.LIGHT)
        onResultChange("Impact Light 已触发")
    }
    DebugTestButton("Impact Medium") {
        haptic.impact(HapticStyle.MEDIUM)
        onResultChange("Impact Medium 已触发")
    }
    DebugTestButton("Impact Heavy") {
        haptic.impact(HapticStyle.HEAVY)
        onResultChange("Impact Heavy 已触发")
    }
}

@Composable
private fun HapticNotificationSection(
    haptic: com.kuikly.init.common.base.platform.haptic.Haptic,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(12.dp)
    Text("通知反馈（Notification）", fontSize = 16.sp, color = Color(0xFF333333))
    DebugVSpacer(4.dp)
    DebugTestButton("Notification Success") {
        haptic.notification(HapticNotification.SUCCESS)
        onResultChange("Notification Success 已触发")
    }
    DebugTestButton("Notification Warning") {
        haptic.notification(HapticNotification.WARNING)
        onResultChange("Notification Warning 已触发")
    }
    DebugTestButton("Notification Failure") {
        haptic.notification(HapticNotification.FAILURE)
        onResultChange("Notification Failure 已触发")
    }
}

@Composable
private fun HapticOtherSection(
    haptic: com.kuikly.init.common.base.platform.haptic.Haptic,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(12.dp)
    DebugTestButton("Selection 震动") {
        haptic.selectionChanged()
        onResultChange("Selection 震动已触发")
    }
    DebugTestButton("停止震动") {
        haptic.stop()
        onResultChange("震动已停止")
    }
    DebugTestButton("连续震动测试") {
        haptic.impact(HapticStyle.LIGHT)
        haptic.impact(HapticStyle.MEDIUM)
        haptic.impact(HapticStyle.HEAVY)
        onResultChange("连续震动测试已触发（Light -> Medium -> Heavy）")
    }
}
