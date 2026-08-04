import deviceInfo from '@ohos.deviceInfo';

/**
 * OHOS 设备信息实现
 *
 * 不继承 Kotlin 接口（ETS 无法实现 Kotlin 接口），
 * 通过 BridgeModule 暴露给 shared 模块。
 */
export class OHOSDeviceInfo {
  /** 设备唯一标识 */
  getDeviceId(): string {
    // OHOS 不提供真实设备 ID，返回简化标识
    return deviceInfo.ODID || 'ohos-device-id';
  }

  /** 操作系统版本 */
  getOSVersion(): string {
    return deviceInfo.distributionOSVersion || 'HarmonyOS';
  }

  /** 设备型号 */
  getDeviceModel(): string {
    return deviceInfo.productModel || 'OHOS Device';
  }
}
