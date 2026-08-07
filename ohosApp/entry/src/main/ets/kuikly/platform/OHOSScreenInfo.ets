import { display } from '@kit.ArkUI';

/**
 * OHOS 屏幕信息实现
 *
 * 基于 @ohos.display，获取屏幕尺寸、密度和旋转信息。
 */
export class OHOSScreenInfo {
  /** 屏幕宽度（像素） */
  getScreenWidth(): number {
    try {
      const d = display.getDefaultDisplaySync();
      return d.width;
    } catch (e) {
      console.error(`[OHOSScreenInfo] getScreenWidth failed: ${e}`);
      return 0;
    }
  }

  /** 屏幕高度（像素） */
  getScreenHeight(): number {
    try {
      const d = display.getDefaultDisplaySync();
      return d.height;
    } catch (e) {
      console.error(`[OHOSScreenInfo] getScreenHeight failed: ${e}`);
      return 0;
    }
  }

  /** 屏幕 DPI */
  getScreenDensityDpi(): number {
    try {
      const d = display.getDefaultDisplaySync();
      return d.densityDPI;
    } catch (e) {
      console.error(`[OHOSScreenInfo] getScreenDensityDpi failed: ${e}`);
      return 160;
    }
  }

  /** 屏幕密度比例 */
  getScreenDensity(): number {
    try {
      const d = display.getDefaultDisplaySync();
      return d.densityPixels;
    } catch (e) {
      console.error(`[OHOSScreenInfo] getScreenDensity failed: ${e}`);
      return 1.0;
    }
  }

  /** 屏幕旋转方向（0/90/180/270） */
  getScreenRotation(): number {
    try {
      const d = display.getDefaultDisplaySync();
      return d.rotation;
    } catch (e) {
      console.error(`[OHOSScreenInfo] getScreenRotation failed: ${e}`);
      return 0;
    }
  }
}
