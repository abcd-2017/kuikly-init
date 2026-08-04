import common from '@ohos.app.ability.common';
import { fileUri } from '@kit.CoreFileKit';

/**
 * OHOS 平台上下文能力实现
 *
 * 提供应用级资源访问能力，封装 OHOS 原生上下文。
 * 不继承 Kotlin 接口（ETS 无法实现 Kotlin 接口），
 * 通过 BridgeModule 暴露给 shared 模块。
 */
export class OHOSContextProvider {
  private context: common.UIAbilityContext;

  constructor(context: common.UIAbilityContext) {
    this.context = context;
  }

  /** 获取缓存目录路径 */
  getCacheDirPath(): string {
    return this.context.cacheDir;
  }

  /** 获取文件存储目录路径 */
  getFilesDirPath(): string {
    return this.context.filesDir;
  }

  /** 读取 rawfile 资源 */
  readAsset(path: string): Uint8Array {
    try {
      const resDir = this.context.resourceDir;
      const fullPath = resDir + '/' + path;
      const uri = fileUri.getUriFromPath(fullPath);
      // 返回空数组，实际读取通过 BridgeModule 的 readAssetFile 方法
      return new Uint8Array(0);
    } catch (e) {
      console.error(`[OHOSContextProvider] readAsset failed: ${e}`);
      return new Uint8Array(0);
    }
  }
}
