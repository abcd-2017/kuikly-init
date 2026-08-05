package com.kuikly.init.common.base.platform

/**
 * 全局 Application Context（由平台侧注入）
 */
expect object AppContext {
    /** Application Context（Android=Application, iOS/OHOS=null） */
    var androidContext: Any?
}
