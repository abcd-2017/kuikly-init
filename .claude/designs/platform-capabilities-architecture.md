# 平台能力抽取架构规范

## 模块结构

所有平台能力放在 `common:base` 模块，纯工具函数放在 `common:util`。

### 分层原则

| 模块 | 判断标准 |
|------|---------|
| **common:base** | OHOS 侧需要走 KNOI 调原生 API 的能力（expect/actual） |
| **common:util** | 纯 Kotlin 计算，三端行为一致，不依赖任何原生 API |

### 包结构

```
common/base/src/
├── commonMain/kotlin/com/kuikly/init/common/base/platform/
│   ├── clipboard/Clipboard.kt          # 剪贴板
│   ├── toast/Toast.kt                  # Toast
│   ├── dialog/Dialog.kt                # 对话框
│   ├── share/Share.kt                  # 系统分享
│   ├── keyboard/Keyboard.kt            # 键盘控制
│   ├── phone/Phone.kt                  # 拨打电话
│   ├── permission/Permission.kt        # 权限请求
│   ├── screen/ScreenInfo.kt            # 屏幕信息
│   ├── app/AppInfo.kt                  # 应用信息
│   ├── time/Timezone.kt                # 时区信息
│   ├── crypto/Crypto.kt                # 加解密
│   ├── picker/FilePicker.kt            # 文件选择器
│   ├── picker/MediaPicker.kt           # 相册选择
│   ├── camera/Camera.kt                # 拍照
│   ├── biometric/Biometric.kt          # 生物识别
│   ├── location/Location.kt            # 地理位置
│   ├── scan/Scan.kt                    # 扫码
│   ├── haptic/Haptic.kt                # 震动反馈
│   ├── settings/Settings.kt            # 系统设置
│   └── service/BaseServiceLocator.kt   # 统一服务访问入口
├── androidMain/kotlin/.../platform/    # Android actual 实现
├── iosMain/kotlin/.../platform/        # iOS actual 实现
└── ohosArm64Main/kotlin/.../platform/   # OHOS actual 实现（KNOI）
```

## expect/actual 模式

### commonMain（expect 声明）

```kotlin
package com.kuikly.init.common.base.platform.clipboard

/** 剪贴板能力 */
expect class Clipboard {
    /** 写入文本到剪贴板 */
    fun copyText(content: String)
    /** 读取剪贴板文本 */
    fun pasteText(): String
    /** 清空剪贴板 */
    fun clear()
}

/** 全局访问入口 */
expect fun provideClipboard(): Clipboard
```

### androidMain（Android actual）

```kotlin
package com.kuikly.init.common.base.platform.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.kuikly.init.common.base.platform.appContext

actual class Clipboard(private val context: Context) {
    actual fun copyText(content: String) {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("kuikly", content))
    }
    actual fun pasteText(): String {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return cm.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
    }
    actual fun clear() {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("", ""))
    }
}

actual fun provideClipboard(): Clipboard = Clipboard(appContext)
```

### iosMain（iOS actual）

```kotlin
package com.kuikly.init.common.base.platform.clipboard

import platform.UIKit.UIPasteboard
import platform.Foundation.NSString

actual class Clipboard {
    actual fun copyText(content: String) {
        UIPasteboard.generalPasteboard.string = content
    }
    actual fun pasteText(): String {
        return UIPasteboard.generalPasteboard.string ?: ""
    }
    actual fun clear() {
        UIPasteboard.generalPasteboard.string = ""
    }
}

actual fun provideClipboard(): Clipboard = Clipboard()
```

### ohosArm64Main（OHOS actual via KNOI）

```kotlin
package com.kuikly.init.common.base.platform.clipboard

import com.kuikly.init.common.base.getIOHOSPlatformServiceApi

actual class Clipboard {
    private val service get() = getIOHOSPlatformServiceApi()
    actual fun copyText(content: String) {
        service?.setPasteboardText(content)
    }
    actual fun pasteText(): String {
        return service?.getPasteboardText() ?: ""
    }
    actual fun clear() {
        service?.clearPasteboard()
    }
}

actual fun provideClipboard(): Clipboard = Clipboard()
```

## OHOS KNOI 扩展模式

### 1. 扩展 IOHOSPlatformService 接口

在 `common/base/src/ohosArm64Main/kotlin/.../PlatformServices.kt` 中新增方法：

```kotlin
@ServiceConsumer
interface IOHOSPlatformService {
    // ... 已有方法 ...

    // 剪贴板
    fun setPasteboardText(content: String)
    fun getPasteboardText(): String
    fun clearPasteboard()

    // Toast
    fun showToast(message: String, duration: Int /* 0=short, 1=long */)
}
```

### 2. 重新构建生成 KNOI 代理

运行 KSP 构建后自动生成 `IOHOSPlatformServiceProxy`。

### 3. OHOS ETS 侧实现

在 `OHOSPlatformServiceImpl.ets` 中实现：

```typescript
setPasteboardText(content: string): void {
    const pasteboard = pasteboardManager.getCommonTextPasteData()
    pasteboard.setText(content)
    pasteboardManager.setPasteboardData(pasteboard)
}

getPasteboardText(): string {
    const pasteboard = pasteboardManager.getCommonTextPasteData()
    return pasteboard.getText() ?? ""
}

clearPasteboard(): void {
    pasteboardManager.clear()
}

showToast(message: string, duration: number): void {
    promptAction.showToast({ message, duration: duration === 0 ? 2000 : 4000 })
}
```

### 4. PlatformServiceLocator.ets 同步扩展

确保所有新增方法在 ServiceLocator 中暴露。

## BaseServiceLocator（统一入口）

```kotlin
package com.kuikly.init.common.base.platform

/**
 * 平台能力统一访问入口
 *
 * 业务代码通过此对象访问所有平台能力，无需关心平台差异。
 */
object BaseServiceLocator {
    val clipboard: Clipboard get() = provideClipboard()
    val toast: Toast get() = provideToast()
    val dialog: Dialog get() = provideDialog()
    val share: Share get() = provideShare()
    // ... 其他能力
}
```

## 业务代码使用方式

```kotlin
import com.kuikly.init.common.base.platform.BaseServiceLocator

// 复制到剪贴板
BaseServiceLocator.clipboard.copyText("hello")

// 显示 Toast
BaseServiceLocator.toast.show("操作成功")

// 系统分享
BaseServiceLocator.share.shareText("分享内容")
```

## 实现注意事项

1. **Context 获取**：Android 侧通过全局 `appContext` 获取 Application Context
2. **主线程检查**：UI 类能力（Toast/Dialog）需确保在主线程调用
3. **权限处理**：涉及权限的能力（相机/定位/存储）需先检查权限状态
4. **空安全**：OHOS KNOI 调用返回 nullable，需做好兜底
5. **错误处理**：所有 actual 实现需 try-catch，避免原生异常穿透到 Kotlin 层
6. **线程安全**：跨线程调用需做好同步处理

## BridgeModule 迁移策略

现有 BridgeModule 中的能力逐步迁移到 BaseServiceLocator：

1. 先在 common:base 中定义新接口并实现
2. 业务代码切换到 BaseServiceLocator
3. BridgeModule 标记 `@Deprecated`
4. 确认无调用后移除旧实现

## 依赖库选择

| 能力 | 库选择 |
|------|--------|
| 加解密 | `dev.whyoleg.cryptography` (KMP, 支持 ohosArm64) |
| 时间 | Kotlinx DateTime |
| JSON | Kotlinx Serialization |
