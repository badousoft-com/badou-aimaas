/**
 * 获取时间戳
 * @param {Date} 日期 默认为今日
 */
function Get_TimeStamp (date) {
    if (!date) return new Date().getTime()
    return new Date(date).getTime()
}

/**
 * 获取日期的年份
 * @param {Date} date 日期
 * @returns
 */
function Get_Year (date) {
    let _date = date ? new Date(date) : new Date()
    return _date.getFullYear()
}

/**
 * 获取当前日期 1990-12-22
 * @returns
 */
function Get_Today () {
    let nowDate = new Date()
    let year = nowDate.getFullYear()
    let month = nowDate.getMonth() + 1
    if (month < 10) {
        month = '0' + month
    }
    let day = nowDate.getDate()
    if (day < 10) {
        day = '0' + day
    }
    return year + '-' + month + '-' + day
}

/**
 * 时间戳转日期 1990-12-22
 * @param {String} timestamp 时间戳
 * @returns
 */
function Convert_Date (timestamp) {
    let date = new Date(timestamp)
    let year = date.getFullYear()
    let month = date.getMonth() + 1
    if (month < 10) {
        month = '0' + month
    }
    let day = date.getDate()
    if (day < 10) {
        day = '0' + day
    }
    return year + '-' + month + '-' + day
}

/**
 * 时间戳转日期 1990年12月22日
 * @param {String} timestamp 时间戳
 */
function Convert_Date_Ch (timestamp) {
    let date = new Date(timestamp)
    let year = date.getFullYear()
    let month = date.getMonth() + 1
    if (month < 10) {
        month = '0' + month
    }
    let day = date.getDate()
    if (day < 10) {
        day = '0' + day
    }
    return year + '年' + month + '月' + day + '日'
}

/**
 * 时间戳转日期时间 1990-12-22 00:00:00
 * @param {String} timestamp 时间戳
 * @returns
 */
function Convert_DateTime (timestamp) {
    let date = new Date(timestamp)
    let year = date.getFullYear()
    let month = date.getMonth() + 1
    if (month < 10) {
        month = '0' + month
    }
    let day = date.getDate()
    if (day < 10) {
        day = '0' + day
    }
    let hour = date.getHours()
    if (hour < 10) {
        hour = '0' + hour
    }
    let minute = date.getMinutes()
    if (minute < 10) {
        minute = '0' + minute
    }
    let second = date.getSeconds()
    if (second < 10) {
        second = '0' + second
    }
    return year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second
}

/**
 * @description: 格式化日期时间
 * @param {String} format 格式
 *     支持使用下述关键字传入自定义的返回格式，如 MM-dd-yyyy HH:mm:ss 返回 12-10-2020 17:33:00
 *         yyyy 年份
 *         M 月份(不补0)  MM 月份(补0)
 *         d 天(不补0)  dd 天(补0)
 *         H 小时(不补0)  HH 小时(补0)
 *         m 分钟(不补0)  mm 分钟(补0)
 *         s 秒(不补0)  ss 秒(补0)
 *     传入'date' 时格式为 yyyy-MM-dd (默认格式)
 *     传入'datetime' 时格式为 yyyy-MM-dd HH:mm:ss
 *     传入'time' 时格式为 HH:mm:ss
 * @param {Date} dateInst Date实例
 */
function Format_Date (format = 'date', dateInst = new Date()) {
    function zerofill (val) {
        return val < 10 ? `0${val}` : val
    }
    if (dateInst.constructor === String) {
        dateInst = new Date(dateInst)
    }
    let yyyy = dateInst.getFullYear()
    let [M, d] = [dateInst.getMonth() + 1, dateInst.getDate()]
    let [MM, dd] = [zerofill(M), zerofill(d)]

    if (format === 'date') {
        return `${yyyy}-${MM}-${dd}`
    }

    let [H, m, s] = [dateInst.getHours(), dateInst.getMinutes(), dateInst.getSeconds()]
    let [HH, mm, ss] = [zerofill(H), zerofill(m), zerofill(s)]

    let res = ''
    switch (format) {
        case 'datetime':
            res = `${yyyy}-${MM}-${dd} ${HH}:${mm}:${ss}`
            break
        case 'time':
            res = `${HH}:${mm}:${ss}`
            break
        default:
            res = (
                format
                    .replace(/yyyy/g, yyyy)
                    .replace(/MM/g, MM).replace(/M/g, M)
                    .replace(/dd/g, dd).replace(/d/g, d)
                    .replace(/HH/g, HH).replace(/H/g, H)
                    .replace(/mm/g, mm).replace(/m/g, m)
                    .replace(/ss/g, ss).replace(/s/g, s)
            )
            break
    }
    return res
}

// import { Get_TimeStamp } from '@/utils/time'
export {
    // 获取时间戳
    Get_TimeStamp,
    // 获取日期的年份
    Get_Year,
    // 获取当前日期 1990-12-22
    Get_Today,
    Get_Today as Get_Now_Time,
    // 格式化日期时间
    Format_Date,
    // 时间戳转日期 1990-12-22
    Convert_Date,
    // 时间戳转日期 1990年12月22日
    Convert_Date_Ch,
    // 时间戳转日期时间 1990-12-22 00:00:00
    Convert_DateTime,
}