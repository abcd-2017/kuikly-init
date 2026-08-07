package com.kuikly.init.business.debug.impl.render

import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
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
import com.tencent.kuikly.compose.ui.layout.ContentScale
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.kuikly.init.base.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugSectionTitle
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.toast.ToastDuration
import com.kuikly.init.common.base.platform.toast.provideToast
import com.tencent.kuikly.compose.coil3.rememberAsyncImagePainter
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

private const val PAGE_TITLE = "🖼️ 图片渲染测试"
private const val SECTION_NETWORK_IMAGE = "网络图片"
private const val SECTION_ROUNDED = "圆角图片"
private const val SECTION_CONTENT_SCALE = "缩放模式"
private const val SECTION_BORDERED = "带边框图片"
private const val SECTION_FIXED_SIZE = "固定尺寸"
private const val SECTION_CLICKABLE = "图片点击"
private const val LABEL_IMAGE_1 = "图1"
private const val LABEL_IMAGE_2 = "图2"
private const val LABEL_IMAGE_3 = "图3"
private const val LABEL_0DP = "0dp"
private const val LABEL_8DP = "8dp"
private const val LABEL_16DP = "16dp"
private const val LABEL_CIRCLE = "圆形"
private const val LABEL_FIT = "Fit"
private const val LABEL_CROP = "Crop"
private const val LABEL_FILL_BOUNDS = "FillBounds"
private const val LABEL_BORDERED = "带边框"
private const val LABEL_50X50 = "50x50"
private const val LABEL_100X100 = "100x100"
private const val LABEL_150X150 = "150x150"
private const val LABEL_CLICK_TEST = "点击测试"
private const val MSG_IMAGE_CLICKED = "图片被点击"
private const val BTN_CLOSE = "关闭页面"

@Page("debug_image")
internal class DebugImagePage : BasePager() {

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
                DebugImageContent(
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
private fun DebugImageContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    LazyColumn(
        modifier = modifier.padding(16.dp)
    ) {
        item { NetworkImageSection() }
        item { RoundedCornerSection() }
        item { ContentScaleSection() }
        item { BorderedImageSection() }
        item { FixedSizeSection() }
        item { ClickableImageSection() }
        item { CloseButtonSection(onClose) }
    }
}

@Composable
private fun NetworkImageSection() {
    DebugSectionTitle(SECTION_NETWORK_IMAGE)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            "https://picsum.photos/200/200?random=1" to LABEL_IMAGE_1,
            "https://picsum.photos/200/200?random=2" to LABEL_IMAGE_2,
            "https://picsum.photos/200/200?random=3" to LABEL_IMAGE_3
        ).forEach { (url, label) ->
            Column {
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = label,
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
                Text(text = label, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun RoundedCornerSection() {
    DebugSectionTitle(SECTION_ROUNDED)
    val url = "https://picsum.photos/200/200?random=4"
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            0.dp to LABEL_0DP,
            8.dp to LABEL_8DP,
            16.dp to LABEL_16DP
        ).forEach { (radius, label) ->
            Column {
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = label,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(radius)),
                    contentScale = ContentScale.Crop
                )
                Text(text = label, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Column {
            Image(
                painter = rememberAsyncImagePainter(url),
                contentDescription = LABEL_CIRCLE,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp)),
                contentScale = ContentScale.Crop
            )
            Text(text = LABEL_CIRCLE, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun ContentScaleSection() {
    DebugSectionTitle(SECTION_CONTENT_SCALE)
    val url = "https://picsum.photos/300/200?random=5"
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            ContentScale.Fit to LABEL_FIT,
            ContentScale.Crop to LABEL_CROP,
            ContentScale.FillBounds to LABEL_FILL_BOUNDS
        ).forEach { (scale, label) ->
            Column {
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = label,
                    modifier = Modifier
                        .size(90.dp, 60.dp),
                    contentScale = scale
                )
                Text(text = label, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun BorderedImageSection() {
    DebugSectionTitle(SECTION_BORDERED)
    val url = "https://picsum.photos/200/200?random=6"
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = LABEL_BORDERED,
        modifier = Modifier
            .size(120.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun FixedSizeSection() {
    DebugSectionTitle(SECTION_FIXED_SIZE)
    val url = "https://picsum.photos/200/200?random=7"
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(50.dp to LABEL_50X50, 100.dp to LABEL_100X100, 150.dp to LABEL_150X150).forEach { (size, label) ->
            Column {
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = label,
                    modifier = Modifier.size(size),
                    contentScale = ContentScale.Crop
                )
                Text(text = label, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun ClickableImageSection() {
    DebugSectionTitle(SECTION_CLICKABLE)
    val url = "https://picsum.photos/200/200?random=8"
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = LABEL_CLICK_TEST,
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                provideToast().show(MSG_IMAGE_CLICKED)
            },
        contentScale = ContentScale.Crop
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
