import { Has_Value } from '@/utils/index'

/**
 * 传入错误的透明度值时，输出语句
 * @param {String} funName 函数名称
 * @param {Number} value 透明值
 */
function Error_Alpha (funName, value) {
    console.log(`函数${funName}传入透明度值出错：当前为${value},正确的alpha值范围为0~1之间`)
}
/**
 * JS颜色十六进制转换为rgb或rgba,返回的格式为 rgba（255，255，255，0.5）字符串
 * sHex为传入的十六进制的色值
 * alpha为rgba的透明度
 */
function Hex_To_RGBA (sHex, alpha = 1) {
    if (!sHex || sHex === 'default') {
        return 'default'
    }
    // 判断alpha的取值是否在正确范围内： 0~1 之间
    if (alpha < 0 || alpha > 1) {
        Error_Alpha('Hex_To_RGBA', alpha)
        return
    }
    // 十六进制颜色值的正则表达式
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
        let newColorChange = []
        for (let i = 1; i < 7; i += 2) {
            newColorChange.push(parseInt('0x' + sColor.slice(i, i + 2)))
        }
        return `rgba(${newColorChange.join(',')},${alpha})`
    } else {
        console.log(`函数Hex_To_RGBA()出错：色值错误，格式应为 '#fff' 或者 '#b9b9b9'`)
        return sColor
    }
}

/**
 * JS颜色rgb转换为rgba,返回的格式为 rgba（255，255，255，1）字符串
 * color为传入的十六进制的色值
 * alpha为rgba的透明度
 */
function RGB_To_RGBA (color, alpha) {
    let reg = /^(rgb|RGB)/
    // alpha不传，默认为1
    if (!Has_Value(alpha)) {
        alpha = 1
    }
    // 判断传入的alpha值知否在正确的范围内 0~1 之间
    if (alpha < 0 || alpha > 1) {
        Error_Alpha('RGB_To_RGBA', alpha)
        return
    }
    if (reg.test(color)) {
        let rgb = color.split('(')[1].split(')')[0].split(',')
        if (rgb.length === 3) {
            if (rgb.some(i => i < 0 || i > 255)) {
                console.log(`函数RGB_To_RGBA()出错：色值错误，色值范围应在0~255之间`)
            } else {
                return `rgba(${rgb.join(',')},${alpha})`
            }
        } else {
            console.log(`函数RGB_To_RGBA()出错：色值错误，格式应为rgb\RGB(122,255,255)`)
        }
    } else {
        console.log(`函数RGB_To_RGBA()出错：色值错误，格式应为rgb\RGB(122,255,255)`)
    }
}

/**
    * rgba rgb 转 16进制
    * @param color 格式为：rgba(122,122,255,0.5)
    * @param alpha 一般这里就是1或者255 默认以1为基准 如果最大值是255 就写255
    */
function Opacity_To_Hex (color, alpha) {
    // 将传入的数据转换为数组格式，例如rgba(122,122,133,0.5) ===> [122,122,133,0.5]
    /* eslint-disable */
    let values = color.replace(/rgba?\(/, '')
                      .replace(/\)/, '')  
                      .replace(/[\s+]/g, '')
                      .split(',')
    /* eslint-disable */
    // 判断color是否为rgb类型
    if (!color.includes('rgba')) {
        if (Has_Value(alpha)) {
            // rgb类型需要将alpha值添加进values数组
            values.push(alpha)
        }
    }
    if (values[3] < 0 || values[3] > 1) {
        Error_Alpha('Opacity_To_Hex', alpha)
    } else {
        // 补位 '#'
        return '#' +  values.map((item, index) => {
            let hexNum = ''
            if (index === 3) {
                hexNum = Number(Math.round(item * 255)).toString(16)
            } else {
                hexNum = Number(item).toString(16)
            }
            // 部分数字小于10的情况下补位 0
            return hexNum.length === 1 ? '0' + hexNum : hexNum
        }).join('')
    }
}

/**
 * 根据颜色字符串'a,b,c', 获取颜色列表['a', 'b', 'c']
 * @param {String} colorStr 颜色字符串
 * @param {String} separator 颜色切割符
 * @returns {Array} 颜色列表
 */
function Get_Color_List (colorStr, separator = ',') {
    // 获取字符串，清除空格
    let _colorStr = colorStr && colorStr.replaceAll(' ', '')
    if (!_colorStr) return []
    // 定义临时替换符，rgba中的【,】号会被切割符识别，所以需要先转化，再进行切割
    let tempSign = '&&'
    // // 定义颜色间的分隔符
    // let seperator = ','
    // 匹配(*)格式的数据，将其中的【,】转化为临时替换符
    _colorStr = _colorStr.replace(/\(.*?\)/g, function (val) {
        if (!val) return
        return val.replaceAll(separator, tempSign)
    })
    // 切割字符串，然后再将临时符替换为原本的【,】符
    return _colorStr.split(separator).map(i => (i.replaceAll(tempSign, separator)))
}

export {
    // JS颜色十六进制转换为rgb或rgba,返回的格式为 rgba（255，255，255，0.5）字符串
    Hex_To_RGBA,
    Hex_To_RGBA as HEX_TO_RGBA, // 即将废弃
    // js rgb或rgba转十六进制，返回的格式为 #7a7aff80 字符串
    Opacity_To_Hex,
    Opacity_To_Hex as OPACITY_TO_HEX, // 即将废弃
    // js rgb转换为rgba,返回的格式为 rgba（255，255，255，1）字符串
    RGB_To_RGBA,
    RGB_To_RGBA as RGB_TO_RGBA, // 即将废弃
    // 根据颜色字符串'a,b,c', 获取颜色列表['a', 'b', 'c']
    Get_Color_List,
}