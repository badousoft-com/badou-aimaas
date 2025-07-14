/**
 * 返回按钮对象
 * @param {String, Object} icon String时为图标名称，Object时为图标对象
 */
function Get_Icon_Obj (icon) {
    if (!icon) return
    // 定义结果数据
    let result = {}
    // 判断传入数据属性引用
    switch (icon.constructor) {
        case String:
            // 字符串拼接为对象
            result = { name: icon }
            break
        case Object:
            // 更新结果数据
            result = icon
            break
        default:
            // do something default
    }
    // 返回结果
    return result
}
export {
    // 根据传入数据，获取图标对象数据
    Get_Icon_Obj,
}