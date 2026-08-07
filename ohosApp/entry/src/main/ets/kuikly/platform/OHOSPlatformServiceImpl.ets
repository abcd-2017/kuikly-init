import { pasteboard } from "@kit.BasicServicesKit"
import promptAction from "@ohos.promptAction"
import { systemShare } from "@kit.ShareKit"
import { picker } from "@kit.CoreFileKit"
import { inputMethod } from "@kit.InputMethodKit"
import { call } from "@kit.TelephonyKit"
import common from "@ohos.app.ability.common"
import { abilityAccessCtrl, bundleManager } from "@kit.AbilityKit"
import userAuth from "@kit.UserAuthenticationKit"
import vibrator from "@kit.SensorServiceKit"
import { OHOSContextProvider } from "./OHOSContextProvider"
import { OHOSDeviceInfo } from "./OHOSDeviceInfo"
import { OHOSFileSystem } from "./OHOSFileSystem"
import { OHOSNetworkMonitor } from "./OHOSNetworkMonitor"
import { OHOSNtpClient } from "./OHOSNtpClient"
import { OHOSCrypto } from "./OHOSCrypto"
import { OHOSScreenInfo } from "./OHOSScreenInfo"
import { OHOSAppInfo } from "./OHOSAppInfo"
import geoLocationManager from "@kit.LocationKit"
import { scanBarcode } from "@kit.ScanKit"
import batteryInfo from "@ohos.batteryInfo"

/**
 * OHOS 平台能力服务实现（KNOI 方式）
 *
 * 通过 registerServiceProvider 注册到 KNOI，
 * Kotlin 侧通过 getIOHOSPlatformServiceApi() 获取此实例。
 */
export class OHOSPlatformServiceImpl {
    private contextProvider: OHOSContextProvider | null = null
    private deviceInfo: OHOSDeviceInfo = new OHOSDeviceInfo()
    private fileSystem: OHOSFileSystem = new OHOSFileSystem()
    private networkMonitor: OHOSNetworkMonitor = new OHOSNetworkMonitor()
    private ntpClient: OHOSNtpClient = new OHOSNtpClient()
    private crypto: OHOSCrypto = new OHOSCrypto()
    private screenInfo: OHOSScreenInfo = new OHOSScreenInfo()
    private appInfo: OHOSAppInfo = new OHOSAppInfo()

    /** 设置上下文提供者（需在 init 后调用） */
    setContextProvider(contextProvider: OHOSContextProvider): void {
        this.contextProvider = contextProvider
    }

    getCacheDirPath(): string {
        return this.contextProvider?.getCacheDirPath() ?? ""
    }

    getFilesDirPath(): string {
        return this.contextProvider?.getFilesDirPath() ?? ""
    }

    readAsset(path: string): Uint8Array {
        return this.contextProvider?.readAsset(path) ?? new Uint8Array(0)
    }

    getDeviceId(): string {
        return this.deviceInfo.getDeviceId()
    }

    getOSVersion(): string {
        return this.deviceInfo.getOSVersion()
    }

    getDeviceModel(): string {
        return this.deviceInfo.getDeviceModel()
    }

    readFile(path: string): Uint8Array {
        return this.fileSystem.readFile(path)
    }

    writeFile(path: string, data: Uint8Array): void {
        this.fileSystem.writeFile(path, data)
    }

    fileExists(path: string): boolean {
        return this.fileSystem.exists(path)
    }

    fileDelete(path: string): boolean {
        return this.fileSystem.delete(path)
    }

    isNetworkConnected(): boolean {
        return this.networkMonitor.isConnected()
    }

    getNetworkType(): string {
        return this.networkMonitor.getNetworkType()
    }

    // ==================== 时区 ====================

    getTimezoneId(): string {
        return this.deviceInfo.getTimezoneId()
    }

    getOffsetMinutes(): number {
        return this.deviceInfo.getOffsetMinutes()
    }

    isDaylightSaving(): boolean {
        return this.deviceInfo.isDaylightSaving()
    }

