package com.kuikly.init.business.debug.impl.home

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.Text
import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.business.debug.impl.DebugPageConfig
import com.kuikly.init.business.debug.impl.ui.widgets.DebugCardItem
import com.kuikly.init.business.debug.impl.ui.widgets.DebugSectionTitle
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.compose.ui.platform.LocalActivity
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_home")
public class DebugHomePage : BasePager() {

    override fun willInit() {
        super.willInit()
        setContent {
            
            DebugHomeContent()

        }
    }
}

@Composable
private fun DebugHomeContent() {
    val localPager = LocalActivity.current.getPager()
    val routerModule = localPager.acquireModule<RouterModule>(RouterModule.MODULE_NAME)

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        DebugHomeHeader(onClose = { routerModule.closePage() })
        DebugHomeCategoryList(
            onItemClick = { routerModule.openPage(it) }
        )
    }
}

@Composable
private fun DebugHomeHeader(onClose: () -> Unit) {
    val titleText = stringResource(DebugImplMR.strings.debug_home_title)
    val statusBarHeight = LocalActivity.current.pageData.statusBarHeight
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = statusBarHeight.dp)
            .height(56.dp)
            .background(Color(0xFF7B7FE4)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = titleText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            text = "✕",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable { onClose() }
                .padding(12.dp)
        )
    }
}

@Composable
private fun DebugHomeCategoryList(onItemClick: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DebugPageConfig.pagesByCategory.forEach { (categoryName, pages) ->
            item {
                DebugCategoryHeader(title = categoryName)
            }
            pages.forEach { page ->
                item {
                    DebugCardItem(
                        title = page.title,
                        description = page.description,
                        onClick = { onItemClick(page.pageId) }
                    )
                }
            }
            item {
                DebugVSpacer(8.dp)
            }
        }
    }
}

@Composable
private fun DebugCategoryHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp)
            .background(Color(0xFFF0F0F0), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF555555)
        )
    }
}
