package com.kuikly.init.business.debug.impl.hardware

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.font.FontFamily
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.picker.PickedFile
import com.kuikly.init.common.base.platform.picker.provideFilePicker
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import kotlinx.coroutines.launch

@Page("debug_file_picker")
public class DebugFilePickerPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            
            val pageTitle = stringResource(DebugImplMR.strings.debug_file_picker_title)
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(pageTitle) },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

                ) { padding ->
                FilePickerTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
            }
        }
    }
}

@Composable
private fun FilePickerTestContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    val resultPlaceholder = stringResource(DebugImplMR.strings.debug_file_picker_result_placeholder)
    var result by remember { mutableStateOf(resultPlaceholder) }
    val scope = rememberCoroutineScope()
    val filePicker = remember { provideFilePicker() }

    val btnPickFile = stringResource(DebugImplMR.strings.debug_file_picker_btn_pick_file)
    val btnPickImage = stringResource(DebugImplMR.strings.debug_file_picker_btn_pick_image)
    val btnPickDocument = stringResource(DebugImplMR.strings.debug_file_picker_btn_pick_document)
    val resultTitle = stringResource(DebugImplMR.strings.debug_file_picker_result_title)
    val resultPickFileSuccess = stringResource(DebugImplMR.strings.debug_file_picker_result_pick_file_success)
    val resultPickImageSuccess = stringResource(DebugImplMR.strings.debug_file_picker_result_pick_image_success)
    val resultPickDocumentSuccess = stringResource(DebugImplMR.strings.debug_file_picker_result_pick_document_success)
    val resultCancel = stringResource(DebugImplMR.strings.debug_file_picker_result_cancel)
    val resultPickFileException = stringResource(DebugImplMR.strings.debug_file_picker_result_pick_file_exception)
    val resultPickImageException = stringResource(DebugImplMR.strings.debug_file_picker_result_pick_image_exception)
    val resultPickDocumentException = stringResource(DebugImplMR.strings.debug_file_picker_result_pick_document_exception)
    val resultFormat = stringResource(DebugImplMR.strings.debug_file_picker_result_format)
    val btnClose = stringResource(DebugImplMR.strings.debug_close_page)

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            FilePickerActionSection(
                scope = scope,
                filePicker = filePicker,
                btnPickFile = btnPickFile,
                btnPickImage = btnPickImage,
                btnPickDocument = btnPickDocument,
                resultPickFileSuccess = resultPickFileSuccess,
                resultPickImageSuccess = resultPickImageSuccess,
                resultPickDocumentSuccess = resultPickDocumentSuccess,
                resultCancel = resultCancel,
                resultPickFileException = resultPickFileException,
                resultPickImageException = resultPickImageException,
                resultPickDocumentException = resultPickDocumentException,
                resultFormat = resultFormat,
                onResultChange = { result = it }
            )
        }
        item {
            FilePickerResultSection(result = result, resultTitle = resultTitle)
        }
        item {
            FilePickerCloseButton(btnText = btnClose, onClose = onClose)
        }
    }
}

@Composable
private fun FilePickerActionSection(
    scope: kotlinx.coroutines.CoroutineScope,
    filePicker: com.kuikly.init.common.base.platform.picker.FilePicker,
    btnPickFile: String,
    btnPickImage: String,
    btnPickDocument: String,
    resultPickFileSuccess: String,
    resultPickImageSuccess: String,
    resultPickDocumentSuccess: String,
    resultCancel: String,
    resultPickFileException: String,
    resultPickImageException: String,
    resultPickDocumentException: String,
    resultFormat: String,
    onResultChange: (String) -> Unit
) {
    HardwareActionButtonPrimary(btnPickFile) {
        scope.launch {
            try {
                val files = filePicker.pickFile()
                onResultChange(
                    if (files.isNotEmpty()) {
                        String.format(resultPickFileSuccess, fileInfo(files.first(), resultFormat))
                    } else {
                        resultCancel
                    }
                )
            } catch (e: Exception) {
                onResultChange(String.format(resultPickFileException, e.message))
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary(btnPickImage) {
        scope.launch {
            try {
                val files = filePicker.pickImage()
                onResultChange(
                    if (files.isNotEmpty()) {
                        String.format(resultPickImageSuccess, fileInfo(files.first(), resultFormat))
                    } else {
                        resultCancel
                    }
                )
            } catch (e: Exception) {
                onResultChange(String.format(resultPickImageException, e.message))
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary(btnPickDocument) {
        scope.launch {
            try {
                val files = filePicker.pickDocument()
                onResultChange(
                    if (files.isNotEmpty()) {
                        String.format(resultPickDocumentSuccess, fileInfo(files.first(), resultFormat))
                    } else {
                        resultCancel
                    }
                )
            } catch (e: Exception) {
                onResultChange(String.format(resultPickDocumentException, e.message))
            }
        }
    }
}

@Composable
private fun FilePickerResultSection(result: String, resultTitle: String) {
    Spacer(Modifier.height(16.dp))
    Text(resultTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .padding(8.dp)
    ) {
        Text(
            text = result,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF333333)
        )
    }
}

@Composable
private fun FilePickerCloseButton(btnText: String, onClose: () -> Unit) {
    Spacer(Modifier.height(16.dp))
    HardwareActionButtonSecondary(btnText, onClick = onClose)
    Spacer(Modifier.height(32.dp))
}

@Composable
private fun HardwareActionButtonPrimary(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = MaterialTheme.colorScheme.onPrimary, fontSize = 15.sp)
    }
}

@Composable
private fun HardwareActionButtonSecondary(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSecondary, fontSize = 15.sp)
    }
}

private fun fileInfo(f: PickedFile, format: String): String {
    return String.format(format, f.name, f.path, f.size, f.mimeType ?: "(未知)")
}
