import socket from '@ohos.net.socket';

/**
 * OHOS NTP 客户端实现
 *
 * 使用 @ohos.net.socket UDPSocket 实现 NTP 协议请求。
 * NTP 报文格式：48 字节
 *   - 第 1 字节：LI(2bit) | VN(3bit) | Mode(3bit) = 0x1B (LI=0, VN=3, Mode=3 客户端)
 *   - 第 40-43 字节：Transmit Timestamp 整数部分（秒，从 1900-01-01 起算）
 *   - 第 44-47 字节：小数部分（忽略）
 *
 * NTP 时间转 Unix 时间：减去 2208988800 秒（1900→1970 的秒数差）
 *
 * TODO: 当前为桩实现。完整实现需要：
 *   1. 将 KNOI 接口改为 suspend / Promise 形式（需要 KNOI 支持异步）
 *   2. 使用 async/await 调用 socket.send() 和监听 'message' 事件
 *   3. 使用 Promise.race 实现超时控制
 * 由于 KNOI 接口当前为同步签名，无法在同步方法内 await 异步 socket 操作。
 */
export class OHOSNtpClient {
  // NTP 1900-01-01 到 Unix 1970-01-01 的秒数差
  private static readonly NTP_EPOCH_OFFSET = 2208988800;
  // NTP 客户端报文首字节：LI=0, VN=3, Mode=3
  private static readonly NTP_MODE_CLIENT = 0x1B;
  // 默认超时 5 秒
  private static readonly DEFAULT_TIMEOUT = 5000;

  private cachedOffset: number = 0;

  /**
   * 获取服务器时间戳（毫秒）
   * @param ntpServer NTP 服务器地址
   * @return 服务器时间戳（毫秒），失败返回 -1
   */
  getServerTime(ntpServer: string): number {
    // TODO: 桩实现，返回当前系统时间
    // 完整实现需要异步 socket 操作，当前 KNOI 接口为同步签名
    return Date.now();
  }

  /**
   * 获取本地时间与服务器时间的偏差（毫秒）
   * @param ntpServer NTP 服务器地址
   * @return 偏差值（毫秒），失败返回 0
   */
  getClockOffset(ntpServer: string): number {
    // TODO: 桩实现，offset 为 0
    this.cachedOffset = 0;
    return 0;
  }

  /**
   * 发送 NTP 请求并解析响应（完整实现参考）
   *
   * 此方法展示完整的 NTP 协议实现逻辑，待 KNOI 支持异步后启用。
   */
  private requestNtpAsync(ntpServer: string): Promise<number> {
    return new Promise<number>((resolve, reject) => {
      // 构造 48 字节 NTP 报文
      const buffer = new ArrayBuffer(48);
      const view = new Uint8Array(buffer);
      view[0] = OHOSNtpClient.NTP_MODE_CLIENT;

      const udpSocket = socket.constructUDPSocketInstance();
      let resolved = false;

      // 超时控制
      const timeoutHandle = setTimeout(() => {
        if (!resolved) {
          resolved = true;
          try { udpSocket.close(); } catch (e) { /* ignore */ }
          reject(new Error('NTP request timed out'));
        }
      }, OHOSNtpClient.DEFAULT_TIMEOUT);

      // 监听响应
      udpSocket.on('message', (msg: socket.SocketMessageInfo) => {
        if (resolved) return;
        resolved = true;
        clearTimeout(timeoutHandle);
        try {
          const result = this.parseNtpResponse(msg.message);
          resolve(result);
        } catch (e) {
          reject(e);
        } finally {
          try { udpSocket.close(); } catch (e) { /* ignore */ }
        }
      });

      // 绑定并发送
      udpSocket.bind({ address: '0.0.0.0', port: 0 })
        .then(() => {
          return udpSocket.send({
            data: buffer,
            address: {
              address: ntpServer,
              port: 123,
              family: 1 // IPv4
            }
          });
        })
        .then(() => {
          // 等待响应，由 'message' 回调处理
        })
        .catch((e: Error) => {
          if (!resolved) {
            resolved = true;
            clearTimeout(timeoutHandle);
            try { udpSocket.close(); } catch (e) { /* ignore */ }
            reject(e);
          }
        });
    });
  }

  /**
   * 解析 NTP 响应报文
   * @param data 响应数据
   * @return 服务器时间戳（毫秒），失败返回 -1
   */
  private parseNtpResponse(data: ArrayBuffer): number {
    try {
      const view = new DataView(data);
      // 第 40-43 字节为 Transmit Timestamp 整数部分（大端序）
      const seconds = view.getUint32(40, false); // false = 大端序
      if (seconds === 0) {
        return -1;
      }
      // NTP 时间从 1900-01-01 起算，转换为 Unix 毫秒时间戳
      const unixSeconds = seconds - OHOSNtpClient.NTP_EPOCH_OFFSET;
      return unixSeconds * 1000;
    } catch (e) {
      console.error(`[OHOSNtpClient] parseNtpResponse error: ${e}`);
      return -1;
    }
  }
}
