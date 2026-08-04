package com.kuikly.init.business.initTask.tasks

import com.kuikly.init.business.initTask.InitTask

/**
 * 网络初始化任务（桩）
 *
 * 具体实现由 net-impl 模块提供并注册到 CommonInitTasks。
 * 此处仅作为占位，确保编译通过。
 */
class NetworkInitTask : InitTask {
    override fun execute() {
        // TODO: 由 net-impl 模块实现
        // 1. 从 Koin 获取 HttpClient
        // 2. 配置 baseUrl、timeout、拦截器等
    }
}
