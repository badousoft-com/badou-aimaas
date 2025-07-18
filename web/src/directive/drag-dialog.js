export default function Dialog_Drag (el, binding, vnode, oldVnode) {
    const dialogHeaderEl = el.querySelector('.el-dialog__header')
    const dragDom = el.querySelector('.el-dialog')
    dialogHeaderEl.style.cssText += ';cursor:move;'
    dragDom.style.cssText += ';top:0px;'

    // 获取原有属性 ie dom元素.currentStyle 火狐谷歌 window.getComputedStyle(dom元素, null)
    const sty = (function () {
        if (window.document.currentStyle) {
            return (dom, attr) => dom.currentStyle[attr]
        } else {
            return (dom, attr) => getComputedStyle(dom, false)[attr]
        }
    })()
    // 鼠标按下事件
    dialogHeaderEl.onmousedown = (e) => {
        // 鼠标按下，计算当前元素距离可视区的距离
        const disX = e.clientX - dialogHeaderEl.offsetLeft
        const disY = e.clientY - dialogHeaderEl.offsetTop

        // body当前宽度
        const screenWidth = document.body.clientWidth
        // 可见区域高度(应为body高度，可某些环境下无法获取)
        const screenHeight = document.documentElement.clientHeight

        // 当前弹窗宽度
        const dragDomWidth = dragDom.offsetWidth
        // 当前弹窗高度
        // const dragDomheight = dragDom.offsetHeight

        // 窗体拖出窗口时的最小可视宽度
        const minCanViewWidth = 90
        // 窗体拖出窗口时的最小可视高度
        const minCanViewHeight = 42

        // 计算弹窗向左可拖动的最远距离
        const minDragDomLeft = dragDomWidth - minCanViewWidth + dragDom.offsetLeft
        // 计算弹窗向右可拖动的最远距离
        const maxDragDomLeft = screenWidth - dragDom.offsetLeft - minCanViewWidth

        // 计算弹窗向上可拖动的最远距离(由于标题栏为拖动按钮，所以向上拖动时始终保持整个窗体在可视界面内)
        const minDragDomTop = dragDom.offsetTop
        // 计算弹窗向下可拖动的最远距离(向下拖动时则保障最小露出弹窗标题栏)
        const maxDragDomTop = screenHeight - dragDom.offsetTop - minCanViewHeight

        // 获取到的值带px 正则匹配替换
        let styL = sty(dragDom, 'left')
        let styT = sty(dragDom, 'top')

        // 注意在ie中 第一次获取到的值为组件自带50% 移动之后赋值为px
        if (styL.includes('%')) {
            styL = +document.body.clientWidth * (+styL.replace(/\%/g, '') / 100)
            styT = +document.body.clientHeight * (+styT.replace(/\%/g, '') / 100)
        } else {
            styL = +styL.replace(/\px/g, '')
            styT = +styT.replace(/\px/g, '')
        }
        // 鼠标按住拖动事件
        document.onmousemove = function (e) {
            // 防止拖拽过程中的内容选中样式出现
            window.getSelection ? window.getSelection().removeAllRanges() : document.selection.empty()
            // 通过事件委托，计算移动的距离
            let left = e.clientX - disX
            let top = e.clientY - disY
            // 拖出左右边界处理
            if (-(left) > minDragDomLeft) {
                left = -(minDragDomLeft)
            } else if (left > maxDragDomLeft) {
                left = maxDragDomLeft
            }
            // 脱出上下边界处理
            if (-(top) > minDragDomTop) {
                top = -(minDragDomTop)
            } else if (top > maxDragDomTop) {
                top = maxDragDomTop
            }
            // 移动当前元素
            dragDom.style.cssText += `;left:${left + styL}px !important;top:${top + styT}px !important;`
            // 更新数据：弹窗已被移动过
            if ('hasDrag' in vnode.context) {
                vnode.context.hasDrag = true
            }
        }
        document.onmouseup = function (e) {
            document.onmousemove = null
            document.onmouseup = null
        }
    }
}
