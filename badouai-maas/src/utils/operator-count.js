/*
 * @Description: 运算方法
 */

import { Has_Value } from './index'
/**
 * @description: 校验只要是数字（包含正负整数，0以及正负浮点数）就返回true
 * @param {*} val
 * @return {*}
 */
function Is_Number (val) {
    if (!Has_Value(val)) return false
    let test_val = String(val)
    // 非负浮点数
    let regPos = /^\d+(\.\d+)?$/
    // 负浮点数
    let regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/
    // 科学计数法
    let regSci = /^(\d+|\d+\.\d+)(e|E)[+-]?(\d+)$/
    return regPos.test(test_val) || regNeg.test(test_val) || regSci.test(test_val)
}

// TODO: 2022-06-24废弃，适用场景有限，操作6位小数时异常
// 两个数字的加法
function Add_Num (num1, num2) {
    num1 = Is_Number(num1) ? num1 : 0
    num2 = Is_Number(num2) ? num2 : 0
    let sq1 = 0, sq2 = 0, m = 0
    sq1 = num1.toString().split('.')[1]?.length || 0
    sq2 = num2.toString().split('.')[1]?.length || 0
    m = Math.pow(10, Math.max(sq1, sq2))
    return (num1 * m + num2 * m) / m
}

/**
 * 补0操作
 * @param {String} data 数字
 * @param {Number, String} count 需要添加0的数目
 * @returns 补0后的数字
 */
function addZero (data, count) {
    if (!data) return '0'
    for (let i = 0; i < parseInt(count); i++) {
        data += '0'
    }
    return data
}

/**
 * 加法运算，调用方法如下
 *     SUM(1,2)  ==>  3
 *     SUM(1,2,3) ==>  6
 *     SUM.apply(null, [1,2,3,4])  ==>  10
 * 注意，传入的数字若为科学计数法则不可超过20位，如2e21将无法计算
 * @returns {Number}
 */
function SUM () {
    // 获取所有参数数字
    let _allNums = Array.from(arguments)
    if (!(_allNums && _allNums.length > 0)) {
        console.error(`sum函数使用:请传入需要操作的值，如sum(1,2)`)
    }
    // 定义数字结果集
    let _nums = []
    // 过滤数字项
    _allNums.forEach(i => {
        if (!isNaN(i)) {
            // 此时使用parseFloat主要将科学计数法的数字转格式，注意只能转正数部分的科学技术法
            //     2e+10 可以转化
            //     2e-10 不能转化
            _nums.push(parseFloat(i).toString())
        } else {
            console.error(`sum函数使用：传入参数异常，${i}不为数字`)
        }
    })
    if (_nums.length === 0) return 0
    function isScientPosi (num) {
        if (!Has_Value(num)) return false
        return /[Ee]-/.test(num)
    }
    // 获取所有小数(成为整数)需要向右偏移位数，方便后续确认最大的放大倍数
    let _pointNums = _nums.map(i => {
        // 定义当前项去除小数点需要的放大位数
        let _res = 0
        // 定义项值
        let _i = i
        // 判断项是否为科学计数法
        if (isScientPosi(_i)) {
            // 定义正数部分：posiNum
            // 定义正数后跟着位数：placeNum
            let [fullNum, posiNum, placeNum] = _i.match(/(.*)[Ee]-(\d+)/)
            fullNum // 暂无用处，eslint规避报错
            // 更新放大位数
            _res = parseInt(placeNum)
            // 更新项
            _i = posiNum
        }
        // 获取项中小数点的下角标
        let index = _i.indexOf('.')
        if (~index) {
            _res = i.length - index - 1 + _res
        }
        return _res
    })
    // 比对所有项去除小数点需要偏移的位数，选择出最大的位数
    let _finallyPointNums = Math.max.apply(null, _pointNums)
    // 将所有的项按最大的位数进行偏移
    _nums = _nums.map((i, index) => {
        // 判断项是否为科学计数法
        if (isScientPosi(i)) {
            // 定义正数部分：posiNum
            // 定义正数后跟着位数：placeNum
            let [fullNum, posiNum, placeNum] = i.match(/(.*)[Ee]-(\d+)/)
            fullNum // 暂无用处，eslint规避报错
            // 超出的偏移位则右侧补0
            return addZero(posiNum, _finallyPointNums - placeNum)
        } else {
            // 去除小数点
            let _num = i.replace(/[.]/g, '')
            // 计算超出的偏移位，进行补0
            _num = addZero(_num, _finallyPointNums - _pointNums[index])
            // 去除项前的无效0
            return _num.replace(/^0+([0-9].*)/, '$1')
        }
    })
    // 计算总和
    let _totalSum = _nums.reduce((res, item) => {
        res += (+item)
        return res
    }, 0)
    // 切割成数组，为后续逻辑准备
    _totalSum = _totalSum.toString().split('')
    // 判断总和的长度是否小于最大偏移位数
    if (_totalSum.length < _finallyPointNums) {
        // 获取补0数目
        let _addZreoTimes = _finallyPointNums - _totalSum.length + 1
        for (let i = 0; i < _addZreoTimes; i++) {
            _totalSum.unshift('0')
        }
    }
    // 补0后添加进小数点
    _totalSum.splice(_totalSum.length - _finallyPointNums, 0, '.')
    return parseFloat(_totalSum.join(''))
}

// import { SUM } from '@/utils/operator-count'
export {
    // TODO：废弃：两个数字的加法
    Add_Num,
    // 加法
    SUM,
    // 是否数字
    Is_Number,
    Is_Number as isNumber,
}