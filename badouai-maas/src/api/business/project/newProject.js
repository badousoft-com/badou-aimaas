import { request } from '@/service/request'

// 获取默认的环境推荐配置
function Get_Default_Env_Config () {
    return request({
        url: '/project/project/projectmanagerlist/getDefaultEnvConfig',
        method: 'get'
    })
}

// 获取一个数据字典值
function Get_Dlc_Item (data) {
    return request({
        url: '/project/project/projectmanagerlist/getDlcItem',
        method: 'get',
        params: data
    })
}

// 申请新项目
function Apply_Project (data) {
    return request(
        {
            url: '/project/project/projectmanageredit/applyProject',
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

// 项目审批
function Approval_Project (data) {
    return request(
        {
            url: '/project/project/projectmanageredit/approvalProject',
            method: 'post',
            params: data
        }
    )
}

// 项目撤销
function Revoke_Project (data) {
    return request(
        {
            url: '/project/project/projectmanageredit/cancelProject',
            method: 'post',
            params: data
        }
    )
}

export {
    Get_Default_Env_Config,
    Get_Dlc_Item,
    Apply_Project,
    Approval_Project,
    Revoke_Project
}