    getTimezoneAbbreviation(): string {
        return this.deviceInfo.getTimezoneAbbreviation()
    }

    // ==================== NTP ====================

    getServerTime(ntpServer: string): number {
        return this.ntpClient.getServerTime(ntpServer)
    }

    getClockOffset(ntpServer: string): number {
        return this.ntpClient.getClockOffset(ntpServer)
    }

    // ==================== 加解密 ====================

    aesEncrypt(plaintext: string, key: string, iv: string | null): string {
        return this.crypto.aesEncrypt(plaintext, key, iv)
    }

    aesDecrypt(ciphertext: string, key: string, iv: string | null): string {
        return this.crypto.aesDecrypt(ciphertext, key, iv)
    }

    md5(input: string): string {
        return this.crypto.md5(input)
    }

    sha256(input: string): string {
        return this.crypto.sha256(input)
    }

    hmacSha256(data: string, key: string): string {
        return this.crypto.hmacSha256(data, key)
    }

    base64Encode(input: string): string {
        return this.crypto.base64Encode(input)
    }

    base64Decode(input: string): string {
        return this.crypto.base64Decode(input)
    }

    // ==================== 剪贴板 ====================

    setPasteboardText(content: string): void {
        try {
            const pasteData = pasteboard.createData(pasteboard.MIMETYPE_TEXT_PLAIN, content)
            pasteboard.getSystemPasteboard().setData(pasteData)
        } catch (e) {
            console.error(`[OHOSPlatformService] setPasteboardText failed: ${e}`)
        }
    }

    getPasteboardText(): string {
        try {
            const data = pasteboard.getSystemPasteboard().getData()
            return data.getPrimaryText() ?? ""
        } catch (e) {
            console.error(`[OHOSPlatformService] getPasteboardText failed: ${e}`)
            return ""
        }
    }

    clearPasteboard(): void {
        try {
            pasteboard.getSystemPasteboard().clearData()
        } catch (e) {
            console.error(`[OHOSPlatformService] clearPasteboard failed: ${e}`)
        }
    }

    // ==================== Toast ====================

