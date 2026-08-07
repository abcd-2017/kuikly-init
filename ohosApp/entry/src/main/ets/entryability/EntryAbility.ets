import AbilityConstant from '@ohos.app.ability.AbilityConstant';
import hilog from '@ohos.hilog';
import UIAbility from '@ohos.app.ability.UIAbility';
import Want from '@ohos.app.ability.Want';
import window from '@ohos.window';
import { KuiklyRenderAdapterManager } from '@kuikly-open/render';
import { LogAdapter } from '../kuikly/adapter/LogAdapter';
import { RouterAdapter } from '../kuikly/adapter/RouterAdapter';
import { PlatformServiceLocator } from '../kuikly/platform/PlatformServiceLocator';
import { setup, init, registerServiceProvider } from '@kuiklybase/knoi';
import BuildProfile from '@kuiklybase/knoi/BuildProfile';
import { OHOSPlatformServiceImpl } from '../kuikly/platform/OHOSPlatformServiceImpl';
import { OHOSContextProvider } from '../kuikly/platform/OHOSContextProvider';

export default class EntryAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onCreate');
  }

  onDestroy(): void {
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onDestroy');
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    // Main window is created, set main page for this ability
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onWindowStageCreate');
    windowStage.getMainWindowSync().setWindowLayoutFullScreen(true);

    // 初始化 OHOS 平台能力服务定位器
    PlatformServiceLocator.getInstance().init(this.context);

    // KNOI 初始化（Kotlin ↔ ArkTS 互调机制）
    // libshared.so 为 KMP 生成的动态库名字，需与实际构建产物一致
    setup("libshared.so", BuildProfile.DEBUG);
    init();

    // 注册平台服务实现（KNOI 方式）
    const platformService = new OHOSPlatformServiceImpl();
    const contextProvider = new OHOSContextProvider(this.context);
    platformService.setContextProvider(contextProvider);
    registerServiceProvider("IOHOSPlatformService", false, platformService);

    KuiklyRenderAdapterManager.krLogAdapter = new LogAdapter();
    KuiklyRenderAdapterManager.krRouterAdapter = new RouterAdapter();

    windowStage.loadContent('pages/Index', (err, data) => {
      if (err.code) {
        hilog.error(0x0000, 'testTag', 'Failed to load the content. Cause: %{public}s', JSON.stringify(err) ?? '');
        return;
      }
      hilog.info(0x0000, 'testTag', 'Succeeded in loading the content. Data: %{public}s', JSON.stringify(data) ?? '');
    });
  }

  onWindowStageDestroy(): void {
    // Main window is destroyed, release UI related resources
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onWindowStageDestroy');
  }

  onForeground(): void {
    // Ability has brought to foreground
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onForeground');
  }

  onBackground(): void {
    // Ability has back to background
    hilog.info(0x0000, 'testTag', '%{public}s', 'Ability onBackground');
  }
}