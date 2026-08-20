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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.time.provideNtpClock
import com.kuikly.init.common.base.platform.time.provideTimezone
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import kotlinx.coroutines.launch

@Page("debug_time")
public class DebugTimePage : BasePager() {

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
    val placeholderText = "点击刷新按钮获取时间信息"
    var result by remember { mutableStateOf(placeholderText) }
    val ntpClock = remember { provideNtpClock() }
    val timezone = remember { provideTimezone() }
    val scope = rememberCoroutineScope()

    val pageTitle = "NTP 时钟"
    val btnClose = "关闭"
    val labelTimeInfo = "时间同步 / 时区信息"
    val btnRefresh = "刷新时间信息"
    val resultFormat = "NTP 服务器时间：%1\$s\nNTP 时间偏差（毫秒）：%2\$s\n本地时区 ID：%3\$s\n时区偏移量：%4\$d 分钟（%5\$d 小时）\n是否夏令时：%6\$s\n时区缩写：%7\$s"
    val fetchFail = "获取失败"
    val detecting = "检测中……"

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
                TimeInfoSection(
                    ntpClock = ntpClock,
                    timezone = timezone,
                    scope = scope,
                    labelTimeInfo = labelTimeInfo,
                    btnRefresh = btnRefresh,
                    resultFormat = resultFormat,
                    fetchFail = fetchFail,
                    detecting = detecting,
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
    labelTimeInfo: String,
    btnRefresh: String,
    resultFormat: String,
    fetchFail: String,
    detecting: String,
    onResultChange: (String) -> Unit
) {
    Text(labelTimeInfo, fontSize = 16.sp, color = Color(0xFF333333))
    DebugVSpacer(8.dp)
    DebugTestButton(btnRefresh) {
        scope.launch {
            onResultChange(detecting)
            val serverTime = ntpClock.getServerTime()
            val offset = ntpClock.getClockOffset()
            val tzId = timezone.getTimezoneId()
            val tzOffsetMin = timezone.getOffsetMinutes()
            val tzOffsetHour = timezone.getOffsetHours()
            val isDst = timezone.isDaylightSaving()
            val abbrev = timezone.getAbbreviation()
            onResultChange(String.format(
                resultFormat,
                serverTime ?: fetchFail,
                offset ?: fetchFail,
                tzId,
                tzOffsetMin,
                tzOffsetHour,
                isDst,
                abbrev
            ))
        }
    }
}
