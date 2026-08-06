package com.kuikly.init.common.base.platform.phone

/**
 * 电话拨打能力抽象
 *
 * 跳转到系统拨号界面，无需权限。
 * 不会直接拨打电话，用户需手动确认拨号。
 */
expect class Phone {
    /** 拨打电话（跳转到拨号界面） */
    fun call(phoneNumber: String)
}

/** 全局访问入口 */
expect fun providePhone(): Phone
