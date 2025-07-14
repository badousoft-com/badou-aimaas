export default {
    beforeRender: function () {
        // 更新地址本-环节处理人
        changeAddressBookType(this.fieldList)
        // 设置流程中的结束环节中字段只展示核心字段，其他字段隐藏
        hideEndNodeField(this.fieldList)
    },
    fieldChange: function (fieldName, value, fieldObj, formScope) {
        // this: 指向moduleForm/index.vue所在页面作用域

        // 场景举例： 字段A值变更时修改字段B的options
        switch (fieldName) {
            case 'nodeHandlerType':
                // 处理人类型一旦变更，则(1)清空环节处理人值(2)更新环节处理人的addressType
                // 获取环节处理人字段对象
                let nodeProcessorText = this.fieldList.find(i => i.name === 'nodeProcessorText')
                nodeProcessorText.value = null
                // 更新地址本-环节处理人
                changeAddressBookType(this.fieldList)
                break
            default: 
                // do something default
        }
    }
}

/**
 * 地址本type格式为2-1-x-2,x的取值如下，下面逻辑更新也主要是更新x的值
 *      0: 组织
 *     20: 用户
 *     40: 角色
 * @param {Array} fieldList 表单字段数组
 */
function changeAddressBookType (fieldList) {
    if (!(fieldList && fieldList.length > 0)) return
    // 处理人类型如'20'
    let nodeHandlerType = fieldList.find(i => i.name === 'nodeHandlerType')
    // 环节处理人如'2-1-20-2'
    let nodeProcessorText = fieldList.find(i => i.name === 'nodeProcessorText')
    // 定义地址本字段键名
    let addressTypeName = 'addressType'
    // 获取环节处理人如'2-1-20-2'
    let addressType = nodeProcessorText?.[addressTypeName]
    // 获取处理人类型如'20'
    let type = nodeHandlerType?.value
    if (!addressType) return 
    if (!type) return addressType
    // 定义分隔符
    let separator = '-'
    // 根据分隔符拆分为数组['2','1','x','2']
    let _valueList = addressType.split(separator)
    // 更新x位置的内容
    _valueList[2] = type
    // 重新转化为新的2-1-x-2返回
    nodeProcessorText[addressTypeName] = _valueList.join(separator)
}

/**
 * 设置流程中的结束环节中字段只展示核心
 * @param {Array} fieldList 字段数组
 */
function hideEndNodeField (fieldList) {
    // 获取环节对象中结束状态字段
    let flgRootField = fieldList.find(i => i.name === 'flgRoot')
    // 1：标识当前为结束环节
    if (flgRootField.value === '1') {
        // 定义需要展示的字段，其他字段默认隐藏
        let showField = ['code', 'name', 'memo']
        // 创建过渡变量_type存储字段真实type，所有字段变更为隐藏
        fieldList.forEach(i => {
            i._type = i.type
            i.type = 'hidden'
        })
        // 设置目标字段还原其type
        showField.forEach(i => {
            let currentField = fieldList.find(j => j.name === i)
            currentField.type = currentField._type
        })
    }
}