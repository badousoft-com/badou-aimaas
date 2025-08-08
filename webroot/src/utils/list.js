import GlobalConst from '@/service/global-const'
import { Has_Value, Remove_Space } from '@/utils'

/**
 * 判断传入数据是否为数组，并且存在值
 * @param {Array} arr
 * @returns Boolean  arr为数组并且有值时返回true
 */
function Has_Array_Data (arr) {
    return arr && arr.constructor === Array && arr.length > 0
}

/**
 * 将数组下对象按照指定属性进行数据重组，一维数组转二维数组
 * @param {Array} list 传入数组
 * @param {String} attrName 指定属性的键名
 * @param {String} defaultAttrValue 指定属性字段的默认值(指定字段无值时会使用此值)
 * @param {Object} options 配置项
 *      {Boolean} checkCase: 是否开启大小写校验. 默认-true：大小写不同时表示值不同
 *      {Array} groupFieldKeys 组别属性键数组
 *      {String} sort 传入属性名值字符串如'a,b'，排序时会将这里设置的组别优先展示
 * @returns
 */
function List_Filter_Group_ByAttr (list, attrName, defaultAttrValue = '基本', options) {
    let {
        checkCase = true,  // 是否校验比对值的大小写。默认-true-大小写不同将视为不一样
        groupFieldKeys = [], // 组别属性键数组
        sort, // 传入组别名字符串如'a,b'，排序时会将这里设置的组别优先展示
    } = Object.assign({}, options)
    if (!list || list.constructor !== Array || list.length === 0 || !attrName) return []
    // 为列表数据刷入属性字段（避免数据项下指定attrName属性缺失或者数据异常）
    list.forEach(i => {
        let _val = i[attrName]
        i[attrName] = Has_Value(_val) ? _val : defaultAttrValue
    })
    let result = []
    list.forEach(i => {
        // 找到数据项所在数组中下角标
        let _index = null
        // 判断是否检查值的大小写
        if (checkCase) {
            _index = result.findIndex(k => k[attrName] === i[attrName])
        } else {
            _index = result.findIndex(k => k[attrName]?.toUpperCase() === i[attrName]?.toUpperCase())
        }
        if (_index !== -1) {
            result[_index].list.push(i)
        } else {
            // 定义组别完整信息对象
            let _groupFieldObj = {}
            if (groupFieldKeys && groupFieldKeys.length > 0) {
                // 根据groupFieldKeys组装完整的对象数据
                _groupFieldObj = groupFieldKeys.reduce((_res, item) => {
                    let _val = i[item]
                    _res[item] = Has_Value(_val) ?  _val : ''
                    return _res
                }, {})
            }
            let groupObj = {
                [attrName]: i[attrName] || '',
                ..._groupFieldObj, // 组别更多属性
                list: [i]
            }
            result.push(groupObj)
        }
    })
    // 2022-10-25:注释以下操作，修改为在初始的时候若发现没有组名则使用默认值
    // let index = result.findIndex(i => !i[attrName])
    // // result数组中可能含有groupName为空的对象，为其设置组名
    // if (index !== -1) {
    //     result[index][attrName] = defaultGroupName
    // }
    // 判断是否传入组别排序的属性sort，且此时排序的数据是有值的
    if (sort && result.length > 0) {
        // 存储已有数据
        let _result = result
        // 清空原有的
        result = []
        // 将传入的sort数据'a,b'转成['a','b']
        let _sortArr = Remove_Space(sort).split(GlobalConst.separator)
        // 遍历按照sort，查找匹配项加入结果集
        _sortArr.forEach(i => {
            let _itemIndex = _result.findIndex(j => j[attrName] === i)
            // 若存在，则将匹配的数据添加进结果集，并且在原列表中删除对应数据
            ~_itemIndex && result.push(..._result.splice(_itemIndex, 1))
        })
        // 合并结果集与剩余的数据
        result = [...result, ..._result]
    }
    // 返回结果
    return result
}

/**
 * 二维数组转化成一维数组
 * @param {Array} list 二维数组
 * @param {String} childrenName 二维数组下的项下的子数组的键名，默认是children
 * @returns {Array} 一维数组
 */
function Get_List_By_TwoDimen (list, childrenName = 'children') {
    if (!(list && list.constructor === Array)) return []
    if (!childrenName) return []
    let result = []
    list.forEach(i => {
        let childList = i[childrenName]
        if (childList && childList.length > 0) {
            let itemField = { ...i }
            delete itemField[childrenName]
            childList.forEach(j => {
                result.push({ ...itemField, ...j})
            })
        }
    })
    return result
}

