package com.kuikly.init.business.debug.impl

import com.kuikly.init.business.debug.api.IDebugService
import com.kuikly.init.business.debug.api.TestPageInfo
import com.tencent.kuikly.core.manager.BridgeManager
import com.tencent.kuikly.core.manager.PagerManager
import com.tencent.kuikly.core.module.RouterModule

/**
 * debug 服务实现
 */
class DebugServiceImpl : IDebugService {

    override fun listTestPages(): List<TestPageInfo> = DebugPageConfig.pages

    override fun navigateToTestPage(pageId: String) {
        val pager = PagerManager.getPager(BridgeManager.currentPageId)
        val routerModule = pager.acquireModule<RouterModule>(RouterModule.MODULE_NAME)
        routerModule.openPage(pageId)
    }
}
