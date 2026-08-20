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
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugResultArea
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTestButton
import com.kuikly.init.business.debug.impl.ui.widgets.DebugTextField
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.compose.ui.platform.LocalActivity
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

@Page("debug_navigate")
public class DebugNavigatePage : BasePager() {

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

    val localPager = LocalActivity.current.getPager()
    val routerModule = localPager.acquireModule<RouterModule>(RouterModule.MODULE_NAME)

    val pageTitle = "页面跳转"
    val sectionOpenPage = "1. 跳转到指定 Page"
    val sectionOpenWithParams = "2. 带参数跳转"
    val sectionClosePage = "3. 关闭当前页"
    val sectionMultiLevel = "4. 多级跳转测试"
    val sectionLog = "5. 跳转结果日志"
    val placeholderPageName = "输入 pageName"
    val placeholderParamPage = "pageName"
    val placeholderKey = "key"
    val placeholderValue = "value"
    val btnOpen = "跳转"
    val btnOpenWithParams = "带参数跳转"
    val btnClosePage = "关闭当前页"
    val btnGotoDebugText = "跳转到 debug_text"
    val labelNoLog = "（暂无日志）"
    val logOpenSuccess = "✅ openPage(\"%1\$s\")"
    val logOpenWithParamsSuccess = "✅ openPage(\"%1\$s\", %2$s)"
    val logCloseSuccess = "✅ closePage()"
    val logPageNameEmpty = "❌ pageName 为空"
    val logMultiLevel = "✅ 跳转到 debug_text → 请在 debug_text 跳转到 debug_image"

    fun appendLog(message: String) {
        logText = if (logText.isEmpty()) message else "\$logText\n\$message"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NavigateHeader(pageTitle)
        NavigateOpenPageSection(
            sectionTitle = sectionOpenPage,
            pageName = pageName,
            onPageNameChange = { pageName = it },
            btnText = btnOpen,
            onOpenPage = {
                if (pageName.isNotBlank()) {
                    routerModule.openPage(pageName)
                    appendLog(String.format(logOpenSuccess, pageName))
                } else {
                    appendLog(logPageNameEmpty)
                }
            }
        )
        NavigateOpenWithParamsSection(
            sectionTitle = sectionOpenWithParams,
            pageName = pageName,
            paramKey = paramKey,
            paramValue = paramValue,
            placeholderPage = placeholderParamPage,
            placeholderKey = placeholderKey,
            placeholderValue = placeholderValue,
            onPageNameChange = { pageName = it },
            onParamKeyChange = { paramKey = it },
            onParamValueChange = { paramValue = it },
            btnText = btnOpenWithParams,
            onOpenWithParams = {
                if (pageName.isNotBlank()) {
                    val params = JSONObject()
                    if (paramKey.isNotBlank() && paramValue.isNotBlank()) {
                        params.put(paramKey, paramValue)
                    }
                    routerModule.openPage(pageName, params)
                    appendLog(String.format(logOpenWithParamsSuccess, pageName, params))
                } else {
                    appendLog(logPageNameEmpty)
                }
            }
        )
        NavigateClosePageSection(
            sectionTitle = sectionClosePage,
            btnText = btnClosePage,
            onClose = {
                routerModule.closePage()
                appendLog(logCloseSuccess)
            }
        )
        NavigateMultiLevelSection(
            sectionTitle = sectionMultiLevel,
            btnText = btnGotoDebugText,
            onNavigate = {
                routerModule.openPage("debug_text")
                appendLog(logMultiLevel)
            }
        )
        NavigateLogSection(sectionTitle = sectionLog, labelNoLog = labelNoLog, logText = logText)
    }
}

@Composable
private fun NavigateHeader(pageTitle: String) {
    Text(
        text = pageTitle,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF333333)
    )
}

@Composable
private fun NavigateOpenPageSection(
    sectionTitle: String,
    pageName: String,
    onPageNameChange: (String) -> Unit,
    onOpenPage: () -> Unit,
    btnText: String
) {
    Text(
        sectionTitle,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF7B7FE4)
    )
    DebugTextField(
        value = pageName,
        placeholder = "输入 pageName",
        onValueChange = onPageNameChange
    )
    DebugTestButton(text = btnText, onClick = onOpenPage)
}

@Composable
private fun NavigateOpenWithParamsSection(
    sectionTitle: String,
    pageName: String,
    paramKey: String,
    paramValue: String,
    placeholderPage: String,
    placeholderKey: String,
    placeholderValue: String,
    onPageNameChange: (String) -> Unit,
    onParamKeyChange: (String) -> Unit,
    onParamValueChange: (String) -> Unit,
    onOpenWithParams: () -> Unit,
    btnText: String
) {
    Text(
        sectionTitle,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF7B7FE4)
    )
    DebugTextField(
        value = pageName,
        placeholder = placeholderPage,
        onValueChange = onPageNameChange
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DebugTextField(
            value = paramKey,
            placeholder = placeholderKey,
            onValueChange = onParamKeyChange,
            modifier = Modifier.weight(1f)
        )
        DebugTextField(
            value = paramValue,
            placeholder = placeholderValue,
            onValueChange = onParamValueChange,
            modifier = Modifier.weight(1f)
        )
    }
    DebugTestButton(text = btnText, onClick = onOpenWithParams)
}

@Composable
private fun NavigateClosePageSection(
    sectionTitle: String,
    btnText: String,
    onClose: () -> Unit
) {
    Text(
        sectionTitle,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF7B7FE4)
    )
    NavigateActionButton(text = btnText, color = Color(0xFFFF6B6B), onClick = onClose)
}

@Composable
private fun NavigateMultiLevelSection(
    sectionTitle: String,
    btnText: String,
    onNavigate: () -> Unit
) {
    Text(
        sectionTitle,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF7B7FE4)
    )
    NavigateActionButton(text = btnText, color = Color(0xFFA65CF9), onClick = onNavigate)
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
private fun NavigateLogSection(
    sectionTitle: String,
    labelNoLog: String,
    logText: String
) {
    Text(
        sectionTitle,
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
            text = if (logText.isEmpty()) labelNoLog else logText,
            style = TextStyle(
                fontSize = 12.sp,
                color = if (logText.isEmpty()) Color(0xFFCCCCCC) else Color(0xFF333333)
            )
        )
    }
}
