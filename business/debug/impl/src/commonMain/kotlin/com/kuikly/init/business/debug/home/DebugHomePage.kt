package com.kuikly.init.business.debug.home

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.ui.widgets.DebugCardItem
import com.kuikly.init.business.debug.ui.widgets.DebugSectionTitle
import com.kuikly.init.business.debug.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

private data class DebugTestItem(
    val pageName: String,
    val title: String,
    val description: String
)

private data class DebugCategory(
    val name: String,
    val items: List<DebugTestItem>
)

private val debugCategories = listOf(
    DebugCategory(
        name = "基础渲染",
        items = listOf(
            DebugTestItem("debug_text", "文本渲染", "字体、颜色、对齐、emoji、富文本"),
            DebugTestItem("debug_image", "图片渲染", "本地图、网络图、占位图、圆角、缩放"),
            DebugTestItem("debug_layout", "布局测试", "Column/Row/Box、滚动、边距、嵌套")
        )
    ),
    DebugCategory(
        name = "平台能力",
        items = listOf(
            DebugTestItem("debug_toast_dialog", "Toast & 对话框", "Toast、Alert、Confirm、ActionSheet"),
            DebugTestItem("debug_clipboard", "剪贴板", "复制、粘贴、清空"),
            DebugTestItem("debug_crypto", "加解密", "AES、MD5、SHA256、Base64"),
            DebugTestItem("debug_file", "文件读写", "写入、读取、删除"),
            DebugTestItem("debug_network", "网络监听", "连接状态、网络类型"),
            DebugTestItem("debug_device", "设备信息", "设备ID、OS版本、屏幕参数"),
            DebugTestItem("debug_haptic", "震动反馈", "三种震动模式"),
            DebugTestItem("debug_time", "NTP 时钟", "服务器时间、时区")
        )
    ),
    DebugCategory(
        name = "平台桥",
        items = listOf(
            DebugTestItem("debug_navigate", "页面跳转", "openPage、closePage、传参"),
            DebugTestItem("debug_cache", "本地缓存", "setCache、getCache"),
            DebugTestItem("debug_network_request", "网络请求", "ssoRequest 测试"),
            DebugTestItem("debug_report", "灯塔上报", "事件上报、实时上报")
        )
    ),
    DebugCategory(
        name = "硬件系统",
        items = listOf(
            DebugTestItem("debug_camera", "相机 & 相册", "拍照、录像、选图"),
            DebugTestItem("debug_scanner", "扫码", "跳转扫码、结果回传"),
            DebugTestItem("debug_file_picker", "文件选择", "选文件、选图片"),
            DebugTestItem("debug_location", "定位", "权限、经纬度"),
            DebugTestItem("debug_biometric", "生物识别", "指纹、人脸认证"),
            DebugTestItem("debug_share", "分享", "文本、链接、图片"),
            DebugTestItem("debug_phone", "电话 & 键盘", "拨号、键盘控制")
        )
    )
)

@Page("debug_home")
internal class DebugHomePage : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {
            DebugHomeContent()
        }
    }
}

@Composable
private fun DebugHomeContent() {
    val localPager = com.tencent.kuikly.compose.ui.platform.LocalActivity.current.getPager()
    val routerModule = localPager.acquireModule<RouterModule>(RouterModule.MODULE_NAME)

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        DebugHomeHeader(onClose = { routerModule.closePage() })
        DebugHomeCategoryList(
            onItemClick = { routerModule.openPage(it.pageName) }
        )
    }
}

@Composable
private fun DebugHomeHeader(onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFF7B7FE4)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🔧 Debug 测试中心",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "✕",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable { onClose() }
                .padding(12.dp)
        )
    }
}

@Composable
private fun DebugHomeCategoryList(onItemClick: (DebugTestItem) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        debugCategories.forEach { category ->
            item {
                DebugCategoryHeader(title = category.name)
            }
            category.items.forEach { testItem ->
                item {
                    DebugCardItem(
                        title = testItem.title,
                        description = testItem.description,
                        onClick = { onItemClick(testItem) }
                    )
                }
            }
            item {
                DebugVSpacer(8.dp)
            }
        }
    }
}

@Composable
private fun DebugCategoryHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF555555)
        )
    }
}
