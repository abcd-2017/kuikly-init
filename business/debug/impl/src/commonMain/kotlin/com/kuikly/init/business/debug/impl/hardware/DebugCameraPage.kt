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
import com.kuikly.init.common.base.platform.camera.CapturedMedia
import com.kuikly.init.common.base.platform.camera.provideCamera
import com.kuikly.init.common.base.platform.mediapicker.MediaMediaType
import com.kuikly.init.common.base.platform.mediapicker.PickedMedia
import com.kuikly.init.common.base.platform.mediapicker.provideMediaPicker
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import kotlinx.coroutines.launch

@Page("debug_camera")
public class DebugCameraPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            
            val pageTitle = stringResource(DebugImplMR.strings.debug_camera_title)
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
                CameraTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
            }
        }
    }
}

@Composable
private fun CameraTestContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    val logPrefix = stringResource(DebugImplMR.strings.debug_camera_log_prefix)
    var log by remember { mutableStateOf(logPrefix) }
    val scope = rememberCoroutineScope()
    val camera = remember { provideCamera() }
    val mediaPicker = remember { provideMediaPicker() }

    val btnTakePhoto = stringResource(DebugImplMR.strings.debug_camera_btn_take_photo)
    val btnRecordVideo = stringResource(DebugImplMR.strings.debug_camera_btn_record_video)
    val btnPickImage = stringResource(DebugImplMR.strings.debug_camera_btn_pick_image)
    val btnPickVideo = stringResource(DebugImplMR.strings.debug_camera_btn_pick_video)
    val logTakePhotoCalling = stringResource(DebugImplMR.strings.debug_camera_log_take_photo_calling)
    val logTakePhotoSuccess = stringResource(DebugImplMR.strings.debug_camera_log_take_photo_success)
    val logTakePhotoFail = stringResource(DebugImplMR.strings.debug_camera_log_take_photo_fail)
    val logTakePhotoException = stringResource(DebugImplMR.strings.debug_camera_log_take_photo_exception)
    val logRecordVideoCalling = stringResource(DebugImplMR.strings.debug_camera_log_record_video_calling)
    val logRecordVideoSuccess = stringResource(DebugImplMR.strings.debug_camera_log_record_video_success)
    val logRecordVideoFail = stringResource(DebugImplMR.strings.debug_camera_log_record_video_fail)
    val logRecordVideoException = stringResource(DebugImplMR.strings.debug_camera_log_record_video_exception)
    val logPickImageCalling = stringResource(DebugImplMR.strings.debug_camera_log_pick_image_calling)
    val logPickImageSuccess = stringResource(DebugImplMR.strings.debug_camera_log_pick_image_success)
    val logPickImageFail = stringResource(DebugImplMR.strings.debug_camera_log_pick_image_fail)
    val logPickImageException = stringResource(DebugImplMR.strings.debug_camera_log_pick_image_exception)
    val logPickVideoCalling = stringResource(DebugImplMR.strings.debug_camera_log_pick_video_calling)
    val logPickVideoSuccess = stringResource(DebugImplMR.strings.debug_camera_log_pick_video_success)
    val logPickVideoFail = stringResource(DebugImplMR.strings.debug_camera_log_pick_video_fail)
    val logPickVideoException = stringResource(DebugImplMR.strings.debug_camera_log_pick_video_exception)
    val logTitle = stringResource(DebugImplMR.strings.debug_operation_log)
    val btnClose = stringResource(DebugImplMR.strings.debug_close_page)

    fun appendLog(msg: String) {
        log = "$msg\n$log"
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            CameraActionSection(
                scope = scope,
                camera = camera,
                btnTakePhoto = btnTakePhoto,
                btnRecordVideo = btnRecordVideo,
                logTakePhotoCalling = logTakePhotoCalling,
                logTakePhotoSuccess = logTakePhotoSuccess,
                logTakePhotoFail = logTakePhotoFail,
                logTakePhotoException = logTakePhotoException,
                logRecordVideoCalling = logRecordVideoCalling,
                logRecordVideoSuccess = logRecordVideoSuccess,
                logRecordVideoFail = logRecordVideoFail,
                logRecordVideoException = logRecordVideoException,
                onLogChange = { appendLog(it) }
            )
        }
        item {
            GalleryActionSection(
                scope = scope,
                mediaPicker = mediaPicker,
                btnPickImage = btnPickImage,
                btnPickVideo = btnPickVideo,
                logPickImageCalling = logPickImageCalling,
                logPickImageSuccess = logPickImageSuccess,
                logPickImageFail = logPickImageFail,
                logPickImageException = logPickImageException,
                logPickVideoCalling = logPickVideoCalling,
                logPickVideoSuccess = logPickVideoSuccess,
                logPickVideoFail = logPickVideoFail,
                logPickVideoException = logPickVideoException,
                onLogChange = { appendLog(it) }
            )
        }
        item {
            CameraLogSection(log = log, logTitle = logTitle)
        }
        item {
            CameraCloseButton(btnText = btnClose, onClose = onClose)
        }
    }
}

