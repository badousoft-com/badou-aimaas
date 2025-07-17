/**
 * 判断是否符合加密地址白名单
 * @param {String} url 访问的接口地址
 * @returns {Boolean} 符合状态
 */
function Suit_Sign_WhiteList (url) {
    if (!url) return false
    // 这些白名单接口，后端也需要做相对应处理，否则400处理
    let whiteList = [
        '/module/design/moduledesignsave/saveField.do',
        '/panel/block/panelblocksave/saveSingleBlock.do',
        '/panel/block/panelblocksave/savePanelBlockLink',
    ]
    let hasDataStatus = whiteList.some(i => ~url.indexOf(i))
    return hasDataStatus
}

export {
    // 判断是否符合加密地址白名单
    Suit_Sign_WhiteList,
}