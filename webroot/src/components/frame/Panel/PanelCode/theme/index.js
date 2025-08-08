/*
 * @FilePath: @/components/frame/Panel/PanelCode/theme/index.js
 * @Description: 主题
 */
/**
 * @description: 获取面板对应主题文件
 * @param {String} themeName：主题名称
 * @return {Object} js 文件内容
 */
export async function Get_Panel_Theme (themeName = 'default') {
    let res = {}
    let lastPath = !themeName || themeName === 'default' ? 'default.js' : `${themeName}/index.js`
    try {
        let tempJs = await require(`@/components/frame/Panel/PanelCode/theme/${lastPath}`)
        res = Object.assign({}, { name: themeName }, tempJs.default)
    } catch (err) {
        console.error(`
            此主题不存在！
            【检查文件@/components/frame/Panel/PanelCode/theme/${lastPath}】
            1. 文件路径找不到 / 路径错误
            2. 文件内容编写有误
            3. 查看下面的详细错误信息
        `)
        console.error(err)
    }
    return res
}

/**
 * @description: 根据索引取色系中的颜色
 * @param {Number} index
 * @param {Array} colorList：色系数组，可['#2163E0', '#91CB74', '#FAC858']
 * @return {String} 对应颜色
 */
export function Get_Color_By_Index (index, colorList) {
    if (Object.prototype.toString.call(colorList) !== '[object Array]' || typeof index !== 'number') {
        return ''
    }
    let len = colorList.length
    let resIndex = index % len
    return colorList[resIndex]
}

/**
 * @description: 获取渐变色
 * @param {Number} index
 * @param {Array} colorList：色系数组，可['#2163E0', '#91CB74', '#FAC858']，亦可[['#006BEF','#00C3FF'],['#F2C620','#FC7561']]
 * @return {Array} 渐变色数组
 */
export function Get_Gradient_Color_By_Index (index, colorList) {
    let res = Get_Color_By_Index(index, colorList)
    if (!res) return []
    if (typeof res === 'string') {
        // ====== TODO：适配不同类型颜色
        return [res + '99', res]
    }
    return res
}