/**
 * 合并两个数组，按照指定属性名，注意：浅拷贝
 * 默认是以前置数组顺序为主，以 后置数组的内容为主
 * @param {Array} oldList 前置数组
 * @param {Array} newList 后置数组
 * @param {String} attrName 匹配的字段键名
 * @param {Object} options 配置项对象
 *      @param {Boolean}} isContentFirst 是否以前置数组的内容为主：默认false:内容后置为主
 *      @param {Boolean}} isOrderFirst 是否以前置数组的字段排序为主: 默认true:前置为主
 *      @param {Boolean}} isSaveNoAttr 是否处理并返回不带指定属性名的数据：默认true
 */
function Merge_List (oldList, newList, attrName, options) {
    // 定义前置数组
    let _oldList = null
    // 定义后置数组
    let _newList = null
    // 定义结果集
    let result = []
    // 处理前置数组
    if (!(oldList && oldList.constructor === Array)) {
        _oldList = []
    } else {
        // 浅拷更新前置数组
        _oldList = Object.assign([], oldList)
    }
    // 处理后置数组
    if (!(newList && newList.constructor === Array)) {
        _newList = []
    } else {
        // 浅拷更新后置数组
        _newList = Object.assign([], newList)
    }
    // 若（前置数组或后置数组存在一个为空）|| 传入判断属性名为空，则直接合并两个数组返回
    if (_oldList.length === 0 || _newList .length === 0 || (!attrName)) {
        return [..._oldList, ..._newList]
    }
    let {
        isContentFirst = false, // 是否以前置数组的内容为主
        isOrderFirst = true,  // 是否以前置数组的字段排序为主
        isSaveNoAttr = true,  // 是否处理并返回不带指定属性名的数据
        isContentMerge = false, // 两个数组中对应的匹配项内容处理：true-合并内容/false-覆盖，默认是false
    } = options || {}
    // 更新结果集
    result = isOrderFirst ? _oldList : _newList
    // 定义获取等待操作的列表数据
    let waitList = !isOrderFirst ? _oldList : _newList
    waitList.forEach(i => {
        // 判断数据项是否含有指定字段名
        if (i.hasOwnProperty(attrName) && i[attrName]) {
            let matchIndex = result.findIndex(j => j[attrName] === i[attrName])
            // 没找到匹配【指定字段属性】，直接添加
            if (matchIndex === -1) {
                result.push(i)
            } else {
                // 根据isContentFirst !== isOrderFirst，可确认哪一些是需要替换的
                // 至于为什么按照此根据，为个人经验，请自行研究
                if (isContentFirst !== isOrderFirst) {
                    // 移动项的位置
                    result.splice(matchIndex, 1, isContentMerge ? {...result[matchIndex], ...i} : i)
                }
            }
        } else {
            // 不含字段名则直接添加
            isSaveNoAttr && result.push(i)
        }
    })
    return result
}

/**
 * 在A数组中删除B数组存在的项,并且返回新的数组；函数不支持对象数组
 * @param {Array} firList 目标数组
 * @param {*} secList 需要删除数组项
 * @param {String} attrName 不传默认为字符串数组，传则是对象数组，指定比较的属性名对应值
 * @param {Object} setting 配置项
 * @returns 不含有secList中项的数组firList
 */
function Filter_AList_Without_BList (firList, secList, attrName = '', setting) {
    if (firList.constructor !== Array) return []
    if (secList.constructor !== Array) return firList
    let {
        ignoreCase = false, // 是否忽略大小写，默认false:严格区分大小写不同则为不同字段
    } = setting || {}
    secList.forEach((i, i_index) => {
        firList.forEach((j, j_index) => {
            if (!attrName) {
                // 不指定属性名，表示当前比较双方是字符串数组，i/j直接表示值，进行比较
                if (i === j) {
                    firList.splice(j_index, 1)
                }
            } else {
                // 指定属性名，表示当前比较双方的对象数据
                // 根据是否忽略大小写获取最终值
                let _val1 = ignoreCase ? i[attrName].toUpperCase() : i[attrName]
                let _val2 = ignoreCase ? j[attrName].toUpperCase() : j[attrName]
                if (_val1 && _val2 && _val1 === _val2) {
                    // 存在一致的数据则删除改项，确保最终保留下的是完全不一致的数据
                    firList.splice(j_index, 1)
                }
            }
        })
    })
    return firList
}

/**
 * 对象数组去重，根据attrName
 * @param {Array} data 数组数据
 * @param {String} attrName 子项对象中用于对比的属性（项为对象时需要传该参数）
 * @returns {Array} 去重后的数组
 */
function Unique_Array (data, attrName) {
    if (!Has_Array_Data(data)) return []
    if (!attrName) {
        return Array.from(new Set(data))
    }
    return [...(data.reduce((res, i) => res.set(i[attrName], i), new Map())).values()]
}

