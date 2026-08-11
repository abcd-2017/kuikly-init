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
import com.kuikly.init.common.widget.LocalContextProvider
import com.kuikly.init.business.debug.impl.ui.widgets.DebugSectionTitle
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_text")
public class DebugTextPage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            LocalContextProvider {
                val pageTitle = stringResource(DebugImplMR.strings.debug_text_title)
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
    val sectionFontSize = stringResource(DebugImplMR.strings.debug_text_section_font_size)
    val sectionColor = stringResource(DebugImplMR.strings.debug_text_section_color)
    val sectionAlign = stringResource(DebugImplMR.strings.debug_text_section_align)
    val sectionFontWeight = stringResource(DebugImplMR.strings.debug_text_section_font_weight)
    val sectionMultiLine = stringResource(DebugImplMR.strings.debug_text_section_multi_line)
    val sectionEmoji = stringResource(DebugImplMR.strings.debug_text_section_emoji)
    val sectionLineHeight = stringResource(DebugImplMR.strings.debug_text_section_line_height)
    val sectionTextBackground =
        stringResource(DebugImplMR.strings.debug_text_section_text_background)
    val sampleText = stringResource(DebugImplMR.strings.debug_text_sample)
    val textAlignLeft = stringResource(DebugImplMR.strings.debug_text_align_left)
    val textAlignCenter = stringResource(DebugImplMR.strings.debug_text_align_center)
    val textAlignRight = stringResource(DebugImplMR.strings.debug_text_align_right)
    val textWeightNormal = stringResource(DebugImplMR.strings.debug_text_weight_normal)
    val textWeightBold = stringResource(DebugImplMR.strings.debug_text_weight_bold)
    val textWeightLight = stringResource(DebugImplMR.strings.debug_text_weight_light)
    val textLongSample = stringResource(DebugImplMR.strings.debug_text_long_sample)
    val textEmoji = stringResource(DebugImplMR.strings.debug_text_emoji)
    val textLineHeightSample = stringResource(DebugImplMR.strings.debug_text_line_height_sample)
    val labelLineHeight12x = stringResource(DebugImplMR.strings.debug_text_label_line_height_1_2x)
    val labelLineHeight15x = stringResource(DebugImplMR.strings.debug_text_label_line_height_1_5x)
    val labelLineHeight20x = stringResource(DebugImplMR.strings.debug_text_label_line_height_2_0x)
    val prefixLineHeight = stringResource(DebugImplMR.strings.debug_text_prefix_line_height)
    val textBgSample1 = stringResource(DebugImplMR.strings.debug_text_bg_sample_1)
    val textBgSample2 = stringResource(DebugImplMR.strings.debug_text_bg_sample_2)
    val btnClose = stringResource(DebugImplMR.strings.debug_close_page)

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
            text = "$prefixLineHeight$label",
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
