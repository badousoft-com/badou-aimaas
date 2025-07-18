/**
 * 处理浏览器相关
 */

/**
 * 判断当前浏览器是否为safari浏览器
 */
function Is_Safari () {
    return /Safari/.test(navigator.userAgent) && !/Chrome/.test(navigator.userAgent)
}
// 判断是否为移动端
function Is_Mobile () {
    // 判断是否移动设备
    return !!navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i)
}
export {
    // 判断当前浏览器是否为safari浏览器
    Is_Safari,
    // 判断是否为移动端
    Is_Mobile
}