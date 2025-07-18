// RGB转换为16进制
String.prototype.RGB_TO_HEX = function () {
    // RGB颜色值的正则
    let reg = /^(rgb|RGB)/
    let color = this
    if (reg.test(color)) {
        let strHex = "#"
        // 把RGB的3个数值变成数组
        let colorArr = color.replace(/(?:\(|\)|rgb|RGB)*/g, "").split(",")
        // 转成16进制
        for (let i = 0; i < colorArr.length; i++) {
            let hex = Number(colorArr[i]).toString(16)
            if (hex === "0") {
                hex += hex
            }
            strHex += hex
        }
        return strHex
    } else {
        return String(color)
    }
}

// RGBA转16进制
String.prototype.RGBA_TO_HEX = function () {
    let values = this
        .replace(/rgba?\(/, '')
        .replace(/\)/, '')
        .replace(/[\s+]/g, '')
        .split(',');
    let a = parseFloat(values[3] || 1),
        r = Math.floor(a * parseInt(values[0]) + (1 - a) * 255),
        g = Math.floor(a * parseInt(values[1]) + (1 - a) * 255),
        b = Math.floor(a * parseInt(values[2]) + (1 - a) * 255)
    return "#" +
        ("0" + r.toString(16)).slice(-2) +
        ("0" + g.toString(16)).slice(-2) +
        ("0" + b.toString(16)).slice(-2)
}


// 16进制转换为RGB
String.prototype.HEX_TO_RGB = function () {
    // 16进制颜色值的正则
    let reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/
    // 把颜色值变成小写
    let color = this.toLowerCase()
    if (reg.test(color)) {
        // 如果只有三位的值，需变成六位，如：#fff => #ffffff
        if (color.length === 4) {
            let colorNew = "#"
            for (let i = 1; i < 4; i += 1) {
                colorNew += color.slice(i, i + 1).concat(color.slice(i, i + 1))
            }
            color = colorNew
        }
        // 处理六位的颜色值，转为RGB
        let colorChange = []
        for (let i = 1; i < 7; i += 2) {
            colorChange.push(parseInt("0x" + color.slice(i, i + 2)))
        }
        return "RGB(" + colorChange.join(",") + ")"
    } else {
        return color
    }
}

let initList = [
    {
        name: '朴素简约',
        primary: 'rgba(3,115,206,1)',
        // 侧栏背景色
        sidebar: '#fff',
        sidebarColor: '#333',
        // 侧栏模块内容背景色
        sidebarPart: 'rgba(3,115,206,0.08)',
        // 侧栏模块内容文字颜色
        sidebarPartColor: '#333',
        // 操作拖拽-提示面板背景色
        dragTipPanel: '#f7f7f7',
        // 拖拽元素悬浮背景
        dragItemBg: 'rgba(255, 224, 1, 0.3)',
    },  {
        name: '乡间泥泞',
        primary: '#458d90',
        // 侧栏背景色
        sidebar: '#625261',
        sidebarColor: '#fff',
        // 侧栏模块内容背景色
        sidebarPart: 'rgba(255,255,255,0.8)',
        // 侧栏模块内容文字颜色
        sidebarPartColor: '#333',
        // 操作拖拽-提示面板背景色
        dragTipPanel: '#e8e8e8',
        // 拖拽元素悬浮背景
        dragItemBg: 'rgba(255, 224, 1, 0.3)',
    },  {
        name: '高堂在上',
        primary: '#24211c',
        // 侧栏背景色
        sidebar: '#ac5118',
        sidebarColor: '#fff',
        // 侧栏模块内容背景色
        sidebarPart: 'rgba(255,255,255,0.8)',
        // 侧栏模块内容文字颜色
        sidebarPartColor: '#333',
        // 操作拖拽-提示面板背景色
        dragTipPanel: '#9f7d50',
        // 拖拽元素悬浮背景
        dragItemBg: 'rgba(255, 224, 1, 0.3)',
    }, {
        name: '血色黎明',
        primary: '#B9121B',
        // 侧栏背景色
        sidebar: '#4C1B1B',
        sidebarColor: '#fff',
        // 侧栏模块内容背景色
        sidebarPart: 'rgba(255,255,255,0.8)',
        // 侧栏模块内容文字颜色
        sidebarPartColor: '#333',
        // 操作拖拽-提示面板背景色
        dragTipPanel: '#FCFAE1',
        // 拖拽元素悬浮背景
        dragItemBg: 'rgba(255, 224, 1, 0.3)',
    }, {
        name: '奇迹暖暖',
        primary: '#155674',
        // 侧栏背景色
        sidebar: '#60beb3',
        sidebarColor: '#fff',
        // 侧栏模块内容背景色
        sidebarPart: 'rgba(255,255,255,0.8)',
        // 侧栏模块内容文字颜色
        sidebarPartColor: '#333',
        // 操作拖拽-提示面板背景色
        dragTipPanel: '#faffd6',
        // 拖拽元素悬浮背景
        dragItemBg: 'rgba(255, 224, 1, 0.3)',
    }
]