    showToast(message: string, duration: number): void {
        try {
            promptAction.showToast({
                message: message,
                duration: duration === 0 ? 2000 : 3500
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] showToast failed: ${e}`)
        }
    }

    // ==================== 系统分享 ====================

    shareText(text: string): void {
        try {
            const shareData: systemShare.ShareData = {
                shareType: systemShare.ShareType.SHARE_TYPE_TEXT,
                text: text
            }
            systemShare.getShareController(this.contextProvider?.getContext()).showShare(shareData)
        } catch (e) {
            console.error(`[OHOSPlatformService] shareText failed: ${e}`)
        }
    }

    shareLink(url: string, title: string | null, description: string | null): void {
        try {
            const shareData: systemShare.ShareData = {
                shareType: systemShare.ShareType.SHARE_TYPE_URL,
                url: url,
                title: title ?? "",
                description: description ?? ""
            }
            systemShare.getShareController(this.contextProvider?.getContext()).showShare(shareData)
        } catch (e) {
            console.error(`[OHOSPlatformService] shareLink failed: ${e}`)
        }
    }

    shareImage(localPath: string): void {
        try {
            const shareData: systemShare.ShareData = {
                shareType: systemShare.ShareType.SHARE_TYPE_IMAGE,
                localPath: localPath
            }
            systemShare.getShareController(this.contextProvider?.getContext()).showShare(shareData)
        } catch (e) {
            console.error(`[OHOSPlatformService] shareImage failed: ${e}`)
        }
    }

    shareFile(localPath: string, mimeType: string | null): void {
        try {
            const shareData: systemShare.ShareData = {
                shareType: systemShare.ShareType.SHARE_TYPE_FILE,
                localPath: localPath,
                mimeType: mimeType ?? ""
            }
            systemShare.getShareController(this.contextProvider?.getContext()).showShare(shareData)
        } catch (e) {
            console.error(`[OHOSPlatformService] shareFile failed: ${e}`)
        }
    }

    // ==================== 文件选择器 ====================

    pickFile(mimeType: string, allowMultiple: boolean): string {
        try {
            const options = new picker.DocumentSelectOptions()
            options.selectMode = picker.DocumentSelectMode.FILE
            options.maxSelectNumber = allowMultiple ? 10 : 1

            // picker.select 返回 Promise，使用 .then() 处理异步结果
            // 注意：KNOI 同步调用无法等待 Promise，此处仅发起选择，结果通过回调处理
            // TODO: 需改用 KNOI 异步回调机制获取实际选择结果
            picker.select(options).then((uris: string[]) => {
                const files = this.resolveFileInfos(uris)
                console.info(`[OHOSPlatformService] pickFile result: ${JSON.stringify({ files: files })}`)
            }).catch((err: object) => {
                console.error(`[OHOSPlatformService] pickFile error: ${err}`)
            })
            return JSON.stringify({ files: [] })
        } catch (e) {
            console.error(`[OHOSPlatformService] pickFile failed: ${e}`)
            return JSON.stringify({ files: [] })
        }
    }

    pickImage(allowMultiple: boolean): string {
        try {
            const options = new picker.DocumentSelectOptions()
            options.selectMode = picker.DocumentSelectMode.FILE
            // OHOS picker 不直接支持 MIME 过滤，选择后需自行判断
            options.maxSelectNumber = allowMultiple ? 10 : 1

            // picker.select 返回 Promise，使用 .then() 处理异步结果
            // TODO: 需改用 KNOI 异步回调机制获取实际选择结果
            picker.select(options).then((uris: string[]) => {
                const files = this.resolveFileInfos(uris).filter((f) => {
                    return f.mimeType?.startsWith("image/") ?? false
                })
                console.info(`[OHOSPlatformService] pickImage result: ${JSON.stringify({ files: files })}`)
            }).catch((err: object) => {
                console.error(`[OHOSPlatformService] pickImage error: ${err}`)
            })
            return JSON.stringify({ files: [] })
        } catch (e) {
            console.error(`[OHOSPlatformService] pickImage failed: ${e}`)
            return JSON.stringify({ files: [] })
        }
    }

    pickDocument(allowMultiple: boolean): string {
        try {
            const options = new picker.DocumentSelectOptions()
            options.selectMode = picker.DocumentSelectMode.FILE
            options.maxSelectNumber = allowMultiple ? 10 : 1

            // picker.select 返回 Promise，使用 .then() 处理异步结果
            // TODO: 需改用 KNOI 异步回调机制获取实际选择结果
            picker.select(options).then((uris: string[]) => {
                const files = this.resolveFileInfos(uris)
                console.info(`[OHOSPlatformService] pickDocument result: ${JSON.stringify({ files: files })}`)
            }).catch((err: object) => {
                console.error(`[OHOSPlatformService] pickDocument error: ${err}`)
            })
            return JSON.stringify({ files: [] })
        } catch (e) {
            console.error(`[OHOSPlatformService] pickDocument failed: ${e}`)
            return JSON.stringify({ files: [] })
        }
    }

    // ==================== 对话框 ====================

    showAlert(title: string, message: string, confirmText: string): void {
        try {
            promptAction.showDialog({
                title: title,
                message: message,
                buttons: [
                    { text: confirmText, color: '#007DFF' }
                ]
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] showAlert failed: ${e}`)
        }
    }

