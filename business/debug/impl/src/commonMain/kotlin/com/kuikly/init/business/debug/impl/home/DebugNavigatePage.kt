package com.kuikly.init.business.debug.impl.home

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

private const val PAGE_TITLE = "🧭 页面跳转测试"
private const val SECTION_OPEN_PAGE = "1. 跳转到指定 Page"
private const val SECTION_OPEN_WITH_PARAMS = "2. 带参数跳转"
private const val SECTION_CLOSE_PAGE = "3. 关闭当前页"
private const val SECTION_MULTI_LEVEL = "4. 多级跳转测试"
private const val SECTION_LOG = "5. 跳转结果日志"
private const val PLACEHOLDER_PAGE_NAME = "输入 pageName"
private const val PLACEHOLDER_PARAM_PAGE = "pageName"
private const val PLACEHOLDER_KEY = "key"
private const val PLACEHOLDER_VALUE = "value"
private const val BTN_OPEN = "跳转"
private const val BTN_OPEN_WITH_PARAMS = "带参数跳转"
private const val BTN_CLOSE_PAGE = "关闭当前页"
private const val BTN_GOTO_DEBUG_TEXT = "跳转到 debug_text"
private const val LABEL_NO_LOG = "（暂无日志）"

@Page("debug_navigate")
internal class DebugNavigatePage : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {
            DebugNavigateContent()
        }
    }
}

@Composable
private fun DebugNavigateContent() {
    var pageName by remember { mutableStateOf("") }
    var paramKey by remember { mutableStateOf("") }
    var paramValue by remember { mutableStateOf("") }
    var logText by remember { mutableStateOf("") }

    val localPager = com.tencent.kuikly.compose.ui.platform.LocalActivity.current.getPager()
    val routerModule = localPager.acquireModule<RouterModule>(RouterModule.MODULE_NAME)

    fun appendLog(message: String) {
        logText = if (logText.isEmpty()) message else "$logText\n$message"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NavigateHeader()
        NavigateOpenPageSection(
            pageName = pageName,
            onPageNameChange = { pageName = it },
            onOpenPage = {
                if (pageName.isNotBlank()) {
                    routerModule.openPage(pageName)
                    appendLog("✅ openPage(\"$pageName\")")
                } else {
                    appendLog("❌ pageName 为空")
                }
            }
        )
        NavigateOpenWithParamsSection(
            pageName = pageName,
            paramKey = paramKey,
            paramValue = paramValue,
            onPageNameChange = { pageName = it },
            onParamKeyChange = { paramKey = it },
            onParamValueChange = { paramValue = it },
            onOpenWithParams = {
                if (pageName.isNotBlank()) {
                    val params = JSONObject()
                    if (paramKey.isNotBlank() && paramValue.isNotBlank()) {
                        params.put(paramKey, paramValue)
                    }
                    routerModule.openPage(pageName, params)
                    appendLog("✅ openPage(\"$pageName\", $params)")
                } else {
                    appendLog("❌ pageName 为空")
                }
            }
        )
        NavigateClosePageSection(onClose = {
            routerModule.closePage()
            appendLog("✅ closePage()")
        })
        NavigateMultiLevelSection(onNavigate = {
            routerModule.openPage("debug_text")
            appendLog("✅ 跳转到 debug_text → 请在 debug_text 跳转到 debug_image")
        })
        NavigateLogSection(logText = logText)
    }
}

@Composable
private fun NavigateHeader() {
    Text(
        text = PAGE_TITLE,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF333333)
    )
}

@Composable
private fun NavigateOpenPageSection(
    pageName: String,
    onPageNameChange: (String) -> Unit,
    onOpenPage: () -> Unit
) {
    Text(
        SECTION_OPEN_PAGE,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF7B7FE4)
    )
    DebugTextField(
        value = pageName,
        placeholder = PLACEHOLDER_PAGE_NAME,
        onValueChange = onPageNameChange
    )
    DebugTestButton(text = BTN_OPEN, onClick = onOpenPage)
}

@Composable
private fun NavigateOpenWithParamsSection(
    pageName: String,
    paramKey: String,
    paramValue: String,
    onPageNameChange: (String) -> Unit,
    onParamKeyChange: (String) -> Unit,
    onParamValueChange: (String) -> Unit,
    onOpenWithParams: () -> Unit
) {
    Text(
        SECTION_OPEN_WITH_PARAMS,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF7B7FE4)
    )
    DebugTextField(
        value = pageName,
        placeholder = PLACEHOLDER_PARAM_PAGE,
        onValueChange = onPageNameChange
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DebugTextField(
            value = paramKey,
            placeholder = PLACEHOLDER_KEY,
            onValueChange = onParamKeyChange,
            modifier = Modifier.weight(1f)
        )
        DebugTextField(
            value = paramValue,
            placeholder = PLACEHOLDER_VALUE,
            onValueChange = onParamValueChange,
            modifier = Modifier.weight(1f)
        )
    }
    DebugTestButton(text = BTN_OPEN_WITH_PARAMS, onClick = onOpenWithParams)
}

@Composable
private fun NavigateClosePageSection(onClose: () -> Unit) {
    Text(
        SECTION_CLOSE_PAGE,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF7B7FE4)
    )
    NavigateActionButton(text = BTN_CLOSE_PAGE, color = Color(0xFFFF6B6B), onClick = onClose)
}

@Composable
private fun NavigateMultiLevelSection(onNavigate: () -> Unit) {
    Text(
        SECTION_MULTI_LEVEL,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF7B7FE4)
    )
    NavigateActionButton(text = BTN_GOTO_DEBUG_TEXT, color = Color(0xFFA65CF9), onClick = onNavigate)
}

@Composable
private fun NavigateActionButton(text: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White)
    }
}

@Composable
private fun NavigateLogSection(logText: String) {
    Text(
        SECTION_LOG,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF7B7FE4)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = if (logText.isEmpty()) LABEL_NO_LOG else logText,
            style = TextStyle(
                fontSize = 12.sp,
                color = if (logText.isEmpty()) Color(0xFFCCCCCC) else Color(0xFF333333)
            )
        )
    }
}
