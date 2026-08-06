package com.kuikly.init.common.base.platform.scan

/**
 * 扫码结果
 *
 * @param content 扫码内容（文本/URL 等）
 * @param format 码制式（QR_CODE / EAN_13 / CODE_128 等）
 */
data class ScanResult(
    val content: String,
    val format: String
)

/**
 * 扫码能力抽象
 *
 * 提供启动扫码界面并获取扫码结果的功能。
 * - Android: 基于 ML Kit BarcodeScanning
 * - iOS: 基于 AVFoundation.AVCaptureMetadataOutput
 * - OHOS: 基于 @kit.ScanKit
 */
expect class Scanner {
    /** 启动扫码（callback 返回扫码结果，取消或失败返回 null） */
    fun startScan(callback: (ScanResult?) -> Unit)
}

/** 全局访问入口 */
expect fun provideScanner(): Scanner
