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
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.dialog.Dialog
import com.kuikly.init.common.base.platform.dialog.provideDialog
import com.kuikly.init.common.base.platform.toast.ToastDuration
import com.kuikly.init.common.base.platform.toast.provideToast
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_toast_dialog")
internal class DebugToastDialogPage : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {
            DebugToastDialogContent { closePage() }
        }
    }

    private fun closePage() {
        acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebugToastDialogContent(onClose: () -> Unit) {
    val placeholderText = stringResource(DebugImplMR.strings.debug_toast_dialog_result_placeholder)
    var result by remember { mutableStateOf(placeholderText) }
    val toast = remember { provideToast() }
    val dialog = remember { provideDialog() }

    val pageTitle = stringResource(DebugImplMR.strings.debug_toast_dialog_title)
    val btnClose = stringResource(DebugImplMR.strings.debug_close)
    val btnToastShort = stringResource(DebugImplMR.strings.debug_toast_dialog_btn_toast_short)
    val btnToastLong = stringResource(DebugImplMR.strings.debug_toast_dialog_btn_toast_long)
    val msgToastShort = stringResource(DebugImplMR.strings.debug_toast_dialog_msg_toast_short)
    val resultToastShort = stringResource(DebugImplMR.strings.debug_toast_dialog_result_toast_short)
    val msgToastLong = stringResource(DebugImplMR.strings.debug_toast_dialog_msg_toast_long)
    val resultToastLong = stringResource(DebugImplMR.strings.debug_toast_dialog_result_toast_long)
    val btnAlert = stringResource(DebugImplMR.strings.debug_toast_dialog_btn_alert)
    val titleAlert = stringResource(DebugImplMR.strings.debug_toast_dialog_title_alert)
    val msgAlert = stringResource(DebugImplMR.strings.debug_toast_dialog_msg_alert)
    val btnTextAlert = stringResource(DebugImplMR.strings.debug_toast_dialog_btn_text_alert)
    val resultAlert = stringResource(DebugImplMR.strings.debug_toast_dialog_result_alert)
    val btnConfirm = stringResource(DebugImplMR.strings.debug_toast_dialog_btn_confirm)
    val titleConfirm = stringResource(DebugImplMR.strings.debug_toast_dialog_title_confirm)
    val msgConfirm = stringResource(DebugImplMR.strings.debug_toast_dialog_msg_confirm)
    val btnTextConfirmOk = stringResource(DebugImplMR.strings.debug_toast_dialog_btn_text_confirm_ok)
    val btnTextConfirmCancel = stringResource(DebugImplMR.strings.debug_toast_dialog_btn_text_confirm_cancel)
    val resultConfirmConfirmed = stringResource(DebugImplMR.strings.debug_toast_dialog_result_confirm_confirmed)
    val resultConfirmCancelled = stringResource(DebugImplMR.strings.debug_toast_dialog_result_confirm_cancelled)
    val btnActionSheet = stringResource(DebugImplMR.strings.debug_toast_dialog_btn_action_sheet)
    val titleActionSheet = stringResource(DebugImplMR.strings.debug_toast_dialog_title_action_sheet)
    val msgActionSheet = stringResource(DebugImplMR.strings.debug_toast_dialog_msg_action_sheet)
    val optionA = stringResource(DebugImplMR.strings.debug_toast_dialog_option_a)
    val optionB = stringResource(DebugImplMR.strings.debug_toast_dialog_option_b)
    val optionC = stringResource(DebugImplMR.strings.debug_toast_dialog_option_c)
    val optionD = stringResource(DebugImplMR.strings.debug_toast_dialog_option_d)
    val resultActionSheetCancel = stringResource(DebugImplMR.strings.debug_toast_dialog_result_action_sheet_cancel)
    val btnQueueAlert = stringResource(DebugImplMR.strings.debug_toast_dialog_btn_queue_alert)
    val titleFirst = stringResource(DebugImplMR.strings.debug_toast_dialog_title_first)
    val msgFirst = stringResource(DebugImplMR.strings.debug_toast_dialog_msg_first)
    val titleSecond = stringResource(DebugImplMR.strings.debug_toast_dialog_title_second)
    val msgSecond = stringResource(DebugImplMR.strings.debug_toast_dialog_msg_second)
    val btnTextOk = stringResource(DebugImplMR.strings.debug_toast_dialog_btn_text_ok)
    val resultQueueAlert = stringResource(DebugImplMR.strings.debug_toast_dialog_result_queue_alert)
    val resultConfirmFormat = stringResource(DebugImplMR.strings.debug_toast_dialog_result_confirm_format)
    val resultActionSheetFormat = stringResource(DebugImplMR.strings.debug_toast_dialog_result_action_sheet_format)

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
                ToastSection(
                    toast = toast,
                    btnToastShort = btnToastShort,
                    btnToastLong = btnToastLong,
                    msgToastShort = msgToastShort,
                    msgToastLong = msgToastLong,
                    resultToastShort = resultToastShort,
                    resultToastLong = resultToastLong,
                    onResultChange = { result = it }
                )
            }
            item {
                DialogSection(
                    dialog = dialog,
                    btnAlert = btnAlert,
                    titleAlert = titleAlert,
                    msgAlert = msgAlert,
                    btnTextAlert = btnTextAlert,
                    resultAlert = resultAlert,
                    btnConfirm = btnConfirm,
                    titleConfirm = titleConfirm,
                    msgConfirm = msgConfirm,
                    btnTextConfirmOk = btnTextConfirmOk,
                    btnTextConfirmCancel = btnTextConfirmCancel,
                    resultConfirmConfirmed = resultConfirmConfirmed,
                    resultConfirmCancelled = resultConfirmCancelled,
                    resultConfirmFormat = resultConfirmFormat,
                    btnActionSheet = btnActionSheet,
                    titleActionSheet = titleActionSheet,
                    msgActionSheet = msgActionSheet,
                    optionA = optionA,
                    optionB = optionB,
                    optionC = optionC,
                    optionD = optionD,
                    resultActionSheetCancel = resultActionSheetCancel,
                    resultActionSheetFormat = resultActionSheetFormat,
                    btnQueueAlert = btnQueueAlert,
                    titleFirst = titleFirst,
                    msgFirst = msgFirst,
                    titleSecond = titleSecond,
                    msgSecond = msgSecond,
                    btnTextOk = btnTextOk,
                    resultQueueAlert = resultQueueAlert,
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
private fun ToastSection(
    toast: com.kuikly.init.common.base.platform.toast.Toast,
    btnToastShort: String,
    btnToastLong: String,
    msgToastShort: String,
    msgToastLong: String,
    resultToastShort: String,
    resultToastLong: String,
    onResultChange: (String) -> Unit
) {
    DebugTestButton(btnToastShort) {
        toast.show(msgToastShort, ToastDuration.SHORT)
        onResultChange(resultToastShort)
    }
    DebugTestButton(btnToastLong) {
        toast.show(msgToastLong, ToastDuration.LONG)
        onResultChange(resultToastLong)
    }
}

@Composable
private fun DialogSection(
    dialog: Dialog,
    btnAlert: String,
    titleAlert: String,
    msgAlert: String,
    btnTextAlert: String,
    resultAlert: String,
    btnConfirm: String,
    titleConfirm: String,
    msgConfirm: String,
    btnTextConfirmOk: String,
    btnTextConfirmCancel: String,
    resultConfirmConfirmed: String,
    resultConfirmCancelled: String,
    resultConfirmFormat: String,
    btnActionSheet: String,
    titleActionSheet: String,
    msgActionSheet: String,
    optionA: String,
    optionB: String,
    optionC: String,
    optionD: String,
    resultActionSheetCancel: String,
    resultActionSheetFormat: String,
    btnQueueAlert: String,
    titleFirst: String,
    msgFirst: String,
    titleSecond: String,
    msgSecond: String,
    btnTextOk: String,
    resultQueueAlert: String,
    onResultChange: (String) -> Unit
) {
    DebugTestButton(btnAlert) {
        dialog.showAlert(titleAlert, msgAlert, btnTextAlert)
        onResultChange(resultAlert)
    }
    DebugTestButton(btnConfirm) {
        dialog.showConfirm(
            title = titleConfirm,
            message = msgConfirm,
            confirmText = btnTextConfirmOk,
            cancelText = btnTextConfirmCancel
        ) { picked ->
            onResultChange(String.format(resultConfirmFormat, if (picked == 0) resultConfirmConfirmed else resultConfirmCancelled))
        }
    }
    DebugTestButton(btnActionSheet) {
        dialog.showActionSheet(
            title = titleActionSheet,
            message = msgActionSheet,
            options = listOf(optionA, optionB, optionC, optionD)
        ) { index ->
            onResultChange(
                if (index >= 0) {
                    String.format(resultActionSheetFormat, ('A' + index), index)
                } else {
                    resultActionSheetCancel
                }
            )
        }
    }
    DebugTestButton(btnQueueAlert) {
        dialog.showAlert(titleFirst, msgFirst, btnTextOk)
        dialog.showAlert(titleSecond, msgSecond, btnTextOk)
        onResultChange(resultQueueAlert)
    }
}
