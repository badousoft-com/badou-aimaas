
import GlobalConst from '@/service/global-const'
import { Get_UUID } from '@/utils'

/**
 * 获取转化为通用的数据字典的options,如设置id/text
 * @param {Array} options 初始的options数组
 * @param {String} name 字段键名
 * @returns 
 */
function Get_Key_Options (options, setting = {}) {
    if (!options) return []
    // 定义值字段
    const Id_Name = GlobalConst.dicOption.idName
    const Text_Name = GlobalConst.dicOption.textName
    let {
        name,
        [Id_Name]: _idKeyNow = Id_Name,
        [Text_Name]: _textKeyNow = Text_Name
    } = setting || {}
    _idKeyNow = _idKeyNow || Id_Name
    _textKeyNow = _textKeyNow || Text_Name
    if (options.constructor !== Array) {
        console.error(`字段${name}:获取的options数据不是Array格式`)
        return []
    }
    return options.map(i => {
        // 获取项
        let item = { ...i }
        // 定义值字段
        let errorTip = `检查字段${name || ''}的options数据，数据项不含有值键或者文本键`
        // 若项中不存在值字段
        if (!(_idKeyNow in item)) {
            // 获取旧版的值字段
            let _idNameOld = GlobalConst.dicOption.idNameOld
            // 存在则使用旧版值字段，没有则设置提示的值
            if (_idNameOld in item) {
                item[Id_Name] = item[_idNameOld]
            } else {
                item[Id_Name] = `noId-${Get_UUID()}`
                console.error(errorTip)
            }
        } else {
            item[Id_Name] = item[_idKeyNow]
        }
        let noDataTip = `暂无文本-${Get_UUID()}`
        if (!(_textKeyNow in item)) {
            // 获取旧版的文本字段
            let _textNameOld = GlobalConst.dicOption.textNameOld
            // 存在则使用旧版文本字段，没有则设置提示的值
            if (_textNameOld in item) {
                item[Text_Name] = item[_textNameOld] || noDataTip
            } else {
                item[Text_Name] = noDataTip
                console.error(errorTip)
            }
        } else {
            item[Text_Name] = item[_textKeyNow] || noDataTip
        }
        return item
    })
}

export {
    // 获取转化为通用的数据字典的options
    Get_Key_Options,
}