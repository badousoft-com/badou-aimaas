/* ================ 面板页面接口 ================ */
import { request } from '@/service/request'

// 请求面板及面板块
function Load_Panel_And_Block (data) {
    return request({
        url: '/panel/layout/layoutlist/loadPanelAndBlock.do',
        method: 'post',
        params: data
    })
}

// 请求块需要的js路径
function Pack_Js_Path (data) {
    return request({
        url: '/panel/cfg/reportshowtype/reportshowtypeengine/packJsPath.do',
        method: 'get',
        params: data
    })
}

const Pack_Data_Url = '/panel/cfg/reportshowtype/reportshowtypeengine/packData.do'

// 请求列表数据
function Pack_Data (data, url) {
    return request({
        url: url || Pack_Data_Url,
        method: 'post',
        params: data
    })
}

// 自定义get 请求
function Custom_Url (url, data) {
    return request({
        url,
        method: 'get',
        params: data
    })
}

// 加载块
function Load_Block (data) {
    return request({
        url: '/panel/layout/layoutlist/loadBlock.do',
        method: 'post',
        params: data
    })
}

// 加载块的下拉框信息
function Find_Block_Settings_Info (data) {
    return request({
        url: '/panel/layout/layoutlist/findBlockSettingsInfo.do',
        method: 'post',
        params: data
    })
}

// 加载当前用户面板
function User_Layouts (data) {
    return request({
        url: '/panel/userlayout/userlayoutlist/userLayouts.do',
        method: 'post',
        params: data
    })
}

// 保存块的位置
function Save_Panel_Pos (data) {
    return request({
        url: '/panel/layout/layoutsave/savePanelPos.do',
        method: 'post',
        params: data
    })
}

// 删除块
function Delete_Block (data) {
    return request({
        url: '/panel/block/panelblockdelete/delete.do',
        method: 'post',
        params: data
    })
}

function Get_Url (data) {
    return request({
        url: '/panel/page/pagelist/getUrl.do',
        method: 'post',
        params: data
    })
}

export {
    // 请求面板及面板块
    Load_Panel_And_Block,
    // 请求块需要的js路径
    Pack_Js_Path,
    // 面板请求数据接口
    Pack_Data_Url,
    // 请求列表数据
    Pack_Data,
    // 自定义get 请求
    Custom_Url,
    // 加载块
    Load_Block,
    // 加载块的下拉框信息
    Find_Block_Settings_Info,
    // 加载当前用户面板
    User_Layouts,
    // 保存块的位置
    Save_Panel_Pos,
    // 删除块
    Delete_Block,
    Get_Url,
}