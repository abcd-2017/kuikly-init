package com.kuikly.init.common.base.platform.keyboard

/**
 * 软键盘控制能力抽象
 *
 * 提供隐藏/显示软键盘功能。
 * iOS show() 需要当前输入框 becomeFirstResponder，当前实现为空操作。
 */
expect class Keyboard {
    /** 隐藏软键盘 */
    fun hide()

    /** 显示软键盘（请求焦点） */
    fun show()
}

/** 全局访问入口 */
expect fun provideKeyboard(): Keyboard