    /**
     * Confirm 确认框
     *
     * 注意：promptAction.showDialog 返回 Promise（异步），KNOI 同步调用无法等待其结果。
     * 当前实现仅弹出对话框，返回值固定为 -1（表示已发起但结果未知）。
     * TODO: 需改用 KNOI 异步回调机制或原生端事件推送，才能获取用户实际选择。
     *       返回值约定：0=确认, 1=取消, -1=未知（异步无法获取）
     */
    showConfirm(title: string, message: string, confirmText: string, cancelText: string, callback: (result: number) => void): void {
        try {
            promptAction.showDialog({
                title: title,
                message: message,
                buttons: [
                    { text: cancelText, color: '#999999' },
                    { text: confirmText, color: '#007DFF' }
                ]
            }).then((result) => {
                callback(result.index)
            }).catch((err: object) => {
                console.error(`[OHOSPlatformService] showConfirm error: ${err}`)
                callback(-1)
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] showConfirm failed: ${e}`)
            callback(-1)
        }
    }

    /**
     * Action Sheet
     *
     * 注意：同上，showDialog 返回 Promise，同步调用无法获取用户选择。
     * promptAction.showDialog 最多支持 6 个按钮。
     * TODO: 需改用 KNOI 异步回调机制或原生端事件推送，才能获取用户实际选择。
     *       返回值约定：>=0 表示用户选择的索引, -1=取消, -2=未知（异步无法获取）
     */
    showActionSheet(title: string | null, message: string | null, options: string[], callback: (result: number) => void): void {
        try {
            const buttons: { text: string, color: string }[] = []
            const maxOptions = Math.min(options.length, 5)
            for (let i = 0; i < maxOptions; i++) {
                buttons.push({ text: options[i], color: '#007DFF' })
            }
            buttons.push({ text: '取消', color: '#999999' })
            promptAction.showDialog({
                title: title ?? undefined,
                message: message ?? undefined,
                buttons: buttons
            }).then((result) => {
                callback(result.index)
            }).catch((err: object) => {
                console.error(`[OHOSPlatformService] showActionSheet error: ${err}`)
                callback(-1)
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] showActionSheet failed: ${e}`)
            callback(-1)
        }
    }

    // ==================== 键盘控制 ====================

    hideKeyboard(): void {
        try {
            inputMethod.getController().hideTextInput((err) => {
                if (err) {
                    console.error(`[OHOSPlatformService] hideKeyboard failed: ${err}`)
                }
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] hideKeyboard failed: ${e}`)
        }
    }

    showKeyboard(): void {
        // OHOS 正常输入框系统自动弹出，无需手动触发
    }

    // ==================== 电话拨打 ====================

    callPhone(phoneNumber: string): void {
        try {
            call.makeCall(phoneNumber, (err) => {
                if (err) {
                    console.error(`[OHOSPlatformService] callPhone failed: ${err}`)
                }
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] callPhone failed: ${e}`)
        }
    }

    // ==================== 权限请求 ====================

    checkPermissionSync(tokenID: number, permission: string): string {
        try {
            const atManager = abilityAccessCtrl.createAtManager()
            const result = atManager.checkAccessTokenSync(tokenID, permission as abilityAccessCtrl.PermissionName)
            if (result === abilityAccessCtrl.GrantStatus.PERMISSION_GRANTED) {
                return "GRANTED"
            } else if (result === abilityAccessCtrl.GrantStatus.PERMISSION_DENIED) {
                return "DENIED"
            }
            return "NOT_DETERMINED"
        } catch (e) {
            console.error(`[OHOSPlatformService] checkPermissionSync failed: ${e}`)
            return "NOT_DETERMINED"
        }
    }

