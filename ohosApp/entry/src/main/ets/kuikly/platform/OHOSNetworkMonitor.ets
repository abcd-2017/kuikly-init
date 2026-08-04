import connection from '@ohos.net.connection';

/**
 * OHOS 网络状态实现
 *
 * 不继承 Kotlin 接口（ETS 无法实现 Kotlin 接口），
 * 通过 BridgeModule 暴露给 shared 模块。
 */
export class OHOSNetworkMonitor {
  /** 当前是否联网 */
  isConnected(): boolean {
    try {
      const netHandle = connection.getDefaultNetSync();
      return netHandle !== null;
    } catch (e) {
      return false;
    }
  }

  /** 当前网络类型：wifi / cellular / none */
  getNetworkType(): string {
    try {
      const netHandle = connection.getDefaultNetSync();
      if (!netHandle) {
        return 'none';
      }
      const netCapabilities = connection.getNetCapabilitiesSync(netHandle);
      if (netCapabilities) {
        if (netCapabilities.bearerTypes.includes(connection.NetBearType.BEARER_WIFI)) {
          return 'wifi';
        }
        if (netCapabilities.bearerTypes.includes(connection.NetBearType.BEARER_CELLULAR)) {
          return 'cellular';
        }
      }
      return 'none';
    } catch (e) {
      return 'none';
    }
  }
}
