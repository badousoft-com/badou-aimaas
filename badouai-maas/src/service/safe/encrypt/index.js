// rsa算法
import { JSEncrypt } from 'jsencrypt'
import PublicKey from './items/publicKey'
import PrivateKey from './items/privateKey'
import { b64tohex, hex2b64 } from 'jsencrypt/lib/lib/jsbn/base64'

/**
    * 长文本加密
    * @param {string} string 待加密长文本
    * @returns {string} 加密后的base64编码
    * */
JSEncrypt.prototype.encryptUnicodeLong = function (string) {
/* eslint-disable */
    var k = this.getKey()
    try {
        var ct = ''
        //RSA每次加密117bytes，需要辅助方法判断字符串截取位置
        //1.获取字符串截取点
        var bytes = new Array()
        bytes.push(0)
        var byteNo = 0
        var len, c
        len = string.length
        var temp = 0
        for (var i = 0; i < len; i++) {
            // 获取每一位的uniCode编码
            c = string.charCodeAt(i)
            if (c >= 0x010000 && c <= 0x10FFFF) {  //特殊字符，如Ř，Ţ
                byteNo += 4
            } else if (c >= 0x000800 && c <= 0x00FFFF) { //中文以及标点符号
                byteNo += 3
            } else if (c >= 0x000080 && c <= 0x0007FF) { //特殊字符，如È，Ò
                byteNo += 2
            } else { // 英文以及标点符号
                byteNo += 1
            }
            // 符合条件的有
            //     117n + 114、
            //     117n + 115、
            //     117n + 116、
            //     117n
            if ((byteNo % 117) >= 114 || (byteNo % 117) == 0) {
                if (byteNo - temp >= 114) {
                    bytes.push(i)
                    temp = byteNo
                }
            }
        }
        //2.截取字符串并分段加密
        if (bytes.length > 1) {
            for (var i = 0; i < bytes.length - 1; i++) {
                var str
                if (i == 0) {
                    str = string.substring(0, bytes[i + 1] + 1)
                } else {
                    str = string.substring(bytes[i] + 1, bytes[i + 1] + 1)
                }
                var t1 = k.encrypt(str)
                ct += t1
            }
            if (bytes[bytes.length - 1] != string.length - 1) {
                var lastStr = string.substring(bytes[bytes.length - 1] + 1)
                ct += k.encrypt(lastStr)
            }
            return hex2b64(ct)
        }
        var t = k.encrypt(string)
        var y = hex2b64(t)
        return y
    } catch (ex) {
        console.log(ex)
        return false
    }
/* eslint-disable */
}

/**
   * 长文本解密
   * @param {string} string 加密后的base64编码
   * @returns {string} 解密后的原文
   * */
JSEncrypt.prototype.decryptUnicodeLong = function (string) {
/* eslint-disable */
    var k = this.getKey()
    // 这里变更
    var maxLength = ((k.n.bitLength() + 7) >> 3)
    try {
        var string = b64tohex(string)
        var ct = ''
        if (string.length > maxLength * 2) {
            var lt = string.match(/.{1,256}/g)  //128位解密。取256位
            lt.forEach(function (entry, index) {
                var t1 = k.decrypt(entry)
                ct += t1
            })
            return ct
        }
        var y = k.decrypt(string)
        return y
    } catch (ex) {
        return false
    }
/* eslint-disable */
}

/**
 * 加密
 * @param {String} str 需要加密的字符串
 * @param {String} publicKey 公钥
 * @returns 
 */
function RSA_Encode (str) {
    const encrypt = new JSEncrypt()
    encrypt.setPublicKey(PublicKey) // 设置 加密公钥
    // const data = encryptStr.encrypt(str.toString()) // 进行加密
    const data = encrypt.encryptUnicodeLong(str.toString()) // 进行加密
    return data
}

/**
 * 解密
 * @param {String} str 需要解密的字符串
 * @param {String} primaryKey 私钥
 * @returns 
 */
function RSA_Decode (str) {
    let decrypt = new JSEncrypt()
    decrypt.setPrivateKey(PrivateKey) // 解密公钥
    let decryptMsg = decrypt.decryptUnicodeLong(str)
    // let decryptMsg = decrypt.decryptLong2(base64ToArrayBuffer(str))
    // let decryptMsg = decrypt.decrypt(str.toString()) // 解密
    return decryptMsg
}
window.RSA_Encode = RSA_Encode
window.RSA_Decode = RSA_Decode
export {
    RSA_Encode, // 加密
    RSA_Decode, // 解密
}