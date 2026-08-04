package com.kuikly.init.business.initTask

/**
 * 初始化任务接口
 *
 * 各模块/平台实现此接口，定义具体的初始化逻辑。
 * 平台入口收集所有 InitTask 实例后顺序执行。
 */
interface InitTask {

    /**
     * 执行初始化逻辑
     *
     * 此方法应完成服务的配置工作（如设置 baseUrl、超时时间等），
     * 依赖绑定由 Koin 在 startKoin 阶段完成。
     */
    fun execute()
}
