import { Has_Value } from '@/utils/index'
import GlobalConst from '@/service/global-const'

/**
 * @param {String} id 匹配项数据id（id值可能对应多项）
 * @param {Array} options 数据字典
 */
export default function getDicText (id, options = []) {
    if (!Has_Value(id)) return id
    // 前提需要id与options有值时才执行获取数据字典文本的逻辑
    if (options && options.length > 0) {
        // 根据值分隔符切割出关键匹配项id
        let idList = id.split(GlobalConst.form.valueSeparator)
        // 定义结果集
        let result = []
        idList.forEach(i => {
            // 获取匹配项
            let item = options.find(j => j[GlobalConst.dicOption.idName] === i)
            // 获取对应文本
            let item_text = item && item[GlobalConst.dicOption.textName] || ''
            // 匹配文本添加进结果集
            item_text && result.push(item_text)
        })
        // 使用展示分隔符重新连接文本，进行展示
        return result.join(` ${GlobalConst.form.showSeparator} `)
    }
    return id
}