/*
 * @Description: bd-icon handler
 * @Author: LXG
 * @Date: 2020-09-21
 * @LastEditTime: 2020-09-22
 */
/**
 * @TIPS
 * 该文件用于处理bd-icon相关的业务逻辑，第一版本处理了以下2点：
 * 1、bd-icon，没有传name时，显示默认图标。
 * 2、模型页的按钮，根据通用按钮方案，提供配置的图标。
 */

/**
 * 图标名数组
 * 解析指定路径下所有svg图标文件的文件名，遍历得到
 */
const iconFiles = require.context('@/icons/svg/', true, /\.svg$/).keys().map(icon => {
    // icon为每个图标的路径，例如 './frame/add.svg'
    // 对路径进行切割，得到图标名 './frame/mixed/pdf.svg'  =>  'pdf'
    return icon.substring(icon.lastIndexOf('/') + 1, icon.length - 4)
})
// 通用按钮方案
const commonBtns = require('./commonBtns.json')

export default {
    iconFiles,
    /**
     * @description: 是否存在图标
     * @param {String} code 图标code
     */
    hasIcon (code) {
        return iconFiles.includes(code)
    },
    /**
     * @description: 匹配通用按钮图标
     * @param {Object} btn 按钮
     */
    codeFromBtn (btn) {
        // console.log('codeFromBtn:', btn)
        if (btn.icon) {
            return btn.icon
        } else if (btn.id || btn.name) {
            let common = commonBtns.find(common => {
                return (common.id === btn.id || common.name === btn.name)
            })
            if (common) {
                return common.icon
            }
        }
        return 'default-fill'
    }
}