/**
 * 获取两个数组中相同的项并返回相同项组成的数组(此时返回的是数组B中对应数据)
 * @param {Array} arrA 数组A
 * @param {Array} arrB 数组B
 * @param {String} attrName 对比属性键名（项为对象时需要传该参数）
 * @returns {Array} 相同项数组
 */
function Get_Same_Array (arrA, arrB, attrName) {
    if (!Has_Array_Data(arrA)) return []
    if (!Has_Array_Data(arrB)) return []
    let _result = []
    arrA.forEach(i => {
        try {
            arrB.forEach(j => {
                let _value1 = attrName ? i[attrName] : i
                let _value2 = attrName ? j[attrName] : j
                if (_value1 === _value2) {
                    _result.push(j)
                    throw 'End'
                }
            })
        } catch (e) {
            // nothing
        }
    })
    return _result
}

/**
 * 在可递归的数组中根据指定键与值找到匹配项
 * @param {Array} data 数组数据(含有可递归的特性)
 * @param {String} attrName 属性键名
 * @param {*} attrVal 属性值
 * @param {Object} setting 配置项
 *      @param {String} recursionFieldName 递归的字段键名，默认为children
 * @returns {Object} 匹配项
 */
function Find_Data_In_Recursion (data, attrName, attrVal, setting) {
    let {
        recursionFieldName = 'children',
    } = setting || {}
    if (!(
        Has_Array_Data(data) &&
        attrName &&
        Has_Value(attrVal) &&
        recursionFieldName)) return
    // 定义返回的结果
    let _res = null
    try {
        data.forEach(i => {
            // 判断存在匹配项时，更新结果，终止循环
            if (attrName in i && i[attrName] === attrVal) {
                _res = i
                throw 'end'
            } else {
                // 判断是否存在可递归字段，则继续往下进行
                if (recursionFieldName in i) {
                    _res = Find_Data_In_Recursion(i[recursionFieldName], attrName, attrVal, setting)
                }
            }
        })
    } catch (_) {
        // do something
    }
    // 返回结果值
    return _res
}

/**
 * 数组(项为对象)按照指定属性进行重新排序，返回新数组
 * @param {Array} data 数组数据
 * @param {String} name 指定的排序字段键名
 * @param {Object} setting 配置项
 *      @param {Boolean} orderAsc 是否按照顺序进行排序(默认true)，true-顺序 / false-倒序
 *      @param {Number} defaultIndex 默认的排序号，当字段对象时会使用此作为当前字段的排序号
 *      @param {Boolean} ignoreNoVal 忽略排序字段无值的项，始终放置最后(默认false)
 * @returns {Array} 排序后的新数组
 */
function Sort_List (data, name, setting) {
    if (!name) {
        console.error(`请传入有效字段name名,当前值为${name}`)
        return
    }
    if (!(data && data.constructor === Array)) return []
    let {
        orderAsc = true, // 升序排序
        defaultIndex = 99999, // 默认排序
        ignoreNoVal = false, // 忽略无排序值项不参与排序，直接放置最后
    } = Object.assign({}, setting)
    // 判断项是否参与排序的函数
    let ignoreSort = (item) => ignoreNoVal && !Has_Value(item[name])
    // 定义结果值
    let _result = data.filter(i => ignoreSort(i))
    // 定义需要排序项
    let _sortList = data.filter(i => !ignoreSort(i))
    // 返回排序后的新数组
    _sortList = _sortList.sort((obj1, obj2) => {
        let _val1 = obj1?.[name]
        let _val2 = obj2?.[name]
        // 需要留意值为0的情况，设置默认排序号
        _val1 = Has_Value(_val1) ? _val1 : defaultIndex
        _val2 = Has_Value(_val2) ? _val2 : defaultIndex
        // 根据顺序倒序决策
        return orderAsc ? (_val1 - _val2) : (_val2 - _val1)
    })
    return _sortList.concat(_result)
}

export {
    // 判断传入数据是否为数组，并且存在值
    Has_Array_Data,
    // 列表数据根据数据项中 指定属性值 进行分组
    List_Filter_Group_ByAttr,
    // 二维数组转化成一维数组
    Get_List_By_TwoDimen,
    // 合并两个数组，按照指定属性名
    Merge_List,
    // 在A数组中删除B数组存在的项,并且返回新的数组；函数不支持对象数组
    Filter_AList_Without_BList,
    // 对象数组去重，根据attrName
    Unique_Array,
    // 获取两个数组中相同的项并返回相同项组成的数组
    Get_Same_Array,
    // 在可递归的数组中根据指定键与值找到匹配项
    Find_Data_In_Recursion,
    // 数组(项为对象)按照指定属性进行重新排序，返回新数组
    Sort_List
}