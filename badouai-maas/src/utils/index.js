// 核心工具类,非业务性质
// import {  } from '@/utils/index'

import { Deep_Clone } from '@/utils/clone'

/**
 * TODO：目前有缺陷：纯空格的话获取到的宽度值为0
 * 获取字符串在渲染时所需要的宽度px值
 * @param {String} val 字符串
 * @param {Object} options 配置项
 * @param {NUmber} distance 差值，结果值会加上这个差值，最终再返回
 * @returns {String} 字符串所需的宽度值
 */
function Get_Word_Width (val, options, distance = 0) {
    // 获取字体大小，字重，以及其余css配置项
    let {
        fontSize = 14,
        fontWeight = '400',
        ...elseStyleText
    } = options || {}
    // 创建span标签
    let span = document.createElement('span')
    // 设置值
    span.innerHTML = val
    // 设置不可见
    span.style.visibility = 'hidden'
    // 设置字体大小
    span.style.fontSize = `${parseInt(fontSize)}px`
    // 设置字重
    span.style.fontWeight = fontWeight
    // 设置样式
    elseStyleText && (span.style.cssText = elseStyleText)
    // dom中添加span标签
    document.body.appendChild(span)
    // 获取所需宽度值（Number）
    let widthVal = span.offsetWidth + distance + 4
    // 删除添加的span标签
    document.body.removeChild(span)
    return `${widthVal}px`
}

/**
 * 根据已有名称列表，获取新的序列名称
 *     如传入['a', 'b']，则会返回名称'default'
 *     如传入['a', 'b', 'default']，则会返回名称'default1'
 *     如传入['a', 'b', 'default1']，则会返回名称'default2'
 * @param list {Array} 已有名称列表
 * @param option {Object} 配置项
 *           {
 *              signKey {String} 默认名称变量名，默认值为default
 *              nameKey {String} 如果list为字符串名称列表['a', 'b', 'c'],则不需要配置该字段；
 *                               如果list为[{key:'a'}, {key:'b'}, {key:'c'}]，则需要配置nameKey:'key'指定项下对应名称的键
 *           }
 * @returns {String} 新的名称
 */
function Get_New_Name (list, option) {
    let {
        signKey = 'default', // 默认名称变量名
        nameKey, // 对应list下的对象键
    } = Object.assign({}, option)
    // 传入数据为空，则直接返回默认名称
    if (!(list && list.length > 0)) {
        return signKey
    }
    // 定义名称列表
    let _list = [...list]
    // 判断是否传入list下项的名称键
    if (nameKey) {
        // 更新名称列表
        _list = list.map(i => i[nameKey])
    }
    // 过滤掉没有名称的数据以及筛选出为字符串的名称
    _list = _list.filter(i => i && typeof i === 'string')
    // 判断过滤后的数据是否为空，空则直接返回默认变量名
    if (!(_list && _list.length > 0)) {
        return signKey
    }
    // 定义名称正则
    let nameReg = new RegExp('^' + signKey + '[0-9]+')
    // 获取所有名称，再根据正则匹配，获取到所由正则规则生成的名称，方便等下+1
    let nameArr = _list.filter(i => i && nameReg.test(i))
    // 判断当前是否还没有符合规则的名称，则返回通用名称+1，如default1
    if (nameArr.length === 0) {
        return `${signKey}1`
    }
    // 获取名称数组中符合正则规则的名称，提取出名称中的索引号
    let sortIndexList = nameArr.map(i => i.replace(signKey, ''))
    // 返回索引号+1的名称
    return signKey + (Math.max(...sortIndexList) + 1)
}

/**
 * 将字符串中的变量${name}转化为实际对应的值，并将转化后的字符串返回
 * @param {String} str 需要处理的字符串数据（含有变量）
 * @param {Object} valueObj 值对象，对象中属性与字符串中变量匹配
 * @param {*} signList 标记变量的符号数组（包含起始），如['${', '}']，目前暂不提供该参数
 * 例如：
 *     str: http://www.baidu.com?name=${name}&sex=${sex}
 *     valueObj: { name: 123, sex:'男' }
 * 转化后数据为：http://www.baidu.com?name=123&sex=男
 */
