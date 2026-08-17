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
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
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
    val placeholderText = stringResource(DebugImplMR.strings.debug_haptic_result_placeholder)
    var result by remember { mutableStateOf(placeholderText) }
    val haptic = remember { provideHaptic() }

    val pageTitle = stringResource(DebugImplMR.strings.debug_haptic_title)
    val btnClose = stringResource(DebugImplMR.strings.debug_close)
    val labelImpact = stringResource(DebugImplMR.strings.debug_haptic_label_impact)
    val labelNotification = stringResource(DebugImplMR.strings.debug_haptic_label_notification)
    val btnImpactLight = stringResource(DebugImplMR.strings.debug_haptic_btn_impact_light)
    val btnImpactMedium = stringResource(DebugImplMR.strings.debug_haptic_btn_impact_medium)
    val btnImpactHeavy = stringResource(DebugImplMR.strings.debug_haptic_btn_impact_heavy)
    val btnNotificationSuccess = stringResource(DebugImplMR.strings.debug_haptic_btn_notification_success)
    val btnNotificationWarning = stringResource(DebugImplMR.strings.debug_haptic_btn_notification_warning)
    val btnNotificationFailure = stringResource(DebugImplMR.strings.debug_haptic_btn_notification_failure)
    val btnSelection = stringResource(DebugImplMR.strings.debug_haptic_btn_selection)
    val btnStop = stringResource(DebugImplMR.strings.debug_haptic_btn_stop)
    val btnContinuous = stringResource(DebugImplMR.strings.debug_haptic_btn_continuous)
    val resultImpactLight = stringResource(DebugImplMR.strings.debug_haptic_result_impact_light)
    val resultImpactMedium = stringResource(DebugImplMR.strings.debug_haptic_result_impact_medium)
    val resultImpactHeavy = stringResource(DebugImplMR.strings.debug_haptic_result_impact_heavy)
    val resultNotificationSuccess = stringResource(DebugImplMR.strings.debug_haptic_result_notification_success)
    val resultNotificationWarning = stringResource(DebugImplMR.strings.debug_haptic_result_notification_warning)
    val resultNotificationFailure = stringResource(DebugImplMR.strings.debug_haptic_result_notification_failure)
    val resultSelection = stringResource(DebugImplMR.strings.debug_haptic_result_selection)
    val resultStop = stringResource(DebugImplMR.strings.debug_haptic_result_stop)
    val resultContinuous = stringResource(DebugImplMR.strings.debug_haptic_result_continuous)

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
