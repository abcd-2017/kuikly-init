// OHOS 兼容版本的 BasicWidget
// 注意：某些 Compose 扩展在 OHOS 不可用，这里提供简化实现

package com.kuikly.init.common.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
public fun Button(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit) = {}
) {
    Box(modifier = modifier.padding(8.dp)) {
        content()
    }
}

@Composable
public fun TextField(
    modifier: Modifier = Modifier,
    value: String = "",
    placeholder: String = "",
    onValueChange: (String) -> Unit,
    textStyle: androidx.compose.ui.text.TextStyle = androidx.compose.ui.text.TextStyle.Default,
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(value)) }
    BasicTextField(
        value = textFieldValue,
        onValueChange = { 
            textFieldValue = it
            onValueChange(it.text)
        },
        modifier = modifier
    )
}
