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
    var result by remember { mutableStateOf("操作结果将在此显示") }
    val toast = remember { provideToast() }
    val dialog = remember { provideDialog() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Toast & 对话框") },
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
                ToastSection(
                    toast = toast,
                    onResultChange = { result = it }
                )
            }
            item {
                DialogSection(
                    dialog = dialog,
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
    onResultChange: (String) -> Unit
) {
    DebugTestButton("Toast SHORT") {
        toast.show("这是一条 SHORT Toast", ToastDuration.SHORT)
        onResultChange("Toast SHORT 已触发")
    }
    DebugTestButton("Toast LONG") {
        toast.show("这是一条 LONG Toast", ToastDuration.LONG)
        onResultChange("Toast LONG 已触发")
    }
}

@Composable
private fun DialogSection(
    dialog: Dialog,
    onResultChange: (String) -> Unit
) {
    DebugTestButton("Alert 提示框（单按钮）") {
        dialog.showAlert("提示", "这是一个 Alert 提示框", "我知道了")
        onResultChange("Alert 已弹出")
    }
    DebugTestButton("Confirm 确认框（双按钮）") {
        dialog.showConfirm(
            title = "确认",
            message = "确定要执行此操作吗？",
            confirmText = "确定",
            cancelText = "取消"
        ) { picked ->
            onResultChange(String.format("Confirm 结果：%1\$s", if (picked == 0) "确认" else "取消"))
        }
    }
    DebugTestButton("Action Sheet（多选项）") {
        dialog.showActionSheet(
            title = "请选择",
            message = "请从下列选项中选择一个",
            options = listOf("选项 A", "选项 B", "选项 C", "选项 D")
        ) { index ->
            onResultChange(
                if (index >= 0) {
                    String.format("Action Sheet 选中：选项 %1\$c (index=%2\$d)", ('A' + index), index)
                } else {
                    "Action Sheet 已取消"
                }
            )
        }
    }
    DebugTestButton("连续弹两个 Alert（测试队列）") {
        dialog.showAlert("第一个", "这是第一个 Alert", "确定")
        dialog.showAlert("第二个", "这是第二个 Alert", "确定")
        onResultChange("已触发两个 Alert（队列模式）")
    }
}
