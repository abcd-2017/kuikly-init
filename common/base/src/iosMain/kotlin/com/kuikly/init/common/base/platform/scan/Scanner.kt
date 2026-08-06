package com.kuikly.init.common.base.platform.scan

/**
 * iOS 扫码实现
 *
 * 注意：完整实现需要 AVFoundation.AVCaptureMetadataOutput + 自定义扫描 ViewController。
 * 当前为占位实现，启动时返回 null。
 * TODO: 实现 AVCaptureSession 扫描界面，通过 UIViewController 呈现。
 */
actual class Scanner {

    actual fun startScan(callback: (ScanResult?) -> Unit) {
        // TODO: 实现 AVFoundation 扫码
        // 1. 创建 AVCaptureSession
        // 2. 配置 AVCaptureMetadataOutput 代理
        // 3. 呈现扫描 ViewController
        // 4. 解析 AVMetadataMachineReadableCodeObject 为 ScanResult
        // 注意：扫描需要 UIViewController 上下文
        callback(null)
    }
}

actual fun provideScanner(): Scanner = Scanner()
