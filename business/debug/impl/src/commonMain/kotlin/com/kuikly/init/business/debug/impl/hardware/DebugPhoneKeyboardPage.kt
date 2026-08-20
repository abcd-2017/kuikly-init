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
import com.kuikly.init.common.base.platform.keyboard.provideKeyboard
import com.kuikly.init.common.base.platform.phone.providePhone
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_phone")
public class DebugPhoneKeyboardPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            
            val pageTitle = "电话 & 键盘"
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
                PhoneKeyboardTestContent(
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
private fun PhoneKeyboardTestContent(
    modifier: Modifier = Modifier,
    bridgeModule: com.kuikly.init.common.widget.BridgeModule,
    onClose: () -> Unit
) {
    val logPrefix = "操作日志：\n"
    var log by remember { mutableStateOf(logPrefix) }
    val phone = remember { providePhone() }
    val keyboard = remember { provideKeyboard() }

    val btnCall = "拨打电话 10086"
    val btnHideKeyboard = "隐藏键盘"
    val btnShowKeyboard = "显示键盘"
    val logTitle = "操作日志"
    val logCall = "[拨打电话] 10086 (跳转拨号界面)"
    val logCallException = "[拨打电话] 异常: %1\$s"
    val logHideKeyboard = "[隐藏键盘] 已调用 hide()"
    val logHideKeyboardException = "[隐藏键盘] 异常: %1\$s"
    val logShowKeyboard = "[显示键盘] 已调用 show() (iOS 可能为空操作)"
    val logShowKeyboardException = "[显示键盘] 异常: %1\$s"
    val btnClose = "关闭页面"

    fun appendLog(msg: String) {
        log = "[\${bridgeModule.currentTimeStamp()}] \$msg\n\$log"
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            PhoneActionsSection(
                phone = phone,
                keyboard = keyboard,
                btnCall = btnCall,
                btnHideKeyboard = btnHideKeyboard,
                btnShowKeyboard = btnShowKeyboard,
                logCall = logCall,
                logCallException = logCallException,
                logHideKeyboard = logHideKeyboard,
                logHideKeyboardException = logHideKeyboardException,
                logShowKeyboard = logShowKeyboard,
                logShowKeyboardException = logShowKeyboardException,
                onLogChange = { appendLog(it) }
            )
        }
        item {
            PhoneLogSection(log = log, logTitle = logTitle)
        }
        item {
            PhoneCloseButton(btnText = btnClose, onClose = onClose)
        }
    }
}

@Composable
private fun PhoneActionsSection(
    phone: com.kuikly.init.common.base.platform.phone.Phone,
    keyboard: com.kuikly.init.common.base.platform.keyboard.Keyboard,
    btnCall: String,
    btnHideKeyboard: String,
    btnShowKeyboard: String,
    logCall: String,
    logCallException: String,
    logHideKeyboard: String,
    logHideKeyboardException: String,
    logShowKeyboard: String,
    logShowKeyboardException: String,
    onLogChange: (String) -> Unit
) {
    HardwareActionButtonPrimary(btnCall) {
        try {
            phone.call("10086")
            onLogChange(logCall)
        } catch (e: Exception) {
            onLogChange(String.format(logCallException, e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary(btnHideKeyboard) {
        try {
            keyboard.hide()
            onLogChange(logHideKeyboard)
        } catch (e: Exception) {
            onLogChange(String.format(logHideKeyboardException, e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary(btnShowKeyboard) {
        try {
            keyboard.show()
            onLogChange(logShowKeyboard)
        } catch (e: Exception) {
            onLogChange(String.format(logShowKeyboardException, e.message))
        }
    }
}

@Composable
private fun PhoneLogSection(log: String, logTitle: String) {
    Spacer(Modifier.height(16.dp))
    Text(logTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
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
private fun PhoneCloseButton(btnText: String, onClose: () -> Unit) {
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
