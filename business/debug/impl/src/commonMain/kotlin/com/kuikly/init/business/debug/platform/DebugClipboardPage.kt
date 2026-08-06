package com.kuikly.init.business.debug.platform

import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
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
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.clipboard.provideClipboard
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_clipboard")
internal class DebugClipboardPage : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {
            DebugClipboardContent { closePage() }
        }
    }

    private fun closePage() {
        acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebugClipboardContent(onClose: () -> Unit) {
    var result by remember { mutableStateOf("操作结果将在此显示") }
    var inputText by remember { mutableStateOf("自定义测试文本") }
    val clipboard = remember { provideClipboard() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Clipboard 测试") },
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
                ClipboardPresetSection(clipboard) { result = it }
            }
            item {
                ClipboardCustomInputSection(
                    inputText = inputText,
                    onInputChange = { inputText = it },
                    clipboard = clipboard,
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
private fun ClipboardPresetSection(
    clipboard: com.kuikly.init.common.base.platform.clipboard.Clipboard,
    onResultChange: (String) -> Unit
) {
    DebugTestButton("复制预设文本到剪贴板") {
        clipboard.copyText("Kuikly Clipboard Test")
        onResultChange("已复制预设文本：Kuikly Clipboard Test")
    }
    DebugTestButton("复制当前时间戳到剪贴板") {
        val ts = System.currentTimeMillis().toString()
        clipboard.copyText(ts)
        onResultChange("已复制时间戳：$ts")
    }
    DebugTestButton("读取剪贴板内容") {
        val content = clipboard.pasteText()
        onResultChange(if (content.isEmpty()) "剪贴板为空" else "剪贴板内容：$content")
    }
    DebugTestButton("清空剪贴板") {
        clipboard.clear()
        onResultChange("剪贴板已清空")
    }
    DebugTestButton("检查剪贴板是否有内容") {
        val has = clipboard.hasText()
        onResultChange("剪贴板是否有内容：$has")
    }
}

@Composable
private fun ClipboardCustomInputSection(
    inputText: String,
    onInputChange: (String) -> Unit,
    clipboard: com.kuikly.init.common.base.platform.clipboard.Clipboard,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(8.dp)
    Text("自定义文本输入：", fontSize = 14.sp, color = Color(0xFF666666))
    DebugVSpacer(4.dp)
    DebugTextField(
        value = inputText,
        placeholder = "输入要复制到剪贴板的文本",
        onValueChange = onInputChange
    )
    DebugVSpacer(8.dp)
    Row {
        DebugTestButton("复制") {
            clipboard.copyText(inputText)
            onResultChange("已复制自定义文本：$inputText")
        }
        Spacer(modifier = Modifier.width(8.dp))
        DebugTestButton("读取验证") {
            val content = clipboard.pasteText()
            val match = content == inputText
            onResultChange("读取结果：$content\n与输入一致：$match")
        }
    }
}