    requestPermissions(permissions: string[], callback: (result: string) => void): void {
        try {
            const atManager = abilityAccessCtrl.createAtManager()
            const ctx = this.contextProvider?.getContext() ?? null
            if (ctx === null) {
                console.warn(`[OHOSPlatformService] requestPermissions: context is null`)
                callback("DENIED")
                return
            }
            atManager.requestPermissionsFromUser(ctx, permissions as abilityAccessCtrl.PermissionName[], (err, result) => {
                if (err) {
                    console.error(`[OHOSPlatformService] requestPermissions error: ${err}`)
                    callback("DENIED")
                } else {
                    const allGranted = result.authResults.every((r: number) => r === 0)
                    callback(allGranted ? "GRANTED" : "DENIED")
                }
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] requestPermissions failed: ${e}`)
            callback("DENIED")
        }
    }

    // ==================== 屏幕信息 ====================

    getScreenWidth(): number {
        return this.screenInfo.getScreenWidth()
    }

    getScreenHeight(): number {
        return this.screenInfo.getScreenHeight()
    }

    getScreenDensityDpi(): number {
        return this.screenInfo.getScreenDensityDpi()
    }

    getScreenDensity(): number {
        return this.screenInfo.getScreenDensity()
    }

    getScreenRotation(): number {
        return this.screenInfo.getScreenRotation()
    }

    // ==================== 应用信息 ====================

    getAppName(): string {
        return this.appInfo.getAppName()
    }

    getAppPackageName(): string {
        return this.appInfo.getAppPackageName()
    }

    getVersionName(): string {
        return this.appInfo.getVersionName()
    }

    getVersionCode(): number {
        return this.appInfo.getVersionCode()
    }

    getBuildType(): string {
        return this.appInfo.getBuildType()
    }

    // ==================== 生物识别 ====================

    isBiometricSupported(): boolean {
        try {
            const challenge = new Uint8Array(8)
            const authType = userAuth.UserAuthType.FACE
            const authTrustLevel = userAuth.AuthTrustLevel.ATL1
            const authInstance = userAuth.getAuthInstance(challenge, authType, authTrustLevel)
            return authInstance !== null && authInstance !== undefined
        } catch (e) {
            console.error(`[OHOSPlatformService] isBiometricSupported failed: ${e}`)
            return false
        }
    }

    getSupportedBiometricTypes(): string {
        try {
            const types: string[] = []
            // 检查人脸
            try {
                const challenge = new Uint8Array(8)
                const authType = userAuth.UserAuthType.FACE
                const authTrustLevel = userAuth.AuthTrustLevel.ATL1
                const faceInstance = userAuth.getAuthInstance(challenge, authType, authTrustLevel)
                if (faceInstance !== null && faceInstance !== undefined) {
                    types.push("FACE")
                }
            } catch (e) {
                // 不支持人脸
            }
            // 检查指纹
            try {
                const challenge = new Uint8Array(8)
                const authType = userAuth.UserAuthType.FINGERPRINT
                const authTrustLevel = userAuth.AuthTrustLevel.ATL1
                const fpInstance = userAuth.getAuthInstance(challenge, authType, authTrustLevel)
                if (fpInstance !== null && fpInstance !== undefined) {
                    types.push("FINGERPRINT")
                }
            } catch (e) {
                // 不支持指纹
            }
            // PIN 始终支持（DEVICE_CREDENTIAL）
            types.push("PIN")
            return JSON.stringify({ types: types })
        } catch (e) {
            console.error(`[OHOSPlatformService] getSupportedBiometricTypes failed: ${e}`)
            return JSON.stringify({ types: [] })
        }
    }

    authenticate(title: string, cancelText: string, callback: (result: string) => void): void {
        try {
            const challenge = new Uint8Array(8)
            const authType = userAuth.UserAuthType.FACE
            const authTrustLevel = userAuth.AuthTrustLevel.ATL1
            const authInstance = userAuth.getAuthInstance(challenge, authType, authTrustLevel)
            if (authInstance === null || authInstance === undefined) {
                callback(JSON.stringify({ result: "NOT_SUPPORTED" }))
                return
            }

            authInstance.auth(challenge, authType, authTrustLevel, {
                onResult: (result: userAuth.AuthResult) => {
                    callback(JSON.stringify({ result: "SUCCESS", token: result.token }))
                },
                onAcquireInfo: (module: number, acquireInfo: number) => {
                    if (acquireInfo === 1) {
                        callback(JSON.stringify({ result: "CANCELED" }))
                    }
                },
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] authenticate failed: ${e}`)
            callback(JSON.stringify({ result: "FAILED" }))
        }
    }

    // ==================== 震动反馈 ====================

