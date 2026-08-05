package com.kuikly.init.common.base.platform.share

/** 分享内容类型 */
sealed class ShareContent {
    data class Text(val text: String) : ShareContent()
    data class Link(val url: String, val title: String?, val description: String?) : ShareContent()
    data class Image(val localPath: String) : ShareContent()
    data class File(val localPath: String, val mimeType: String?) : ShareContent()
}

/**
 * 系统分享能力抽象
 *
 * 提供文本、链接、图片、文件的系统级分享功能。
 */
expect class Share {
    /** 分享文本 */
    fun shareText(text: String)

    /** 分享链接 */
    fun shareLink(url: String, title: String? = null, description: String? = null)

    /** 分享图片（本地路径） */
    fun shareImage(localPath: String)

    /** 分享文件（本地路径 + MIME 类型） */
    fun shareFile(localPath: String, mimeType: String? = null)
}

/** 全局访问入口 */
expect fun provideShare(): Share
