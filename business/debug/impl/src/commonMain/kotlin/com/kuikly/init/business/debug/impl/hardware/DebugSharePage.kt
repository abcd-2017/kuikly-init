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
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.common.widget.bridgeModule
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.share.provideShare
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_share")
public class DebugSharePage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            
            val pageTitle = "分享"
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
                ShareTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    bridgeModule = bridgeModule,
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
            }
        }
    }
}

@Composable
private fun ShareTestContent(
    modifier: Modifier = Modifier,
    bridgeModule: com.kuikly.init.common.widget.BridgeModule,
    onClose: () -> Unit
) {
    val logPrefix = "分享日志：\n"
    var log by remember { mutableStateOf(logPrefix) }
    val share = remember { provideShare() }

    val btnShareText = "分享文本"
    val btnShareLink = "分享链接"
    val btnShareImage = "分享图片"
    val btnShareFile = "分享文件"
    val logTitle = "分享日志"
    val textContent = "Hello Kuikly! 这是一条测试分享文本。"
    val linkUrl = "https://github.com/kuikly"
    val linkTitle = "Kuikly 跨端框架"
    val linkDescription = "Kuikly 是腾讯推出的跨端开发框架"
    val imagePath = "/data/local/tmp/test.png"
    val filePath = "/data/local/tmp/test.pdf"
    val logShareText = "[分享文本] 内容: %1\$s"
    val logShareTextException = "[分享文本] 异常: %1\$s"
    val logShareLink = "[分享链接] url: %1\$s"
    val logShareLinkException = "[分享链接] 异常: %1\$s"
    val logShareImage = "[分享图片] 路径: %1\$s (文件可能不存在)"
    val logShareImageException = "[分享图片] 异常: %1\$s"
    val logShareFile = "[分享文件] 路径: %1\$s, mime=application/pdf (文件可能不存在)"
    val logShareFileException = "[分享文件] 异常: %1\$s"
    val btnClose = "关闭页面"

    fun appendLog(msg: String) {
        log = "[\${bridgeModule.currentTimeStamp()}] \$msg\n\$log"
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
