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
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.location.Location
import com.kuikly.init.common.base.platform.location.LocationAccuracy
import com.kuikly.init.common.base.platform.location.provideLocationProvider
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_location")
public class DebugLocationPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("定位") },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { padding ->
                LocationTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
        }
    }
}

@Composable
private fun LocationTestContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    var log by remember { mutableStateOf("定位日志：\n提示：定位功能依赖硬件和权限，当前平台可能不支持。\n") }
    val locationProvider = remember { provideLocationProvider() }

    fun appendLog(msg: String) {
        log = "\$msg\n\$log"
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            LocationPermissionSection(
                locationProvider = locationProvider,
                onLogChange = { appendLog(it) }
            )
        }
        item {
            LocationGetSection(
                locationProvider = locationProvider,
                onLogChange = { appendLog(it) }
            )
        }
        item {
            LocationLogSection(log = log)
        }
        item {
            LocationCloseButton(onClose = onClose)
        }
    }
}

@Composable
private fun LocationPermissionSection(
    locationProvider: com.kuikly.init.common.base.platform.location.LocationProvider,
    onLogChange: (String) -> Unit
) {
    HardwareActionButtonPrimary("请求定位权限") {
        try {
            onLogChange("[权限请求] 调用 requestPermission…")
            locationProvider.requestPermission { granted ->
                onLogChange(
                    String.format(
                        "[权限请求] 结果: %1\$s",
                        if (granted) "已授权" else "被拒绝"
                    )
                )
            }
        } catch (e: Exception) {
            onLogChange(String.format("[权限请求] 异常: %1\$s", e.message))
        }
    }
}

@Composable
private fun LocationGetSection(
    locationProvider: com.kuikly.init.common.base.platform.location.LocationProvider,
    onLogChange: (String) -> Unit
) {
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary("获取当前位置 (高精度)") {
        try {
            onLogChange(String.format("[获取位置] 调用 getCurrentLocation(%1\$s)…", "PRECISE"))
            locationProvider.getCurrentLocation(LocationAccuracy.PRECISE) { loc ->
                if (loc != null) {
                    onLogChange(String.format("[获取位置] 成功:\n%1\$s", formatLocation(loc)))
                } else {
                    onLogChange("[获取位置] 返回 null")
                }
            }
        } catch (e: Exception) {
            onLogChange(String.format("[获取位置] 异常: %1\$s", e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonSecondary("获取当前位置 (均衡)") {
        try {
            onLogChange(String.format("[获取位置] 调用 getCurrentLocation(%1\$s)…", "BALANCED"))
            locationProvider.getCurrentLocation(LocationAccuracy.BALANCED) { loc ->
                if (loc != null) {
                    onLogChange(String.format("[获取位置] 成功:\n%1\$s", formatLocation(loc)))
                } else {
                    onLogChange("[获取位置] 返回 null")
                }
            }
        } catch (e: Exception) {
            onLogChange(String.format("[获取位置] 异常: %1\$s", e.message))
        }
    }
}

@Composable
private fun LocationLogSection(log: String) {
    Spacer(Modifier.height(16.dp))
    Text("定位日志", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
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
private fun LocationCloseButton(onClose: () -> Unit) {
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

private fun formatLocation(loc: Location): String {
    val accuracyPart = loc.accuracy?.let { "\n精度: \${it}米" } ?: ""
    val altitudePart = loc.altitude?.let { "\n海拔: \${it}米" } ?: ""
    val speedPart = loc.speed?.let { "\n速度: \${it}m/s" } ?: ""
    val timestampPart = loc.timestamp?.let { "\n时间戳: \$it" } ?: ""
    return String.format(
        "纬度: %1\$s\n经度: %2\$s%3\$s%4\$s%5\$s%6\$s",
        loc.latitude,
        loc.longitude,
        accuracyPart,
        altitudePart,
        speedPart,
        timestampPart
    )
}