// 为主题列表添加默认属性处理
function handler (list) {
    let resultList = list
    resultList.forEach(i => {
        // 色差： 深
        i.primaryHover = i.primaryHover || deepColor(i.primary, 1111)
        // 色差：浅
        i.primarySimple = i.primarySimple || opacityColor(i.primary, 0.8)
        // 遮罩
        i.primaryShadow = i.primaryShadow || opacityColor(i.primary, 0.1)
        // 遮罩-hover时-->透明度加大，颜色变深
        i.primaryShadowHover = i.primaryShadowHover || opacityColor(i.primaryShadow, 0.15)
    })
    return resultList
}

/**
 * val颜色值基础上加深，并返回该值
 * @param {String} val 色值
 * @param {Number} dis 偏差值，0前补齐
 */
function deepColor (val, dis) {
    // 转为小写并且去除空格
    let color = val.toLowerCase().trim()
    if (color.includes('rgba')) {
        // 传入颜色值为'RGBA'
        color = color.RGBA_TO_HEX()
    } else if (color.includes('rgb')) {
        // 传入颜色值为'RGB'
        color = color.RGB_TO_HEX()
    } else {
        // 传入颜色值为十六进制
        // 位数小于7表示当前使用的是简写，例如#ccc,需要恢复为#cccccc
        color = color.length < 7 ? `${color}${color.substr(-3)}` : color
    }
    // 获取16进制色号
    let hexNum = color.substr(-6)
    // 16进制转10进制
    let hexNumR = parseInt(hexNum, 16)
    // 获取10进制差距色号
    let disNumR = parseInt(dis, 16)
    // 获取结果10进制色号，若当前色值小于差距值则不减了，直接原值作为结果值
    let resultNumR = hexNumR > disNumR ? hexNumR - disNumR : hexNumR
    return `#${( "000000" + resultNumR.toString(16) ).substr(-6)}`
}

/**
 * RGB/RGBA颜色转RGBA(支持修改透明度)
 * @param {String} val 色值
 * @param {String} returnType 返回数据类型 RGB / RGBA，默认为RGBA
 * @param {Number} opacity 修改透明度值，默认1
 */
function RGB_TO_OPACITY (val, returnType = 'RGBA', opacity = 1) {
    let color = val.toLowerCase().trim()
    let rgbList = color.substring(color.lastIndexOf('(') + 1, color.lastIndexOf(')')).split(',')
    return `${returnType.toLowerCase()}(${rgbList[0]}, ${rgbList[1]}, ${rgbList[2]}, ${opacity})`
}

/**
 * 十六进制 + 透明度转RGBA
 * @param {String} sHex 十六进制色值
 * @param {Number} opacity  透明度
 */
function HEX_OPACITY_TO_RGBA (sHex, opacity) {
    let reg = /^#([0-9a-fA-f]{3}|[0-9a-fA-f]{6})$/
    /* 16进制颜色转为RGB格式 */
    let sColor = sHex.toLowerCase()
    if (sColor && reg.test(sColor)) {
        if (sColor.length === 4) {
            let sColorNew = '#'
            for (let i = 1; i < 4; i += 1) {
                sColorNew += sColor.slice(i, i + 1).concat(sColor.slice(i, i + 1))
            }
            sColor = sColorNew
        }
        //  处理六位的颜色值
        let sColorChange = []
        for (let i = 1; i < 7; i += 2) {
            sColorChange.push(parseInt('0x' + sColor.slice(i, i + 2)))
        }
        // return sColorChange.join(',')
        // 或
        return `rgba(${sColorChange.join(',')},${opacity})`
    } else {
        return sColor
    }
}

/**
 * 颜色值基础上添加透明度，并返回该值[RGBA类型]
 * @param {String} val 
 * @param {Number} opacity 
 */
function opacityColor (val, opacity = 1) {
    if (val.includes('rgba')) {
        // 传入颜色值为'RGBA'
        return RGB_TO_OPACITY(val, 'RGBA', opacity)
    } else if (val.includes('rgb')) {
        // 传入颜色值为'RGB'
        return RGB_TO_OPACITY(val, 'RGB', opacity)
    } else {
        // 传入颜色值为十六进制
        return HEX_OPACITY_TO_RGBA(val, opacity)
    }
}

let themeList = handler(initList)
export default themeList