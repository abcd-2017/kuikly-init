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
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

private const val PAGE_TITLE = "Toast / Dialog 测试"
private const val BTN_CLOSE = "关闭"
private const val RESULT_PLACEHOLDER = "操作结果将在此显示"
private const val BTN_TOAST_SHORT = "Toast SHORT"
private const val BTN_TOAST_LONG = "Toast LONG"
private const val MSG_TOAST_SHORT = "这是一条 SHORT Toast"
private const val RESULT_TOAST_SHORT = "Toast SHORT 已触发"
private const val MSG_TOAST_LONG = "这是一条 LONG Toast"
private const val RESULT_TOAST_LONG = "Toast LONG 已触发"
private const val BTN_ALERT = "Alert 提示框（单按钮）"
private const val TITLE_ALERT = "提示"
private const val MSG_ALERT = "这是一个 Alert 提示框"
private const val BTN_TEXT_ALERT = "我知道了"
private const val RESULT_ALERT = "Alert 已弹出"
private const val BTN_CONFIRM = "Confirm 确认框（双按钮）"
private const val TITLE_CONFIRM = "确认"
private const val MSG_CONFIRM = "确定要执行此操作吗？"
private const val BTN_TEXT_CONFIRM_OK = "确定"
private const val BTN_TEXT_CONFIRM_CANCEL = "取消"
private const val RESULT_CONFIRM_CONFIRMED = "确认"
private const val RESULT_CONFIRM_CANCELLED = "取消"
private const val BTN_ACTION_SHEET = "Action Sheet（多选项）"
private const val TITLE_ACTION_SHEET = "请选择"
private const val MSG_ACTION_SHEET = "请从下列选项中选择一个"
private const val OPTION_A = "选项 A"
private const val OPTION_B = "选项 B"
private const val OPTION_C = "选项 C"
private const val OPTION_D = "选项 D"
private const val RESULT_ACTION_SHEET_CANCEL = "Action Sheet 已取消"
private const val BTN_QUEUE_ALERT = "连续弹两个 Alert（测试队列）"
private const val TITLE_FIRST = "第一个"
private const val MSG_FIRST = "这是第一个 Alert"
private const val TITLE_SECOND = "第二个"
private const val MSG_SECOND = "这是第二个 Alert"
private const val BTN_TEXT_OK = "确定"
private const val RESULT_QUEUE_ALERT = "已触发两个 Alert（队列模式）"

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
    var result by remember { mutableStateOf(RESULT_PLACEHOLDER) }
    val toast = remember { provideToast() }
    val dialog = remember { provideDialog() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(PAGE_TITLE) },
                actions = {
                    Text(
                        text = BTN_CLOSE,
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
                ToastSection(toast, result) { result = it }
            }
            item {
                DialogSection(dialog) { result = it }
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
    result: String,
    onResultChange: (String) -> Unit
) {
    DebugTestButton(BTN_TOAST_SHORT) {
        toast.show(MSG_TOAST_SHORT, ToastDuration.SHORT)
        onResultChange(RESULT_TOAST_SHORT)
    }
    DebugTestButton(BTN_TOAST_LONG) {
        toast.show(MSG_TOAST_LONG, ToastDuration.LONG)
        onResultChange(RESULT_TOAST_LONG)
    }
}

@Composable
private fun DialogSection(
    dialog: Dialog,
    onResultChange: (String) -> Unit
) {
    DebugTestButton(BTN_ALERT) {
        dialog.showAlert(TITLE_ALERT, MSG_ALERT, BTN_TEXT_ALERT)
        onResultChange(RESULT_ALERT)
    }
    DebugTestButton(BTN_CONFIRM) {
        dialog.showConfirm(
            title = TITLE_CONFIRM,
            message = MSG_CONFIRM,
            confirmText = BTN_TEXT_CONFIRM_OK,
            cancelText = BTN_TEXT_CONFIRM_CANCEL
        ) { picked ->
            onResultChange("Confirm 结果：${if (picked == 0) RESULT_CONFIRM_CONFIRMED else RESULT_CONFIRM_CANCELLED}")
        }
    }
    DebugTestButton(BTN_ACTION_SHEET) {
        dialog.showActionSheet(
            title = TITLE_ACTION_SHEET,
            message = MSG_ACTION_SHEET,
            options = listOf(OPTION_A, OPTION_B, OPTION_C, OPTION_D)
        ) { index ->
            onResultChange(
                if (index >= 0) {
                    "Action Sheet 选中：选项 ${('A' + index)} (index=$index)"
                } else {
                    RESULT_ACTION_SHEET_CANCEL
                }
            )
        }
    }
    DebugTestButton(BTN_QUEUE_ALERT) {
        dialog.showAlert(TITLE_FIRST, MSG_FIRST, BTN_TEXT_OK)
        dialog.showAlert(TITLE_SECOND, MSG_SECOND, BTN_TEXT_OK)
        onResultChange(RESULT_QUEUE_ALERT)
    }
}
