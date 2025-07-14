import { request } from '@/service/request'

// 通过发布历史表，获取运行实例
function Get_Project_Instance (data) {
    return request({
        url: '/project/project/projectpubhistoryedit/getProjectInstanceById',
        method: 'get',
        params: data
    })
}

// 获取应用的实例
function Get_App_Instance (data) {
    return request({
        url: '/project/project/envresourcelist/getInstanceStatus',
        method: 'POST',
        params: data,
    }
    )
}

export {
    Get_Project_Instance,
    Get_App_Instance
}