import { Is_External_Url } from '@/utils/validate/index'
/**
 * 获取完整带域名的地址
 * @param {String} url 传入地址
 * @returns {String} 完整带域名地址
 */
function Get_Full_Url (url) {
    if (!url) return
    // 已有域名，直接返回
    if (Is_External_Url(url)) return url
    // 拼接域名
    let _url = `${process.env.VUE_APP_BASE_API}/${url}`
    // 去除多余斜杆
    return _url.replace(/(\/)+/g, '/').replace(/:\//, '://')
}

/**
 * 解析url地址，解构返回地址与参数
 * @param {String} url 请求地址
 * @returns Object {
 *                   url: 请求地址（不含查询参数）
 *                   params: url上的请求参数，组装为对象形式
 *                 }
 */
function Separate_Url (url) {
    if (!(url && url.constructor === String)) {
        console.error('传入获取参数的url值无效或者格式异常:' + url)
        return {}
    }
    // 获取问号所在下角标
    let signIndex = url.indexOf('?')
    // 不存在则直接返回
    if (!~signIndex) return { url }
    // 获取？后面查询内容
    let paramsPath = url.substring(signIndex + 1)
    // 不存在直接返回
    if (!paramsPath) return { url }
    // 定义返回的结果集
    let result = {}
    // 根据&切割为多块参数数据
    let queryList = paramsPath.split('&')
    queryList.forEach(i => {
        // 获取=号下角标
        let querySignIndex = i.indexOf('=')
        // 存在=时才将他加入结果集
        if (~querySignIndex) {
            // 获取=前面的键与后面的值，更新给结果集result
            // 这里不使用分隔符split，因为不确定值中是否也会存在=号
            let key = i.substring(0, querySignIndex)
            let value = i.substring(querySignIndex + 1) || ''
            result[key] = value
        }
    })
    // 返回结果集
    return {
        url: url.slice(0, signIndex),
        params: result
    }
}

/**
 * 根据传入url与参数对象，组装get形式使用url地址以查询方式拼接参数
 * @param {String} wholeUrl url地址（支持有查询参数）
 * @param {Object} restParams 参数
 * @returns
 */
function Combine_Url (wholeUrl, restParams) {
    // 分解传入的url地址，获取不带查询参数的url与参数
    let { url, params } = Separate_Url(wholeUrl)
    // 获取完整的查询参数
    let _params = Object.assign({}, params, restParams)
    // 获取查询参数键数组
    let _paramsList = Object.keys(_params)
    // 没有查询参数就直接返回url
    if (_paramsList.length === 0) return url
    // 存在查询参先加个查询符
    url = `${url}?`
    // 遍历组装url
    _paramsList.forEach(i => {
        url += `${i}=${_params[i]}&`
    })
    // 最后一位会多出一个&，所以长度减1后返回
    return url.slice(0, url.length - 1)
}

// import { Get_Full_Url } from '@/service/url'
export {
    // 获取完整带域名的地址
    Get_Full_Url,
    // 获取请求上的参数，并组装为对象返回
    Separate_Url,
    // 根据传入url与参数对象，组装get形式使用url地址以查询方式拼接参数
    Combine_Url
}