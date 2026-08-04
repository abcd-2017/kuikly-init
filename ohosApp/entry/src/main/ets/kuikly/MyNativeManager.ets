import { KuiklyNativeManager } from '@kuikly-open/render';
import Napi from 'libentry.so';

class MyNativeManager extends KuiklyNativeManager {
  protected loadNative(): number {
    // 调用Napi接口，初始化 Kuikly Native
    const handler = Napi.initKuikly();
    // 初始化 OHOS 平台能力（Koin + InitTask）
    if (Napi.setupPlatform) {
      Napi.setupPlatform();
    }
    return handler;
  }
}

// 导出一个全局的 KuiklyNativeManager 实例给 Kuikly 页面共用
const globalNativeManager = new MyNativeManager();

export default globalNativeManager;