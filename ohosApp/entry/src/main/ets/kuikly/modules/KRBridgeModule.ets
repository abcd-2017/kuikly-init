import { common } from '@kit.AbilityKit';
import { BusinessError } from '@kit.BasicServicesKit';
import { KRAny } from '@kuikly-open/render';
import { KuiklyRenderBaseModule } from '@kuikly-open/render';
import { KuiklyRenderCallback } from '@kuikly-open/render';
import util from '@ohos.util';
import fs from '@ohos.file.fs';
import { fileUri } from '@kit.CoreFileKit';
import { PlatformServiceLocator } from '../platform/PlatformServiceLocator';

export class KRBridgeModule extends KuiklyRenderBaseModule {
  static readonly MODULE_NAME = "HRBridgeModule";
  static readonly SHOW_ALERT = "showAlert";
  static readonly CLOSE_PAGE = "closePage";
  static readonly OPEN_PAGE = "openPage";
  static readonly COPY_TO_PASTE_BOARD = "copyToPasteboard";
  static readonly TOAST = "toast";
  static readonly LOG = "log";
  static readonly REPORT_DT = "reportDT";
  static readonly REPORT_REAL_TIME = "reportRealtime";
  static readonly LOCAL_SERVE_TIME = "localServeTime";
  static readonly CURRENT_TIME_STAMP = "currentTimestamp";
  static readonly DATE_FORMATTER = "dateFormatter";
  static readonly READ_ASSET_FILE = "readAssetFile";

  // 平台能力桥接方法
  static readonly GET_CACHE_DIR = "getCacheDir";
  static readonly GET_FILES_DIR = "getFilesDir";
  static readonly GET_DEVICE_ID = "getDeviceId";
  static readonly GET_OS_VERSION = "getOSVersion";
  static readonly GET_DEVICE_MODEL = "getDeviceModel";
  static readonly FILE_EXISTS = "fileExists";
  static readonly FILE_DELETE = "fileDelete";
  static readonly IS_NETWORK_CONNECTED = "isNetworkConnected";
  static readonly GET_NETWORK_TYPE = "getNetworkType";

  syncMode(): boolean {
    return false;
  }

  call(method: string, params: KRAny, callback: KuiklyRenderCallback): KRAny {
    switch (method) {
      case KRBridgeModule.SHOW_ALERT: {
        this.showAlert(params, callback);
        break;
      }
      case KRBridgeModule.CLOSE_PAGE: {
        this.closePage();
        break;
      }
      case KRBridgeModule.OPEN_PAGE: {
        this.openPage();
        break;
      }
      case KRBridgeModule.COPY_TO_PASTE_BOARD: {
        this.copyToPasteboard();
        break;
      }
      case KRBridgeModule.TOAST: {
        this.toast();
        break;
      }
      case KRBridgeModule.LOG: {
        this.log(params);
        break;
      }
      case KRBridgeModule.REPORT_DT: {
        this.reportDT(params);
        break;
      }
      case KRBridgeModule.REPORT_REAL_TIME: {
        this.reportRealtime(params);
        break;
      }
      case KRBridgeModule.LOCAL_SERVE_TIME: {
        this.localServeTime(params, callback);
        break;
      }
      case KRBridgeModule.CURRENT_TIME_STAMP: {
        return this.currentTimestamp();
      }
      case KRBridgeModule.DATE_FORMATTER: {
        return this.dateFormatter(params);
      }
      case KRBridgeModule.READ_ASSET_FILE: {
        this.readAssetFile(params, callback);
        break;
      }
      case KRBridgeModule.GET_CACHE_DIR: {
        callback({ "path": this.getCacheDir() });
        break;
      }
      case KRBridgeModule.GET_FILES_DIR: {
        callback({ "path": this.getFilesDir() });
        break;
      }
      case KRBridgeModule.GET_DEVICE_ID: {
        callback({ "deviceId": this.getDeviceId() });
        break;
      }
      case KRBridgeModule.GET_OS_VERSION: {
        callback({ "version": this.getOSVersion() });
        break;
      }
      case KRBridgeModule.GET_DEVICE_MODEL: {
        callback({ "model": this.getDeviceModel() });
        break;
      }
      case KRBridgeModule.FILE_EXISTS: {
        const existsParams = JSON.parse(params as string) as Record<string, string>;
        callback({ "exists": this.fileExists(existsParams["path"]) });
        break;
      }
      case KRBridgeModule.FILE_DELETE: {
        const deleteParams = JSON.parse(params as string) as Record<string, string>;
        callback({ "success": this.fileDelete(deleteParams["path"]) });
        break;
      }
      case KRBridgeModule.IS_NETWORK_CONNECTED: {
        callback({ "connected": this.isNetworkConnected() });
        break;
      }
      case KRBridgeModule.GET_NETWORK_TYPE: {
        callback({ "type": this.getNetworkType() });
        break;
      }
      default:
        break;
    }
    return null;
  }

