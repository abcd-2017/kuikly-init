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
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.common.widget.LocalContextProvider
import com.kuikly.init.business.debug.impl.ui.widgets.DebugSectionTitle
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_layout")
public class DebugLayoutPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            LocalContextProvider {
                val pageTitle = stringResource(DebugImplMR.strings.debug_layout_title)
                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(pageTitle) },
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
}

@Composable
private fun DebugLayoutContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    val sectionColumnNest = stringResource(DebugImplMR.strings.debug_layout_section_column_nest)
    val sectionRowArrange = stringResource(DebugImplMR.strings.debug_layout_section_row_arrange)
    val sectionBoxOverlap = stringResource(DebugImplMR.strings.debug_layout_section_box_overlap)
    val sectionWeight = stringResource(DebugImplMR.strings.debug_layout_section_weight)
    val sectionScroll = stringResource(DebugImplMR.strings.debug_layout_section_scroll)
    val sectionPadding = stringResource(DebugImplMR.strings.debug_layout_section_padding)
    val sectionCenterAlign = stringResource(DebugImplMR.strings.debug_layout_section_center_align)
    val textOuterColumn = stringResource(DebugImplMR.strings.debug_layout_text_outer_column)
    val textInnerColumn = stringResource(DebugImplMR.strings.debug_layout_text_inner_column)
    val labelRed = stringResource(DebugImplMR.strings.debug_layout_label_red)
    val labelBlue = stringResource(DebugImplMR.strings.debug_layout_label_blue)
    val labelGreen = stringResource(DebugImplMR.strings.debug_layout_label_green)
    val textWeight1 = stringResource(DebugImplMR.strings.debug_layout_text_weight_1)
    val textWeight2 = stringResource(DebugImplMR.strings.debug_layout_text_weight_2)
    val prefixCard = stringResource(DebugImplMR.strings.debug_layout_prefix_card)
    val label8dp = stringResource(DebugImplMR.strings.debug_layout_label_8dp)
    val label16dp = stringResource(DebugImplMR.strings.debug_layout_label_16dp)
    val label24dp = stringResource(DebugImplMR.strings.debug_layout_label_24dp)
    val textCenter = stringResource(DebugImplMR.strings.debug_layout_text_center)
    val btnClose = stringResource(DebugImplMR.strings.debug_close_page)

    LazyColumn(
        modifier = modifier.padding(16.dp)
    ) {
        item { ColumnNestSection(sectionColumnNest, textOuterColumn, textInnerColumn) }
        item { RowArrangeSection(sectionRowArrange, labelRed, labelBlue, labelGreen) }
        item { BoxOverlapSection(sectionBoxOverlap) }
        item { WeightLayoutSection(sectionWeight, textWeight1, textWeight2) }
        item { ScrollSection(sectionScroll, prefixCard) }
        item { PaddingCompareSection(sectionPadding, label8dp, label16dp, label24dp) }
        item { CenterAlignSection(sectionCenterAlign, textCenter) }
        item { CloseButtonSection(btnClose, onClose) }
    }
}

@Composable
private fun ColumnNestSection(
    title: String,
    textOuterColumn: String,
    textInnerColumn: String
) {
    DebugSectionTitle(title)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE3F2FD))
            .padding(12.dp)
    ) {
        Text(textOuterColumn, fontSize = 14.sp, color = Color(0xFF1565C0))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFFFE0B2))
                .padding(12.dp)
        ) {
            Text(textInnerColumn, fontSize = 14.sp, color = Color(0xFFE65100))
        }
    }
}

@Composable
private fun RowArrangeSection(
    title: String,
    labelRed: String,
    labelBlue: String,
    labelGreen: String
) {
    DebugSectionTitle(title)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            Color(0xFFEF5350) to labelRed,
            Color(0xFF42A5F5) to labelBlue,
            Color(0xFF66BB6A) to labelGreen
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
private fun BoxOverlapSection(title: String) {
    DebugSectionTitle(title)
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
private fun WeightLayoutSection(
    title: String,
    textWeight1: String,
    textWeight2: String
) {
    DebugSectionTitle(title)
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
            Text(textWeight1, color = Color.White, fontSize = 14.sp)
        }
        Box(
            modifier = Modifier
                .weight(2f)
                .height(50.dp)
                .clip(RoundedCornerShape(topEnd = 6.dp, bottomEnd = 6.dp))
                .background(Color(0xFF26A69A)),
            contentAlignment = Alignment.Center
        ) {
            Text(textWeight2, color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
private fun ScrollSection(title: String, prefixCard: String) {
    DebugSectionTitle(title)
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
                        text = "$prefixCard${index + 1}",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PaddingCompareSection(
    title: String,
    label8dp: String,
    label16dp: String,
    label24dp: String
) {
    DebugSectionTitle(title)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            8.dp to label8dp,
            16.dp to label16dp,
            24.dp to label24dp
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
private fun CenterAlignSection(title: String, textCenter: String) {
    DebugSectionTitle(title)
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
            Text(textCenter, color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
private fun CloseButtonSection(btnText: String, onClose: () -> Unit) {
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
            text = btnText,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp
        )
    }
    DebugVSpacer(32.dp)
}