function Variable_Replaced_String (str, valueObj) {
    // 判断是否字符串类型
    if (str.constructor !== String) {
        console.error('函数Variable_Replaced_String：传入的字符串数据异常：非字符串或无值')
        return ''
    }
    // 去除空格
    str = str.replace(/\s*/g, '')
    // 判断是否存在值
    if (!str) {
        console.error('函数Variable_Replaced_String：传入的字符串数据异常：非字符串或无值')
        return ''
    }
    if (!(valueObj && valueObj.constructor === Object)) {
        console.error('函数Variable_Replaced_String：传入的值对象数据异常：非对象或无值')
        return str
    }
    // 正则截取${}匹配文本
    // 截取【${】开头,【}】结尾的字符，并且字符中间不再包含【${】，避免出现多个变量时取了第一个变量的头与最后一个变量的尾
    let regexp = /\$\{([^$}]*)\}/
    // 获取匹配数据
    //     1. 若不含变量(${})，则此处返回null
    //     2. 为了方便对数据的操作，这里的正则不添加全局/g,将使用递归函数对符合规则的进行字符逐一处理
    //     3. 若含变量，则result为['匹配文本', '子表达式匹配文本',...],例如这里会返回['${name}', 'name', ...]
    let result = str.match(regexp)
    if (result) {
        // 定义获取需要转化的文本：replaceContent
        // 定义获取变量字段名：fieldName
        let [replaceContent, fieldName] = result
        // 将钻取地址上的变量转化为具体对应值，更新钻取地址
        str = str.replace(replaceContent, valueObj[fieldName])
        return Variable_Replaced_String(str, valueObj)
    } else {
        return str
    }
}

/**
 * 模拟空值合并运算符功能函数，空值运算符??在IE不兼容，所以不直接使用
 * @param {*} val 使用值
 * @param {*} _default 默认值
 */
function Null_Judge (val, _default = '') {
    if (val === null || val === undefined) {
        return _default
    }
    return val
}

/** 判断字符串是否为空
 * @param {String} value 字符串值
 * @returns {Boolean}  为true时表示字符串等同无值
 */
function Is_Str_Empty (value) {
    if (value && value.constructor === String) {
        value = value.replace(/\s*/g, '')
        if (value !== '') {
            return false
        }
        return true
    }
    return true
}

/**
 * 判断是否有值，注意0是有值
 * @param {*} value
 * @returns {Boolean} 是否有值的状态
 */
function Has_Value (value) {
    // 注意这里不能使用isNaN，isNaN只在区分数字的场景下使用
    if (value + '' === 'NaN') return false
    return (value !== null && value !== undefined && value !== '')
}

/**
 * 去除字符串中空格
 * @param {String} val 需要处理的字符串
 */
function Remove_Space (val) {
    if (val && val.constructor === String) {
        return val.replace(/\s*/g, '')
    }
    return val
}

/**
 * 防抖函数
 * 使用规则
 *      let debounce = Debounce(fn, duration)
 *      若duration为1000ms时，则以下3个调用只会触发最后一个
 *          debounce()
 *          debounce()
 *          debounce()
 *      若想要动态传参，则这样传
 *      let debounce = Debounce(fn(data), duration)
 *          debounce(data)
 *          debounce(data)
 *          debounce(data)
 * @param {*} fn 函数体
 * @param {*} duration 时间
 * @returns
 */
function Debounce (fn, duration) {
    let timer = null
    return function () {
        clearTimeout(timer) // 如果持续触发，那么就清除定时器，定时器的回调就不会执行。
        timer = setTimeout(() => {
            fn.apply(this, arguments)
        }, duration)
    }
}

