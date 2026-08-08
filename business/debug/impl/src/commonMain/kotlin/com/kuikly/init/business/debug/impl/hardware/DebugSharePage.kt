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
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.font.FontFamily
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.base.bridgeModule
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.share.provideShare
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_share")
internal class DebugSharePage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            val pageTitle = stringResource(DebugImplMR.strings.debug_share_title)
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(pageTitle) },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { padding ->
                ShareTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    bridgeModule = bridgeModule,
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
        }
    }
}

@Composable
private fun ShareTestContent(
    modifier: Modifier = Modifier,
    bridgeModule: com.kuikly.init.base.BridgeModule,
    onClose: () -> Unit
) {
    val logPrefix = stringResource(DebugImplMR.strings.debug_share_log_prefix)
    var log by remember { mutableStateOf(logPrefix) }
    val share = remember { provideShare() }

    val btnShareText = stringResource(DebugImplMR.strings.debug_share_btn_share_text)
    val btnShareLink = stringResource(DebugImplMR.strings.debug_share_btn_share_link)
    val btnShareImage = stringResource(DebugImplMR.strings.debug_share_btn_share_image)
    val btnShareFile = stringResource(DebugImplMR.strings.debug_share_btn_share_file)
    val logTitle = stringResource(DebugImplMR.strings.debug_share_log_title)
    val textContent = stringResource(DebugImplMR.strings.debug_share_text_content)
    val linkUrl = stringResource(DebugImplMR.strings.debug_share_link_url)
    val linkTitle = stringResource(DebugImplMR.strings.debug_share_link_title)
    val linkDescription = stringResource(DebugImplMR.strings.debug_share_link_description)
    val imagePath = stringResource(DebugImplMR.strings.debug_share_image_path)
    val filePath = stringResource(DebugImplMR.strings.debug_share_file_path)
    val logShareText = stringResource(DebugImplMR.strings.debug_share_log_share_text)
    val logShareTextException = stringResource(DebugImplMR.strings.debug_share_log_share_text_exception)
    val logShareLink = stringResource(DebugImplMR.strings.debug_share_log_share_link)
    val logShareLinkException = stringResource(DebugImplMR.strings.debug_share_log_share_link_exception)
    val logShareImage = stringResource(DebugImplMR.strings.debug_share_log_share_image)
    val logShareImageException = stringResource(DebugImplMR.strings.debug_share_log_share_image_exception)
    val logShareFile = stringResource(DebugImplMR.strings.debug_share_log_share_file)
    val logShareFileException = stringResource(DebugImplMR.strings.debug_share_log_share_file_exception)
    val btnClose = stringResource(DebugImplMR.strings.debug_close_page)

    fun appendLog(msg: String) {
        log = "[${bridgeModule.currentTimeStamp()}] $msg\n$log"
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            ShareActionsSection(
                share = share,
                btnShareText = btnShareText,
                btnShareLink = btnShareLink,
                btnShareImage = btnShareImage,
                btnShareFile = btnShareFile,
                textContent = textContent,
                linkUrl = linkUrl,
                linkTitle = linkTitle,
                linkDescription = linkDescription,
                imagePath = imagePath,
                filePath = filePath,
                logShareText = logShareText,
                logShareTextException = logShareTextException,
                logShareLink = logShareLink,
                logShareLinkException = logShareLinkException,
                logShareImage = logShareImage,
                logShareImageException = logShareImageException,
                logShareFile = logShareFile,
                logShareFileException = logShareFileException,
                onLogChange = { appendLog(it) }
            )
        }
        item {
            ShareLogSection(log = log, logTitle = logTitle)
        }
        item {
            ShareCloseButton(btnText = btnClose, onClose = onClose)
        }
    }
}

@Composable
private fun ShareActionsSection(
    share: com.kuikly.init.common.base.platform.share.Share,
    btnShareText: String,
    btnShareLink: String,
    btnShareImage: String,
    btnShareFile: String,
    textContent: String,
    linkUrl: String,
    linkTitle: String,
    linkDescription: String,
    imagePath: String,
    filePath: String,
    logShareText: String,
    logShareTextException: String,
    logShareLink: String,
    logShareLinkException: String,
    logShareImage: String,
    logShareImageException: String,
    logShareFile: String,
    logShareFileException: String,
    onLogChange: (String) -> Unit
) {
    HardwareActionButtonPrimary(btnShareText) {
        try {
            share.shareText(textContent)
            onLogChange(String.format(logShareText, textContent))
        } catch (e: Exception) {
            onLogChange(String.format(logShareTextException, e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary(btnShareLink) {
        try {
            share.shareLink(
                url = linkUrl,
                title = linkTitle,
                description = linkDescription
            )
            onLogChange(String.format(logShareLink, linkUrl))
        } catch (e: Exception) {
            onLogChange(String.format(logShareLinkException, e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary(btnShareImage) {
        try {
            share.shareImage(imagePath)
            onLogChange(String.format(logShareImage, imagePath))
        } catch (e: Exception) {
            onLogChange(String.format(logShareImageException, e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary(btnShareFile) {
        try {
            share.shareFile(filePath, "application/pdf")
            onLogChange(String.format(logShareFile, filePath))
        } catch (e: Exception) {
            onLogChange(String.format(logShareFileException, e.message))
        }
    }
}

@Composable
private fun ShareLogSection(log: String, logTitle: String) {
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
private fun ShareCloseButton(btnText: String, onClose: () -> Unit) {
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
