import fs from '@ohos.file.fs';
import { fileUri } from '@kit.CoreFileKit';

/**
 * OHOS 文件系统实现
 *
 * 不继承 Kotlin 接口（ETS 无法实现 Kotlin 接口），
 * 通过 BridgeModule 暴露给 shared 模块。
 */
export class OHOSFileSystem {
  /** 读取文件内容 */
  readFile(path: string): Uint8Array {
    try {
      const uri = fileUri.getUriFromPath(path);
      const file = fs.openSync(uri, fs.OpenMode.READ_ONLY);
      const stat = fs.statSync(file.fd);
      const arrayBuffer = new ArrayBuffer(stat.size);
      fs.readSync(file.fd, arrayBuffer);
      fs.closeSync(file);
      return new Uint8Array(arrayBuffer);
    } catch (e) {
      console.error(`[OHOSFileSystem] readFile failed: ${e}`);
      return new Uint8Array(0);
    }
  }

  /** 写入文件内容 */
  writeFile(path: string, data: Uint8Array): void {
    try {
      const uri = fileUri.getUriFromPath(path);
      const file = fs.openSync(uri, fs.OpenMode.CREATE | fs.OpenMode.WRITE_ONLY);
      fs.writeSync(file.fd, data.buffer);
      fs.closeSync(file);
    } catch (e) {
      console.error(`[OHOSFileSystem] writeFile failed: ${e}`);
    }
  }

  /** 判断文件是否存在 */
  exists(path: string): boolean {
    try {
      const uri = fileUri.getUriFromPath(path);
      return fs.accessSync(uri);
    } catch (e) {
      return false;
    }
  }

  /** 删除文件 */
  delete(path: string): boolean {
    try {
      const uri = fileUri.getUriFromPath(path);
      fs.unlinkSync(uri);
      return true;
    } catch (e) {
      console.error(`[OHOSFileSystem] delete failed: ${e}`);
      return false;
    }
  }
}
