package com.kuikly.init.business.debug.hardware

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
import com.tencent.kuikly.compose.runtime.getValue
import com.tencent.kuikly.compose.runtime.mutableStateOf
import com.tencent.kuikly.compose.runtime.remember
import com.tencent.kuikly.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.platform.LocalActivity
import com.tencent.kuikly.compose.ui.text.font.FontFamily
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.base.bridgeModule
import com.kuikly.init.business.debug.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.share.provideShare
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_share")
internal class DebugSharePage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        val ctx = this
        setContent {
            val pager = LocalActivity.current.getPager()
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("分享测试") },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { padding ->
                ShareTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    bridgeModule = pager.bridgeModule,
                    onClose = { ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
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
    var log by remember { mutableStateOf("分享日志：\n") }
    val share = remember { provideShare() }

    fun appendLog(msg: String) {
        log = "[${bridgeModule.currentTimeStamp()}] $msg\n$log"
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            ShareActionsSection(
                share = share,
                onLogChange = { appendLog(it) }
            )
        }
        item {
            ShareLogSection(log = log)
        }
        item {
            ShareCloseButton(onClose)
        }
    }
}

@Composable
private fun ShareActionsSection(
    share: com.kuikly.init.common.base.platform.share.Share,
    onLogChange: (String) -> Unit
) {
    HardwareActionButtonPrimary("分享文本") {
        try {
            val text = "Hello Kuikly! 这是一条测试分享文本。"
            share.shareText(text)
            onLogChange("[分享文本] 内容: $text")
        } catch (e: Exception) {
            onLogChange("[分享文本] 异常: ${e.message}")
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary("分享链接") {
        try {
            val url = "https://github.com/kuikly"
            share.shareLink(
                url = url,
                title = "Kuikly 跨端框架",
                description = "Kuikly 是腾讯推出的跨端开发框架"
            )
            onLogChange("[分享链接] url: $url")
        } catch (e: Exception) {
            onLogChange("[分享链接] 异常: ${e.message}")
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary("分享图片") {
        try {
            val localPath = "/data/local/tmp/test.png"
            share.shareImage(localPath)
            onLogChange("[分享图片] 路径: $localPath (文件可能不存在)")
        } catch (e: Exception) {
            onLogChange("[分享图片] 异常: ${e.message}")
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary("分享文件") {
        try {
            val localPath = "/data/local/tmp/test.pdf"
            share.shareFile(localPath, "application/pdf")
            onLogChange("[分享文件] 路径: $localPath, mime=application/pdf (文件可能不存在)")
        } catch (e: Exception) {
            onLogChange("[分享文件] 异常: ${e.message}")
        }
    }
}

@Composable
private fun ShareLogSection(log: String) {
    Spacer(Modifier.height(16.dp))
    Text("分享日志", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
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
private fun ShareCloseButton(onClose: () -> Unit) {
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
