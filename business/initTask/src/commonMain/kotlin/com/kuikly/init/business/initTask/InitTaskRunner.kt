package com.kuikly.init.business.initTask

/**
 * 初始化任务执行器
 *
 * 平台入口调用 [runAll] 执行所有初始化任务。
 */
object InitTaskRunner {

    /**
     * 执行所有初始化任务
     *
     * @param platformTasks 平台/变体差异化任务列表
     */
    fun runAll(platformTasks: List<InitTask> = emptyList()) {
        val allTasks = CommonInitTasks.getCommonTasks() + platformTasks
        allTasks.forEach { task ->
            runCatching { task.execute() }
                .onFailure { throwable ->
                    // 初始化失败不应阻断后续任务，输出错误后继续
                    println("[InitTaskRunner] Task failed: ${task::class.simpleName}, error: ${throwable.message}")
                }
        }
    }
}
