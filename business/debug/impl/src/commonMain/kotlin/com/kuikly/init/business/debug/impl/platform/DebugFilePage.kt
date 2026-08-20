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
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.FileSystem
import com.kuikly.init.common.base.platform.provideFileSystem
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_file")
public class DebugFilePage : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {
            
            DebugFileContent { closePage() }

        }
    }

    private fun closePage() {
        acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebugFileContent(onClose: () -> Unit) {
    val placeholderText = "操作结果将在此显示"
    var result by remember { mutableStateOf(placeholderText) }
    val defaultFileName = "test.txt"
    val defaultFileContent = "Hello Kuikly FileSystem"
    var fileName by remember { mutableStateOf(defaultFileName) }
    var fileContent by remember { mutableStateOf(defaultFileContent) }
    val fileSystem = remember { provideFileSystem() }

    val pageTitle = "文件读写"
    val btnClose = "关闭"
    val labelFileName = "文件名："
    val labelFileContent = "文件内容："
    val labelLargeFile = "大文件写入测试："
    val placeholderFileName = "输入文件名"
    val placeholderFileContent = "输入文件内容"
    val btnWriteFile = "写入文件"
    val btnReadFile = "读取文件"
    val btnCheckExists = "检查文件是否存在"
    val btnDeleteFile = "删除文件"
    val btnGetPath = "获取文件绝对路径"
    val btnWrite1kb = "写入 1KB 文件"
    val btnWrite10kb = "写入 10KB 文件"
    val btnWrite100kb = "写入 100KB 文件"
    val resultWriteSuccess = "写入成功：%1\$s\n内容：%2\$s"
    val resultWriteFail = "写入失败：%1\$s"
    val resultFileNotExist = "文件不存在：%1\$s"
    val resultReadSuccess = "读取成功：%1\$s\n内容：%2\$s"
    val resultReadFail = "读取失败：%1\$s"
    val resultExists = "文件 %1\$s 存在：%2\$s"
    val resultDelete = "删除 %1\$s：%2\$s"
    val resultDeleteFail = "删除失败：%1\$s"
    val resultGetPath = "当前测试文件路径：%1\$s\n（FileSystem 抽象层不提供目录枚举能力）"
    val resultWriteSize = "写入 %1\$s（%2\$d 字节）成功"

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
                FileInputSection(
                    fileName = fileName,
                    fileContent = fileContent,
                    onFileNameChange = { fileName = it },
                    onFileContentChange = { fileContent = it },
                    labelFileName = labelFileName,
                    labelFileContent = labelFileContent,
                    placeholderFileName = placeholderFileName,
                    placeholderFileContent = placeholderFileContent
                )
            }
            item {
                FileBasicOpsSection(
                    fileSystem = fileSystem,
                    fileName = fileName,
                    fileContent = fileContent,
                    btnWriteFile = btnWriteFile,
                    btnReadFile = btnReadFile,
                    btnCheckExists = btnCheckExists,
                    btnDeleteFile = btnDeleteFile,
                    btnGetPath = btnGetPath,
                    resultWriteSuccess = resultWriteSuccess,
                    resultWriteFail = resultWriteFail,
                    resultFileNotExist = resultFileNotExist,
                    resultReadSuccess = resultReadSuccess,
                    resultReadFail = resultReadFail,
                    resultExists = resultExists,
                    resultDelete = resultDelete,
                    resultDeleteFail = resultDeleteFail,
                    resultGetPath = resultGetPath,
                    onResultChange = { result = it }
                )
            }
            item {
                FileLargeFileSection(
                    fileSystem = fileSystem,
                    labelLargeFile = labelLargeFile,
                    btnWrite1kb = btnWrite1kb,
                    btnWrite10kb = btnWrite10kb,
                    btnWrite100kb = btnWrite100kb,
                    resultWriteSize = resultWriteSize,
                    resultWriteFail = resultWriteFail,
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
private fun FileInputSection(
    fileName: String,
    fileContent: String,
    onFileNameChange: (String) -> Unit,
    onFileContentChange: (String) -> Unit,
    labelFileName: String,
    labelFileContent: String,
    placeholderFileName: String,
    placeholderFileContent: String
) {
    Text(labelFileName, fontSize = 14.sp, color = Color(0xFF666666))
    DebugTextField(
        value = fileName,
        placeholder = placeholderFileName,
        onValueChange = onFileNameChange
    )
    DebugVSpacer(4.dp)
    Text(labelFileContent, fontSize = 14.sp, color = Color(0xFF666666))
    DebugTextField(
        value = fileContent,
        placeholder = placeholderFileContent,
        onValueChange = onFileContentChange
    )
}

@Composable
private fun FileBasicOpsSection(
    fileSystem: FileSystem,
    fileName: String,
    fileContent: String,
    btnWriteFile: String,
    btnReadFile: String,
    btnCheckExists: String,
    btnDeleteFile: String,
    btnGetPath: String,
    resultWriteSuccess: String,
    resultWriteFail: String,
    resultFileNotExist: String,
    resultReadSuccess: String,
    resultReadFail: String,
    resultExists: String,
    resultDelete: String,
    resultDeleteFail: String,
    resultGetPath: String,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(8.dp)
    DebugTestButton(btnWriteFile) {
        try {
            fileSystem.writeFile(fileName, fileContent.encodeToByteArray())
            onResultChange(String.format(resultWriteSuccess, fileName, fileContent))
        } catch (e: Exception) {
            onResultChange(String.format(resultWriteFail, e.message))
        }
    }
    DebugTestButton(btnReadFile) {
        try {
            if (!fileSystem.exists(fileName)) {
                onResultChange(String.format(resultFileNotExist, fileName))
            } else {
                val data = fileSystem.readFile(fileName)
                val content = data.decodeToString()
                onResultChange(String.format(resultReadSuccess, fileName, content))
            }
        } catch (e: Exception) {
            onResultChange(String.format(resultReadFail, e.message))
        }
    }
    DebugTestButton(btnCheckExists) {
        val exists = fileSystem.exists(fileName)
        onResultChange(String.format(resultExists, fileName, exists))
    }
    DebugTestButton(btnDeleteFile) {
        try {
            val deleted = fileSystem.delete(fileName)
            onResultChange(String.format(resultDelete, fileName, deleted))
        } catch (e: Exception) {
            onResultChange(String.format(resultDeleteFail, e.message))
        }
    }
    DebugTestButton(btnGetPath) {
        onResultChange(String.format(resultGetPath, fileName))
    }
}

@Composable
private fun FileLargeFileSection(
    fileSystem: FileSystem,
    labelLargeFile: String,
    btnWrite1kb: String,
    btnWrite10kb: String,
    btnWrite100kb: String,
    resultWriteSize: String,
    resultWriteFail: String,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(12.dp)
    Text(labelLargeFile, fontSize = 14.sp, color = Color(0xFF666666))
    DebugTestButton(btnWrite1kb) {
        val data = "A".repeat(1024).encodeToByteArray()
        val name = "test_1kb.bin"
        try {
            fileSystem.writeFile(name, data)
            onResultChange(String.format(resultWriteSize, name, data.size))
        } catch (e: Exception) {
            onResultChange(String.format(resultWriteFail, e.message))
        }
    }
    DebugTestButton(btnWrite10kb) {
        val data = "B".repeat(10240).encodeToByteArray()
        val name = "test_10kb.bin"
        try {
            fileSystem.writeFile(name, data)
            onResultChange(String.format(resultWriteSize, name, data.size))
        } catch (e: Exception) {
            onResultChange(String.format(resultWriteFail, e.message))
        }
    }
    DebugTestButton(btnWrite100kb) {
        val data = "C".repeat(102400).encodeToByteArray()
        val name = "test_100kb.bin"
        try {
            fileSystem.writeFile(name, data)
            onResultChange(String.format(resultWriteSize, name, data.size))
        } catch (e: Exception) {
            onResultChange(String.format(resultWriteFail, e.message))
        }
    }
}
