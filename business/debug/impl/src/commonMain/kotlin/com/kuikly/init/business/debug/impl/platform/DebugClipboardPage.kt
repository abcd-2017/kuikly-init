package com.kuikly.init.business.debug.impl.platform

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.clipboard.provideClipboard
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

private const val PAGE_TITLE = "Clipboard 测试"
private const val BTN_CLOSE = "关闭"
private const val RESULT_PLACEHOLDER = "操作结果将在此显示"
private const val INPUT_DEFAULT = "自定义测试文本"
private const val BTN_COPY_PRESET = "复制预设文本到剪贴板"
private const val BTN_COPY_TIMESTAMP = "复制当前时间戳到剪贴板"
private const val BTN_PASTE = "读取剪贴板内容"
private const val BTN_CLEAR = "清空剪贴板"
private const val BTN_CHECK = "检查剪贴板是否有内容"
private const val PRESET_TEXT = "Kuikly Clipboard Test"
private const val RESULT_PRESET_COPIED = "已复制预设文本：Kuikly Clipboard Test"
private const val RESULT_CLIPBOARD_EMPTY = "剪贴板为空"
private const val RESULT_CLIPBOARD_CLEARED = "剪贴板已清空"
private const val LABEL_CUSTOM_INPUT = "自定义文本输入："
private const val PLACEHOLDER_CUSTOM_INPUT = "输入要复制到剪贴板的文本"
private const val BTN_COPY = "复制"
private const val BTN_VERIFY = "读取验证"

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
    var result by remember { mutableStateOf(RESULT_PLACEHOLDER) }
    var inputText by remember { mutableStateOf(INPUT_DEFAULT) }
    val clipboard = remember { provideClipboard() }

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
    DebugTestButton(BTN_COPY_PRESET) {
        clipboard.copyText(PRESET_TEXT)
        onResultChange(RESULT_PRESET_COPIED)
    }
    DebugTestButton(BTN_COPY_TIMESTAMP) {
        val ts = System.currentTimeMillis().toString()
        clipboard.copyText(ts)
        onResultChange("已复制时间戳：$ts")
    }
    DebugTestButton(BTN_PASTE) {
        val content = clipboard.pasteText()
        onResultChange(if (content.isEmpty()) RESULT_CLIPBOARD_EMPTY else "剪贴板内容：$content")
    }
    DebugTestButton(BTN_CLEAR) {
        clipboard.clear()
        onResultChange(RESULT_CLIPBOARD_CLEARED)
    }
    DebugTestButton(BTN_CHECK) {
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
    Text(LABEL_CUSTOM_INPUT, fontSize = 14.sp, color = Color(0xFF666666))
    DebugVSpacer(4.dp)
    DebugTextField(
        value = inputText,
        placeholder = PLACEHOLDER_CUSTOM_INPUT,
        onValueChange = onInputChange
    )
    DebugVSpacer(8.dp)
    Row {
        DebugTestButton(BTN_COPY) {
            clipboard.copyText(inputText)
            onResultChange("已复制自定义文本：$inputText")
        }
        Spacer(modifier = Modifier.width(8.dp))
        DebugTestButton(BTN_VERIFY) {
            val content = clipboard.pasteText()
            val match = content == inputText
            onResultChange("读取结果：$content\n与输入一致：$match")
        }
    }
}
