/**
 * A对象对比B对象，获取B中不含有A的属性总和对象
 * 例如A:{aa:1,bb:2}, B:{bb:2,cc:3}, Get_Rest_Obj(A,B)则返回{aa:1}
 * @param {Object} mainObj 主对象
 * @param {Object} compareObj 对比对象
 * @returns {Object}
 */
function Get_Rest_Obj (mainObj, compareObj) {
    // 主对象为空，返回空对象
    if (!mainObj) return {}
    // 如果对比对象为空，则返回主对象
    if (!compareObj) return mainObj
    // 获取主对象的属性数组
    let _mainAttr = Object.keys(mainObj)
    // 获取对比对象的属性数组
    let _compareAttr = Object.keys(compareObj)
    // 定义结果集
    let _result = {}
    // 遍历主对象属性
    _mainAttr.forEach(i => {
        // 确认对比对象中是否不存在，则添加进结果集
        if (!_compareAttr.includes(i)) {
            _result[i] = mainObj[i]
        }
    })
    return _result
}

/**
 * 判断两个对象是否不一致
 * @param {Object} oldVal 比较的对象1
 * @param {Object} newVal 比较的对象2
 * @param {Object} option 配置项
 *      @param {Function} arrayCompareFun // 自定义数组比对方法
 * @returns {Boolean} 返回是否不一致的Boolean值
 */
function Is_Obj_Different (oldVal, newVal, option) {
    let isOldObj = oldVal instanceof Object
    let isNewObj = newVal instanceof Object
    let {
        arrayCompareFun, // 自定义数组比对方法
    } = option || {}
    // 如果不是对象 直接判断数据是否相等
    if (!isOldObj || !isNewObj) {
        return oldVal !== newVal
    }
    // 判断对象的可枚举属性组成的数组长度, 若长度不一致，则直接返回true（表示变更过）
    if (Object.keys(oldVal).length !== Object.keys(newVal).length) {
        return true
    }
    for (let key in oldVal) {
        let isSourceObj  = Object.prototype.toString.call(oldVal[key]) === '[object Object]'
        let isCompareObj = Object.prototype.toString.call(newVal[key]) === '[object Object]'
        let isArr = Object.prototype.toString.call(oldVal[key]) === '[object Array]'
        if (isSourceObj && isCompareObj) {
            // 如果是对象继续判断
            return Is_Obj_Different(oldVal[key], newVal[key])
        } else if (isArr) {
            // 如果是数组判断，判断传入自定义的数组比对方法
            if (arrayCompareFun &&
                typeof arrayCompareFun === 'function') {
                // 只需要管理true的情况
                if (arrayCompareFun.call(null, oldVal[key], newVal[key])) {
                    return true
                }
            } else {
                if (oldVal[key].toString() !== newVal[key].toString()) {
                    return true
                }
            }
        } else if (oldVal[key] !== newVal[key]) {
            // 不是对象的就判断数值是否相等
            return true
        }
    }
    return false
}

/**
 * 合并两个对象，返回新对象【可处理多层级对象合并】
 * @param {Object} baseObj 基础对象
 * @param {Object} customObj 自定义对象
 * @returns {Object} 合并后对象
 */
function Merge_Obj (baseObj, customObj) {
    // 基础项baseObj数据格式非对象时，返回空对象
    if (!(baseObj && baseObj.constructor === Object)) return {}
    // 自定义项customObj数据格式非对象时，返回基础项baseObj
    if (!(customObj && customObj.constructor === Object)) return baseObj
    // 浅拷贝基础项，避免更改到原数据项
    let result = Object.assign({}, baseObj)
    Object.keys(customObj).forEach(i => {
        // 定义获取自定义对象下字段对应值
        let _customValue = customObj[i]
        if (!(_customValue && _customValue.constructor === Object)) {
            // 值非对象Object时，直接更新结果集对应属性
            result[i] = _customValue
        } else {
            // 定义获取基础对象下字段对应值
            let _baseValue = baseObj[i]
            // 值非对象Object时，直接更新结果集对应属性
            if (!(_baseValue && _baseValue.constructor === Object)) {
                result[i] = customObj[i]
            } else {
                // 值为对象，调用递归，继续转化
                result[i] = Merge_Obj(result[i], customObj[i])
            }
        }
    })
    // 格式化输出控制台
    // console.log(JSON.stringify(result, null, 2))
    return result
}

// import { Get_Rest_Obj } from '@/utils/object'
export {
    // A对象对比B对象，获取B中不含有A的属性总和对象
    Get_Rest_Obj,
    // 判断两个对象是否不一致
    Is_Obj_Different,
    // 合并两个对象，返回新对象【可处理多层级对象合并】
    Merge_Obj,
}