import { bundleManager } from '@kit.AbilityKit';

/**
 * OHOS 应用信息实现
 *
 * 基于 bundleManager，获取应用名称、包号、版本等信息。
 */
export class OHOSAppInfo {
  /** 应用名称 */
  getAppName(): string {
    try {
      const info = bundleManager.getBundleInfoForSelfSync(bundleManager.BundleFlag.GET_BUNDLE_INFO_DEFAULT);
      return info.name || '';
    } catch (e) {
      console.error(`[OHOSAppInfo] getAppName failed: ${e}`);
      return '';
    }
  }

  /** 包名 */
  getAppPackageName(): string {
    try {
      const info = bundleManager.getBundleInfoForSelfSync(bundleManager.BundleFlag.GET_BUNDLE_INFO_DEFAULT);
      // API 12: BundleInfo 不再有 bundleName 属性，使用 appInfo.name
      return info.appInfo.name || '';
    } catch (e) {
      console.error(`[OHOSAppInfo] getAppPackageName failed: ${e}`);
      return '';
    }
  }

  /** 版本名称 */
  getVersionName(): string {
    try {
      const info = bundleManager.getBundleInfoForSelfSync(bundleManager.BundleFlag.GET_BUNDLE_INFO_DEFAULT);
      return info.versionName || '';
    } catch (e) {
      console.error(`[OHOSAppInfo] getVersionName failed: ${e}`);
      return '';
    }
  }

  /** 版本号 */
  getVersionCode(): number {
    try {
      const info = bundleManager.getBundleInfoForSelfSync(bundleManager.BundleFlag.GET_BUNDLE_INFO_DEFAULT);
      return info.versionCode;
    } catch (e) {
      console.error(`[OHOSAppInfo] getVersionCode failed: ${e}`);
      return 0;
    }
  }

  /** 构建类型（debug / release） */
  getBuildType(): string {
    try {
      // OHOS 通过 buildProfile 判断构建类型
      // 默认返回 'debug'，发布包会覆盖
      return 'debug';
    } catch (e) {
      console.error(`[OHOSAppInfo] getBuildType failed: ${e}`);
      return 'unknown';
    }
  }
}
