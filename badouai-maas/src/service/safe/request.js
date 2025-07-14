
import { RSA_Encode } from './encrypt/index'
import GlobalConst from '@/service/global-const'
import { Suit_Sign_WhiteList } from './white-list'

/**
 * 返回请求headers需要添加的参数对象
 * @param {Object} params json格式参数
 * @params {String} url 请求的接口地址，这里主要用于匹配是否加sign的白名单地址
 * @return {Object} headers属性参数
 */
function Get_Header_Safe_Params (params = {}, url) {
    // 检查全局的配置项中是否开启了参数加密，没有开启，则直接停止逻辑
    if (!GlobalConst.openSafeRequestParams) return
    if (params.constructor !== Object) {
        console.error(`传入Get_Header_Safe_Params的参数不是Object,值为${params}`)
        params = {}
    }
    // 存在特殊现象，参数中含有不应该出现的sign，未重现。考虑sign作为安全参数，该字段不应被使用，这里去除
    delete params.sign
    // 这个用于构造加密参数使用
    let _params = Object.assign({}, params)
    // 去除附件数组值，更改为'',同时后端不对附件做校验处理
    Object.keys(_params).forEach(key => {
        let _value = _params[key]
        if (_value instanceof Array) {
            // 目前场景：数组则为附件，附件无法校验，踢出sign参数拼装
            // 目前是通过数组判断是否为附件，因为常规的数组数据一般会转为json，纯数组时应该是附件数组
            delete _params[key]
        } else if (_value && _value.constructor === File) {
            // 单个值为附件时，也踢出sign参数拼装
            delete _params[key]
        }
    })
    let nonce = {
        timestamp: new Date().getTime(), // 时间戳
        // MAC: xxx,
        // IP: xxx
    }
    // 将_params加密后后值追加给真正的请求参数params
    // 需求：设计器页面的部分参数过多，保存时参数值处理过慢，暂时处理为白名单，不添加sign加密
    if (!Suit_Sign_WhiteList(url)) {
        // 这里使用encodeURIComponent，不使用encodeURI
        // 主要为了解决中文编码问题引入encodeURIComponent，同时解决数据中的特殊符号'+'没有被编译导致传到后台为空
        (params.sign = RSA_Encode(encodeURIComponent(JSON.stringify(_params))))
    }
    return {
        nonce: RSA_Encode(JSON.stringify(nonce)),
        // 加多一层ecodeURI，避免中文加密异常,修改为请求
        // sign: RSA_Encode(encodeURI(JSON.stringify(params)))
        // sign: RSA_Encode(JSON.stringify(params))
    }
}

export {
    Get_Header_Safe_Params
}