/*
 * @LastEditTime: 2022-09-20 14:30:24
 * @Description: 面板渲染页接口
 */
import { request } from '@/service/request'

// 请求面板及面板块
function Load_Panel_And_Block (data) {
    return request({
        url: `/panel/layout/layoutlist/loadPanelAndBlockWithId/${data.panelId}`,
        method: 'post',
        params: {}
    })
}
// 请求列表数据
function Pack_Data (data, url) {
    return request({
        url: url || '/panel/cfg/reportshowtype/reportshowtypeengine/packData.do',
        method: 'post',
        params: data
    })
}

export {
    // 请求面板及面板块
    Load_Panel_And_Block,
    // 请求列表数据
    Pack_Data
}