  // ---- 平台能力方法实现 ----

  private getPlatformLocator(): PlatformServiceLocator {
    return PlatformServiceLocator.getInstance();
  }

  private getCacheDir(): string {
    return this.getPlatformLocator().getContextProvider().getCacheDirPath();
  }

  private getFilesDir(): string {
    return this.getPlatformLocator().getContextProvider().getFilesDirPath();
  }

  private getDeviceId(): string {
    return this.getPlatformLocator().getDeviceInfo().getDeviceId();
  }

  private getOSVersion(): string {
    return this.getPlatformLocator().getDeviceInfo().getOSVersion();
  }

  private getDeviceModel(): string {
    return this.getPlatformLocator().getDeviceInfo().getDeviceModel();
  }

  private fileExists(path: string): boolean {
    if (!path) return false;
    return this.getPlatformLocator().getFileSystem().exists(path);
  }

  private fileDelete(path: string): boolean {
    if (!path) return false;
    return this.getPlatformLocator().getFileSystem().delete(path);
  }

  private isNetworkConnected(): boolean {
    return this.getPlatformLocator().getNetworkMonitor().isConnected();
  }

  private getNetworkType(): string {
    return this.getPlatformLocator().getNetworkMonitor().getNetworkType();
  }

  private showAlert(params: KRAny, callback: KuiklyRenderCallback) {
  }

  // TODO close page
  private closePage() {
  }

  // TODO open new page
  private openPage() {
  }

  // TODO copy to paste board
  private copyToPasteboard() {
  }

  // TODO open toast
  private toast() {
  }

  private log(params: KRAny) {
    if (params == null) {
      return
    }
    console.log(`KuiklyRender ${JSON.stringify(params)}`,)
  }

  private reportDT(params: KRAny) {
  }

  private reportRealtime(params: KRAny) {
  }

  private localServeTime(params: KRAny, callback: KuiklyRenderCallback) {
    callback({ "time": Date.now() / 1000.0 });
  }

  private currentTimestamp(): string {
    return Date.now().toString()
  }

  // TODO format date
  private dateFormatter(params: KRAny): string {
    console.log(`========== ${params}`)
    return "";
  }

  private readAssetFile(params: KRAny, callback: KuiklyRenderCallback) {
    // let startTimeMills = Date.now();
    let records = JSON.parse(params as string) as Record<string, string>;
    let assetPath = records["assetPath"];

    let context = getContext(this) as common.UIAbilityContext;
    /*
    context.resourceManager.getRawFileContent(assetPath, (error: BusinessError, value: Uint8Array) => {
      let textDecoder: util.TextDecoder = util.TextDecoder.create();
      let res: string = textDecoder.decodeWithStream(value);
      callback({
        "result": JSON.parse(res)
      });
    });
    */
    let resDir = context.resourceDir
    let path = resDir + '/' + assetPath
    let assetURI = fileUri.getUriFromPath(path)
    const file = fs.openSync(assetURI, fs.OpenMode.READ_ONLY);
    const stat = fs.statSync(file.fd);
    const arrayBuffer = new ArrayBuffer(stat.size);
    fs.read(file.fd, arrayBuffer, (err: BusinessError, readLen: number) => {
      if (err) {
        console.error("read failed with error message: " + err.message + ", error code: " + err.code);
      } else {
        let buf = new Uint8Array(arrayBuffer, 0, readLen);
        const textDecoder = new util.TextDecoder('utf-8');
        const res = textDecoder.decodeWithStream(new Uint8Array(buf));
        callback({
          'result': JSON.parse(res)
        });
      }
      fs.closeSync(file);
    });
  }
}