// 深拷贝-简单场景版
// 1. JSON.parse与JSON.stringify的使用
// 2. 无法拷贝 undefined ， function， RegExp 等等类型

// 深拷贝-递归拷贝覆盖全面版
// 可以拷贝undefined ， function， RegExp类型的拷贝
function Deep_Clone (target) {
    // 定义一个变量
    let result = null
    // 如果当前需要深拷贝的是一个对象的话
    if (typeof target === 'object') {
        if (Array.isArray(target)) {
            // 如果是一个数组的话
            result = [] // 将result赋值为一个数组，并且执行遍历
            for (let i in target) {
                // 递归克隆数组中的每一项
                result.push(Deep_Clone(target[i]))
            }
        } else if (target === null) {
            // 判断如果当前的值是null的话；直接赋值为null
            result = null
        } else if (target.constructor === RegExp) {
            // 判断如果当前的值是一个RegExp对象的话，直接赋值
            result = target
        } else {
            // 否则是普通对象，直接for in循环，递归赋值对象的所有值
            result = {}
            for (let i in target) {
                result[i] = Deep_Clone(target[i])
            }
        }
    } else {
        // 如果不是对象的话，就是基本数据类型，那么直接赋值
        result = target
    }
    // 返回最终结果
    return result
}

export {
    Deep_Clone,
    Deep_Clone as DeepClone, // TODO：待删除，不推荐使用
    Deep_Clone as deepClone, // TODO：待删除，不推荐使用
}