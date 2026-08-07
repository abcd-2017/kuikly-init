package com.kuikly.init.business.debug.impl.render

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
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
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.text.style.TextOverflow
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugSectionTitle
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

private const val PAGE_TITLE = "📝 文本渲染测试"
private const val SECTION_FONT_SIZE = "字体大小"
private const val SECTION_COLOR = "颜色"
private const val SECTION_ALIGN = "对齐"
private const val SECTION_FONT_WEIGHT = "字重"
private const val SECTION_MULTI_LINE = "多行截断"
private const val SECTION_EMOJI = "Emoji"
private const val SECTION_LINE_HEIGHT = "行高"
private const val SECTION_TEXT_BACKGROUND = "文本背景"
private const val SAMPLE_TEXT = "Kuikly 跨端脚手架"
private const val TEXT_ALIGN_LEFT = "左对齐文本 — Kuikly 跨端脚手架"
private const val TEXT_ALIGN_CENTER = "居中文本 — Kuikly 跨端脚手架"
private const val TEXT_ALIGN_RIGHT = "右对齐文本 — Kuikly 跨端脚手架"
private const val TEXT_WEIGHT_NORMAL = "Normal 字重"
private const val TEXT_WEIGHT_BOLD = "Bold 字重"
private const val TEXT_WEIGHT_LIGHT = "Light 字重"
private const val TEXT_LONG_SAMPLE = "这是一段很长的文本，用于测试 maxLines 和 overflow 的截断效果。" +
        "Kuikly 是腾讯推出的跨端开发框架，支持 Android、iOS、鸿蒙三端。" +
        "它基于 Kotlin Multiplatform 技术，提供了统一的 DSL 和组件能力。"
private const val TEXT_EMOJI = "🎉🔥💯👍🚀❤️✨"
private const val TEXT_LINE_HEIGHT_SAMPLE = "这是一段用于测试不同行高效果的文本。行高影响段落的可读性和视觉密度。"
private const val LABEL_LINE_HEIGHT_1_2X = "1.2x"
private const val LABEL_LINE_HEIGHT_1_5X = "1.5x"
private const val LABEL_LINE_HEIGHT_2_0X = "2.0x"
private const val PREFIX_LINE_HEIGHT = "↑ lineHeight "
private const val TEXT_BG_SAMPLE_1 = "带背景色的文字"
private const val TEXT_BG_SAMPLE_2 = "另一种背景色"
private const val BTN_CLOSE = "关闭页面"

@Page("debug_text")
internal class DebugTextPage : BasePager() {

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
                DebugTextContent(
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
private fun DebugTextContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(16.dp)
    ) {
        item { FontSizeSection() }
        item { ColorSection() }
        item { AlignSection() }
        item { FontWeightSection() }
        item { MultiLineTruncateSection() }
        item { EmojiSection() }
        item { LineHeightSection() }
        item { TextBackgroundSection() }
        item { CloseButtonSection(onClose) }
    }
}

@Composable
private fun FontSizeSection() {
    DebugSectionTitle(SECTION_FONT_SIZE)
    listOf(12.sp, 14.sp, 16.sp, 20.sp, 24.sp).forEach { size ->
        Text(text = SAMPLE_TEXT, fontSize = size)
    }
}

@Composable
private fun ColorSection() {
    DebugSectionTitle(SECTION_COLOR)
    val colors = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Gray,
        Color.Black
    )
    colors.forEach { color ->
        Text(text = SAMPLE_TEXT, color = color, fontSize = 16.sp)
    }
}

@Composable
private fun AlignSection() {
    DebugSectionTitle(SECTION_ALIGN)
    Text(
        text = TEXT_ALIGN_LEFT,
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 16.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = TEXT_ALIGN_CENTER,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 16.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = TEXT_ALIGN_RIGHT,
        textAlign = TextAlign.End,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 16.sp
    )
}

@Composable
private fun FontWeightSection() {
    DebugSectionTitle(SECTION_FONT_WEIGHT)
    Text(text = TEXT_WEIGHT_NORMAL, fontWeight = FontWeight.Normal, fontSize = 16.sp)
    Text(text = TEXT_WEIGHT_BOLD, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Text(text = TEXT_WEIGHT_LIGHT, fontWeight = FontWeight.Light, fontSize = 16.sp)
}

@Composable
private fun MultiLineTruncateSection() {
    DebugSectionTitle(SECTION_MULTI_LINE)
    Text(
        text = TEXT_LONG_SAMPLE,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        fontSize = 16.sp
    )
}

@Composable
private fun EmojiSection() {
    DebugSectionTitle(SECTION_EMOJI)
    Text(text = TEXT_EMOJI, fontSize = 24.sp)
}

@Composable
private fun LineHeightSection() {
    DebugSectionTitle(SECTION_LINE_HEIGHT)
    val lineHeights = listOf(1.2f to LABEL_LINE_HEIGHT_1_2X, 1.5f to LABEL_LINE_HEIGHT_1_5X, 2.0f to LABEL_LINE_HEIGHT_2_0X)
    val baseSize = 16.sp
    lineHeights.forEach { (multiplier, label) ->
        Text(
            text = TEXT_LINE_HEIGHT_SAMPLE,
            fontSize = baseSize,
            lineHeight = baseSize * multiplier,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(4.dp)
        )
        Text(
            text = "$PREFIX_LINE_HEIGHT$label",
            fontSize = 10.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun TextBackgroundSection() {
    DebugSectionTitle(SECTION_TEXT_BACKGROUND)
    Text(
        text = TEXT_BG_SAMPLE_1,
        fontSize = 16.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFFFF3E0))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = TEXT_BG_SAMPLE_2,
        fontSize = 16.sp,
        color = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
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
