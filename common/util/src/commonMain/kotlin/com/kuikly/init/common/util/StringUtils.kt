package com.kuikly.init.common.util

/**
 * 字符串工具类
 */
object StringUtils {

    /**
     * 判断字符串是否为空或空白
     */
    fun isBlank(text: String?): Boolean {
        return text.isNullOrBlank()
    }

    /**
     * 判断字符串是否非空
     */
    fun isNotBlank(text: String?): Boolean {
        return !isBlank(text)
    }

    /**
     * 截断字符串，超出长度显示省略号
     */
    fun truncate(text: String, maxLength: Int, suffix: String = "..."): String {
        return if (text.length <= maxLength) {
            text
        } else {
            text.take(maxLength) + suffix
        }
    }
}
