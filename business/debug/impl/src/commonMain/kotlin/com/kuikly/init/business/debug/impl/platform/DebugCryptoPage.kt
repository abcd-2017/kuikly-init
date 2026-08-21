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
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.crypto.provideCrypto
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_crypto")
public class DebugCryptoPage : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {

            DebugCryptoContent { closePage() }

        }
    }

    private fun closePage() {
        acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DebugCryptoContent(onClose: () -> Unit) {
    var result by remember { mutableStateOf("操作结果将在此显示") }
    var plainText by remember { mutableStateOf("Hello Kuikly") }
    var keyText by remember { mutableStateOf("1234567890123456") }
    var cipherText by remember { mutableStateOf("") }
    val crypto = remember { provideCrypto() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("加解密") },
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
                CryptoAesInputSection(
                    plainText = plainText,
                    keyText = keyText,
                    onPlainTextChange = { plainText = it },
                    onKeyTextChange = { keyText = it }
                )
            }
            item {
                CryptoAesActionSection(
                    crypto = crypto,
                    plainText = plainText,
                    keyText = keyText,
                    cipherText = cipherText,
                    onCipherTextChange = { cipherText = it },
                    onResultChange = { result = it }
                )
            }
            item {
                CryptoHashSection(
                    crypto = crypto,
                    plainText = plainText,
                    cipherText = cipherText,
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
private fun CryptoAesInputSection(
    plainText: String,
    keyText: String,
    onPlainTextChange: (String) -> Unit,
    onKeyTextChange: (String) -> Unit
) {
    Text("AES 明文：", fontSize = 14.sp, color = Color(0xFF666666))
    DebugTextField(
        value = plainText,
        placeholder = "输入明文",
        onValueChange = onPlainTextChange
    )
    DebugVSpacer(4.dp)
    Text("AES 密钥：", fontSize = 14.sp, color = Color(0xFF666666))
    DebugTextField(
        value = keyText,
        placeholder = "输入密钥",
        onValueChange = onKeyTextChange
    )
}

@Composable
private fun CryptoAesActionSection(
    crypto: com.kuikly.init.common.base.platform.crypto.Crypto,
    plainText: String,
    keyText: String,
    cipherText: String,
    onCipherTextChange: (String) -> Unit,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(8.dp)
    DebugTestButton("AES 加密") {
        val enc = crypto.aesEncrypt(plainText, keyText)
        if (enc != null) {
            onCipherTextChange(enc)
            onResultChange(String.format("AES 加密结果：%1\$s", enc))
        } else {
            onResultChange("AES 加密失败")
        }
    }
    DebugTestButton("AES 解密") {
        if (cipherText.isEmpty()) {
            onResultChange("请先执行 AES 加密")
        } else {
            val dec = crypto.aesDecrypt(cipherText, keyText)
            onResultChange(if (dec != null) String.format("AES 解密结果：%1\$s", dec) else "AES 解密失败")
        }
    }
}

@Composable
private fun CryptoHashSection(
    crypto: com.kuikly.init.common.base.platform.crypto.Crypto,
    plainText: String,
    cipherText: String,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(12.dp)
    Text("哈希 / 编码：", fontSize = 14.sp, color = Color(0xFF666666))
    DebugTextField(
        value = plainText,
        placeholder = "输入要哈希/编码的文本",
        onValueChange = {}
    )
    DebugTestButton("MD5 哈希") {
        val md5 = crypto.md5(plainText)
        onResultChange(if (md5 != null) String.format("MD5：%1\$s", md5) else "MD5 计算失败")
    }
    DebugTestButton("SHA-256 哈希") {
        val sha = crypto.sha256(plainText)
        onResultChange(if (sha != null) String.format("SHA-256：%1\$s", sha) else "SHA-256 计算失败")
    }
    DebugTestButton("Base64 编码") {
        val enc = crypto.base64Encode(plainText)
        if (enc != null) {
            onResultChange(String.format("Base64 编码：%1\$s", enc))
        } else {
            onResultChange("Base64 编码失败")
        }
    }
    DebugTestButton("Base64 解码") {
        if (cipherText.isEmpty()) {
            onResultChange("请先执行 Base64 编码")
        } else {
            val dec = crypto.base64Decode(cipherText)
            onResultChange(if (dec != null) String.format("Base64 解码：%1\$s", dec) else "Base64 解码失败")
        }
    }
    DebugVSpacer(12.dp)
    DebugTestButton("内置测试用例（\"Hello Kuikly\"）") {
        val sample = "Hello Kuikly"
        val md5 = crypto.md5(sample)
        val sha = crypto.sha256(sample)
        val enc = crypto.aesEncrypt(sample, "1234567890123456")
        onResultChange(String.format(
            "测试文本：\"%1\$s\"\nMD5：%2\$s\nSHA-256：%3\$s\nAES(1234567890123456)：%4\$s",
            sample,
            md5 ?: "失败",
            sha ?: "失败",
            enc ?: "失败"
        ))
    }
}
