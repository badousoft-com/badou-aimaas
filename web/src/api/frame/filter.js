/* ================过滤器相关接口================ */

// import {  } from '@/api/frame/filter'
import { request } from '@/service/request'

// 根据模型mdCode获取过滤器列表
function Get_Module_Filter_List (data) {
    return request({
        url: '/filter/filter/mdfilterlist/loadFilterJson',
        method: 'post',
        params: data
    })
}

// 保存过滤器
function Save_Filter (data) {
    return request({
        url: '/filter/filter/mdfiltersave/saveFilter',
        method: 'post',
        params: data
    })
}

// 切换过滤器类型：系统or用户自定义
function Change_Filter_State (data) {
    let {
        id,
        ...otherPostParams
    } = data || {}
    return request({
        url: `/filter/filter/mdfiltersave/editFilterState/${id}`,
        method: 'post',
        params: otherPostParams
    })
}

// 分享过滤器给指定用户
function Share_Filter (data) {
    return request({
        url: '/filter/filter/mdfiltersave/batchSave',
        method: 'post',
        params: data
    })
}

export {
    // 根据模型mdCode获取过滤器列表
    Get_Module_Filter_List,
    // 保存过滤器
    Save_Filter,
    // 切换过滤器类型：系统or用户自定义
    Change_Filter_State,
    // 分享过滤器给指定用户
    Share_Filter
}