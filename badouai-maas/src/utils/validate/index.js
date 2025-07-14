/**
 * 是否为外部链接
 * @param {string} path
 * @returns {Boolean}
 */
function Is_External_Url (path) {
    return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * @description: 校验电子邮箱格式
 * @param {String} val 电子邮箱
 */
function Is_Email (val) {
    // 有值且格式正确，true
    // 有值且格式不对，false
    // 无值，则不做校验，默认true
    if (val) {
        return /^[a-zA-Z0-9_-]+@[a-zA-Z0-9]+\.[a-zA-Z0-9]+$/.test(val.trim())
    }
    return true
}

// 【！！！！！！！！！！！！！！！目前函数转化功能无效，待进一步确认】
// 将【字符串格式的正则表达式字面量】转成【RegExp构造函数需要的规则】
//     1.正则表达式字面量：rule = /^[a-zA-Z]+$/  [注意这里是没有单引号或双引号的，为Object格式非String]
//     2.使用RegExp构造函数正则表达式  rule = new RegExp("^[a-zA-Z]+$", "g")
// 此处的转化则是将  '/^[a-zA-Z]+$/'  =>  '^[a-zA-Z]+$' 【后续new RegExp才可以使用】
function Str_To_RegExpRule (rule) {
    if (!(rule && rule.constructor === String)) return
    // 转化步骤1：截取前后斜线【/】中间内容
    let sign = '/'
    let startIndex = rule.indexOf(sign)
    let endIndex = rule.lastIndexOf(sign)
    if (startIndex === -1) startIndex = 0
    if (endIndex === -1) endIndex = rule.length - 1
    rule = rule.slice(startIndex, endIndex)
    if (!rule) return
    // 转化步骤2：将所有转义符【\】转成【\\】
    rule = rule.replace(/\//g, '\\\\')
    return rule
}

export {
    // 是否为外部链接
    Is_External_Url,
    // 是否为邮箱
    Is_Email,
    // 将【字符串格式的正则表达式字面量】转成【RegExp构造函数需要的规则】
    Str_To_RegExpRule
}