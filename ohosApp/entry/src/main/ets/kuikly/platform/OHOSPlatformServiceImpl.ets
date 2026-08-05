import { OHOSContextProvider } from "./OHOSContextProvider"
import { OHOSDeviceInfo } from "./OHOSDeviceInfo"
import { OHOSFileSystem } from "./OHOSFileSystem"
import { OHOSNetworkMonitor } from "./OHOSNetworkMonitor"

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
}
