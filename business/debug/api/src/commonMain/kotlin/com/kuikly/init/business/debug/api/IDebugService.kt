package com.kuikly.init.business.debug.api

/**
 * debug 服务接口
 */
interface IDebugService {
    /**
     * 获取所有 debug 测试页面列表
     */
    fun listTestPages(): List<TestPageInfo>

    /**
     * 跳转到指定 debug 测试页
     */
    fun navigateToTestPage(pageId: String)
}

/**
 * Debug 测试页面信息
 */
data class TestPageInfo(
    val pageId: String,
    val title: String,
    val description: String,
    val category: String
)
