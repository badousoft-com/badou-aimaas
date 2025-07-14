/**
 * 通过类名获取父级DOM元素
 * @param {DOMElement} element 当前dom元素
 * @param {String} aimClassName 所指定的父级类名
 */
function Find_Parent_By_ClassName (element, aimClassName) {
    // 初始化返回元素
    let _element = element
    try {
        // 使用循环，向上逐渐找父亲，直到匹配对应类名
        do {
            _element = _element.parentNode
        } while (!hasClassName(_element.classList, aimClassName))
    } catch (e) {
        console.error(`使用函数Find_Parent_By_ClassName异常：${e}`)
    }
    return _element
}

/**
 * 获取指定类名的子元素
 * @param {DOMElement} parentElement 直接父级dom元素
 * @param {String} aimClassName 目标类名
 * @param {Boolean} isNextLevel 是否只在直接下一级中寻找； false-将一直往下转层级
 * @return {Array | DOMElement} aimList 匹配的元素数据
 */
function Find_By_ClassName (parentElement, aimClassName, isNextLevel = false) {
    // 设置父元素默认为document
    parentElement = parentElement || document
    // 定义目标数组
    let aimList = []
    // 获取父级元素下所有子元素-HTMLCollection类型
    let childCollections = isNextLevel ? parentElement.children : parentElement.getElementsByTagName('*')
    // 获取父级元素下所有子元素集合Array类型
    let childElements = [...childCollections]
    // 遍历子元素，寻找与目标类名匹配的子元素
    childElements.forEach(i => {
        try {
            if (i.className &&
                typeof i.className === 'string' &&
                hasClassName(i.classList, aimClassName)) {
                aimList.push(i)
            }
        } catch (e) {
            console.error(`查找dom元素异常：${e}`)
        }
    })
    // 没有找到返回项，返回null
    if (aimList.length === 0) {
        return null
    }
    // 一般来说返回数组类型数据，当数据只有一条的时候返回该条对象数据方便接收后直接处理
    if (aimList.length === 1) {
        return aimList[0]
    }
    return aimList
}
/**
 * 获取父级下第一个子元素
 * @param {DOMElement} parentElement 直接父级dom元素
 * @return {Array | DOMElement}  匹配的元素数据
 */
function Find_First_Child (parentElement) {
    // 设置父元素默认为document
    parentElement = parentElement || document
    // 获取父级元素下所有子元素-HTMLCollection类型
    let childCollections = parentElement.getElementsByTagName('*')
    // 获取父级元素下所有子元素集合Array类型
    let childElements = [...childCollections]
    return childElements[0] || []
}

/**
 * 获取当前dom元素的兄弟节点元素集合
 * @param {DOMElement} element 当前dom元素
 * @return {Array}  匹配的兄弟DOM
 */
function Find_Siblings (element) {
    let domSiblings = element.parentNode.children
    let arraySiblings = [...domSiblings]
    let aimList = []
    arraySiblings.forEach(i => {
        if (i.nodeType === 1 && i !== element) {
            aimList.push(i)
        }
    })
    return aimList
}

/**
 * 判断元素是否含有指定类名
 * @param {DOMTokenList} domList
 * @param {String} aimClassName
 * @return {Boolean} status
 */
function hasClassName (domList, aimClassName) {
    let list = [...domList]
    let status = false
    for (let i = 0; i < list.length; i++) {
        if (list[i] === aimClassName) {
            status = true
            break
        }
    }
    return status
}

export {
    // 通过类名获取父级DOM元素
    Find_Parent_By_ClassName,
    // 获取指定类名的子元素
    Find_By_ClassName,
    // 获取父级下第一个子元素
    Find_First_Child,
    // 获取当前dom元素的兄弟节点元素集合
    Find_Siblings,
}