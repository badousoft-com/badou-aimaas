/** 设置项目标签页favicon图标
 * @export
 * @param {String} url:图标地址
 */
export default function Set_Page_Favicon (url) {
    let link = document.querySelector('link[rel*="icon"]') || document.createElement('link')
    link.type = 'image/x-icon'
    link.rel = 'shortcut icon'
    link.href = url
    document.getElementsByTagName('head')[0].appendChild(link)
}