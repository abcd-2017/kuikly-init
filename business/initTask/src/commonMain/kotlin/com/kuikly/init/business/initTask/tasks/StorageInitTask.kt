package com.kuikly.init.business.initTask.tasks

import com.kuikly.init.business.initTask.InitTask

/**
 * 存储初始化任务（桩）
 *
 * 具体实现由 store-impl 模块提供并注册到 CommonInitTasks。
 */
class StorageInitTask : InitTask {
    override fun execute() {
        // TODO: 由 store-impl 模块实现
        // 1. 从 Koin 获取 Storage
        // 2. 初始化缓存目录、命名空间
    }
}
