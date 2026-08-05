package com.kuikly.init.common.base.platform.clipboard

/**
 * 剪贴板能力抽象
 *
 * 提供文本复制、粘贴、清空和状态查询功能。
 * Android 10+ 后台无法读取剪贴板，pasteText() 会兜底返回空字符串。
 */
expect class Clipboard {
    /** 写入文本到剪贴板 */
    fun copyText(content: String)

    /** 读取剪贴板文本（无内容时返回空字符串） */
    fun pasteText(): String

    /** 清空剪贴板 */
    fun clear()

    /** 判断剪贴板是否包含文本 */
    fun hasText(): Boolean
}

/** 全局访问入口 */
expect fun provideClipboard(): Clipboard
