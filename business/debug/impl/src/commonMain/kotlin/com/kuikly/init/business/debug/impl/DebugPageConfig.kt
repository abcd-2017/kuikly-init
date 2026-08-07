package com.kuikly.init.business.debug.impl

import com.kuikly.init.business.debug.api.TestPageInfo

/**
 * Debug 测试页面集中配置
 * 统一管理所有测试页面的元数据（pageName、标题、描述、分类）
 */
object DebugPageConfig {

    // 分类常量
    const val CATEGORY_RENDER = "基础渲染"
    const val CATEGORY_PLATFORM = "平台能力"
    const val CATEGORY_BRIDGE = "平台桥"
    const val CATEGORY_HARDWARE = "硬件系统"

    // 所有页面信息集中定义
    val pages: List<TestPageInfo> = listOf(
        // 基础渲染
        TestPageInfo("debug_text", "文本渲染", "字体、颜色、对齐、emoji、富文本", CATEGORY_RENDER),
        TestPageInfo("debug_image", "图片渲染", "本地图、网络图、占位图、圆角、缩放", CATEGORY_RENDER),
        TestPageInfo("debug_layout", "布局测试", "Column/Row/Box、滚动、边距、嵌套", CATEGORY_RENDER),
        // 平台能力
        TestPageInfo("debug_toast_dialog", "Toast & 对话框", "Toast、Alert、Confirm、ActionSheet", CATEGORY_PLATFORM),
        TestPageInfo("debug_clipboard", "剪贴板", "复制、粘贴、清空", CATEGORY_PLATFORM),
        TestPageInfo("debug_crypto", "加解密", "AES、MD5、SHA256、Base64", CATEGORY_PLATFORM),
        TestPageInfo("debug_file", "文件读写", "写入、读取、删除", CATEGORY_PLATFORM),
        TestPageInfo("debug_network", "网络监听", "连接状态、网络类型", CATEGORY_PLATFORM),
        TestPageInfo("debug_device", "设备信息", "设备ID、OS版本、屏幕参数", CATEGORY_PLATFORM),
        TestPageInfo("debug_haptic", "震动反馈", "三种震动模式", CATEGORY_PLATFORM),
        TestPageInfo("debug_time", "NTP 时钟", "服务器时间、时区", CATEGORY_PLATFORM),
        // 平台桥
        TestPageInfo("debug_navigate", "页面跳转", "openPage、closePage、传参", CATEGORY_BRIDGE),
        TestPageInfo("debug_cache", "本地缓存", "setCache、getCache", CATEGORY_BRIDGE),
        TestPageInfo("debug_network_request", "网络请求", "ssoRequest 测试", CATEGORY_BRIDGE),
        TestPageInfo("debug_report", "灯塔上报", "事件上报、实时上报", CATEGORY_BRIDGE),
        // 硬件系统
        TestPageInfo("debug_camera", "相机 & 相册", "拍照、录像、选图", CATEGORY_HARDWARE),
        TestPageInfo("debug_scanner", "扫码", "跳转扫码、结果回传", CATEGORY_HARDWARE),
        TestPageInfo("debug_file_picker", "文件选择", "选文件、选图片", CATEGORY_HARDWARE),
        TestPageInfo("debug_location", "定位", "权限、经纬度", CATEGORY_HARDWARE),
        TestPageInfo("debug_biometric", "生物识别", "指纹、人脸认证", CATEGORY_HARDWARE),
        TestPageInfo("debug_share", "分享", "文本、链接、图片", CATEGORY_HARDWARE),
        TestPageInfo("debug_phone", "电话 & 键盘", "拨号、键盘控制", CATEGORY_HARDWARE)
    )

    // 按分类分组
    val pagesByCategory: Map<String, List<TestPageInfo>> = pages.groupBy { it.category }

    // 根据 pageId 查找
    fun findPage(pageId: String): TestPageInfo? = pages.find { it.pageId == pageId }
}
