package com.kuikly.init.business.debug.render

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
import com.kuikly.init.business.debug.ui.widgets.DebugSectionTitle
import com.kuikly.init.business.debug.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.toast.Toast
import com.tencent.kuikly.compose.coil3.rememberAsyncImagePainter
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_image")
internal class DebugImagePage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        val ctx = this
        setContent {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("🖼️ 图片渲染测试") },
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
                    onClose = { ctx.acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage() }
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
    DebugSectionTitle("网络图片")
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            "https://picsum.photos/200/200?random=1" to "图1",
            "https://picsum.photos/200/200?random=2" to "图2",
            "https://picsum.photos/200/200?random=3" to "图3"
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
    DebugSectionTitle("圆角图片")
    val url = "https://picsum.photos/200/200?random=4"
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            0.dp to "0dp",
            8.dp to "8dp",
            16.dp to "16.dp"
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
                contentDescription = "圆形",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp)),
                contentScale = ContentScale.Crop
            )
            Text(text = "圆形", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun ContentScaleSection() {
    DebugSectionTitle("缩放模式")
    val url = "https://picsum.photos/300/200?random=5"
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            ContentScale.Fit to "Fit",
            ContentScale.Crop to "Crop",
            ContentScale.FillBounds to "FillBounds"
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
    DebugSectionTitle("带边框图片")
    val url = "https://picsum.photos/200/200?random=6"
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = "带边框",
        modifier = Modifier
            .size(120.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun FixedSizeSection() {
    DebugSectionTitle("固定尺寸")
    val url = "https://picsum.photos/200/200?random=7"
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(50.dp to "50x50", 100.dp to "100x100", 150.dp to "150x150").forEach { (size, label) ->
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
    DebugSectionTitle("图片点击")
    val url = "https://picsum.photos/200/200?random=8"
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = "点击测试",
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                Toast().show("图片被点击")
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
            text = "关闭页面",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp
        )
    }
    DebugVSpacer(32.dp)
}
