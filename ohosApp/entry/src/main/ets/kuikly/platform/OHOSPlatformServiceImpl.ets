import { pasteboard } from "@kit.BasicServicesKit"
import promptAction from "@ohos.promptAction"
import { systemShare } from "@kit.ShareKit"
import { picker } from "@kit.CoreFileKit"
import { OHOSContextProvider } from "./OHOSContextProvider"
import { OHOSDeviceInfo } from "./OHOSDeviceInfo"
import { OHOSFileSystem } from "./OHOSFileSystem"
import { OHOSNetworkMonitor } from "./OHOSNetworkMonitor"
import { OHOSNtpClient } from "./OHOSNtpClient"
import { OHOSCrypto } from "./OHOSCrypto"

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

            const uris = picker.select(options)
            const files = this.resolveFileInfos(uris)
            return JSON.stringify({ files: files })
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

            const uris = picker.select(options)
            const files = this.resolveFileInfos(uris).filter((f) => {
                return f.mimeType?.startsWith("image/") ?? false
            })
            return JSON.stringify({ files: files })
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

            const uris = picker.select(options)
            const files = this.resolveFileInfos(uris)
            return JSON.stringify({ files: files })
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
     * 当前实现仅弹出对话框，返回值固定为 0（占位）。
     * TODO: 需改用 KNOI 异步回调机制或原生端事件推送，才能获取用户实际选择。
     */
    showConfirm(title: string, message: string, confirmText: string, cancelText: string): number {
        try {
            promptAction.showDialog({
                title: title,
                message: message,
                buttons: [
                    { text: cancelText, color: '#999999' },
                    { text: confirmText, color: '#007DFF' }
                ]
            }).then((result) => {
                console.info(`[OHOSPlatformService] showConfirm result.index: ${result.index}`)
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] showConfirm failed: ${e}`)
        }
        return 0 // 占位：异步结果无法通过同步 KNOI 调用返回
    }

    /**
     * Action Sheet
     *
     * 注意：同上，showDialog 返回 Promise，同步调用无法获取用户选择。
     * promptAction.showDialog 最多支持 6 个按钮。
     * TODO: 需改用 KNOI 异步回调机制或原生端事件推送，才能获取用户实际选择。
     */
    showActionSheet(title: string | null, message: string | null, options: string[]): number {
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
                console.info(`[OHOSPlatformService] showActionSheet result.index: ${result.index}`)
            })
        } catch (e) {
            console.error(`[OHOSPlatformService] showActionSheet failed: ${e}`)
        }
        return 0 // 占位：异步结果无法通过同步 KNOI 调用返回
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
