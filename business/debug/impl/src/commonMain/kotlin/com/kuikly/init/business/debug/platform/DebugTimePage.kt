package com.kuikly.init.business.debug.platform

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
import com.tencent.kuikly.compose.runtime.getValue
import com.tencent.kuikly.compose.runtime.mutableStateOf
import com.tencent.kuikly.compose.runtime.remember
import com.tencent.kuikly.compose.runtime.rememberCoroutineScope
import com.tencent.kuikly.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.time.provideNtpClock
import com.kuikly.init.common.base.platform.time.provideTimezone
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import kotlinx.coroutines.launch

@Page("debug_time")
internal class DebugTimePage : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {
            DebugTimeContent { closePage() }
        }
    }

    private fun closePage() {
        acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebugTimeContent(onClose: () -> Unit) {
    var result by remember { mutableStateOf("点击刷新按钮获取时间信息") }
    val ntpClock = remember { provideNtpClock() }
    val timezone = remember { provideTimezone() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Time 测试") },
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
                TimeInfoSection(
                    ntpClock = ntpClock,
                    timezone = timezone,
                    scope = scope,
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
private fun TimeInfoSection(
    ntpClock: com.kuikly.init.common.base.platform.time.NtpClock,
    timezone: com.kuikly.init.common.base.platform.time.Timezone,
    scope: kotlinx.coroutines.CoroutineScope,
    onResultChange: (String) -> Unit
) {
    Text("时间同步 / 时区信息", fontSize = 16.sp, color = Color(0xFF333333))
    DebugVSpacer(8.dp)
    DebugTestButton("刷新时间信息") {
        scope.launch {
            onResultChange("检测中……")
            val serverTime = ntpClock.getServerTime()
            val offset = ntpClock.getClockOffset()
            val tzId = timezone.getTimezoneId()
            val tzOffsetMin = timezone.getOffsetMinutes()
            val tzOffsetHour = timezone.getOffsetHours()
            val isDst = timezone.isDaylightSaving()
            val abbrev = timezone.getAbbreviation()
            onResultChange(
                buildString {
                    appendLine("NTP 服务器时间：${serverTime ?: "获取失败"}")
                    appendLine("NTP 时间偏差（毫秒）：${offset ?: "获取失败"}")
                    appendLine("本地时区 ID：$tzId")
                    appendLine("时区偏移量：$tzOffsetMin 分钟（$tzOffsetHour 小时）")
                    appendLine("是否夏令时：$isDst")
                    appendLine("时区缩写：$abbrev")
                }
            )
        }
    }
}
