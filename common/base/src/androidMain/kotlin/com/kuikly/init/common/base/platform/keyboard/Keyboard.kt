package com.kuikly.init.common.base.platform.keyboard

import android.content.Context
import android.view.inputmethod.InputMethodManager
import com.kuikly.init.common.base.platform.AppContext

/**
 * Android 软键盘控制实现
 *
 * 基于 InputMethodManager，通过当前焦点 View 操作键盘。
 * 若无当前焦点 View，hide()/show() 为空操作。
 */
actual class Keyboard(private val context: Context) {

    private val imm: InputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    actual fun hide() {
        try {
            val activity = AppContext.currentActivity ?: return
            val focusView = activity.currentFocus ?: return
            imm.hideSoftInputFromWindow(focusView.windowToken, 0)
        } catch (e: Exception) {
            // 隐藏失败静默处理
        }
    }

    actual fun show() {
        try {
            val activity = AppContext.currentActivity ?: return
            val focusView = activity.currentFocus ?: return
            focusView.requestFocus()
            imm.showSoftInput(focusView, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {
            // 显示失败静默处理
        }
    }
}

actual fun provideKeyboard(): Keyboard = Keyboard(AppContext.application)
