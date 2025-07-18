/*
 * @Description: 面板的数据字典
 */
import { Has_Value } from '@/utils/index'
// 给列表进行排序
/**
 * @description: 给列表进行排序
 * @param {Array} list：需要排序的列表
 * @param {String} prop：排序号参数
 * @return {Array}：返回排序后的列表
 */
export function Sort_List (list, prop) {
    // 不参与排序的列表
    let otherList = []
    // 参与排序列表
    let sortList = []
    list.forEach(o => {
        Has_Value(o[prop]) ? sortList.push(o) : otherList.push(o)
    })
    sortList.sort((obj1, obj2) => {
        return (parseInt(obj1[prop])) - parseInt((obj2[prop]))
    })
    return sortList.concat(otherList)
}

export function Is_Url (str_url) {
    let strRegex = '^((https|http|ftp|rtsp|mms)?://)'
        + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
        + '(([0-9]{1,3}\.){3}[0-9]{1,3}' // IP形式的URL- 199.194.52.184
        + '|' // 允许IP和DOMAIN（域名）
        + "([0-9a-z_!~*'()-]+\.)*" // 域名- www.
        + '([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\.' // 二级域名
        + '[a-z]{2,6})' // first level domain- .com or .museum
        + '(:[0-9]{1,4})?' // 端口- :80
        + '((/?)|' // a slash isn't required if there is no file name
        + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$"
    let re = new RegExp(strRegex)
    if (re.test(str_url)) {
        return true
    } else {
        return false
    }
}