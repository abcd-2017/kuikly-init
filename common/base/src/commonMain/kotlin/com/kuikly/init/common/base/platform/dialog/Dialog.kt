package com.kuikly.init.common.base.platform.dialog

/** 对话框操作类型 */
enum class DialogAction { POSITIVE, NEGATIVE, NEUTRAL, DISMISSED }

/**
 * 对话框能力抽象
 *
 * 提供 Alert / Confirm / ActionSheet 三类对话框。
 * UI 类能力，具体实现需确保在主线程调用。
 */
expect class Dialog {
    /** Alert 提示框：标题 + 消息 + 确认按钮 */
    fun showAlert(title: String, message: String, confirmText: String = "确定")

    /** Confirm 确认框：返回用户是否点击确认 */
    suspend fun showConfirm(
        title: String,
        message: String,
        confirmText: String = "确定",
        cancelText: String = "取消"
    ): Boolean

    /** Action Sheet：多选项，返回用户选择的索引（-1 表示取消） */
    suspend fun showActionSheet(
        title: String?,
        message: String?,
        options: List<String>
    ): Int
}

/** 全局访问入口 */
expect fun provideDialog(): Dialog
