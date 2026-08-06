package com.kuikly.init

/**
 * 列出指定目录下的文件名。
 *
 * 默认实现返回空列表（iOS / OHOS 当前未实现文件枚举）。
 * Android 通过 actual 覆盖使用 java.io.File 枚举。
 */
internal expect fun listFilesInDirectory(dir: String): List<String>
