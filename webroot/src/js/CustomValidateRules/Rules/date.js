const DATE_REG = /^\d{4}-\d{2}-\d{2}$/

/**
 * 将字符串转换为日期
 * @param {string} dateStr
 * @return {Date}
 */
function strToDate (dateStr) {
    return new Date(dateStr.replace(/-/g, '/'))
}

// 把 date 转换为没有时间的日期
function getNoTimeDate (date) {
    let dateStr = `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
    return strToDate(dateStr)
}

/**
 * 当前时间之前
 * @param {string} value 选择的日期
 * @param {string} include 值为 'include' 时包含当前日期
 * @return {boolean}
 */
function beforeValid (value, include) {
    let chooseDate = strToDate(value)
    let now = getNoTimeDate(new Date())
    if (include === 'include') {
        return now >= chooseDate
    }
    return now > chooseDate
}

/**
 * 当前时间之后
 * @param {string} value 选择的日期
 * @param {string} include 值为 'include' 时包含当前日期
 * @return {boolean}
 */
function afterValid (value, include) {
    let chooseDate = strToDate(value)
    let now = getNoTimeDate(new Date())
    if (include === 'include') {
        return now <= chooseDate
    }
    return now < chooseDate
}

export default {
    // 当前时间之前的验证规则
    beforCurrentDate: {
        getMessage: () => '必须小于当前时间',
        validate: (value, [include]) => {
            if (DATE_REG.test(value)) {
                return beforeValid(value, include)
            }
            return false
        }
    },
    afterCurrentDate: {
        getMessage: () => '必须大于当前时间',
        validate: (value, [include]) => {
            if (DATE_REG.test(value)) {
                return afterValid(value, include)
            }
            return false
        }
    }
}
