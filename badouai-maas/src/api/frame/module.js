/* ================模型请求相关接口================ */
import { request } from '@/service/request'

// 模型列表数据
function Standard_List (data) {
    return request({
        url: '/jdbc/common/basecommonlist/listModule.do',
        method: 'get',
        params: data
    })
}
// 模型编辑数据
function Standard_Edit (data) {
    return request({
        url: '/jdbc/common/basecommonedit/editModule.do',
        method: 'get',
        params: data
    })
}
// 模型查看数据
function Standard_View (data) {
    return request({
        url: '/jdbc/common/basecommonedit/viewModule.do',
        method: 'get',
        params: data
    })
}

// 刷新模型缓存
function Refresh_Module_Cache (data) {
    return request({
        url: '/jdbc/common/basecommonedit/flushModuleCache',
        method: 'get',
        params: data
    })
}

export {
    // 模型列表数据
    Standard_List,
    // 模型编辑数据
    Standard_Edit,
    // 模型查看数据
    Standard_View,
    // 刷新模型缓存
    Refresh_Module_Cache
}