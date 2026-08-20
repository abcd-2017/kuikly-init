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
    val placeholderText = "操作结果将在此显示"
    var result by remember { mutableStateOf(placeholderText) }
    val defaultPlain = "Hello Kuikly"
    val defaultKey = "1234567890123456"
    var plainText by remember { mutableStateOf(defaultPlain) }
    var keyText by remember { mutableStateOf(defaultKey) }
    var cipherText by remember { mutableStateOf("") }
    val crypto = remember { provideCrypto() }

    val pageTitle = "加解密"
    val btnClose = "关闭"
    val labelAesPlain = "AES 明文："
    val labelAesKey = "AES 密钥："
    val labelHashEncode = "哈希 / 编码："
    val placeholderInput = "输入明文"
    val placeholderKey = "输入密钥"
    val placeholderHashInput = "输入要哈希/编码的文本"
    val btnAesEncrypt = "AES 加密"
    val btnAesDecrypt = "AES 解密"
    val btnMd5 = "MD5 哈希"
    val btnSha256 = "SHA-256 哈希"
    val btnBase64Encode = "Base64 编码"
    val btnBase64Decode = "Base64 解码"
    val btnBuiltinTest = "内置测试用例（\"Hello Kuikly\"）"
    val resultAesEncryptSuccess = "AES 加密结果：%1\$s"
    val resultAesEncryptFail = "AES 加密失败"
    val resultAesDecryptEmpty = "请先执行 AES 加密"
    val resultAesDecryptSuccess = "AES 解密结果：%1\$s"
    val resultAesDecryptFail = "AES 解密失败"
    val resultMd5Success = "MD5：%1\$s"
    val resultMd5Fail = "MD5 计算失败"
    val resultSha256Success = "SHA-256：%1\$s"
    val resultSha256Fail = "SHA-256 计算失败"
    val resultBase64EncodeSuccess = "Base64 编码：%1\$s"
    val resultBase64EncodeFail = "Base64 编码失败"
    val resultBase64DecodeEmpty = "请先执行 Base64 编码"
    val resultBase64DecodeSuccess = "Base64 解码：%1\$s"
    val resultBase64DecodeFail = "Base64 解码失败"
    val resultBuiltinTest = "测试文本：\"%1\$s\"\nMD5：%2$s\nSHA-256：%3$s\nAES(1234567890123456)：%4$s"

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
                CryptoAesInputSection(
                    plainText = plainText,
                    keyText = keyText,
                    onPlainTextChange = { plainText = it },
                    onKeyTextChange = { keyText = it },
                    labelAesPlain = labelAesPlain,
                    labelAesKey = labelAesKey,
                    placeholderInput = placeholderInput,
                    placeholderKey = placeholderKey
                )
            }
            item {
                CryptoAesActionSection(
                    crypto = crypto,
                    plainText = plainText,
                    keyText = keyText,
                    cipherText = cipherText,
                    onCipherTextChange = { cipherText = it },
                    btnAesEncrypt = btnAesEncrypt,
                    btnAesDecrypt = btnAesDecrypt,
                    resultAesEncryptSuccess = resultAesEncryptSuccess,
                    resultAesEncryptFail = resultAesEncryptFail,
                    resultAesDecryptEmpty = resultAesDecryptEmpty,
                    resultAesDecryptSuccess = resultAesDecryptSuccess,
                    resultAesDecryptFail = resultAesDecryptFail,
                    onResultChange = { result = it }
                )
            }
            item {
                CryptoHashSection(
                    crypto = crypto,
                    plainText = plainText,
                    cipherText = cipherText,
                    labelHashEncode = labelHashEncode,
                    placeholderHashInput = placeholderHashInput,
                    btnMd5 = btnMd5,
                    btnSha256 = btnSha256,
                    btnBase64Encode = btnBase64Encode,
                    btnBase64Decode = btnBase64Decode,
                    btnBuiltinTest = btnBuiltinTest,
                    resultMd5Success = resultMd5Success,
                    resultMd5Fail = resultMd5Fail,
                    resultSha256Success = resultSha256Success,
                    resultSha256Fail = resultSha256Fail,
                    resultBase64EncodeSuccess = resultBase64EncodeSuccess,
                    resultBase64EncodeFail = resultBase64EncodeFail,
                    resultBase64DecodeEmpty = resultBase64DecodeEmpty,
                    resultBase64DecodeSuccess = resultBase64DecodeSuccess,
                    resultBase64DecodeFail = resultBase64DecodeFail,
                    resultBuiltinTest = resultBuiltinTest,
                    defaultPlain = defaultPlain,
                    defaultKey = defaultKey,
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
    onKeyTextChange: (String) -> Unit,
    labelAesPlain: String,
    labelAesKey: String,
    placeholderInput: String,
    placeholderKey: String
) {
    Text(labelAesPlain, fontSize = 14.sp, color = Color(0xFF666666))
    DebugTextField(
        value = plainText,
        placeholder = placeholderInput,
        onValueChange = onPlainTextChange
    )
    DebugVSpacer(4.dp)
    Text(labelAesKey, fontSize = 14.sp, color = Color(0xFF666666))
    DebugTextField(
        value = keyText,
        placeholder = placeholderKey,
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
    btnAesEncrypt: String,
    btnAesDecrypt: String,
    resultAesEncryptSuccess: String,
    resultAesEncryptFail: String,
    resultAesDecryptEmpty: String,
    resultAesDecryptSuccess: String,
    resultAesDecryptFail: String,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(8.dp)
    DebugTestButton(btnAesEncrypt) {
        val enc = crypto.aesEncrypt(plainText, keyText)
        if (enc != null) {
            onCipherTextChange(enc)
            onResultChange(String.format(resultAesEncryptSuccess, enc))
        } else {
            onResultChange(resultAesEncryptFail)
        }
    }
    DebugTestButton(btnAesDecrypt) {
        if (cipherText.isEmpty()) {
            onResultChange(resultAesDecryptEmpty)
        } else {
            val dec = crypto.aesDecrypt(cipherText, keyText)
            onResultChange(if (dec != null) String.format(resultAesDecryptSuccess, dec) else resultAesDecryptFail)
        }
    }
}

@Composable
private fun CryptoHashSection(
    crypto: com.kuikly.init.common.base.platform.crypto.Crypto,
    plainText: String,
    cipherText: String,
    labelHashEncode: String,
    placeholderHashInput: String,
    btnMd5: String,
    btnSha256: String,
    btnBase64Encode: String,
    btnBase64Decode: String,
    btnBuiltinTest: String,
    resultMd5Success: String,
    resultMd5Fail: String,
    resultSha256Success: String,
    resultSha256Fail: String,
    resultBase64EncodeSuccess: String,
    resultBase64EncodeFail: String,
    resultBase64DecodeEmpty: String,
    resultBase64DecodeSuccess: String,
    resultBase64DecodeFail: String,
    resultBuiltinTest: String,
    defaultPlain: String,
    defaultKey: String,
    onResultChange: (String) -> Unit
) {
    DebugVSpacer(12.dp)
    Text(labelHashEncode, fontSize = 14.sp, color = Color(0xFF666666))
    DebugTextField(
        value = plainText,
        placeholder = placeholderHashInput,
        onValueChange = {}
    )
    DebugTestButton(btnMd5) {
        val md5 = crypto.md5(plainText)
        onResultChange(if (md5 != null) String.format(resultMd5Success, md5) else resultMd5Fail)
    }
    DebugTestButton(btnSha256) {
        val sha = crypto.sha256(plainText)
        onResultChange(if (sha != null) String.format(resultSha256Success, sha) else resultSha256Fail)
    }
    DebugTestButton(btnBase64Encode) {
        val enc = crypto.base64Encode(plainText)
        if (enc != null) {
            onResultChange(String.format(resultBase64EncodeSuccess, enc))
        } else {
            onResultChange(resultBase64EncodeFail)
        }
    }
    DebugTestButton(btnBase64Decode) {
        if (cipherText.isEmpty()) {
            onResultChange(resultBase64DecodeEmpty)
        } else {
            val dec = crypto.base64Decode(cipherText)
            onResultChange(if (dec != null) String.format(resultBase64DecodeSuccess, dec) else resultBase64DecodeFail)
        }
    }
    DebugVSpacer(12.dp)
    DebugTestButton(btnBuiltinTest) {
        val sample = defaultPlain
        val md5 = crypto.md5(sample)
        val sha = crypto.sha256(sample)
        val enc = crypto.aesEncrypt(sample, defaultKey)
        onResultChange(String.format(
            resultBuiltinTest,
            sample,
            md5 ?: "失败",
            sha ?: "失败",
            enc ?: "失败"
        ))
    }
}
