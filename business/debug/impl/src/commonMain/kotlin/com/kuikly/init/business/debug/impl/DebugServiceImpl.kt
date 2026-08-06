package com.kuikly.init.business.debug.impl

import com.kuikly.init.business.debug.api.IDebugService
import com.kuikly.init.business.debug.api.TestPageInfo
import com.tencent.kuikly.compose.ui.platform.LocalActivity
import com.tencent.kuikly.core.module.RouterModule

/**
 * debug 服务实现
 */
class DebugServiceImpl : IDebugService {

    override fun listTestPages(): List<TestPageInfo> {
        return listOf(
            // 基础渲染
            TestPageInfo("debug_text", "文本渲染", "字体、颜色、对齐、emoji、富文本", "基础渲染"),
            TestPageInfo("debug_image", "图片渲染", "本地图、网络图、占位图、圆角、缩放", "基础渲染"),
            TestPageInfo("debug_layout", "布局测试", "Column/Row/Box、滚动、边距、嵌套", "基础渲染"),
            // 平台能力
            TestPageInfo("debug_toast_dialog", "Toast & 对话框", "Toast、Alert、Confirm、ActionSheet", "平台能力"),
            TestPageInfo("debug_clipboard", "剪贴板", "复制、粘贴、清空", "平台能力"),
            TestPageInfo("debug_crypto", "加解密", "AES、MD5、SHA256、Base64", "平台能力"),
            TestPageInfo("debug_file", "文件读写", "写入、读取、删除", "平台能力"),
            TestPageInfo("debug_network", "网络监听", "连接状态、网络类型", "平台能力"),
            TestPageInfo("debug_device", "设备信息", "设备ID、OS版本、屏幕参数", "平台能力"),
            TestPageInfo("debug_haptic", "震动反馈", "三种震动模式", "平台能力"),
            TestPageInfo("debug_time", "NTP 时钟", "服务器时间、时区", "平台能力"),
            // 平台桥
            TestPageInfo("debug_navigate", "页面跳转", "openPage、closePage、传参", "平台桥"),
            TestPageInfo("debug_cache", "本地缓存", "setCache、getCache", "平台桥"),
            TestPageInfo("debug_network_request", "网络请求", "ssoRequest 测试", "平台桥"),
            TestPageInfo("debug_report", "灯塔上报", "事件上报、实时上报", "平台桥"),
            // 硬件系统
            TestPageInfo("debug_camera", "相机 & 相册", "拍照、录像、选图", "硬件系统"),
            TestPageInfo("debug_scanner", "扫码", "跳转扫码、结果回传", "硬件系统"),
            TestPageInfo("debug_file_picker", "文件选择", "选文件、选图片", "硬件系统"),
            TestPageInfo("debug_location", "定位", "权限、经纬度", "硬件系统"),
            TestPageInfo("debug_biometric", "生物识别", "指纹、人脸认证", "硬件系统"),
            TestPageInfo("debug_share", "分享", "文本、链接、图片", "硬件系统"),
            TestPageInfo("debug_phone", "电话 & 键盘", "拨号、键盘控制", "硬件系统")
        )
    }

    override fun navigateToTestPage(pageId: String) {
        val pager = LocalActivity.current.getPager()
        val routerModule = pager.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
        routerModule.openPage(pageId)
    }
}
