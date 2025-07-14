/**
 * 滚动动画-可滚动元素滚动至顶部
 * @param {DOMElement} el dom元素
 * @param {Number} duration 完成动画所需要总时间
 * @param {Number} eachTime 单次动画的时间，决定动画的流畅性
 */
function Scroll_Top (el, duration = 200, eachTime = 16) {
    // 获取元素距离顶部距离
    let distance = el.scrollTop || false
    // 当获取元素距离顶部高度失败时 或者 顶部距离为0时，停止运行
    if (!distance) {
        return false
    }
    // 获取每次滚动距离，当滚动距离非常小时值会变成0，此时使用1，表示每次滚动1
    let rate = Math.floor(distance * eachTime / duration) || 1
    // 创建时间函数，执行滚动事件
    let scrollMove = setInterval(() => {
        if (el.scrollTop < rate) {
            // 当即将滚动至顶部时，清除时间函数
            el.scrollTop = 0
            clearInterval(scrollMove)
        } else {
            // 按照每次滚动距离设置元素
            el.scrollTop -= rate
        }
    }, eachTime)
}

/**
 * 滚动动画-可滚动元素滚动至底部
 * @param {DOMElement} el dom元素
 * @param {Number} duration 完成动画所需要总时间
 * @param {*Number} eachTime 单次动画的时间，决定动画的流畅性
 */
function Scroll_Bottom (el, duration = 200, eachTime = 16) {
    if (!el) return
    // 定义获取滚动高度scrollHeight
    // 定义获取可视高度clientHeight
    let { scrollHeight, clientHeight } = el
    // 计算当前位置与到底部之间的距离
    let _scrollDic = scrollHeight - clientHeight
    // 若已到达指定位置，则停止
    if (!(_scrollDic && _scrollDic > 0 && el.scrollTop < _scrollDic)) return
    // 获取每次滚动距离，当滚动距离非常小时值会变成0，此时使用1，表示每次滚动1
    let _rate = Math.floor(_scrollDic * eachTime / duration) || 1
    // 创建时间函数，执行滚动事件
    let _scrollMove = setInterval(() => {
        if (_scrollDic - el.scrollTop < _rate) {
            // 当即将滚动至底部时，直接设置滚动到最底部，并且清除时间函数
            el.scrollTop = _scrollDic
            clearInterval(_scrollMove)
        } else {
            // 按照每次滚动距离设置元素
            el.scrollTop += _rate
        }
    }, eachTime)
}

/**
 * 从一个点往另一个点的抛物线动画
 * @param {DOM Object} fromEl 来源点的dom对象
 * @param {DOM Object} toEl 目标点的dom对象
 * @param {Object} option 配置项对象
 *     @param {String} throwCssText: style的cssText
 *     @param {String} className: 类名
 *     @param {Number} timeDic: 每个时间间隔下的移动距离
 *     @param {Number} k: 抛物线方程元素
 *     @returns
 */
function Throw (fromEl, toEl, option = {}) {
    return new Promise((resolve, reject) => {
        // 判断时间函数与抛块是否存在，存在表示上一次未执行完
        if (timer || throwEl) {
            reject()
            // 清除事件
            clearInterval(timer)
            // 删除抛点
            document.body.removeChild(appendThrowEl)
            return
        }
        let {
            throwCssText = 'width:20px;height:20px;border-radius:50%',
            className = 'primaryBg',
            timeDic = 20, // 定义每个时间间隔下的移动距离
            k = 0.0015, // 定义抛物线方程元素
        } = option || {}
        // 定义抛块id，用于动画抛的物体
        let throwId = 'throwId'
        // 创建新元素
        let appendThrowEl = document.createElement('div')
        // 设置id
        appendThrowEl.id = throwId
        // 设置抛块样式
        appendThrowEl.style.cssText = throwCssText
        appendThrowEl.className = className
        // 添加抛块
        document.body.appendChild(appendThrowEl)
        // 获取抛过程中的来源对象
        let fromObj = fromEl.getBoundingClientRect()
        // 获取抛过程中的目标对象
        let toObj = toEl.getBoundingClientRect()
        // 定义来源对象坐标x
        let from_x = fromObj.x
        // 定义来源对象坐标y
        let from_y = fromObj.y
        let {
            width, // 获取目标元素宽度
            height, // 获取目标元素高度
            x, // 获取目标元素所在横坐标
            y // 获取目标元素所在纵坐标
        } = toObj
        /* 定义目标对象x */
        let to_x = x + width / 2
        /* 定义目标对象y */
        let to_y = y + height / 4

        /* 定义抛物线方程元素a */
        let a = null
        /* 定义抛物线方程元素b */
        let b = null
        /* 定义动画时间间隔,默认16ms,流畅动画时间 */
        let time = 16
        // 定义抛块的初始透明度
        let opacity = 1
        /* 一元二次方程求b: y = kx^2 + ax + b */
        a = ((from_y - to_y) - k * (Math.pow(from_x, 2) - Math.pow(to_x, 2))) / (from_x - to_x)
        b = from_y - k * Math.pow(from_x, 2) - a * from_x
        let throwEl = document.getElementById(throwId)
        // 定义轨迹运动状态goLeftStatus
        let goLeftStatus = from_x > to_x
        // 创建时间函数，用于抛块以抛物线轨迹运动
        let timer = setInterval(() => {
            try {
                // 确保状态一致下执行
                // 随着运动，from_x值会不断靠近to_x，当状态不一致时表示运动结束
                if ((from_x > to_x) === goLeftStatus) {
                    // 根据抛物线公式获取当前抛块所在横坐标
                    let running_x = from_x + 'px'
                    // 根据抛物线公式获取当前抛块所在纵坐标
                    let running_y = k * Math.pow(from_x, 2) + a * from_x + b + 'px'
                    // 此处组装成cssText会失效，暂时拆分为style下多个属性控制
                    throwEl.style.position = 'fixed'
                    throwEl.style.top = running_y
                    throwEl.style.left = running_x
                    throwEl.style.zIndex = 2
                    throwEl.style.opacity = opacity
                    // 更新抛块下一个前进点的横坐标
                    from_x = from_x + (!goLeftStatus ? timeDic : -timeDic)
                    // 弱化抛块的透明度
                    opacity = opacity - 0.01
                } else {
                    // 清除时间函数
                    clearInterval(timer)
                    // 在接收抛块后，设置目标点启用动画：弹力球
                    toEl.style.animation = 'hBall 0.5s 1'
                    // 设置抛块透明度
                    throwEl.style.opacity = 0
                    // 设置抛块隐藏
                    throwEl.style.display = 'none'
                    // 删除抛块dom
                    document.body.removeChild(appendThrowEl)
                    setTimeout(() => {
                        // 清除目标点的弹力球动画
                        toEl.style.animation = 'none'
                    }, 300)
                    // 返回状态
                    resolve()
                }
            } catch (e) {
                // 出现异常时清除时间函数
                clearInterval(timer)
                // 删除抛块
                document.body.removeChild(appendThrowEl)
            }
        }, time)
    })
}

export {
    // 滚动动画-可滚动元素滚动至顶部
    Scroll_Top,
    // 滚动动画-可滚动元素滚动至底部
    Scroll_Bottom,
    // 从一个点往另一个点的抛物线动画
    Throw
}