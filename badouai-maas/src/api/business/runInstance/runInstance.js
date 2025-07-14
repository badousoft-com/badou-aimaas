import { request } from '@/service/request'

// 获取运行实例列表
function Get_Instance (data) {
    return request({
        url: '/project/project/projectinstancelist/listJSON?mdCode=fbpt_pod_run',
        method: 'post',
        params: data
    })
}

// 获取实例访问地址
function Get_Instance_Address (data) {
    return request({
        url: '/project/project/projectinstancelist/getInstanceAddress',
        method: 'get',
        params: data
    })
}

// 重启实例
function Restart_Instance (data) {
    return request({
        url: '/project/project/projectinstanceedit/restartInstance',
        method: 'post',
        params: data
    })
}

// 删除实例
function Delete_Instance (data) {
    return request({
        url: '/project/project/projectinstanceedit/deleteInstance',
        method: 'post',
        params: data
    })
}

export {

    Get_Instance_Address,
    Get_Instance,
    Restart_Instance,
    Delete_Instance
}