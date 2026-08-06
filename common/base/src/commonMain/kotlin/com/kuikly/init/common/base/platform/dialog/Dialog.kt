package com.kuikly.init.common.base.platform.dialog

/** 对话框操作类型 */
enum class DialogAction { POSITIVE, NEGATIVE, NEUTRAL, DISMISSED }

/**
 * 对话框能力抽象
 *
 * 提供 Alert / Confirm / ActionSheet 三类对话框。
 * UI 类能力，具体实现需确保在主线程调用。
 * 异步方法通过 callback 返回结果。
 */
expect class Dialog {
    /** Alert 提示框：标题 + 消息 + 确认按钮 */
    fun showAlert(title: String, message: String, confirmText: String = "确定")

    /** Confirm 确认框：callback 返回用户选择 0=确认, 1=取消 */
    fun showConfirm(
        title: String,
        message: String,
        confirmText: String = "确定",
        cancelText: String = "取消",
        callback: (Int) -> Unit
    )

    /** Action Sheet：多选项，callback 返回用户选择的索引（-1 表示取消） */
    fun showActionSheet(
        title: String?,
        message: String?,
        options: List<String>,
        callback: (Int) -> Unit
    )
}

/** 全局访问入口 */
expect fun provideDialog(): Dialog
