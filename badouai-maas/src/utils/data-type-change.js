/**
 * 字符串与数字之间的相互转化
 * @param {Number / String} 传入值
 * @param {Boolean} isOrder false时逻辑相反：执行数字转字符串
 */
function String_To_Number (value, isOrder = true) {
    // 字符串转数字
    if (value === undefined || value === null || value === '') {
        return value
    }
    if (isOrder) {
        value += ''
        let lastStr = value.charAt(value.length - 1)
        // 输入'2.'会转成2，为避免此情况最后一位为小数点时不转成数字
        if (lastStr === '.') {
            return value
        }
        return value * 1
    }
    return value + ''
}

/**
 * 传入字符串转对象返回
 * @param {*} val
 */
function Str_To_Obj (val) {
    if (!val) return null
    // 传入为对象，直接返回该对象
    if (val.constructor === Object) return val
    // 传入值需要为字符串才处理逻辑
    if (val.constructor === String) {
        try {
            // 执行转化，并返回转化后对象
            return JSON.parse(val)
        } catch (e) {
            console.error(`使用Str_To_Obj函数转化数据出现异常,传入数据为${val}`)
            // 转化过程出现异常，返回null对象
            return null
        }
    }
    // 类型异常，返回null对象
    return null
}

/**
 * 将数字以千分位进行展示，如1234500将转化为1,234,500
 * @param {String, Number} num 处理的数
 */
function Big_Num_Show (num) {
    if (!num) {
        return ''
    }
    // 将num中的$,去掉，将num变成一个纯粹的数据格式字符串
    num = num.toString().replace(/\$|\,/g, '')
    // 如果num不是数字，则将num置0，并返回
    if (num === '' || isNaN(num)) {
        return ''
    }
    // 如果num是负数，则获取她的符号
    let sign = num.indexOf('-') > 0 ? '-' : ''
    // 如果存在小数点，则获取数字的小数部分
    let cents = num.indexOf('.') > 0 ? num.substr(num.indexOf('.')) : ''
    cents = cents.length > 1 ? cents : ''   // 注意：这里如果是使用change方法不断的调用，小数是输入不了的
    // 获取数字的整数数部分
    num = num.indexOf('.') > 0 ? num.substring(0, (num.indexOf('.'))) : num
    // 如果没有小数点，整数部分不能以0开头
    if (cents === '') {
        if (num.length > 1 && num.substr(0, 1) === '0') {
            return ''
        }
    } else {
        // 如果有小数点，且整数的部分的长度大于1，则整数部分不能以0开头
        if (num.length > 1 && num.substr(0, 1) === '0') return ''
    }
    // 小数保留两位
    /* if (cents.length > 2) {
        cents = Math.round(cents * 100) / 100
    } */
    // 针对整数部分进行格式化处理，这是此方法的核心，也是稍难理解的一个地方，逆向的来思考或者采用简单的事例来实现就容易多了
    /*
      也可以这样想象，现在有一串数字字符串在你面前，如果让你给他家千分位的逗号的话，你是怎么来思考和操作的?
      字符串长度为0/1/2/3时都不用添加
      字符串长度大于3的时候，从右往左数，有三位字符就加一个逗号，然后继续往前数，直到不到往前数少于三位字符为止
     */
    for (let i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++) {
        num = num.substring(0, num.length - (4 * i + 3)) + ',' + num.substring(num.length - (4 * i + 3))
    }
    // 将数据（符号、整数部分、小数部分）整体组合返回
    return (sign + num + (cents + '').substr((cents + '').indexOf('.')))
}

export {
    // 字符串与数字之间的相互转化
    String_To_Number,
    // 传入字符串转对象返回
    Str_To_Obj,
    // 将数字以千分位进行展示，如1234500将转化为1,234,500
    Big_Num_Show,
}
