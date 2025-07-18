import GlobalConst from '@/service/global-const'
import globalStyle from '@/styles/theme.scss'
import iconHandler from '@/components/frame/Icon/index.js'

// 通用按钮方案
const defaultBtnList = require('@/components/frame/Icon/commonBtns.json')

// default: 默认系统主色
// warning: 提醒：撤销当前输入，改动数据等操作
// success: 成功：用于完成类型
// danger:  危险： 谨慎操作类型警告
const defaultBgList = {
    default: globalStyle.primary,
    warning: globalStyle.warning,
    success: globalStyle.success,
    danger: globalStyle.danger
}

// 简写，inserted与update同时调用相同逻辑时使用
//     使用inserted确保初始可以显示按钮背景
//     考虑模型按钮数据可能来源接口，因此需要update
//     两个使用相同逻辑，因此使用简写，省略inserted与update
export default function btnBg (el, binging) {
    // 获取按钮数据对象
    let btnObj = binging.value
    // 默认使用主题色
    let finalVal = defaultBgList.default

    // 根据名称获取对应匹配模块
    let partByName = getPartByName(btnObj.name)
    // 更新图标所在背景模块颜色值
    finalVal = (partByName && getBgByType(partByName.type)) || finalVal

    // 根据id获取对应匹配模块
    let partById = getPartById(btnObj.id)
    // 更新图标所在背景模块颜色值
    finalVal = (partById && defaultBgList[partById.type]) || finalVal

    // 检查类型匹配
    if (btnObj.hasOwnProperty('type')) {
        finalVal = getBgByType(btnObj.type) || finalVal
    }
    // 检查背景--优先级由上往下逐渐递增
    if (btnObj.hasOwnProperty('background')) {
        finalVal = btnObj.background || finalVal
    }

    el.style.background = finalVal
    el.style.border = finalVal
    el.style.color = '#fff'

    setTimeout(() => {
        /**
         * 图标的处理已经独立出去。
         * 为了兼容旧版本，这里保留一份完整的处理 通用图标/默认图标 的逻辑。
         * 迭代几个版本后删除。
         */
        // 获取图标元素
        let iconEl = el.children[0].children
        let iconSvg = null
        if (iconEl.length > 0) {
            iconSvg = iconEl[0].children[0]
        } else {
            return false
        }

        let finalIcon = iconHandler.codeFromBtn(btnObj)
        if (!finalIcon.includes(GlobalConst.icon.prefix) &&
            !finalIcon.includes('bd ') &&
            !finalIcon.includes('iconfont')) {
            if (!iconHandler.hasIcon(finalIcon)) {
                finalIcon = this.icon
            }
            finalIcon = GlobalConst.icon.prefix + finalIcon
        }
        iconSvg.setAttribute('xlink:href', finalIcon)
    })
}

/**
 * 根据id返回匹配对象模块
 * @param {String} id 图标id
 * @return {Object} 匹配对象模块
 */
function getPartById (id) {
    let typeObj = defaultBtnList.find(i => i.id === id)
    return typeObj
}

/**
 * 根据类型返回色值，默认返回主题色default值
 * @param {*} type
 */
function getBgByType (type) {
    return defaultBgList[type] || ''
}

/**
 * 根据图标名称返回匹配对象模块
 * @param {String} name 图标名称
 * @return {Object} 匹配对象模块
 */
function getPartByName (name) {
    let btnObject = defaultBtnList.find(i => name.indexOf(i.name) > -1)
    return btnObject
}