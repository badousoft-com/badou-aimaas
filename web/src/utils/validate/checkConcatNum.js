// 该模块用于放置通讯联系方式相关检验

/**
 * 校验手机号码有效性
 * @param {String} val 传入值
 * @return { Object } { isVerify:{Boolean} 校验状态, message:{String} 错误信息 }
 */
function Verify_Mobile_Num (val) {
    if (!val) {
        return {
            isVerify: false,
            message: '请输入手机号码'
        }
    }
    if (!(/^1(3|4|5|6|7|8|9)\d{9}$/.test(val))) {
        return {
            isVerify: false,
            message: '手机号码格式有误'
        }
    }
    return {
        isVerify: true,
        message: '手机号码校验成功'
    }
}

/**
 * 校验电话号码有效性（座机号码）
 * 正确格式为：XXXX-XXXXXXX，XXXX-XXXXXXXX，XXX-XXXXXXX，XXX-XXXXXXXX，XXXXXXX，XXXXXXXX
 * @param {String} val 传入值
 * @return { Object } { isVerify:{Boolean} 校验状态, message:{String} 错误信息 }
 */
function Verify_Telephone_Num (val) {
    if (!val) {
        return {
            isVerify: false,
            message: '请输入座机号码'
        }
    }
    if (!(/^(\d3,4\d3,4|\d{3,4}-)?\d{7,8}$/.test(val))) {
        return {
            isVerify: false,
            message: '座机号码格式错误'
        }
    }
    return {
        isVerify: true,
        message: '座机号码验成功'
    }
}

/**
 * 校验电话号码有效性（座机号码 + 手机号码）
 * @param {String} val 传入值
 * @return { Object } { isVerify:{Boolean} 校验状态, message:{String} 错误信息 }
 */
function Verify_Phone_Num (val) {
    let { isVerify: mobileVerify, message: mobileMessage } = Verify_Mobile_Num(val)
    let { isVerify: telVerify, message: telMessage } = Verify_Telephone_Num(val)
    if (mobileVerify) {
        return {
            isVerify: true,
            message: mobileMessage
        }
    }
    if (telVerify) {
        return {
            isVerify: true,
            message: telMessage
        }
    }
    return {
        isVerify: false,
        message: '电话号码格式错误'
    }
}

export {
    // 校验手机号码有效性（手机号码）
    Verify_Mobile_Num,
    // 校验电话号码有效性（座机号码）
    Verify_Telephone_Num,
    // 校验电话号码有效性（座机号码 + 手机号码）
    Verify_Phone_Num,
}