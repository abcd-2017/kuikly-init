package com.kuikly.init.business.debug.impl.bridge

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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import kotlinx.coroutines.launch

@Page("debug_network_request")
public class DebugNetworkRequestPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            
            val pageTitle = stringResource(DebugImplMR.strings.debug_network_request_title)
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
                NetworkTestContent(
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
private fun NetworkTestContent(
    modifier: Modifier = Modifier,
    bridgeModule: com.kuikly.init.common.widget.BridgeModule,
    onClose: () -> Unit
) {
    var cmdInput by remember { mutableStateOf("test.cmd") }
    var paramsInput by remember { mutableStateOf("{\"userId\":\"12345\"}") }
    val resultPlaceholder = stringResource(DebugImplMR.strings.debug_network_request_result_placeholder)
    var result by remember { mutableStateOf(resultPlaceholder) }
    val scope = rememberCoroutineScope()

    val labelCmd = stringResource(DebugImplMR.strings.debug_network_request_label_cmd)
    val labelParams = stringResource(DebugImplMR.strings.debug_network_request_label_params)
    val placeholderCmd = stringResource(DebugImplMR.strings.debug_network_request_placeholder_cmd)
    val placeholderParams = stringResource(DebugImplMR.strings.debug_network_request_placeholder_params)
    val btnSend = stringResource(DebugImplMR.strings.debug_network_request_btn_send)
    val presetTitle = stringResource(DebugImplMR.strings.debug_network_request_preset_title)
    val presetA = stringResource(DebugImplMR.strings.debug_network_request_preset_a)
    val presetB = stringResource(DebugImplMR.strings.debug_network_request_preset_b)
    val presetC = stringResource(DebugImplMR.strings.debug_network_request_preset_c)
    val resultTitle = stringResource(DebugImplMR.strings.debug_network_request_result_title)
    val resultSuccess = stringResource(DebugImplMR.strings.debug_network_request_result_success)
    val resultNull = stringResource(DebugImplMR.strings.debug_network_request_result_null)
    val resultFail = stringResource(DebugImplMR.strings.debug_network_request_result_fail)
    val requesting = stringResource(DebugImplMR.strings.debug_network_request_requesting)
    val btnClose = stringResource(DebugImplMR.strings.debug_close_page)

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            NetworkInputSection(
                cmdInput = cmdInput,
                paramsInput = paramsInput,
                onCmdChange = { cmdInput = it },
                onParamsChange = { paramsInput = it },
                labelCmd = labelCmd,
                labelParams = labelParams,
                placeholderCmd = placeholderCmd,
                placeholderParams = placeholderParams
            )
        }
        item {
            NetworkRequestSection(
                bridgeModule = bridgeModule,
                cmdInput = cmdInput,
                paramsInput = paramsInput,
                scope = scope,
                btnSend = btnSend,
                resultSuccess = resultSuccess,
                resultNull = resultNull,
                resultFail = resultFail,
                requesting = requesting,
                onResultChange = { result = it }
            )
        }
        item {
            NetworkPresetSection(
                onSelect = { cmd, params ->
                    cmdInput = cmd
                    paramsInput = params
                },
                presetTitle = presetTitle,
                presetA = presetA,
                presetB = presetB,
                presetC = presetC
            )
        }
        item {
            NetworkResultSection(result = result, resultTitle = resultTitle)
        }
        item {
            NetworkCloseButton(btnText = btnClose, onClose = onClose)
        }
    }
}

@Composable
private fun NetworkInputSection(
    cmdInput: String,
    paramsInput: String,
    onCmdChange: (String) -> Unit,
    onParamsChange: (String) -> Unit,
    labelCmd: String,
    labelParams: String,
    placeholderCmd: String,
    placeholderParams: String
) {
    Text(labelCmd, fontSize = 14.sp, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    DebugTextField(
        value = cmdInput,
        placeholder = placeholderCmd,
        onValueChange = onCmdChange
    )
    Spacer(Modifier.height(8.dp))
    Text(labelParams, fontSize = 14.sp, color = Color.Gray)
    Spacer(Modifier.height(4.dp))
    DebugTextField(
        value = paramsInput,
        placeholder = placeholderParams,
        onValueChange = onParamsChange
    )
}

@Composable
private fun NetworkRequestSection(
    bridgeModule: com.kuikly.init.common.widget.BridgeModule,
    cmdInput: String,
    paramsInput: String,
    scope: kotlinx.coroutines.CoroutineScope,
    btnSend: String,
    resultSuccess: String,
    resultNull: String,
    resultFail: String,
    requesting: String,
    onResultChange: (String) -> Unit
) {
    Spacer(Modifier.height(12.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable {
                scope.launch {
                    try {
                        val reqParams = JSONObject(paramsInput)
                        val startTime = bridgeModule.currentTimeStamp()
                        onResultChange(requesting)
                        val resp = bridgeModule.ssoRequest(cmdInput, reqParams)
                        val elapsed = bridgeModule.currentTimeStamp() - startTime
                        onResultChange(
                            if (resp != null) {
                                String.format(resultSuccess, elapsed, formatJson(resp.toString()))
                            } else {
                                String.format(resultNull, elapsed)
                            }
                        )
                    } catch (e: Exception) {
                        onResultChange(String.format(resultFail, e.message))
                    }
                }
            }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(btnSend, color = MaterialTheme.colorScheme.onPrimary, fontSize = 15.sp)
    }
}

@Composable
private fun NetworkPresetSection(
    onSelect: (String, String) -> Unit,
    presetTitle: String,
    presetA: String,
    presetB: String,
    presetC: String
) {
    val presetCases = listOf(
        presetA to ("test.cmd" to "{\"userId\":\"12345\"}"),
        presetB to ("user.info" to "{\"userId\":\"999\"}"),
        presetC to ("app.config" to "{}")
    )

    Spacer(Modifier.height(16.dp))
    Text(presetTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(8.dp))
    presetCases.forEach { (label, caseData) ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable { onSelect(caseData.first, caseData.second) }
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, fontSize = 14.sp)
        }
    }
}

@Composable
private fun NetworkResultSection(result: String, resultTitle: String) {
    Spacer(Modifier.height(16.dp))
    Text(resultTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
    Spacer(Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .padding(8.dp)
    ) {
        Text(
            text = result,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = Color(0xFF333333)
        )
    }
}

@Composable
private fun NetworkCloseButton(btnText: String, onClose: () -> Unit) {
    Spacer(Modifier.height(16.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.outline)
            .clickable(onClick = onClose)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(btnText, color = Color.White, fontSize = 15.sp)
    }
    Spacer(Modifier.height(32.dp))
}

private fun formatJson(raw: String): String {
    return try {
        val obj = JSONObject(raw)
        obj.toString()
    } catch (e: Exception) {
        raw
    }
}