    hapticImpact(style: number): void {
        try {
            // style: 0=HEAVY, 1=MEDIUM, 2=LIGHT
            const effectId = [
                vibrator.EffectId.HAPTIC_HEAVY_CLICK,
                vibrator.EffectId.HAPTIC_MEDIUM_CLICK,
                vibrator.EffectId.HAPTIC_LIGHT_CLICK
            ][style] ?? vibrator.EffectId.HAPTIC_MEDIUM_CLICK
            const effect: vibrator.VibrateEffect = {
                type: vibrator.EffectType.VIBRATE_PRESET,
                effectId: effectId
            }
            const attribute: vibrator.VibratorAttribute = {
                usage: 'unknown'
            }
            vibrator.startVibration(effect, attribute).then(() => {
                console.info('[OHOSPlatformService] hapticImpact started')
            }).catch((err: object) => {
                console.error(`[OHOSPlatformService] hapticImpact failed: ${err}`)
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] hapticImpact failed: ${e}`)
        }
    }

    hapticNotification(type: number): void {
        try {
            // type: 0=SUCCESS, 1=WARNING, 2=FAILURE
            const effectId = [
                vibrator.EffectId.HAPTIC_HEAVY_CLICK,
                vibrator.EffectId.HAPTIC_MEDIUM_CLICK,
                vibrator.EffectId.HAPTIC_LIGHT_CLICK
            ][type] ?? vibrator.EffectId.HAPTIC_MEDIUM_CLICK
            const effect: vibrator.VibrateEffect = {
                type: vibrator.EffectType.VIBRATE_PRESET,
                effectId: effectId
            }
            const attribute: vibrator.VibratorAttribute = {
                usage: 'unknown'
            }
            vibrator.startVibration(effect, attribute).then(() => {
                console.info('[OHOSPlatformService] hapticNotification started')
            }).catch((err: object) => {
                console.error(`[OHOSPlatformService] hapticNotification failed: ${err}`)
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] hapticNotification failed: ${e}`)
        }
    }

    hapticSelectionChanged(): void {
        try {
            const effect: vibrator.VibrateEffect = {
                type: vibrator.EffectType.VIBRATE_PRESET,
                effectId: vibrator.EffectId.HAPTIC_LIGHT_CLICK
            }
            const attribute: vibrator.VibratorAttribute = {
                usage: 'unknown'
            }
            vibrator.startVibration(effect, attribute).then(() => {
                console.info('[OHOSPlatformService] hapticSelectionChanged started')
            }).catch((err: object) => {
                console.error(`[OHOSPlatformService] hapticSelectionChanged failed: ${err}`)
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] hapticSelectionChanged failed: ${e}`)
        }
    }

    hapticStop(): void {
        try {
            vibrator.stopVibration(vibrator.VibratorStopMode.STOP_MODE_IMMIDIATELY).then(() => {
                console.info('[OHOSPlatformService] hapticStop success')
            }).catch((err: object) => {
                console.error(`[OHOSPlatformService] hapticStop failed: ${err}`)
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] hapticStop failed: ${e}`)
        }
    }

    // ==================== 地理位置 ====================

    requestLocationPermission(): boolean {
        try {
            const atManager = abilityAccessCtrl.createAtManager()
            const permissions = [
                "ohos.permission.LOCATION",
                "ohos.permission.APPROXIMATELY_LOCATION"
            ]
            const ctx = this.contextProvider?.getContext()
            if (!ctx) {
                console.warn("[OHOSPlatformService] requestLocationPermission: context is null")
                return false
            }
            // 异步申请权限，返回占位值
            // TODO: 需改用 KNOI 异步回调机制获取实际授权结果
            atManager.requestPermissionsFromUser(ctx, permissions as abilityAccessCtrl.PermissionName[], (err, result) => {
                if (err) {
                    console.error(`[OHOSPlatformService] requestLocationPermission error: ${err}`)
                } else {
                    console.info(`[OHOSPlatformService] requestLocationPermission result: ${result}`)
                }
            })
            return true // 占位：假设申请已发起
        } catch (e) {
            console.error(`[OHOSPlatformService] requestLocationPermission failed: ${e}`)
            return false
        }
    }

