import cryptoFramework from '@ohos.security.cryptoFramework';
import util from '@ohos.util';

/**
 * OHOS 加解密实现
 *
 * 基于 @ohos.security.cryptoFramework：
 * - AES: cryptoFramework.createCipher
 * - MD5/SHA-256: cryptoFramework.createMd
 * - HMAC: cryptoFramework.createMac
 * - Base64: @ohos.util.base64
 */
export class OHOSCrypto {

    /** AES-256-CBC 加密，返回 Base64 编码密文 */
    aesEncrypt(plaintext: string, key: string, iv: string | null): string {
        try {
            const keyBytes = this.deriveKey(key);
            const ivBytes = this.deriveIv(key, iv);

            // AES-256-CBC with PKCS5 padding
            const generator = cryptoFramework.createSymKeyGenerator('AES256');
            const symKey = generator.convertSync({ data: keyBytes });

            const cipher = cryptoFramework.createCipher('AES256|CBC|PKCS5');
            const encryptoData: cryptoFramework.DataBlob = { data: this.stringToBytes(plaintext) };

            // Initialize cipher with IV (IvParamsSpec = { data: Uint8Array })
            const ivParams: cryptoFramework.IvParamsSpec = { data: ivBytes };
            cipher.initSync(cryptoFramework.CryptoMode.ENCRYPT_MODE, symKey, ivParams);

            const encrypted = cipher.doFinalSync(encryptoData);
            return this.bytesToBase64(encrypted.data);
        } catch (e) {
            console.error(`[OHOSCrypto] aesEncrypt failed: ${e}`);
            return "";
        }
    }

    /** AES-256-CBC 解密 */
    aesDecrypt(ciphertext: string, key: string, iv: string | null): string {
        try {
            const keyBytes = this.deriveKey(key);
            const ivBytes = this.deriveIv(key, iv);

            const generator = cryptoFramework.createSymKeyGenerator('AES256');
            const symKey = generator.convertSync({ data: keyBytes });

            const cipher = cryptoFramework.createCipher('AES256|CBC|PKCS5');
            const decryptoData: cryptoFramework.DataBlob = { data: this.base64ToBytes(ciphertext) };

            const ivParams: cryptoFramework.IvParamsSpec = { data: ivBytes };
            cipher.initSync(cryptoFramework.CryptoMode.DECRYPT_MODE, symKey, ivParams);

            const decrypted = cipher.doFinalSync(decryptoData);
            return this.bytesToString(decrypted.data);
        } catch (e) {
            console.error(`[OHOSCrypto] aesDecrypt failed: ${e}`);
            return "";
        }
    }

    /** MD5 哈希（32 位小写 hex） */
    md5(input: string): string {
        try {
            const md = cryptoFramework.createMd('MD5');
            const inputBlob: cryptoFramework.DataBlob = { data: this.stringToBytes(input) };
            const digest = md.digestSync(inputBlob);
            return this.bytesToHex(digest.data);
        } catch (e) {
            console.error(`[OHOSCrypto] md5 failed: ${e}`);
            return "";
        }
    }

    /** SHA-256 哈希（64 位小写 hex） */
    sha256(input: string): string {
        try {
            const md = cryptoFramework.createMd('SHA256');
            const inputBlob: cryptoFramework.DataBlob = { data: this.stringToBytes(input) };
            const digest = md.digestSync(inputBlob);
            return this.bytesToHex(digest.data);
        } catch (e) {
            console.error(`[OHOSCrypto] sha256 failed: ${e}`);
            return "";
        }
    }

    /** HMAC-SHA256（Base64 编码） */
    hmacSha256(data: string, key: string): string {
        try {
            const keyBytes = this.stringToBytes(key);
            const generator = cryptoFramework.createSymKeyGenerator('HMAC');
            const symKey = generator.convertSync({ data: keyBytes });

            const mac = cryptoFramework.createMac('SHA256');
            mac.initSync(symKey);

            const inputBlob: cryptoFramework.DataBlob = { data: this.stringToBytes(data) };
            const result = mac.doFinalSync(inputBlob);
            return this.bytesToBase64(result.data);
        } catch (e) {
            console.error(`[OHOSCrypto] hmacSha256 failed: ${e}`);
            return "";
        }
    }

    /** Base64 编码 */
    base64Encode(input: string): string {
        try {
            const bytes = this.stringToBytes(input);
            const base64 = new util.Base64Helper();
            return base64.encodeToStringSync(bytes);
        } catch (e) {
            console.error(`[OHOSCrypto] base64Encode failed: ${e}`);
            return "";
        }
    }

    /** Base64 解码 */
    base64Decode(input: string): string {
        try {
            const base64 = new util.Base64Helper();
            const bytes = base64.decodeSync(input);
            const textDecoder = new util.TextDecoder('utf-8');
            return textDecoder.decodeWithStream(bytes, { stream: false });
        } catch (e) {
            console.error(`[OHOSCrypto] base64Decode failed: ${e}`);
            return "";
        }
    }

    // ==================== 私有辅助方法 ====================

    /** 密钥处理：不足 32 字节用 SHA-256 哈希后取前 32 字节 */
    private deriveKey(key: string): Uint8Array {
        const keyBytes = this.stringToBytes(key);
        if (keyBytes.length === 32) {
            return keyBytes;
        }
        // Use SHA-256 to derive a 32-byte key
        const md = cryptoFramework.createMd('SHA256');
        const inputBlob: cryptoFramework.DataBlob = { data: keyBytes };
        const digest = md.digestSync(inputBlob);
        return digest.data;
    }

    /** IV 处理：如果未提供，用 key 的 MD5 前 16 字节 */
    private deriveIv(key: string, iv: string | null): Uint8Array {
        if (iv !== null && iv !== undefined && iv.length > 0) {
            const ivBytes = this.stringToBytes(iv);
            if (ivBytes.length === 16) {
                return ivBytes;
            }
            // Pad or truncate to 16 bytes
            const result = new Uint8Array(16);
            result.set(ivBytes.slice(0, 16));
            return result;
        }
        // Derive IV from key MD5
        const md = cryptoFramework.createMd('MD5');
        const inputBlob: cryptoFramework.DataBlob = { data: this.stringToBytes(key) };
        const digest = md.digestSync(inputBlob);
        return digest.data;
    }

    private stringToBytes(str: string): Uint8Array {
        const encoder = new util.TextEncoder();
        return encoder.encodeInto(str);
    }

    private bytesToString(bytes: Uint8Array): string {
        try {
            const decoder = new util.TextDecoder('utf-8');
            return decoder.decodeWithStream(bytes, { stream: false });
        } catch (e) {
            return "";
        }
    }

    private base64ToBytes(base64: string): Uint8Array {
        try {
            const base64Helper = new util.Base64Helper();
            return base64Helper.decodeSync(base64);
        } catch (e) {
            return new Uint8Array(0);
        }
    }

    private bytesToBase64(data: Uint8Array): string {
        try {
            const base64Helper = new util.Base64Helper();
            return base64Helper.encodeToStringSync(data);
        } catch (e) {
            return "";
        }
    }

    private bytesToHex(data: Uint8Array): string {
        let hex = '';
        for (let i = 0; i < data.length; i++) {
            hex += data[i].toString(16).padStart(2, '0');
        }
        return hex;
    }
}
