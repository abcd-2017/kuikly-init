package com.kuikly.init.business.debug.impl.render

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
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.Card
import com.tencent.kuikly.compose.material3.CardDefaults
import com.tencent.kuikly.compose.material3.CenterAlignedTopAppBar
import com.tencent.kuikly.compose.material3.ExperimentalMaterial3Api
import com.tencent.kuikly.compose.material3.MaterialTheme
import com.tencent.kuikly.compose.material3.Scaffold
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugSectionTitle
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

private const val PAGE_TITLE = "📐 布局测试"
private const val SECTION_COLUMN_NEST = "Column 嵌套"
private const val SECTION_ROW_ARRANGE = "Row 排列"
private const val SECTION_BOX_OVERLAP = "Box 层叠"
private const val SECTION_WEIGHT = "权重布局 (1:2)"
private const val SECTION_SCROLL = "滚动测试 (20 卡片)"
private const val SECTION_PADDING = "边距对比 (8dp / 16dp / 24dp)"
private const val SECTION_CENTER_ALIGN = "居中对齐"
private const val TEXT_OUTER_COLUMN = "外层 Column"
private const val TEXT_INNER_COLUMN = "内层 Column"
private const val LABEL_RED = "红"
private const val LABEL_BLUE = "蓝"
private const val LABEL_GREEN = "绿"
private const val TEXT_WEIGHT_1 = "1"
private const val TEXT_WEIGHT_2 = "2"
private const val PREFIX_CARD = "卡片 #"
private const val LABEL_8DP = "8dp"
private const val LABEL_16DP = "16dp"
private const val LABEL_24DP = "24dp"
private const val TEXT_CENTER = "中"
private const val BTN_CLOSE = "关闭页面"

@Page("debug_layout")
internal class DebugLayoutPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(PAGE_TITLE) },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) { padding ->
                DebugLayoutContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    onClose = { acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
                )
            }
        }
    }
}

@Composable
private fun DebugLayoutContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(16.dp)
    ) {
        item { ColumnNestSection() }
        item { RowArrangeSection() }
        item { BoxOverlapSection() }
        item { WeightLayoutSection() }
        item { ScrollSection() }
        item { PaddingCompareSection() }
        item { CenterAlignSection() }
        item { CloseButtonSection(onClose) }
    }
}

@Composable
private fun ColumnNestSection() {
    DebugSectionTitle(SECTION_COLUMN_NEST)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE3F2FD))
            .padding(12.dp)
    ) {
        Text(TEXT_OUTER_COLUMN, fontSize = 14.sp, color = Color(0xFF1565C0))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFFFE0B2))
                .padding(12.dp)
        ) {
            Text(TEXT_INNER_COLUMN, fontSize = 14.sp, color = Color(0xFFE65100))
        }
    }
}

@Composable
private fun RowArrangeSection() {
    DebugSectionTitle(SECTION_ROW_ARRANGE)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            Color(0xFFEF5350) to LABEL_RED,
            Color(0xFF42A5F5) to LABEL_BLUE,
            Color(0xFF66BB6A) to LABEL_GREEN
        ).forEach { (color, label) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Text(text = label, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun BoxOverlapSection() {
    DebugSectionTitle(SECTION_BOX_OVERLAP)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(Color(0xFF42A5F5).copy(alpha = 0.5f))
        )
        Box(
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .background(Color(0xFFEF5350).copy(alpha = 0.5f))
        )
    }
}

@Composable
private fun WeightLayoutSection() {
    DebugSectionTitle(SECTION_WEIGHT)
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                .background(Color(0xFF7E57C2)),
            contentAlignment = Alignment.Center
        ) {
            Text(TEXT_WEIGHT_1, color = Color.White, fontSize = 14.sp)
        }
        Box(
            modifier = Modifier
                .weight(2f)
                .height(50.dp)
                .clip(RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp))
                .background(Color(0xFF26A69A)),
            contentAlignment = Alignment.Center
        ) {
            Text(TEXT_WEIGHT_2, color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ScrollSection() {
    DebugSectionTitle(SECTION_SCROLL)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFAFAFA))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(20) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (index % 2 == 0) Color.White else Color(0xFFF5F5F5)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Text(
                        text = "$PREFIX_CARD${index + 1}",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PaddingCompareSection() {
    DebugSectionTitle(SECTION_PADDING)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            8.dp to LABEL_8DP,
            16.dp to LABEL_16DP,
            24.dp to LABEL_24DP
        ).forEach { (padValue, label) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE0E0E0))
                    .padding(padValue),
                contentAlignment = Alignment.Center
            ) {
                Text(text = label, fontSize = 12.sp, color = Color(0xFF424242))
            }
        }
    }
}

@Composable
private fun CenterAlignSection() {
    DebugSectionTitle(SECTION_CENTER_ALIGN)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE8F5E9)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF4CAF50)),
            contentAlignment = Alignment.Center
        ) {
            Text(TEXT_CENTER, color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
private fun CloseButtonSection(onClose: () -> Unit) {
    DebugVSpacer(24.dp)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable { onClose() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = BTN_CLOSE,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp
        )
    }
    DebugVSpacer(32.dp)
}
