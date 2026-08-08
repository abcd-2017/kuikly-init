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
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.crypto.provideCrypto
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_crypto")
internal class DebugCryptoPage : BasePager() {

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
    val placeholderText = stringResource(DebugImplMR.strings.debug_crypto_result_placeholder)
    var result by remember { mutableStateOf(placeholderText) }
    val defaultPlain = stringResource(DebugImplMR.strings.debug_crypto_default_plain_text)
    val defaultKey = stringResource(DebugImplMR.strings.debug_crypto_default_key_text)
    var plainText by remember { mutableStateOf(defaultPlain) }
    var keyText by remember { mutableStateOf(defaultKey) }
    var cipherText by remember { mutableStateOf("") }
    val crypto = remember { provideCrypto() }

    val pageTitle = stringResource(DebugImplMR.strings.debug_crypto_title)
    val btnClose = stringResource(DebugImplMR.strings.debug_close)
    val labelAesPlain = stringResource(DebugImplMR.strings.debug_crypto_label_aes_plain)
    val labelAesKey = stringResource(DebugImplMR.strings.debug_crypto_label_aes_key)
    val labelHashEncode = stringResource(DebugImplMR.strings.debug_crypto_label_hash_encode)
    val placeholderInput = stringResource(DebugImplMR.strings.debug_crypto_placeholder_input)
    val placeholderKey = stringResource(DebugImplMR.strings.debug_crypto_placeholder_key)
    val placeholderHashInput = stringResource(DebugImplMR.strings.debug_crypto_placeholder_hash_input)
    val btnAesEncrypt = stringResource(DebugImplMR.strings.debug_crypto_btn_aes_encrypt)
    val btnAesDecrypt = stringResource(DebugImplMR.strings.debug_crypto_btn_aes_decrypt)
    val btnMd5 = stringResource(DebugImplMR.strings.debug_crypto_btn_md5)
    val btnSha256 = stringResource(DebugImplMR.strings.debug_crypto_btn_sha256)
    val btnBase64Encode = stringResource(DebugImplMR.strings.debug_crypto_btn_base64_encode)
    val btnBase64Decode = stringResource(DebugImplMR.strings.debug_crypto_btn_base64_decode)
    val btnBuiltinTest = stringResource(DebugImplMR.strings.debug_crypto_btn_builtin_test)
    val resultAesEncryptSuccess = stringResource(DebugImplMR.strings.debug_crypto_result_aes_encrypt_success)
    val resultAesEncryptFail = stringResource(DebugImplMR.strings.debug_crypto_result_aes_encrypt_fail)
    val resultAesDecryptEmpty = stringResource(DebugImplMR.strings.debug_crypto_result_aes_decrypt_empty)
    val resultAesDecryptSuccess = stringResource(DebugImplMR.strings.debug_crypto_result_aes_decrypt_success)
    val resultAesDecryptFail = stringResource(DebugImplMR.strings.debug_crypto_result_aes_decrypt_fail)
    val resultMd5Success = stringResource(DebugImplMR.strings.debug_crypto_result_md5_success)
    val resultMd5Fail = stringResource(DebugImplMR.strings.debug_crypto_result_md5_fail)
    val resultSha256Success = stringResource(DebugImplMR.strings.debug_crypto_result_sha256_success)
    val resultSha256Fail = stringResource(DebugImplMR.strings.debug_crypto_result_sha256_fail)
    val resultBase64EncodeSuccess = stringResource(DebugImplMR.strings.debug_crypto_result_base64_encode_success)
    val resultBase64EncodeFail = stringResource(DebugImplMR.strings.debug_crypto_result_base64_encode_fail)
    val resultBase64DecodeEmpty = stringResource(DebugImplMR.strings.debug_crypto_result_base64_decode_empty)
    val resultBase64DecodeSuccess = stringResource(DebugImplMR.strings.debug_crypto_result_base64_decode_success)
    val resultBase64DecodeFail = stringResource(DebugImplMR.strings.debug_crypto_result_base64_decode_fail)
    val resultBuiltinTest = stringResource(DebugImplMR.strings.debug_crypto_result_builtin_test)

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
