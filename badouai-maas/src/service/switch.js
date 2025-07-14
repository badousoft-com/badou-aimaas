import { Has_Value } from '@/utils'

// 定义switch两种结果值
const YES = { Number: 1, Boolean: true }
const NO = { Number: 0, Boolean: false }
/**
 * 获取switch实际对应的Boolean值
 * @param {String, Number, Boolean} val 传入值
 * @returns {Boolean} true/false
 */
function Get_Switch_Val (val) {
    if (Has_Value(val)) {
        if (val.constructor === Boolean) {
            return val ? YES.Boolean : NO.Boolean
        }
        if (val.constructor === Number) {
            return val !== NO.Number ? YES.Boolean : NO.Boolean
        }
        if (val.constructor === String) {
            if (val === 'true' || (val === YES.Number + '')) {
                return YES.Boolean
            }
            if (val === 'false' || (val === NO.Number + '')) {
                return NO.Boolean
            }
            return YES.Boolean
        }
    }
    return NO.Boolean
}

/**
 * 获取switch类型的数字值，目前为 0 / 1
 * @param {*} val 传入值
 * @returns
 */
function Get_Switch_Num (val) {
    if (!Has_Value(val)) return NO.Number
    // 转化字符串类型，方便测试
    let _valStr = '' + val
    // 判断是否为'0'或者'false'，是则返回0
    if ((_valStr === '' + NO.Boolean) || (_valStr === '' + NO.Number)) {
        return NO.Number
    }
    return YES.Number
}

// import { YES, NO, Get_Switch_Val } from '@/service/switch'
export {
    YES,
    NO,
    // 获取switch实际对应的Boolean值
    Get_Switch_Val,
    // 获取switch类型的Number值，目前为 0 / 1
    Get_Switch_Num,
}