@Composable
private fun CameraActionSection(
    scope: kotlinx.coroutines.CoroutineScope,
    camera: com.kuikly.init.common.base.platform.camera.Camera,
    btnTakePhoto: String,
    btnRecordVideo: String,
    logTakePhotoCalling: String,
    logTakePhotoSuccess: String,
    logTakePhotoFail: String,
    logTakePhotoException: String,
    logRecordVideoCalling: String,
    logRecordVideoSuccess: String,
    logRecordVideoFail: String,
    logRecordVideoException: String,
    onLogChange: (String) -> Unit
) {
    HardwareActionButton(btnTakePhoto) {
        scope.launch {
            try {
                onLogChange(logTakePhotoCalling)
                camera.capturePhoto { result ->
                    if (result != null) {
                        onLogChange(String.format(logTakePhotoSuccess, mediaInfo(result)))
                    } else {
                        onLogChange(logTakePhotoFail)
                    }
                }
            } catch (e: Exception) {
                onLogChange(String.format(logTakePhotoException, e.message))
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButton(btnRecordVideo) {
        scope.launch {
            try {
                onLogChange(logRecordVideoCalling)
                camera.recordVideo { result ->
                    if (result != null) {
                        onLogChange(String.format(logRecordVideoSuccess, mediaInfo(result)))
                    } else {
                        onLogChange(logRecordVideoFail)
                    }
                }
            } catch (e: Exception) {
                onLogChange(String.format(logRecordVideoException, e.message))
            }
        }
    }
}

@Composable
private fun GalleryActionSection(
    scope: kotlinx.coroutines.CoroutineScope,
    mediaPicker: com.kuikly.init.common.base.platform.mediapicker.MediaPicker,
    btnPickImage: String,
    btnPickVideo: String,
    logPickImageCalling: String,
    logPickImageSuccess: String,
    logPickImageFail: String,
    logPickImageException: String,
    logPickVideoCalling: String,
    logPickVideoSuccess: String,
    logPickVideoFail: String,
    logPickVideoException: String,
    onLogChange: (String) -> Unit
) {
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonSecondary(btnPickImage) {
        scope.launch {
            try {
                onLogChange(logPickImageCalling)
                mediaPicker.pickMedia(MediaMediaType.IMAGE, false) { results ->
                    if (results.isNotEmpty()) {
                        onLogChange(String.format(logPickImageSuccess, pickedInfo(results.first())))
                    } else {
                        onLogChange(logPickImageFail)
                    }
                }
            } catch (e: Exception) {
                onLogChange(String.format(logPickImageException, e.message))
            }
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonSecondary(btnPickVideo) {
        scope.launch {
            try {
                onLogChange(logPickVideoCalling)
                mediaPicker.pickMedia(MediaMediaType.VIDEO, false) { results ->
                    if (results.isNotEmpty()) {
                        onLogChange(String.format(logPickVideoSuccess, pickedInfo(results.first())))
                    } else {
                        onLogChange(logPickVideoFail)
                    }
                }
            } catch (e: Exception) {
                onLogChange(String.format(logPickVideoException, e.message))
            }
        }
    }
}

@Composable
private fun CameraLogSection(log: String, logTitle: String) {
    Spacer(Modifier.height(16.dp))
    Text(logTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
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
private fun CameraCloseButton(btnText: String, onClose: () -> Unit) {
    Spacer(Modifier.height(16.dp))
    HardwareActionButtonSecondary(btnText, onClick = onClose)
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
