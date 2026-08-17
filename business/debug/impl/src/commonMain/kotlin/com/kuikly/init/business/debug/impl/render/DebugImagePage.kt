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
import com.kuikly.init.common.widget.BasePager
import com.kuikly.init.business.debug.impl.ui.widgets.DebugSectionTitle
import com.kuikly.init.business.debug.impl.ui.widgets.DebugVSpacer
import com.kuikly.init.common.base.platform.toast.ToastDuration
import com.kuikly.init.common.base.platform.toast.provideToast
import com.tencent.kuikly.compose.coil3.rememberAsyncImagePainter
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.setContent
import com.tencent.tmm.kmmresource.compose.stringResource
import com.kuikly.init.business.debug.impl.DebugImplMR
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.module.RouterModule

@Page("debug_image")
public class DebugImagePage : BasePager() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun willInit() {
        super.willInit()
        setContent {
            
            val pageTitle = stringResource(DebugImplMR.strings.debug_image_title)
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
}

@Composable
private fun DebugImageContent(
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    val sectionNetworkImage = stringResource(DebugImplMR.strings.debug_image_section_network_image)
    val sectionRounded = stringResource(DebugImplMR.strings.debug_image_section_rounded)
    val sectionContentScale = stringResource(DebugImplMR.strings.debug_image_section_content_scale)
    val sectionBordered = stringResource(DebugImplMR.strings.debug_image_section_bordered)
    val sectionFixedSize = stringResource(DebugImplMR.strings.debug_image_section_fixed_size)
    val sectionClickable = stringResource(DebugImplMR.strings.debug_image_section_clickable)
    val labelImage1 = stringResource(DebugImplMR.strings.debug_image_label_image_1)
    val labelImage2 = stringResource(DebugImplMR.strings.debug_image_label_image_2)
    val labelImage3 = stringResource(DebugImplMR.strings.debug_image_label_image_3)
    val label0dp = stringResource(DebugImplMR.strings.debug_image_label_0dp)
    val label8dp = stringResource(DebugImplMR.strings.debug_image_label_8dp)
    val label16dp = stringResource(DebugImplMR.strings.debug_image_label_16dp)
    val labelCircle = stringResource(DebugImplMR.strings.debug_image_label_circle)
    val labelFit = stringResource(DebugImplMR.strings.debug_image_label_fit)
    val labelCrop = stringResource(DebugImplMR.strings.debug_image_label_crop)
    val labelFillBounds = stringResource(DebugImplMR.strings.debug_image_label_fill_bounds)
    val labelBordered = stringResource(DebugImplMR.strings.debug_image_label_bordered)
    val label50x50 = stringResource(DebugImplMR.strings.debug_image_label_50x50)
    val label100x100 = stringResource(DebugImplMR.strings.debug_image_label_100x100)
    val label150x150 = stringResource(DebugImplMR.strings.debug_image_label_150x150)
    val labelClickTest = stringResource(DebugImplMR.strings.debug_image_label_click_test)
    val msgImageClicked = stringResource(DebugImplMR.strings.debug_image_msg_image_clicked)
    val btnClose = stringResource(DebugImplMR.strings.debug_close_page)

    LazyColumn(
        modifier = modifier.padding(16.dp)
    ) {
        item { NetworkImageSection(sectionNetworkImage, labelImage1, labelImage2, labelImage3) }
        item { RoundedCornerSection(sectionRounded, label0dp, label8dp, label16dp, labelCircle) }
        item { ContentScaleSection(sectionContentScale, labelFit, labelCrop, labelFillBounds) }
        item { BorderedImageSection(sectionBordered, labelBordered) }
        item { FixedSizeSection(sectionFixedSize, label50x50, label100x100, label150x150) }
        item { ClickableImageSection(sectionClickable, labelClickTest, msgImageClicked) }
        item { CloseButtonSection(btnClose, onClose) }
    }
}

@Composable
private fun NetworkImageSection(
    title: String,
    labelImage1: String,
    labelImage2: String,
    labelImage3: String
) {
    DebugSectionTitle(title)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            "https://picsum.photos/200/200?random=1" to labelImage1,
            "https://picsum.photos/200/200?random=2" to labelImage2,
            "https://picsum.photos/200/200?random=3" to labelImage3
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
private fun RoundedCornerSection(
    title: String,
    label0dp: String,
    label8dp: String,
    label16dp: String,
    labelCircle: String
) {
    DebugSectionTitle(title)
    val url = "https://picsum.photos/200/200?random=4"
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            0.dp to label0dp,
            8.dp to label8dp,
            16.dp to label16dp
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
                contentDescription = labelCircle,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(40.dp)),
                contentScale = ContentScale.Crop
            )
            Text(text = labelCircle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun ContentScaleSection(
    title: String,
    labelFit: String,
    labelCrop: String,
    labelFillBounds: String
) {
    DebugSectionTitle(title)
    val url = "https://picsum.photos/300/200?random=5"
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(
            ContentScale.Fit to labelFit,
            ContentScale.Crop to labelCrop,
            ContentScale.FillBounds to labelFillBounds
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
private fun BorderedImageSection(title: String, labelBordered: String) {
    DebugSectionTitle(title)
    val url = "https://picsum.photos/200/200?random=6"
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = labelBordered,
        modifier = Modifier
            .size(120.dp)
            .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun FixedSizeSection(
    title: String,
    label50x50: String,
    label100x100: String,
    label150x150: String
) {
    DebugSectionTitle(title)
    val url = "https://picsum.photos/200/200?random=7"
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        listOf(50.dp to label50x50, 100.dp to label100x100, 150.dp to label150x150).forEach { (size, label) ->
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
private fun ClickableImageSection(
    title: String,
    labelClickTest: String,
    msgImageClicked: String
) {
    DebugSectionTitle(title)
    val url = "https://picsum.photos/200/200?random=8"
    Image(
        painter = rememberAsyncImagePainter(url),
        contentDescription = labelClickTest,
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                provideToast().show(msgImageClicked)
            },
        contentScale = ContentScale.Crop
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
