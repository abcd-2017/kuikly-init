package com.kuikly.init.business.debug.impl.ui.widgets

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.CenterAlignedTopAppBar
import com.tencent.kuikly.compose.material3.ExperimentalMaterial3Api
import com.tencent.kuikly.compose.material3.MaterialTheme
import com.tencent.kuikly.compose.material3.Scaffold
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.material3.TextField
import com.tencent.kuikly.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Brush
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR

/**
 * Debug 页面通用按钮样式（渐变紫色）
 */
@Composable
internal fun DebugTestButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF7B7FE4), Color(0xFFA65CF9))
                ),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.White, fontSize = 15.sp)
    }
}

/**
 * Debug 页面通用结果展示区
 */
@Composable
internal fun DebugResultArea(text: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color(0xFF333333),
            style = TextStyle(lineHeight = 20.sp)
        )
    }
}

/**
 * Debug 页面分组标题
 */
@Composable
internal fun DebugSectionTitle(title: String) {
    Text(
        title,
        fontSize = 16.sp,
        color = Color(0xFF333333),
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

/**
 * Debug 信息行（标签: 值）
 */
@Composable
internal fun DebugInfoRow(label: String, value: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "$label: $value",
            fontSize = 14.sp,
            color = Color(0xFF555555)
        )
    }
}

/**
 * 可点击卡片（带箭头）
 */
@Composable
internal fun DebugCardItem(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color(0xFF999999)
            )
        }
        Text(
            text = ">",
            fontSize = 18.sp,
            color = Color(0xFFCCCCCC)
        )
    }
}

/**
 * Debug 页面垂直间距
 */
@Composable
internal fun DebugVSpacer(height: Dp = 12.dp) {
    Spacer(modifier = Modifier.height(height))
}

/**
 * Debug 页面通用输入框
 */
@Composable
internal fun DebugTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {
    TextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        textStyle = TextStyle(color = Color.Black),
        placeholder = { Text(placeholder) },
        onValueChange = onValueChange
    )
}

/**
 * Debug 页面脚手架（带顶部栏 + 关闭按钮）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DebugScaffold(
    title: String,
    onClose: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val closeText = stringResource(DebugImplMR.strings.debug_close)
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                actions = {
                    Text(
                        text = closeText,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .clickable { onClose() }
                            .padding(10.dp)
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        content = content
    )
}
