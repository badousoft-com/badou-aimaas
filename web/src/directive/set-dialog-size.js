import GlobalConst from '@/service/global-const'

// 可以设置的属性
//     width: ''(String): 支持数字，像素值，百分比
//     height: ''(String)： 支持数字，像素值，百分比
//     isFixedDialog: false(Boolean)：是否使用最大尺寸展示
export default function setDialogSize (el, binding) {
    // 判断是否禁用弹窗尺寸调节
    if (binding?.value?.disabled) return
    // 定义等待DOM生成的时间
    let _time = 200
    // 是否为全屏，全屏则不做宽高处理，直接return
    if (binding.value.fullscreen) {
        // 通过弹窗右上角的全屏按钮控制时，需要先清除已设置的样式
        setTimeout(function () {
            let dialogEl = (el.childNodes)[0]
            dialogEl.style.cssText = ''
            let dialogElMain = dialogEl.childNodes
            if (!dialogElMain[1].style) {
                return false
            }
            dialogElMain[1].style.cssText = ''
        }, _time)
    } else {
        // 当弹窗状态为true时，启用后续规则计算
        if (!binding.value.visibleStatus) {
            return false
        }
        // 判断弹窗的状态栏，如果状态由true变为false（表示关闭弹窗，则此时不再执行后续逻辑）
        if (!binding.value.visibleStatus &&
            binding.oldValue &&
            binding.oldValue.visibleStatus) {
            return false
        }
        // 传入属性isAutoFix：true时使用自适应高度（根据内容自动撑），不动态计算
        // 注意：这里支持宽度自定义传入
        if (binding.value &&
            binding.value.visibleStatus &&
            binding.value.isAutoFix) {
            // 获取传入自定义宽度值
            let dialogWidth = binding.value && binding.value.width
            // 若存在则设置
            if (dialogWidth) {
                setTimeout(function () {
                    let dialogEl = (el.childNodes)[0]
                    let { aimWidth } = getPosition(dialogWidth)
                    dialogEl.style.cssText = `
                        width: ${aimWidth};
                        margin-top: ${GlobalConst.dialog.marginTop};`
                }, _time)
            }
            return false
        }
        // 获取默认滚动条宽度
        // let scrollWidth = GlobalConst.scrollbar.width
        // 获取弹窗展示宽度相关值
        let dialogWidth = (binding.value && binding.value.width) || GlobalConst.dialog.width
        // 获取弹窗展示高度相关值
        let dialogHeight = (binding.value && binding.value.height) || GlobalConst.dialog.height
        // 使用时间函数延迟，不然获取不到dialog body模块，只有header与footer
        setTimeout(function () {
            let dialogEl = (el.childNodes)[0]
            let { aimWidth, aimHeight, aimMarginTop } = getPosition(dialogWidth, dialogHeight)
            dialogEl.style.cssText =
                `width: ${aimWidth};` +
                `height: ${aimHeight}; ` +
                `margin-top: ${aimMarginTop};` +
                'overflow: auto;'
            let dialogElMain = dialogEl.childNodes
            if (!dialogElMain[1].style) {
                return false
            }
            // 获取弹窗头部高度
            let dHeader = dialogElMain[0].clientHeight || 0
            // 获取弹窗底部高度
            let dFooter = dialogElMain[2].clientHeight || 0
            // 计算弹窗body区域高度
            dialogElMain[1].style.cssText = `height: calc(${changeViewUnit(dialogHeight)} - ${dHeader + dFooter + 2}px)`
        }, _time)
    }
}

/**
 * 设置弹窗距离顶部距离，当使用百分比时垂直居中，使用像素则使用默认距离
 * @param {*} val 弹窗高度值，可能为1, 1px, 1%
 */
function setMarginTop (val) {
    // 值含有%号，表示使用百分比
    if (typeof val === 'string' && val.includes('%')) {
        return (100 - parseInt(val)) / 2 + 'vh !important'
    }
    return `calc(50vh - ${parseInt(val) / 2}px) !important`
}

/**
 * 视图尺寸数据，百分比需要转化为vh单位进行样式渲染，数字转为为px单位使用
 * @param {*} val 弹窗高度值，可能为1，1px,1%
 */
function changeViewUnit (val, viewType = 1) {
    // 值含有%号，表示使用百分比
    if (typeof val === 'string' && val.includes('%')) {
        return `${parseInt(val)}${viewType === 1 ? 'vh' : 'vw'}`
    }
    return `${parseInt(val)}px`
}

function getPosition (width, height) {
    return {
        aimWidth: changeViewUnit(width, 0) || GlobalConst.dialog.width,
        aimHeight: changeViewUnit(height, 1) || GlobalConst.dialog.height,
        aimMarginTop: setMarginTop(height) || GlobalConst.dialog.marginTop
    }
}
