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
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.clipboard.provideClipboard
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_clipboard")
public class DebugClipboardPage : BasePager() {

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
    val placeholderText = stringResource(DebugImplMR.strings.debug_clipboard_result_placeholder)
    var result by remember { mutableStateOf(placeholderText) }
    val defaultInput = stringResource(DebugImplMR.strings.debug_clipboard_input_default)
    var inputText by remember { mutableStateOf(defaultInput) }
    val clipboard = remember { provideClipboard() }

    val pageTitle = stringResource(DebugImplMR.strings.debug_clipboard_title)
    val btnClose = stringResource(DebugImplMR.strings.debug_close)
    val btnCopyPreset = stringResource(DebugImplMR.strings.debug_clipboard_btn_copy_preset)
    val btnCopyTimestamp = stringResource(DebugImplMR.strings.debug_clipboard_btn_copy_timestamp)
    val btnPaste = stringResource(DebugImplMR.strings.debug_clipboard_btn_paste)
    val btnClear = stringResource(DebugImplMR.strings.debug_clipboard_btn_clear)
    val btnCheck = stringResource(DebugImplMR.strings.debug_clipboard_btn_check)
    val presetText = stringResource(DebugImplMR.strings.debug_clipboard_preset_text)
    val resultPresetCopied = stringResource(DebugImplMR.strings.debug_clipboard_result_preset_copied)
    val resultClipboardEmpty = stringResource(DebugImplMR.strings.debug_clipboard_result_clipboard_empty)
    val resultClipboardCleared = stringResource(DebugImplMR.strings.debug_clipboard_result_clipboard_cleared)
    val labelCustomInput = stringResource(DebugImplMR.strings.debug_clipboard_label_custom_input)
    val placeholderCustomInput = stringResource(DebugImplMR.strings.debug_clipboard_placeholder_custom_input)
    val btnCopy = stringResource(DebugImplMR.strings.debug_clipboard_btn_copy)
    val btnVerify = stringResource(DebugImplMR.strings.debug_clipboard_btn_verify)
    val resultCopyTimestamp = stringResource(DebugImplMR.strings.debug_clipboard_result_copy_timestamp)
    val resultPasteContent = stringResource(DebugImplMR.strings.debug_clipboard_result_paste_content)
    val resultHasText = stringResource(DebugImplMR.strings.debug_clipboard_result_has_text)
    val resultCopyCustom = stringResource(DebugImplMR.strings.debug_clipboard_result_copy_custom)
    val resultVerify = stringResource(DebugImplMR.strings.debug_clipboard_result_verify)

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
                ClipboardPresetSection(
                    clipboard = clipboard,
                    btnCopyPreset = btnCopyPreset,
                    btnCopyTimestamp = btnCopyTimestamp,
                    btnPaste = btnPaste,
                    btnClear = btnClear,
                    btnCheck = btnCheck,
                    presetText = presetText,
                    resultPresetCopied = resultPresetCopied,
                    resultClipboardEmpty = resultClipboardEmpty,
                    resultClipboardCleared = resultClipboardCleared,
                    resultCopyTimestamp = resultCopyTimestamp,
                    resultPasteContent = resultPasteContent,
                    resultHasText = resultHasText,
                    onResultChange = { result = it }
                )
            }
            item {
                ClipboardCustomInputSection(
                    inputText = inputText,
                    onInputChange = { inputText = it },
                    clipboard = clipboard,
                    labelCustomInput = labelCustomInput,
                    placeholderCustomInput = placeholderCustomInput,
                    btnCopy = btnCopy,
                    btnVerify = btnVerify,
                    resultCopyCustom = resultCopyCustom,
                    resultVerify = resultVerify,
                    resultClipboardEmpty = resultClipboardEmpty,
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
    btnCopyPreset: String,
    btnCopyTimestamp: String,
    btnPaste: String,
    btnClear: String,
    btnCheck: String,
    presetText: String,
    resultPresetCopied: String,
    resultClipboardEmpty: String,
    resultClipboardCleared: String,
    resultCopyTimestamp: String,
    resultPasteContent: String,
    resultHasText: String,
    onResultChange: (String) -> Unit
) {
    DebugTestButton(btnCopyPreset) {
        clipboard.copyText(presetText)
        onResultChange(resultPresetCopied)
    }
    DebugTestButton(btnCopyTimestamp) {
        val ts = System.currentTimeMillis().toString()
        clipboard.copyText(ts)
        onResultChange(String.format(resultCopyTimestamp, ts))
    }
    DebugTestButton(btnPaste) {
        val content = clipboard.pasteText()
        onResultChange(if (content.isEmpty()) resultClipboardEmpty else String.format(resultPasteContent, content))
    }
    DebugTestButton(btnClear) {
        clipboard.clear()
        onResultChange(resultClipboardCleared)
    }
    DebugTestButton(btnCheck) {
        val has = clipboard.hasText()
        onResultChange(String.format(resultHasText, has))
    }
}

@Composable
private fun ClipboardCustomInputSection(
    inputText: String,
    onInputChange: (String) -> Unit,
    clipboard: com.kuikly.init.common.base.platform.clipboard.Clipboard,
    labelCustomInput: String,
    placeholderCustomInput: String,
    btnCopy: String,
    btnVerify: String,
    resultCopyCustom: String,
    resultVerify: String,
    resultClipboardEmpty: String,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(8.dp)
    Text(labelCustomInput, fontSize = 14.sp, color = Color(0xFF666666))
    DebugVSpacer(4.dp)
    DebugTextField(
        value = inputText,
        placeholder = placeholderCustomInput,
        onValueChange = onInputChange
    )
    DebugVSpacer(8.dp)
    Row {
        DebugTestButton(btnCopy) {
            clipboard.copyText(inputText)
            onResultChange(String.format(resultCopyCustom, inputText))
        }
        Spacer(modifier = Modifier.width(8.dp))
        DebugTestButton(btnVerify) {
            val content = clipboard.pasteText()
            val match = content == inputText
            onResultChange(String.format(resultVerify, content, match))
        }
    }
}
