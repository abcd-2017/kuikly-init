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
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.haptic.HapticNotification
import com.kuikly.init.common.base.platform.haptic.HapticStyle
import com.kuikly.init.common.base.platform.haptic.provideHaptic
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_haptic")
public class DebugHapticPage : BasePager() {

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
    val placeholderText = "点击按钮触发震动"
    var result by remember { mutableStateOf(placeholderText) }
    val haptic = remember { provideHaptic() }

    val pageTitle = "震动反馈"
    val btnClose = "关闭"
    val labelImpact = "冲击反馈（Impact）"
    val labelNotification = "通知反馈（Notification）"
    val btnImpactLight = "Impact Light"
    val btnImpactMedium = "Impact Medium"
    val btnImpactHeavy = "Impact Heavy"
    val btnNotificationSuccess = "Notification Success"
    val btnNotificationWarning = "Notification Warning"
    val btnNotificationFailure = "Notification Failure"
    val btnSelection = "Selection 震动"
    val btnStop = "停止震动"
    val btnContinuous = "连续震动测试"
    val resultImpactLight = "Impact Light 已触发"
    val resultImpactMedium = "Impact Medium 已触发"
    val resultImpactHeavy = "Impact Heavy 已触发"
    val resultNotificationSuccess = "Notification Success 已触发"
    val resultNotificationWarning = "Notification Warning 已触发"
    val resultNotificationFailure = "Notification Failure 已触发"
    val resultSelection = "Selection 震动已触发"
    val resultStop = "震动已停止"
    val resultContinuous = "连续震动测试已触发（Light -> Medium -> Heavy）"

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
                HapticImpactSection(
                    haptic = haptic,
                    labelImpact = labelImpact,
                    btnImpactLight = btnImpactLight,
                    btnImpactMedium = btnImpactMedium,
                    btnImpactHeavy = btnImpactHeavy,
                    resultImpactLight = resultImpactLight,
                    resultImpactMedium = resultImpactMedium,
                    resultImpactHeavy = resultImpactHeavy,
                    onResultChange = { result = it }
                )
            }
            item {
                HapticNotificationSection(
                    haptic = haptic,
                    labelNotification = labelNotification,
                    btnNotificationSuccess = btnNotificationSuccess,
                    btnNotificationWarning = btnNotificationWarning,
                    btnNotificationFailure = btnNotificationFailure,
                    resultNotificationSuccess = resultNotificationSuccess,
                    resultNotificationWarning = resultNotificationWarning,
                    resultNotificationFailure = resultNotificationFailure,
                    onResultChange = { result = it }
                )
            }
            item {
                HapticOtherSection(
                    haptic = haptic,
                    btnSelection = btnSelection,
                    btnStop = btnStop,
                    btnContinuous = btnContinuous,
                    resultSelection = resultSelection,
                    resultStop = resultStop,
                    resultContinuous = resultContinuous,
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
private fun HapticImpactSection(
    haptic: com.kuikly.init.common.base.platform.haptic.Haptic,
    labelImpact: String,
    btnImpactLight: String,
    btnImpactMedium: String,
    btnImpactHeavy: String,
    resultImpactLight: String,
    resultImpactMedium: String,
    resultImpactHeavy: String,
    onResultChange: (String) -> Unit
) {
    Text(labelImpact, fontSize = 16.sp, color = Color(0xFF333333))
    DebugVSpacer(4.dp)
    DebugTestButton(btnImpactLight) {
        haptic.impact(HapticStyle.LIGHT)
        onResultChange(resultImpactLight)
    }
    DebugTestButton(btnImpactMedium) {
        haptic.impact(HapticStyle.MEDIUM)
        onResultChange(resultImpactMedium)
    }
    DebugTestButton(btnImpactHeavy) {
        haptic.impact(HapticStyle.HEAVY)
        onResultChange(resultImpactHeavy)
    }
}

@Composable
private fun HapticNotificationSection(
    haptic: com.kuikly.init.common.base.platform.haptic.Haptic,
    labelNotification: String,
    btnNotificationSuccess: String,
    btnNotificationWarning: String,
    btnNotificationFailure: String,
    resultNotificationSuccess: String,
    resultNotificationWarning: String,
    resultNotificationFailure: String,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(12.dp)
    Text(labelNotification, fontSize = 16.sp, color = Color(0xFF333333))
    DebugVSpacer(4.dp)
    DebugTestButton(btnNotificationSuccess) {
        haptic.notification(HapticNotification.SUCCESS)
        onResultChange(resultNotificationSuccess)
    }
    DebugTestButton(btnNotificationWarning) {
        haptic.notification(HapticNotification.WARNING)
        onResultChange(resultNotificationWarning)
    }
    DebugTestButton(btnNotificationFailure) {
        haptic.notification(HapticNotification.FAILURE)
        onResultChange(resultNotificationFailure)
    }
}

@Composable
private fun HapticOtherSection(
    haptic: com.kuikly.init.common.base.platform.haptic.Haptic,
    btnSelection: String,
    btnStop: String,
    btnContinuous: String,
    resultSelection: String,
    resultStop: String,
    resultContinuous: String,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(12.dp)
    DebugTestButton(btnSelection) {
        haptic.selectionChanged()
        onResultChange(resultSelection)
    }
    DebugTestButton(btnStop) {
        haptic.stop()
        onResultChange(resultStop)
    }
    DebugTestButton(btnContinuous) {
        haptic.impact(HapticStyle.LIGHT)
        haptic.impact(HapticStyle.MEDIUM)
        haptic.impact(HapticStyle.HEAVY)
        onResultChange(resultContinuous)
    }
}
