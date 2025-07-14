import { request } from '@/service/request'

// 新增环境
function New_Env (data) {
    return request(
        {
            url: '/project/project/projectenvedit/addNewProjectEnv',
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

// 获取环境新增选项
function Get_Env_Item (data) {
    return request({
        url: '/project/project/projectmanagerlist/getDlcItem',
        method: 'get',
        params: { code: 'ENVCODES' }
    })
}

// 环境绑定服务器
function BIND_NEW_SERVER (data) {
    return request({
        url: '/project/project/projectenvedit/bindNewServer',
        method: 'post',
        params: data
    })
}

// 环境可视化
function Env_Visualization_Config (data) {
    return request({
        url: '/project/project/projectenvedit/projectEnvVisualizationConfig',
        method: 'post',
        params: data
    })
}

export {
    New_Env,
    Get_Env_Item,
    BIND_NEW_SERVER,
    Env_Visualization_Config
}