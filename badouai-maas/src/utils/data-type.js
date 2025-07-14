/*
 * @FilePath: @/utils/data-type.js
 * @Description: 判断数据类型
 */

// 是否为Object类型
export function Is_Object (data) {
    return Object.prototype.toString.call(data) === '[object Object]'
}

// 是否为Array类型
export function Is_Array (data) {
    return Object.prototype.toString.call(data) === '[object Array]'
}

// 是否为Function类型
export function Is_Function (data) {
    return data && data.constructor === Function
}

/**
 * @description: 是否为Number类型
 * @param {*} data：判断的值类型
 * @param {*} ingnoreSpecial：是否忽略NaN，正无穷、负无穷等特殊值（即返回false），默认忽略
 * @return {Boolean}
 */
export function Is_Number (data, ingnoreSpecial = true) {
    if (ingnoreSpecial) return isFinite(data)
    return typeof data === 'number'
}

// 判断是否为String类型
export function Is_String (data) {
    return typeof data === 'string'
}