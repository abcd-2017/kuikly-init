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
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.biometric.BiometricType
import com.kuikly.init.common.base.platform.biometric.provideBiometric
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_biometric")
internal class DebugBiometricPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("生物识别测试") },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { padding ->
                BiometricTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
        }
    }
}

@Composable
private fun BiometricTestContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    var result by remember { mutableStateOf("认证结果将在此显示…\n提示：生物识别依赖硬件，当前平台可能不支持。") }
    val biometric = remember { provideBiometric() }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            BiometricDetectSection(
                biometric = biometric,
                onResultChange = { result = it }
            )
        }
        item {
            BiometricResultSection(result = result)
        }
        item {
            BiometricCloseButton(onClose)
        }
    }
}

@Composable
private fun BiometricDetectSection(
    biometric: com.kuikly.init.common.base.platform.biometric.Biometric,
    onResultChange: (String) -> Unit
) {
    HardwareActionButtonPrimary("检测设备是否支持") {
        try {
            val supported = biometric.isSupported()
            onResultChange("设备支持生物识别: $supported")
        } catch (e: Exception) {
            onResultChange("检测支持异常: ${e.message}")
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary("获取支持的类型") {
        try {
            val types = biometric.getSupportedTypes()
            onResultChange(
                if (types.isEmpty()) {
                    "无支持的生物识别类型"
                } else {
                    "支持的类型:\n${types.joinToString("\n") { "• ${typeLabel(it)}" }}"
                }
            )
        } catch (e: Exception) {
            onResultChange("获取类型异常: ${e.message}")
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonSecondary("发起生物识别认证") {
        onResultChange("发起认证…")
        try {
            biometric.authenticate(
                title = "身份验证",
                cancelText = "取消"
            ) { authResult ->
                onResultChange("认证结果: $authResult")
            }
        } catch (e: Exception) {
            onResultChange("认证异常: ${e.message}")
        }
    }
}

@Composable
private fun BiometricResultSection(result: String) {
    Spacer(Modifier.height(16.dp))
    Text("认证结果", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
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
            text = result,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF333333)
        )
    }
}

@Composable
private fun BiometricCloseButton(onClose: () -> Unit) {
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

private fun typeLabel(type: BiometricType): String = when (type) {
    BiometricType.FACE -> "人脸"
    BiometricType.FINGERPRINT -> "指纹"
    BiometricType.PIN -> "PIN/密码"
}
