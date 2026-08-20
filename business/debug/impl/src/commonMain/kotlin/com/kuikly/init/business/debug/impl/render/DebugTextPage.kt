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
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugSectionTitle
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_text")
public class DebugTextPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            
            val pageTitle = "文本渲染测试"
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(pageTitle) },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )

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
}

@Composable
private fun DebugTextContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    val sectionFontSize = "字体大小"
    val sectionColor = "颜色"
    val sectionAlign = "对齐"
    val sectionFontWeight = "字重"
    val sectionMultiLine = "多行截断"
    val sectionEmoji = "Emoji"
    val sectionLineHeight = "行高"
    val sectionTextBackground =
        "文本背景"
    val sampleText = "Kuikly 跨端脚手架"
    val textAlignLeft = "左对齐文本 — Kuikly 跨端脚手架"
    val textAlignCenter = "居中文本 — Kuikly 跨端脚手架"
    val textAlignRight = "右对齐文本 — Kuikly 跨端脚手架"
    val textWeightNormal = "Normal 字重"
    val textWeightBold = "Bold 字重"
    val textWeightLight = "Light 字重"
    val textLongSample = "这是一段很长的文本，用于测试 maxLines 和 overflow 的截断效果。Kuikly 是腾讯推出的跨端开发框架，支持 Android、iOS、鸿蒙三端。它基于 Kotlin Multiplatform 技术，提供了统一的 DSL 和组件能力。"
    val textEmoji = "🎉🔥💯👍🚀❤️✨"
    val textLineHeightSample = "这是一段用于测试不同行高效果的文本。行高影响段落的可读性和视觉密度。"
    val labelLineHeight12x = "1.2x"
    val labelLineHeight15x = "1.5x"
    val labelLineHeight20x = "2.0x"
    val prefixLineHeight = "↑ lineHeight "
    val textBgSample1 = "带背景色的文字"
    val textBgSample2 = "另一种背景色"
    val btnClose = "关闭页面"

    LazyColumn(
        modifier = modifier.padding(16.dp)
    ) {
        item { FontSizeSection(sectionFontSize, sampleText) }
        item { ColorSection(sectionColor, sampleText) }
        item { AlignSection(sectionAlign, textAlignLeft, textAlignCenter, textAlignRight) }
        item {
            FontWeightSection(
                sectionFontWeight,
                textWeightNormal,
                textWeightBold,
                textWeightLight
            )
        }
        item { MultiLineTruncateSection(sectionMultiLine, textLongSample) }
        item { EmojiSection(sectionEmoji, textEmoji) }
        item {
            LineHeightSection(
                sectionLineHeight,
                textLineHeightSample,
                labelLineHeight12x,
                labelLineHeight15x,
                labelLineHeight20x,
                prefixLineHeight
            )
        }
        item { TextBackgroundSection(sectionTextBackground, textBgSample1, textBgSample2) }
        item { CloseButtonSection(btnClose, onClose) }
    }
}

@Composable
private fun FontSizeSection(title: String, sampleText: String) {
    DebugSectionTitle(title)
    listOf(12.sp, 14.sp, 16.sp, 20.sp, 24.sp).forEach { size ->
        Text(text = sampleText, fontSize = size)
    }
}

@Composable
private fun ColorSection(title: String, sampleText: String) {
    DebugSectionTitle(title)
    val colors = listOf(
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Gray,
        Color.Black
    )
    colors.forEach { color ->
        Text(text = sampleText, color = color, fontSize = 16.sp)
    }
}

@Composable
private fun AlignSection(
    title: String,
    textAlignLeft: String,
    textAlignCenter: String,
    textAlignRight: String
) {
    DebugSectionTitle(title)
    Text(
        text = textAlignLeft,
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 16.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = textAlignCenter,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 16.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = textAlignRight,
        textAlign = TextAlign.End,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 16.sp
    )
}

@Composable
private fun FontWeightSection(
    title: String,
    textWeightNormal: String,
    textWeightBold: String,
    textWeightLight: String
) {
    DebugSectionTitle(title)
    Text(text = textWeightNormal, fontWeight = FontWeight.Normal, fontSize = 16.sp)
    Text(text = textWeightBold, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Text(text = textWeightLight, fontWeight = FontWeight.Light, fontSize = 16.sp)
}

@Composable
private fun MultiLineTruncateSection(title: String, textLongSample: String) {
    DebugSectionTitle(title)
    Text(
        text = textLongSample,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        fontSize = 16.sp
    )
}

@Composable
private fun EmojiSection(title: String, textEmoji: String) {
    DebugSectionTitle(title)
    Text(text = textEmoji, fontSize = 24.sp)
}

@Composable
private fun LineHeightSection(
    title: String,
    textLineHeightSample: String,
    label12x: String,
    label15x: String,
    label20x: String,
    prefixLineHeight: String
) {
    DebugSectionTitle(title)
    val lineHeights = listOf(1.2f to label12x, 1.5f to label15x, 2.0f to label20x)
    val baseSize = 16.sp
    lineHeights.forEach { (multiplier, label) ->
        Text(
            text = textLineHeightSample,
            fontSize = baseSize,
            lineHeight = baseSize * multiplier,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(4.dp)
        )
        Text(
            text = "\$prefixLineHeight\$label",
            fontSize = 10.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun TextBackgroundSection(
    title: String,
    textBgSample1: String,
    textBgSample2: String
) {
    DebugSectionTitle(title)
    Text(
        text = textBgSample1,
        fontSize = 16.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFFFF3E0))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = textBgSample2,
        fontSize = 16.sp,
        color = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
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
