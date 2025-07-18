import { request } from '@/service/request'

// 源码仓库快速发布
function Code_Quick_Release (data) {
    return request(
        {
            url: '/project/project/projectappedit/fastPub',
            method: 'post',
            params: data
        },
        {
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json'
            }
        })
}

// 源码仓库快速发布
function Product_Quick_Release (data) {
    return request(
        {
            url: '/project/project/projectappedit/productPub',
            method: 'post',
            params: data
        },
        {
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json'
            }
        })
}

// 请求分支
function Get_Branch (data) {
    return request({
        url: '/project/project/projectapplist/getAppBranch',
        method: 'get',
        params: data
    })
}

// 请求环境数据
function Get_Env (data) {
    return request({
        url: '/project/project/projectenvlist/getEnvList',
        method: 'get',
        params: data
    })
}

// 根据环境id请求所属服务器配置
function Get_Server_Confs (data) {
    return request({
        url: '/project/project/projectenvlist/getServerConfs',
        method: 'get',
        params: data
    })
}

// 请求资源列表
function Get_Resource (data) {
    return request({
        url: '/jdbc/common/basecommonlist/listJSON.do?mdCode=fbpt_env_resource',
        method: 'get',
        params: data
    })
}

// 获取当前应用的发布构建信息
function Get_Current_App_Task_Status (data) {
    return request({
        url: '/project/project/projectappedit/getCurrentAppTaskStatus',
        method: 'get',
        params: data
    })
}

// 获取最新的发布路径跟版本
function Get_App_Pub_History_Msg (data) {
    return request({
        url: '/project/project/projectapplist/getAppPubHistoryMsg',
        method: 'get',
        params: data
    })
}

// 根据发布主键 获取实例主键
function Get_App_Pub_INSTACE_Msg (data) {
    return request({
        url: '/project/project/projectinstancelist/getInstanceMsg',
        method: 'get',
        params: data
    })
}

// 根据资源名字 获取资源信息
function GET_NAMESPACE_RESOURCE_STATUS (data) {
    return request({
        url: '/project/project/envresourcelist/getNameSpaceResoureStr',
        method: 'get',
        params: data
    })
}

// 获取域名
function GET_HOST_LIST (data) {
    return request({
        url: '/jdbc/common/basecommonlist/listJSON.do?mdCode=fbpt_host',
        method: 'post',
        params: data
    })
}

// 获取快速/制品发布弹框时，选中环境后的资源下拉框内容
function Get_Publish_Resource (data) {
    return request({
        url: '/jdbc/common/basecommonlist/listJSON.do?mdCode=app_publish_fbpt_env_resource',
        method: 'get',
        params: data
    })
}

// 取消任务发布
function CANCEL_TASK (data) {
    return request({
        url: '/project/project/projectappedit/cancelPubTask',
        method: 'get',
        params: data
    })
}

// 使用上一次成功的配置发布应用
function History_Pub (data) {
    return request({
        url: '/project/project/projectappedit/startLastSuccessPubTask',
        method: 'post',
        params: data
    })
}

export {
    Code_Quick_Release,
    Product_Quick_Release,
    Get_Branch,
    Get_Env,
    Get_Server_Confs,
    Get_Resource,
    Get_Current_App_Task_Status,
    Get_App_Pub_History_Msg,
    Get_App_Pub_INSTACE_Msg,
    GET_NAMESPACE_RESOURCE_STATUS,
    GET_HOST_LIST,
    Get_Publish_Resource,
    CANCEL_TASK,
    History_Pub
}