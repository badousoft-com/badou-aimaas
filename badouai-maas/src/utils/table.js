/**
 * 合并表头
 * @param {Array} data 源数据，一维数组
 * @param {Array} format 自定义的数据格式，children为默认属性键
 * @param {String} attrName 对比的属性键名
 * @returns 格式化后的数据
 */
function Merge_Table_Column (data, format, attrName = 'name') {
    return format.map(i => {
        // 判断是否存在关键键
        if (i[attrName]) {
            // 找回源字段完整属性
            let _fieldObj = data.find(j => j[attrName] === i[attrName])
            // 更新字段对象
            i = Object.assign({}, _fieldObj, i)
        }
        // 判断是否存在下级数据
        if (i.children && i.children.length > 0) {
            // 递归调用，拼装返回
            i.children = Merge_Table_Column(data, i.children)
        }
        return i
    })
}

export {
    // 合并表头
    Merge_Table_Column,
    Merge_Table_Column as Merge_TableColumn, // TODO:即将废弃
}