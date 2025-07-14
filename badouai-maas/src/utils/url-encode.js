/**
 * 编码相关js
 */

const URL_Code = {
    encode: (url) => encodeURIComponent(url), // 编码
    decode: (urlCode) => decodeURIComponent(urlCode), // 解码
}

// import { URL_Code } from '@/utils/encode'
export {
    URL_Code
}