/**
 * @description: 生成UUID
 * @param {Boolean} hasLine 是否有横线
 * @returns {String} 返回32的随机编码
 */
function Get_UUID (hasLine = true) {
    function hexRandom (len) {
        return (Math.floor(Math.random() * (Math.pow(16, len) - Math.pow(16, len - 1))) + Math.pow(16, len - 1)).toString(16)
    }
    let now = (Date.now() % Math.pow(10, 12)).toString()
    let uuid = [hexRandom(8), now.substr(0, 4), now.substr(4, 4), now.substr(8, 4), hexRandom(12)]

    return uuid.join(hasLine ? '-' : '')
}

/**
 * 返回对象下指定路径下的数据，若path为'A，B'，则返回res[A][B]
 * @param {*} res  输入数据
 * @param {String} path：数据下路径
 * @return {*} 返回指定路径下数据
 */
function Get_Data_By_Path (res, path) {
    // 两者中若存在一个无值的，则直接返回初始数据
    if (!(res && path)) {
        return res
    }
    // 若res不为对象（Object或者Array），则path存在没意义，直接返回
    if (typeof res !== 'object') {
        return res
    }
    // 存储初始数据，在后续步骤中出错时方便直接返回
    let _res = Deep_Clone(res)
    try {
        // 获取数据所在路径数组
        let pathList = path.split(',')
        // 按照指定路径获取所选数据
        pathList.forEach(i => {
            if (!(i in res)) {
                console.error(`Get_Data_By_Path:匹配不到属性键${i}`)
                console.log(`当前操作对象为:`, res)
            } else {
                res = res[i]
            }
        })
    } catch (e) {
        console.error(`当前操作数据为${JSON.stringify(res)}, 提供的路径为${path},请检查路径是否异常`)
        return _res
    }
    return res
}

/**
 * 获取url上面的查询数据，以对象格式返回
 * @param {string} url 页面地址栏地址，含查询参数
 * @returns {Object} 以对象形式返回查询参数
 */
function Get_Url_QueryObj (url) {
    const search = url.split('?')[1]
    if (!search) {
        return {}
    }
    return JSON.parse(
        '{"' +
        decodeURIComponent(search)
            .replace(/"/g, '\\"')
            .replace(/&/g, '","')
            .replace(/=/g, '":"')
            .replace(/\+/g, ' ') +
        '"}'
    )
}

/**
 * 数组根据指定属性值进行排序
 * @param data {Array} 传入数据
 * @param prop {String 排序所依赖的对象字段名
 */
function Sort_By_Prop (data, prop, defaultIndex = 99999) {
    function sortFn () {
        return function (obj1, obj2) {
            let _num1 = parseInt(obj1[prop])
            let _num2 = parseInt(obj2[prop])
            // 需要留意值为0的情况
            _num1 = Has_Value(_num1) ? _num1 : defaultIndex
            _num2 = Has_Value(_num2) ? _num2 : defaultIndex
            return _num1 - _num2
        }
    }
    return data.sort(sortFn())
}

export {
    // 获取字符串在渲染时所需要的宽度px值
    Get_Word_Width,
    // 根据已有名称列表，获取新的序列名称
    Get_New_Name,
    // 将字符串中的变量${name}转化为实际对应的值，并将转化后的字符串返回
    Variable_Replaced_String,
    // 模拟空值合并运算符功能函数，空值运算符??在IE不兼容，所以不直接使用
    Null_Judge,
    // 判断字符串是否为空
    Is_Str_Empty,
    // 判断是否有值，注意0是有值的情况返回true
    Has_Value,
    // 去除字符串中空格
    Remove_Space,
    // 防抖函数
    Debounce,
    // 生成UUID
    Get_UUID,
    // 返回对象下指定路径下的数据，若path为'A，B'，则返回res[A][B]
    Get_Data_By_Path,
    // 获取url上面的查询数据，以对象格式返回
    Get_Url_QueryObj,
    Sort_By_Prop,
}