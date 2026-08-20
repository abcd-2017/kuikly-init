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
import com.kuikly.init.common.base.platform.biometric.BiometricType
import com.kuikly.init.common.base.platform.biometric.provideBiometric
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_biometric")
public class DebugBiometricPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            
            val pageTitle = "生物识别测试"
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
                BiometricTestContent(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
            }
        }
    }
}

@Composable
private fun BiometricTestContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    val resultPlaceholder = "认证结果将在此显示…\n提示：生物识别依赖硬件，当前平台可能不支持。"
    var result by remember { mutableStateOf(resultPlaceholder) }
    val biometric = remember { provideBiometric() }

    val btnDetectSupport = "检测设备是否支持"
    val btnGetTypes = "获取支持的类型"
    val btnAuthenticate = "发起生物识别认证"
    val resultTitle = "认证结果"
    val resultDetectSupport = "设备支持生物识别: %1\$s"
    val resultDetectSupportException = "检测支持异常: %1\$s"
    val resultNoTypes = "无支持的生物识别类型"
    val resultGetTypesSuccess = "支持的类型:\n%1\$s"
    val resultGetTypesException = "获取类型异常: %1\$s"
    val resultAuthenticateCalling = "发起认证…"
    val resultAuthenticateResult = "认证结果: %1\$s"
    val resultAuthenticateException = "认证异常: %1\$s"
    val authenticateTitle = "身份验证"
    val authenticateCancel = "取消"
    val typeFace = "人脸"
    val typeFingerprint = "指纹"
    val typePin = "PIN/密码"
    val btnClose = "关闭页面"

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            BiometricDetectSection(
                biometric = biometric,
                btnDetectSupport = btnDetectSupport,
                btnGetTypes = btnGetTypes,
                btnAuthenticate = btnAuthenticate,
                resultDetectSupport = resultDetectSupport,
                resultDetectSupportException = resultDetectSupportException,
                resultNoTypes = resultNoTypes,
                resultGetTypesSuccess = resultGetTypesSuccess,
                resultGetTypesException = resultGetTypesException,
                resultAuthenticateCalling = resultAuthenticateCalling,
                resultAuthenticateResult = resultAuthenticateResult,
                resultAuthenticateException = resultAuthenticateException,
                authenticateTitle = authenticateTitle,
                authenticateCancel = authenticateCancel,
                typeFace = typeFace,
                typeFingerprint = typeFingerprint,
                typePin = typePin,
                onResultChange = { result = it }
            )
        }
        item {
            BiometricResultSection(result = result, resultTitle = resultTitle)
        }
        item {
            BiometricCloseButton(btnText = btnClose, onClose = onClose)
        }
    }
}

@Composable
private fun BiometricDetectSection(
    biometric: com.kuikly.init.common.base.platform.biometric.Biometric,
    btnDetectSupport: String,
    btnGetTypes: String,
    btnAuthenticate: String,
    resultDetectSupport: String,
    resultDetectSupportException: String,
    resultNoTypes: String,
    resultGetTypesSuccess: String,
    resultGetTypesException: String,
    resultAuthenticateCalling: String,
    resultAuthenticateResult: String,
    resultAuthenticateException: String,
    authenticateTitle: String,
    authenticateCancel: String,
    typeFace: String,
    typeFingerprint: String,
    typePin: String,
    onResultChange: (String) -> Unit
) {
    HardwareActionButtonPrimary(btnDetectSupport) {
        try {
            val supported = biometric.isSupported()
            onResultChange(String.format(resultDetectSupport, supported))
        } catch (e: Exception) {
            onResultChange(String.format(resultDetectSupportException, e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary(btnGetTypes) {
        try {
            val types = biometric.getSupportedTypes()
            onResultChange(
                if (types.isEmpty()) {
                    resultNoTypes
                } else {
                    val labelList = types.joinToString("\n") { "• \${typeLabel(it, typeFace, typeFingerprint, typePin)}" }
                    String.format(resultGetTypesSuccess, labelList)
                }
            )
        } catch (e: Exception) {
            onResultChange(String.format(resultGetTypesException, e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonSecondary(btnAuthenticate) {
        onResultChange(resultAuthenticateCalling)
        try {
            biometric.authenticate(
                title = authenticateTitle,
                cancelText = authenticateCancel
            ) { authResult ->
                onResultChange(String.format(resultAuthenticateResult, authResult))
            }
        } catch (e: Exception) {
            onResultChange(String.format(resultAuthenticateException, e.message))
        }
    }
}

@Composable
private fun BiometricResultSection(result: String, resultTitle: String) {
    Spacer(Modifier.height(16.dp))
    Text(resultTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
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
private fun BiometricCloseButton(btnText: String, onClose: () -> Unit) {
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

private fun typeLabel(type: BiometricType, typeFace: String, typeFingerprint: String, typePin: String): String = when (type) {
    BiometricType.FACE -> typeFace
    BiometricType.FINGERPRINT -> typeFingerprint
    BiometricType.PIN -> typePin
}
