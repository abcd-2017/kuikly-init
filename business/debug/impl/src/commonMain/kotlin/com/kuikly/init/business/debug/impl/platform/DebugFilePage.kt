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
import com.kuikly.init.base.BasePager
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
internal class DebugFilePage : BasePager() {

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
    var result by remember { mutableStateOf("操作结果将在此显示") }
    var fileName by remember { mutableStateOf("test.txt") }
    var fileContent by remember { mutableStateOf("Hello Kuikly FileSystem") }
    val fileSystem = remember { provideFileSystem() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("FileSystem 测试") },
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
                FileInputSection(
                    fileName = fileName,
                    fileContent = fileContent,
                    onFileNameChange = { fileName = it },
                    onFileContentChange = { fileContent = it }
                )
            }
            item {
                FileBasicOpsSection(
                    fileSystem = fileSystem,
                    fileName = fileName,
                    fileContent = fileContent,
                    onResultChange = { result = it }
                )
            }
            item {
                FileLargeFileSection(
                    fileSystem = fileSystem,
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
    onFileContentChange: (String) -> Unit
) {
    Text("文件名：", fontSize = 14.sp, color = Color(0xFF666666))
    DebugTextField(
        value = fileName,
        placeholder = "输入文件名",
        onValueChange = onFileNameChange
    )
    DebugVSpacer(4.dp)
    Text("文件内容：", fontSize = 14.sp, color = Color(0xFF666666))
    DebugTextField(
        value = fileContent,
        placeholder = "输入文件内容",
        onValueChange = onFileContentChange
    )
}

@Composable
private fun FileBasicOpsSection(
    fileSystem: FileSystem,
    fileName: String,
    fileContent: String,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(8.dp)
    DebugTestButton("写入文件") {
        try {
            fileSystem.writeFile(fileName, fileContent.encodeToByteArray())
            onResultChange("写入成功：$fileName\n内容：$fileContent")
        } catch (e: Exception) {
            onResultChange("写入失败：${e.message}")
        }
    }
    DebugTestButton("读取文件") {
        try {
            if (!fileSystem.exists(fileName)) {
                onResultChange("文件不存在：$fileName")
            } else {
                val data = fileSystem.readFile(fileName)
                val content = data.decodeToString()
                onResultChange("读取成功：$fileName\n内容：$content")
            }
        } catch (e: Exception) {
            onResultChange("读取失败：${e.message}")
        }
    }
    DebugTestButton("检查文件是否存在") {
        val exists = fileSystem.exists(fileName)
        onResultChange("文件 $fileName 存在：$exists")
    }
    DebugTestButton("删除文件") {
        try {
            val deleted = fileSystem.delete(fileName)
            onResultChange("删除 $fileName：$deleted")
        } catch (e: Exception) {
            onResultChange("删除失败：${e.message}")
        }
    }
    DebugTestButton("获取文件绝对路径") {
        onResultChange("当前测试文件路径：$fileName\n（FileSystem 抽象层不提供目录枚举能力）")
    }
}

@Composable
private fun FileLargeFileSection(
    fileSystem: FileSystem,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(12.dp)
    Text("大文件写入测试：", fontSize = 14.sp, color = Color(0xFF666666))
    DebugTestButton("写入 1KB 文件") {
        val data = "A".repeat(1024).encodeToByteArray()
        val name = "test_1kb.bin"
        try {
            fileSystem.writeFile(name, data)
            onResultChange("写入 $name（${data.size} 字节）成功")
        } catch (e: Exception) {
            onResultChange("写入失败：${e.message}")
        }
    }
    DebugTestButton("写入 10KB 文件") {
        val data = "B".repeat(10240).encodeToByteArray()
        val name = "test_10kb.bin"
        try {
            fileSystem.writeFile(name, data)
            onResultChange("写入 $name（${data.size} 字节）成功")
        } catch (e: Exception) {
            onResultChange("写入失败：${e.message}")
        }
    }
    DebugTestButton("写入 100KB 文件") {
        val data = "C".repeat(102400).encodeToByteArray()
        val name = "test_100kb.bin"
        try {
            fileSystem.writeFile(name, data)
            onResultChange("写入 $name（${data.size} 字节）成功")
        } catch (e: Exception) {
            onResultChange("写入失败：${e.message}")
        }
    }
}
