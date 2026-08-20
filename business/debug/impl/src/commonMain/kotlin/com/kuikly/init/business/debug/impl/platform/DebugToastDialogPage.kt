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
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.dialog.Dialog
import com.kuikly.init.common.base.platform.dialog.provideDialog
import com.kuikly.init.common.base.platform.toast.ToastDuration
import com.kuikly.init.common.base.platform.toast.provideToast
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_toast_dialog")
public class DebugToastDialogPage : BasePager() {

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
    val placeholderText = "操作结果将在此显示"
    var result by remember { mutableStateOf(placeholderText) }
    val toast = remember { provideToast() }
    val dialog = remember { provideDialog() }

    val pageTitle = "Toast & 对话框"
    val btnClose = "关闭"
    val btnToastShort = "Toast SHORT"
    val btnToastLong = "Toast LONG"
    val msgToastShort = "这是一条 SHORT Toast"
    val resultToastShort = "Toast SHORT 已触发"
    val msgToastLong = "这是一条 LONG Toast"
    val resultToastLong = "Toast LONG 已触发"
    val btnAlert = "Alert 提示框（单按钮）"
    val titleAlert = "提示"
    val msgAlert = "这是一个 Alert 提示框"
    val btnTextAlert = "我知道了"
    val resultAlert = "Alert 已弹出"
    val btnConfirm = "Confirm 确认框（双按钮）"
    val titleConfirm = "确认"
    val msgConfirm = "确定要执行此操作吗？"
    val btnTextConfirmOk = "确定"
    val btnTextConfirmCancel = "取消"
    val resultConfirmConfirmed = "确认"
    val resultConfirmCancelled = "取消"
    val btnActionSheet = "Action Sheet（多选项）"
    val titleActionSheet = "请选择"
    val msgActionSheet = "请从下列选项中选择一个"
    val optionA = "选项 A"
    val optionB = "选项 B"
    val optionC = "选项 C"
    val optionD = "选项 D"
    val resultActionSheetCancel = "Action Sheet 已取消"
    val btnQueueAlert = "连续弹两个 Alert（测试队列）"
    val titleFirst = "第一个"
    val msgFirst = "这是第一个 Alert"
    val titleSecond = "第二个"
    val msgSecond = "这是第二个 Alert"
    val btnTextOk = "确定"
    val resultQueueAlert = "已触发两个 Alert（队列模式）"
    val resultConfirmFormat = "Confirm 结果：%1\$s"
    val resultActionSheetFormat = "Action Sheet 选中：选项 %1\$c (index=%2\$d)"

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
