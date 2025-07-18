/* ================ 面板设计器接口 ================ */
/* 部分接口与用户面板共用，谨慎修改 */
import { request } from '@/service/request'
// 请求面板及面板块
function Load_Block (data) {
    return request({
        url: `/panel/layout/layoutlist/loadPanelAndBlockWithId/${data.panelId}`,
        method: 'get',
        params: {}
    })
}
// 请求下拉框数据
function Find_Block_Settings_Info (data) {
    return request({
        url: '/panel/layout/layoutlist/findBlockSettingsInfo',
        method: 'post',
        params: data
    })
}
// 请求面板列表
function User_Layouts (data) {
    return request({
        url: '/panel/userlayout/userlayoutlist/userLayouts',
        method: 'post',
        params: data
    })
}
// 删除面板块
function Delete_Block (data) {
    return request({
        url: '/panel/block/panelblockdelete/deletePanelBlockLink',
        method: 'get',
        params: data
    })
}
// 保存面板位置信息
function Save_Panel_Pos (data) {
    return request({
        url: '/panel/layout/layoutsave/savePosWithPanelId',
        method: 'post',
        params: data
    })
}
// 保存面板
function Save_Panel_Info (data) {
    return request({
        url: '/panel/layout/layoutsave/savePanel',
        method: 'post',
        params: data
    })
}
// 保存块
function Save_Block (data) {
    return request({
        url: '/panel/block/panelblocksave/savePanelBlockLink',
        method: 'post',
        params: data
    })
}
// 获取面板数据
function Get_Panel_Info (data) {
    return request({
        url: '/panel/layout/layoutedit/editJSON.do',
        method: 'post',
        params: data
    })
}
// 选择对应报表后的额外新增表单设置
function Get_Report_Setting (data) {
    return request({
        url: '/panel/cfg/reportshowtype/reportshowtypeengine/packSettings.do',
        method: 'post',
        params: data
    })
}
// 选择对应报表后的额外新增表单设置
function Change_Hide_Status (data) {
    return request({
        url: '/panel/block/panelblocksave/hideOrShowContent',
        method: 'get',
        params: data
    })
}
// 根据块id获取分享内容列表
function Load_Share_Content (data) {
    return request({
        url: `/panel/content/panelcontentsave/getContentListBeforeShare/${data.blockId}`,
        method: 'get',
        params: {}
    })
}
// 保存块分享
function Save_Block_Share (data) {
    return request({
        url: '/panel/block/panelblocksave/shareBlock',
        method: 'post',
        params: data
    })
}
// 批量保存内容分享
function Save_Content_Share (data) {
    return request({
        url: '/panel/content/panelcontentsave/batchShareContent',
        method: 'post',
        params: data
    })
}
// 根据块id获取块分享内容对象（回显）
function Get_Block_Share (data) {
    return request({
        url: `/panel/layout/layoutlist/findShareArray/${data.blockId}`,
        method: 'get',
        params: {}
    })
}
// 根据内容id获取内容分享对象（回显）
function Get_Content_Share (data) {
    return request({
        url: `/panel/content/panelcontentsave/findShareArray/${data.contentId}`,
        method: 'get',
        params: {}
    })
}
// 获取分享内容（我分享的：shareType = 0；分享给我的：shareType = 1）
function Find_Share_Content (data) {
    return request({
        url: `/panel/content/panelcontentlist/getUserContentList/${data.shareType}`,
        method: 'get',
        params: {}
    })
}
// 上传图片
const Upload_File_Url = '/attach/action/attachsupport/uploadFile.do'
// 预览图片地址
const Down_File_Url = process.env.VUE_APP_BASE_API + '/attach/action/attachsupport/downloadAttach.do?attachId='

// 上传单附件
function Upload_File (params) {
    return request({
        url: Upload_File_Url,
        method: 'post',
        params
    })
}

export {
    // 请求面板及面板块
    Load_Block,
    // 请求下拉框数据
    Find_Block_Settings_Info,
    // 请求面板列表
    User_Layouts,
    // 删除面板块
    Delete_Block,
    // 保存面板位置信息
    Save_Panel_Pos,
    // 保存面板
    Save_Panel_Info,
    // 保存块
    Save_Block,
    // 获取面板数据
    Get_Panel_Info,
    // 选择对应报表后的额外新增表单设置
    Get_Report_Setting,
    // 选择对应报表后的额外新增表单设置
    Change_Hide_Status,
    // 根据块id获取分享内容列表
    Load_Share_Content,
    // 保存块分享
    Save_Block_Share,
    // 批量保存内容分享
    Save_Content_Share,
    // 根据块id获取块分享内容对象（回显）
    Get_Block_Share,
    // 根据内容id获取内容分享对象（回显）
    Get_Content_Share,
    // 获取分享内容（我分享的：shareType = 0；分享给我的：shareType = 1）
    Find_Share_Content,
    // 上传图片
    Upload_File_Url,
    // 预览图片地址
    Down_File_Url,
    // 上传单附件
    Upload_File
}