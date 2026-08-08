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
import com.kuikly.init.common.base.platform.location.Location
import com.kuikly.init.common.base.platform.location.LocationAccuracy
import com.kuikly.init.common.base.platform.location.provideLocationProvider
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_location")
internal class DebugLocationPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            val pageTitle = stringResource(DebugImplMR.strings.debug_location_title)
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(pageTitle) },
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
    val logPrefix = stringResource(DebugImplMR.strings.debug_location_log_prefix)
    var log by remember { mutableStateOf(logPrefix) }
    val locationProvider = remember { provideLocationProvider() }

    val btnRequestPermission = stringResource(DebugImplMR.strings.debug_location_btn_request_permission)
    val btnGetLocationPrecise = stringResource(DebugImplMR.strings.debug_location_btn_get_location_precise)
    val btnGetLocationBalanced = stringResource(DebugImplMR.strings.debug_location_btn_get_location_balanced)
    val logTitle = stringResource(DebugImplMR.strings.debug_location_log_title)
    val logRequestPermissionCalling = stringResource(DebugImplMR.strings.debug_location_log_request_permission_calling)
    val logRequestPermissionResult = stringResource(DebugImplMR.strings.debug_location_log_request_permission_result)
    val logRequestPermissionException = stringResource(DebugImplMR.strings.debug_location_log_request_permission_exception)
    val logGetLocationCalling = stringResource(DebugImplMR.strings.debug_location_log_get_location_calling)
    val logGetLocationSuccess = stringResource(DebugImplMR.strings.debug_location_log_get_location_success)
    val logGetLocationNull = stringResource(DebugImplMR.strings.debug_location_log_get_location_null)
    val logGetLocationException = stringResource(DebugImplMR.strings.debug_location_log_get_location_exception)
    val permissionGranted = stringResource(DebugImplMR.strings.debug_location_permission_granted)
    val permissionDenied = stringResource(DebugImplMR.strings.debug_location_permission_denied)
    val resultFormat = stringResource(DebugImplMR.strings.debug_location_result_format)
    val btnClose = stringResource(DebugImplMR.strings.debug_close_page)

    fun appendLog(msg: String) {
        log = "$msg\n$log"
    }

    LazyColumn(modifier = modifier.padding(16.dp)) {
        item {
            LocationPermissionSection(
                locationProvider = locationProvider,
                btnRequestPermission = btnRequestPermission,
                logRequestPermissionCalling = logRequestPermissionCalling,
                logRequestPermissionResult = logRequestPermissionResult,
                logRequestPermissionException = logRequestPermissionException,
                permissionGranted = permissionGranted,
                permissionDenied = permissionDenied,
                onLogChange = { appendLog(it) }
            )
        }
        item {
            LocationGetSection(
                locationProvider = locationProvider,
                btnGetLocationPrecise = btnGetLocationPrecise,
                btnGetLocationBalanced = btnGetLocationBalanced,
                logGetLocationCalling = logGetLocationCalling,
                logGetLocationSuccess = logGetLocationSuccess,
                logGetLocationNull = logGetLocationNull,
                logGetLocationException = logGetLocationException,
                resultFormat = resultFormat,
                onLogChange = { appendLog(it) }
            )
        }
        item {
            LocationLogSection(log = log, logTitle = logTitle)
        }
        item {
            LocationCloseButton(btnText = btnClose, onClose = onClose)
        }
    }
}

@Composable
private fun LocationPermissionSection(
    locationProvider: com.kuikly.init.common.base.platform.location.LocationProvider,
    btnRequestPermission: String,
    logRequestPermissionCalling: String,
    logRequestPermissionResult: String,
    logRequestPermissionException: String,
    permissionGranted: String,
    permissionDenied: String,
    onLogChange: (String) -> Unit
) {
    HardwareActionButtonPrimary(btnRequestPermission) {
        try {
            onLogChange(logRequestPermissionCalling)
            locationProvider.requestPermission { granted ->
                onLogChange(String.format(logRequestPermissionResult, if (granted) permissionGranted else permissionDenied))
            }
        } catch (e: Exception) {
            onLogChange(String.format(logRequestPermissionException, e.message))
        }
    }
}

@Composable
private fun LocationGetSection(
    locationProvider: com.kuikly.init.common.base.platform.location.LocationProvider,
    btnGetLocationPrecise: String,
    btnGetLocationBalanced: String,
    logGetLocationCalling: String,
    logGetLocationSuccess: String,
    logGetLocationNull: String,
    logGetLocationException: String,
    resultFormat: String,
    onLogChange: (String) -> Unit
) {
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonPrimary(btnGetLocationPrecise) {
        try {
            onLogChange(String.format(logGetLocationCalling, "PRECISE"))
            locationProvider.getCurrentLocation(LocationAccuracy.PRECISE) { loc ->
                if (loc != null) {
                    onLogChange(String.format(logGetLocationSuccess, formatLocation(loc, resultFormat)))
                } else {
                    onLogChange(logGetLocationNull)
                }
            }
        } catch (e: Exception) {
            onLogChange(String.format(logGetLocationException, e.message))
        }
    }
    Spacer(Modifier.height(8.dp))
    HardwareActionButtonSecondary(btnGetLocationBalanced) {
        try {
            onLogChange(String.format(logGetLocationCalling, "BALANCED"))
            locationProvider.getCurrentLocation(LocationAccuracy.BALANCED) { loc ->
                if (loc != null) {
                    onLogChange(String.format(logGetLocationSuccess, formatLocation(loc, resultFormat)))
                } else {
                    onLogChange(logGetLocationNull)
                }
            }
        } catch (e: Exception) {
            onLogChange(String.format(logGetLocationException, e.message))
        }
    }
}

@Composable
private fun LocationLogSection(log: String, logTitle: String) {
    Spacer(Modifier.height(16.dp))
    Text(logTitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
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
private fun LocationCloseButton(btnText: String, onClose: () -> Unit) {
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

private fun formatLocation(loc: Location, format: String): String {
    val accuracyPart = loc.accuracy?.let { "\n精度: ${it}米" } ?: ""
    val altitudePart = loc.altitude?.let { "\n海拔: ${it}米" } ?: ""
    val speedPart = loc.speed?.let { "\n速度: ${it}m/s" } ?: ""
    val timestampPart = loc.timestamp?.let { "\n时间戳: $it" } ?: ""
    return String.format(format, loc.latitude, loc.longitude, accuracyPart, altitudePart, speedPart, timestampPart)
}
