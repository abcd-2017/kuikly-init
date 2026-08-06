package com.kuikly.init.business.debug.render

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
import com.kuikly.init.business.debug.ui.widgets.DebugSectionTitle
import com.kuikly.init.business.debug.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_text")
internal class DebugTextPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        val ctx = this
        setContent {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("📝 文本渲染测试") },
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
                    onClose = { ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
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
    DebugSectionTitle("字体大小")
    val sampleText = "Kuikly 跨端脚手架"
    listOf(12.sp, 14.sp, 16.sp, 20.sp, 24.sp).forEach { size ->
        Text(text = sampleText, fontSize = size)
    }
}

@Composable
private fun ColorSection() {
    DebugSectionTitle("颜色")
    val colors = listOf(
        Color.Red to "红色",
        Color.Green to "绿色",
        Color.Blue to "蓝色",
        Color.Gray to "灰色",
        Color.Black to "黑色"
    )
    colors.forEach { (color, _) ->
        Text(text = "Kuikly 跨端脚手架", color = color, fontSize = 16.sp)
    }
}

@Composable
private fun AlignSection() {
    DebugSectionTitle("对齐")
    Text(
        text = "左对齐文本 — Kuikly 跨端脚手架",
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 16.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "居中文本 — Kuikly 跨端脚手架",
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 16.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "右对齐文本 — Kuikly 跨端脚手架",
        textAlign = TextAlign.End,
        modifier = Modifier.fillMaxWidth(),
        fontSize = 16.sp
    )
}

@Composable
private fun FontWeightSection() {
    DebugSectionTitle("字重")
    Text(text = "Normal 字重", fontWeight = FontWeight.Normal, fontSize = 16.sp)
    Text(text = "Bold 字重", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Text(text = "Light 字重", fontWeight = FontWeight.Light, fontSize = 16.sp)
}

@Composable
private fun MultiLineTruncateSection() {
    DebugSectionTitle("多行截断")
    Text(
        text = "这是一段很长的文本，用于测试 maxLines 和 overflow 的截断效果。" +
                "Kuikly 是腾讯推出的跨端开发框架，支持 Android、iOS、鸿蒙三端。" +
                "它基于 Kotlin Multiplatform 技术，提供了统一的 DSL 和组件能力。",
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        fontSize = 16.sp
    )
}

@Composable
private fun EmojiSection() {
    DebugSectionTitle("Emoji")
    Text(text = "🎉🔥💯👍🚀❤️✨", fontSize = 24.sp)
}

@Composable
private fun LineHeightSection() {
    DebugSectionTitle("行高")
    val sampleText = "这是一段用于测试不同行高效果的文本。行高影响段落的可读性和视觉密度。"
    val lineHeights = listOf(1.2f to "1.2x", 1.5f to "1.5x", 2.0f to "2.0x")
    val baseSize = 16.sp
    lineHeights.forEach { (multiplier, label) ->
        Text(
            text = sampleText,
            fontSize = baseSize,
            lineHeight = baseSize * multiplier,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(4.dp)
        )
        Text(
            text = "↑ lineHeight $label",
            fontSize = 10.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun TextBackgroundSection() {
    DebugSectionTitle("文本背景")
    Text(
        text = "带背景色的文字",
        fontSize = 16.sp,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFFFF3E0))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = "另一种背景色",
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
            text = "关闭页面",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp
        )
    }
    DebugVSpacer(32.dp)
}
