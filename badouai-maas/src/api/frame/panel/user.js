/*
 * @Description: 用户面板设计器
 */
import { request } from '@/service/request'
// 获取用户选择块
function Get_User_Block (data) {
    return request({
        url: `/panel/layout/layoutlist/loadPanelAndBlockWithId/${data.panelId}`,
        method: 'get',
        params: {}
    })
}
// 获取分享块（我分享的：shareType = 0；分享给我的：shareType = 1）
function Find_Share_Block (data) {
    let { shareType, ...params } = data
    return request({
        url: `/panel/block/panelblocklist/getBlockListByUser/${shareType}`,
        method: 'get',
        params: params
    })
}
// 文件预览地址
const Down_File_Url = process.env.VUE_APP_BASE_API + '/attach/action/attachsupport/downloadAttach.do?attachId='

export {
    // 获取用户选择块
    Get_User_Block,
    // 获取分享块（我分享的：shareType = 0；分享给我的：shareType = 1）
    Find_Share_Block,
    // 文件预览地址
    Down_File_Url
}