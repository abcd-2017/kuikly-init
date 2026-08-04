import common from '@ohos.app.ability.common';
import { OHOSContextProvider } from './OHOSContextProvider';
import { OHOSDeviceInfo } from './OHOSDeviceInfo';
import { OHOSFileSystem } from './OHOSFileSystem';
import { OHOSNetworkMonitor } from './OHOSNetworkMonitor';

/**
 * OHOS 平台能力服务定位器
 *
 * 提供对平台能力实现的访问，替代 Koin（ETS 无法使用 Kotlin DI 框架）。
 * 在 EntryAbility 中初始化，通过 BridgeModule 暴露给 shared 模块。
 */
export class PlatformServiceLocator {
  private static instance: PlatformServiceLocator | null = null;

  private contextProvider: OHOSContextProvider | null = null;
  private deviceInfo: OHOSDeviceInfo | null = null;
  private fileSystem: OHOSFileSystem | null = null;
  private networkMonitor: OHOSNetworkMonitor | null = null;

  private constructor() {
    // 单例模式
  }

  static getInstance(): PlatformServiceLocator {
    if (!PlatformServiceLocator.instance) {
      PlatformServiceLocator.instance = new PlatformServiceLocator();
    }
    return PlatformServiceLocator.instance;
  }

  /** 初始化平台能力实例 */
  init(context: common.UIAbilityContext): void {
    this.contextProvider = new OHOSContextProvider(context);
    this.deviceInfo = new OHOSDeviceInfo();
    this.fileSystem = new OHOSFileSystem();
    this.networkMonitor = new OHOSNetworkMonitor();
    console.log('[PlatformServiceLocator] initialized');
  }

  getContextProvider(): OHOSContextProvider {
    if (!this.contextProvider) {
      throw new Error('[PlatformServiceLocator] not initialized');
    }
    return this.contextProvider;
  }

  getDeviceInfo(): OHOSDeviceInfo {
    if (!this.deviceInfo) {
      throw new Error('[PlatformServiceLocator] not initialized');
    }
    return this.deviceInfo;
  }

  getFileSystem(): OHOSFileSystem {
    if (!this.fileSystem) {
      throw new Error('[PlatformServiceLocator] not initialized');
    }
    return this.fileSystem;
  }

  getNetworkMonitor(): OHOSNetworkMonitor {
    if (!this.networkMonitor) {
      throw new Error('[PlatformServiceLocator] not initialized');
    }
    return this.networkMonitor;
  }
}