    getCurrentLocation(accuracy: number, callback: (result: string) => void): void {
        try {
            // accuracy: 0=COARSE, 1=BALANCED, 2=PRECISE
            const request: geoLocationManager.CurrentLocationRequest = {
                priority: geoLocationManager.LocationRequestPriority.FIX_FIRST,
                timeoutMs: 10000
            }
            // 根据精度调整优先级
            if (accuracy === 0) {
                request.priority = geoLocationManager.LocationRequestPriority.UNSET
            } else if (accuracy === 2) {
                request.priority = geoLocationManager.LocationRequestPriority.ACCURACY
            }

            geoLocationManager.getCurrentLocation(request).then((location) => {
                if (location) {
                    const result = JSON.stringify({
                        latitude: location.latitude,
                        longitude: location.longitude,
                        altitude: location.altitude,
                        accuracy: location.accuracy,
                        speed: location.speed,
                        timestamp: location.timeStamp
                    })
                    callback(result)
                } else {
                    callback("{}")
                }
            }).catch((err: object) => {
                console.error(`[OHOSPlatformService] getCurrentLocation error: ${err}`)
                callback("{}")
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] getCurrentLocation failed: ${e}`)
            callback("{}")
        }
    }

    // ==================== 扫码 ====================

    startScan(callback: (result: string) => void): void {
        try {
            scanBarcode.startScan().then((scanResult: scanBarcode.ScanResult) => {
                if (scanResult) {
                    const result = JSON.stringify({
                        content: scanResult.value,
                        format: scanResult.scanType
                    })
                    callback(result)
                } else {
                    callback("{}")
                }
            }).catch((err: object) => {
                console.error(`[OHOSPlatformService] startScan error: ${err}`)
                callback("{}")
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] startScan failed: ${e}`)
            callback("{}")
        }
    }

    stopScan(): void {
        try {
            scanBarcode.stopScan()
        } catch (e) {
            console.error(`[OHOSPlatformService] stopScan failed: ${e}`)
        }
    }

    // ==================== 电池状态 ====================

    getBatteryLevel(): number {
        try {
            return batteryInfo.level
        } catch (e) {
            console.error(`[OHOSPlatformService] getBatteryLevel failed: ${e}`)
            return -1
        }
    }

    isCharging(): boolean {
        try {
            return batteryInfo.isCharging()
        } catch (e) {
            console.error(`[OHOSPlatformService] isCharging failed: ${e}`)
            return false
        }
    }

    isLowBattery(): boolean {
        try {
            return batteryInfo.level <= 20
        } catch (e) {
            console.error(`[OHOSPlatformService] isLowBattery failed: ${e}`)
            return false
        }
    }

    // ==================== 系统设置 ====================

    openSystemSettings(): void {
        // OHOS 不直接支持跳转到系统设置首页，暂为空操作
        console.warn("[OHOSPlatformService] openSystemSettings: not supported on OHOS")
    }

    openAppSettings(): void {
        // OHOS 不直接支持跳转到应用设置页，暂为空操作
        console.warn("[OHOSPlatformService] openAppSettings: not supported on OHOS")
    }

    /**
     * 将 URI 列表解析为文件信息数组
     *
     * 注意：OHOS picker 返回的 URI 需要通过文件系统获取详细信息。
     * 当前实现仅返回 URI 和从 URI 中提取的文件名。
     */
    private resolveFileInfos(uris: string[]): Array<Record<string, Object>> {
        const result: Array<Record<string, Object>> = []
        for (const uri of uris) {
            try {
                // 从 URI 提取文件名（OHOS URI 格式：file://.../filename.ext）
                const parts = uri.split("/")
                const name = parts[parts.length - 1] ?? "unknown"
                result.push({
                    path: uri,
                    name: name,
                    size: -1,
                    mimeType: null
                })
            } catch (e) {
                console.error(`[OHOSPlatformService] resolveFileInfo failed for ${uri}: ${e}`)
            }
        }
        return result
    }
}
