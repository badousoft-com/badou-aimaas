import CryptoJS from 'crypto-js'
export default {
    // 加密
    aesEncrypt (a) {
        let b = CryptoJS.enc.Utf8.parse(a)
        let c = CryptoJS.enc.Utf8.parse('izMNRXR9Cx96fTiE')
        let d = CryptoJS.enc.Utf8.parse('izMNRXR9Cx96fTiE')
        let e = CryptoJS.AES.encrypt(b, c, { iv: d, mode: CryptoJS.mode.CBC })
        return CryptoJS.enc.Base64.stringify(e.ciphertext)
    },
    // 解密
    aesDecrypt (a) {
        let b = CryptoJS.enc.Utf8.parse('izMNRXR9Cx96fTiE')
        let c = CryptoJS.enc.Utf8.parse('izMNRXR9Cx96fTiE')
        let d = CryptoJS.AES.decrypt(a, b, { iv: c, mode: CryptoJS.mode.CBC })
        return d.toString(CryptoJS.enc.Utf8)
    }
}
