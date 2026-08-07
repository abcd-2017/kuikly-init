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
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.camera.CapturedMedia
import com.kuikly.init.common.base.platform.camera.provideCamera
import com.kuikly.init.common.base.platform.mediapicker.MediaMediaType
import com.kuikly.init.common.base.platform.mediapicker.PickedMedia
import com.kuikly.init.common.base.platform.mediapicker.provideMediaPicker
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import kotlinx.coroutines.launch

@Page("debug_camera")
internal class DebugCameraPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("相机/相册测试") },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { padding ->
                CameraTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
        }
    }
}

@Composable
private fun CameraTestContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    var log by remember { mutableStateOf("操作日志：\n提示：相机/录像功能依赖硬件，当前平台可能不支持。\n") }
    val scope = rememberCoroutineScope()
    val camera = remember { provideCamera() }
    val mediaPicker = remember { provideMediaPicker() }

    fun appendLog(msg: String) {
        log = "$msg\n$log"
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            CameraActionSection(
                scope = scope,
                camera = camera,
                onLogChange = { appendLog(it) }
            )
        }
        item {
            GalleryActionSection(
                scope = scope,
                mediaPicker = mediaPicker,
                onLogChange = { appendLog(it) }
            )
        }
        item {
            CameraLogSection(log = log)
        }
        item {
            CameraCloseButton(onClose)
        }
    }
}

@Composable
private fun CameraActionSection(
    scope: kotlinx.coroutines.CoroutineScope,
    camera: com.kuikly.init.common.base.platform.camera.Camera,
    onLogChange: (String) -> Unit
) {
    HardwareActionButton("拍照") {
        scope.launch {
            try {
                onLogChange("[拍照] 调用中…")
                camera.capturePhoto { result ->
                    if (result != null) {
                        onLogChange("[拍照] 成功:\n${mediaInfo(result)}")
                    } else {
                        onLogChange("[拍照] 取消或失败")
                    }
                }
            } catch (e: Exception) {
                onLogChange("[拍照] 异常: ${e.message}")
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButton("录像") {
        scope.launch {
            try {
                onLogChange("[录像] 调用中…")
                camera.recordVideo { result ->
                    if (result != null) {
                        onLogChange("[录像] 成功:\n${mediaInfo(result)}")
                    } else {
                        onLogChange("[录像] 取消或失败")
                    }
                }
            } catch (e: Exception) {
                onLogChange("[录像] 异常: ${e.message}")
            }
        }
    }
}

@Composable
private fun GalleryActionSection(
    scope: kotlinx.coroutines.CoroutineScope,
    mediaPicker: com.kuikly.init.common.base.platform.mediapicker.MediaPicker,
    onLogChange: (String) -> Unit
) {
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonSecondary("从相册选择图片") {
        scope.launch {
            try {
                onLogChange("[相册选图] 调用中…")
                mediaPicker.pickMedia(MediaMediaType.IMAGE, false) { results ->
                    if (results.isNotEmpty()) {
                        onLogChange("[相册选图] 成功:\n${pickedInfo(results.first())}")
                    } else {
                        onLogChange("[相册选图] 取消或失败")
                    }
                }
            } catch (e: Exception) {
                onLogChange("[相册选图] 异常: ${e.message}")
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonSecondary("从相册选择视频") {
        scope.launch {
            try {
                onLogChange("[相册选视频] 调用中…")
                mediaPicker.pickMedia(MediaMediaType.VIDEO, false) { results ->
                    if (results.isNotEmpty()) {
                        onLogChange("[相册选视频] 成功:\n${pickedInfo(results.first())}")
                    } else {
                        onLogChange("[相册选视频] 取消或失败")
                    }
                }
            } catch (e: Exception) {
                onLogChange("[相册选视频] 异常: ${e.message}")
            }
        }
    }
}

@Composable
private fun CameraLogSection(log: String) {
    Spacer(Modifier.height(16.dp))
    Text("操作日志", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .padding(8.dp)
    ) {
        Text(
            text = log,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF333333)
        )
    }
}

@Composable
private fun CameraCloseButton(onClose: () -> Unit) {
    Spacer(Modifier.height(16.dp))
    HardwareActionButtonSecondary("关闭页面", onClick = onClose)
    Spacer(Modifier.height(32.dp))
}

@Composable
private fun HardwareActionButton(text: String, onClick: () -> Unit) {
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

private fun mediaInfo(media: CapturedMedia): String {
    return "path=${media.path}\nname=${media.name}\nsize=${media.size}B\nmime=${media.mimeType}"
}

private fun pickedInfo(media: PickedMedia): String {
    return "path=${media.path}\nname=${media.name}\nsize=${media.size}B\nmime=${media.mimeType}" +
            (media.duration?.let { "\nduration=${it}ms" } ?: "")
}
