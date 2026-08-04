package com.kuikly.init.business.initTask

/**
 * 公共初始化任务收集点
 *
 * 各变体/地区通用的初始化任务在此注册。
 * 平台入口通过 [getCommonTasks] 获取公共任务列表，
 * 与平台差异化任务合并后统一执行。
 */
object CommonInitTasks {

    private val commonTasks: MutableList<InitTask> = mutableListOf()

    /**
     * 注册公共初始化任务
     *
     * 在模块初始化时调用，将通用 task 加入公共列表。
     */
    fun register(task: InitTask) {
        commonTasks.add(task)
    }

    /**
     * 获取公共初始化任务列表（副本）
     *
     * 返回副本以避免外部修改影响内部状态。
     */
    fun getCommonTasks(): List<InitTask> = commonTasks.toList()
}
