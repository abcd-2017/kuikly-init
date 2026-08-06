import deviceInfo from '@ohos.deviceInfo';
import i18n from '@ohos.i18n';

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

  /** 时区 ID，如 "Asia/Shanghai" */
  getTimezoneId(): string {
    try {
      const tz = i18n.getTimeZone();
      return tz.getID() || 'Asia/Shanghai';
    } catch (e) {
      console.error(`[OHOSDeviceInfo] getTimezoneId failed: ${e}`);
      return 'Asia/Shanghai';
    }
  }

  /** 时区偏移量（分钟），如东八区返回 480 */
  getOffsetMinutes(): number {
    try {
      const tz = i18n.getTimeZone();
      return (tz.getRawOffset() || 0) / 60000;
    } catch (e) {
      console.error(`[OHOSDeviceInfo] getOffsetMinutes failed: ${e}`);
      return 480;
    }
  }

  /** 当前是否处于夏令时 */
  isDaylightSaving(): boolean {
    try {
      const tz = i18n.getTimeZone();
      return tz.isDaylightTime(new Date());
    } catch (e) {
      console.error(`[OHOSDeviceInfo] isDaylightSaving failed: ${e}`);
      return false;
    }
  }

  /** 时区缩写，如 "CST" */
  getTimezoneAbbreviation(): string {
    try {
      const tz = i18n.getTimeZone();
      // 参数：locale, 是否夏令时, 是否长格式
      return tz.getDisplayName(false, false) || 'CST';
    } catch (e) {
      console.error(`[OHOSDeviceInfo] getTimezoneAbbreviation failed: ${e}`);
      return 'CST';
    }
  }
}
