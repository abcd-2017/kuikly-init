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

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("分享") },
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
    bridgeModule: com.kuikly.init.common.widget.BridgeModule,
    onClose: () -> Unit
) {
    var log by remember { mutableStateOf("分享日志：\n") }
    val share = remember { provideShare() }

    fun appendLog(msg: String) {
        log = "[\${bridgeModule.currentTimeStamp()}] \$msg\n\$log"
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
            ShareCloseButton(onClose = onClose)
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
            share.shareText("Hello Kuikly! 这是一条测试分享文本。")
            onLogChange(
                String.format(
                    "[分享文本] 内容: %1\$s",
                    "Hello Kuikly! 这是一条测试分享文本。"
                )
            )
        } catch (e: Exception) {
            onLogChange(String.format("[分享文本] 异常: %1\$s", e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary("分享链接") {
        try {
            share.shareLink(
                url = "https://github.com/kuikly",
                title = "Kuikly 跨端框架",
                description = "Kuikly 是腾讯推出的跨端开发框架"
            )
            onLogChange(String.format("[分享链接] url: %1\$s", "https://github.com/kuikly"))
        } catch (e: Exception) {
            onLogChange(String.format("[分享链接] 异常: %1\$s", e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary("分享图片") {
        try {
            share.shareImage("/data/local/tmp/test.png")
            onLogChange(
                String.format(
                    "[分享图片] 路径: %1\$s (文件可能不存在)",
                    "/data/local/tmp/test.png"
                )
            )
        } catch (e: Exception) {
            onLogChange(String.format("[分享图片] 异常: %1\$s", e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary("分享文件") {
        try {
            share.shareFile("/data/local/tmp/test.pdf", "application/pdf")
            onLogChange(
                String.format(
                    "[分享文件] 路径: %1\$s, mime=application/pdf (文件可能不存在)",
                    "/data/local/tmp/test.pdf"
                )
            )
        } catch (e: Exception) {
            onLogChange(String.format("[分享文件] 异常: %1\$s", e.message))
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
