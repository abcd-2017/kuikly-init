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
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import kotlinx.coroutines.launch

@Page("debug_file_picker")
public class DebugFilePickerPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("文件选择") },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { padding ->
                FilePickerTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
        }
    }
}

@Composable
private fun FilePickerTestContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    var result by remember { mutableStateOf("选择的文件信息将在此显示…") }
    val scope = rememberCoroutineScope()
    val filePicker = remember { provideFilePicker() }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            FilePickerActionSection(
                scope = scope,
                filePicker = filePicker,
                onResultChange = { result = it }
            )
        }
        item {
            FilePickerResultSection(result = result)
        }
        item {
            FilePickerCloseButton(onClose = onClose)
        }
    }
}

@Composable
private fun FilePickerActionSection(
    scope: kotlinx.coroutines.CoroutineScope,
    filePicker: com.kuikly.init.common.base.platform.picker.FilePicker,
    onResultChange: (String) -> Unit
) {
    HardwareActionButtonPrimary("pickFile 选择任意文件") {
        scope.launch {
            try {
                val files = filePicker.pickFile()
                onResultChange(
                    if (files.isNotEmpty()) {
                        String.format("选择文件成功:\n%1\$s", fileInfo(files.first()))
                    } else {
                        "取消选择"
                    }
                )
            } catch (e: Exception) {
                onResultChange(String.format("选择文件异常: %1\$s", e.message))
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary("pickImage 选择图片") {
        scope.launch {
            try {
                val files = filePicker.pickImage()
                onResultChange(
                    if (files.isNotEmpty()) {
                        String.format("选择图片成功:\n%1\$s", fileInfo(files.first()))
                    } else {
                        "取消选择"
                    }
                )
            } catch (e: Exception) {
                onResultChange(String.format("选择图片异常: %1\$s", e.message))
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary("pickDocument 选择文档") {
        scope.launch {
            try {
                val files = filePicker.pickDocument()
                onResultChange(
                    if (files.isNotEmpty()) {
                        String.format("选择文档成功:\n%1\$s", fileInfo(files.first()))
                    } else {
                        "取消选择"
                    }
                )
            } catch (e: Exception) {
                onResultChange(String.format("选择文档异常: %1\$s", e.message))
            }
        }
    }
}

@Composable
private fun FilePickerResultSection(result: String) {
    Spacer(Modifier.height(16.dp))
    Text("文件信息", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
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
private fun FilePickerCloseButton(onClose: () -> Unit) {
    Spacer(Modifier.height(16.dp))
    HardwareActionButtonSecondary("关闭页面", onClick = onClose)
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

private fun fileInfo(f: PickedFile): String {
    return String.format(
        "name=%1\$s\npath=%2\$s\nsize=%3\$sB\nmime=%4\$s",
        f.name,
        f.path,
        f.size,
        f.mimeType ?: "(未知)"
    )
}
