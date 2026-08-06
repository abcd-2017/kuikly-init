package com.kuikly.init.business.debug.render

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
import com.kuikly.init.business.debug.ui.widgets.DebugSectionTitle
import com.kuikly.init.business.debug.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_layout")
internal class DebugLayoutPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        val ctx = this
        setContent {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("📐 布局测试") },
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
                    onClose = { ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
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
    DebugSectionTitle("Column 嵌套")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE3F2FD))
            .padding(12.dp)
    ) {
        Text("外层 Column", fontSize = 14.sp, color = Color(0xFF1565C0))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFFFE0B2))
                .padding(12.dp)
        ) {
            Text("内层 Column", fontSize = 14.sp, color = Color(0xFFE65100))
        }
    }
}

@Composable
private fun RowArrangeSection() {
    DebugSectionTitle("Row 排列")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            Color(0xFFEF5350) to "红",
            Color(0xFF42A5F5) to "蓝",
            Color(0xFF66BB6A) to "绿"
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
    DebugSectionTitle("Box 层叠")
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
    DebugSectionTitle("权重布局 (1:2)")
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
            Text("1", color = Color.White, fontSize = 14.sp)
        }
        Box(
            modifier = Modifier
                .weight(2f)
                .height(50.dp)
                .clip(RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp))
                .background(Color(0xFF26A69A)),
            contentAlignment = Alignment.Center
        ) {
            Text("2", color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ScrollSection() {
    DebugSectionTitle("滚动测试 (20 卡片)")
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
                        text = "卡片 #${index + 1}",
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
    DebugSectionTitle("边距对比 (8dp / 16dp / 24dp)")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            8.dp to "8dp",
            16.dp to "16dp",
            24.dp to "24dp"
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
    DebugSectionTitle("居中对齐")
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
            Text("中", color = Color.White, fontSize = 16.sp)
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
            text = "关闭页面",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp
        )
    }
    DebugVSpacer(32.dp)
}
