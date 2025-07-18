import { Verify_Mobile_Num, Verify_Telephone_Num, Verify_Phone_Num } from '@/utils/validate/checkConcatNum'
import Verify_IDCard from '@/utils/validate/checkIDCard'

/**
 * 基于表单的手机号码检验函数
 */
function Check_Mobile_Num (rule, value, callback) {
    if (!value) {
        // 不做必填校验
        callback()
    }
    let { isVerify, message } = Verify_Mobile_Num(value)
    if (!isVerify) {
        callback(new Error(message))
    } else {
        callback()
    }
}

/**
 * 定义座机号码检验规则
 */
function Check_Telephone_Num (rule, value, callback) {
    if (!value) {
        // 不做必填校验
        callback()
    }
    let { isVerify, message } = Verify_Telephone_Num(value)
    if (!isVerify) {
        callback(new Error(message))
    } else {
        callback()
    }
}

/**
 * 定义电话号码/联系方式检验规则
 */
function Check_Phone_Num (rule, value, callback) {
    if (!value) {
        // 不做必填校验
        callback()
    }
    let { isVerify, message } = Verify_Phone_Num(value)
    if (!isVerify) {
        callback(new Error(message))
    } else {
        callback()
    }
}

/**
 * 身份证-校验规则
 */
function Check_IDCard (rule, value, callback) {
    if (!value) {
        // 不做必填校验
        callback()
    }
    let isVerify = Verify_IDCard(value)
    let message = '身份证号码输入错误'
    if (!isVerify) {
        callback(new Error(message))
    } else {
        callback()
    }
}

export default {
    // 校验手机号码
    Check_Mobile_Num,
    // 检验座机号码
    Check_Telephone_Num,
    // 定义电话号码检验规则： (手机 + 座机)
    Check_Phone_Num,
    // 校验身份证号码
    Check_IDCard,
}