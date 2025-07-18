import { request } from '@/service/request'

// 保存 Git 仓库用户信息
function Save_Git_Account_Bingding (data) {
    return request({
        url: '/project/accountbinding/accountbindingsave/save',
        method: 'post',
        params: data
    })
}

// 获取所有的 Git 平台相关信息
function Get_Git_Info_List (data) {
    return request({
        url: '/jdbc/common/basecommonlist/listJSON.do?mdCode=5',
        method: 'post',
        params: data
    })
}

// 密码解码
function Decode_Password (data) {
    return request({
        url: '/project/accountbinding/accountbindingsave/decodePassword',
        method: 'post',
        params: data
    })
}

// 删除凭证
function Delete_Credential (data) {
    return request({
        url: '/project/accountbinding/accountbindingdelete/deleteAccount',
        method: 'post',
        params: data
    })
}

export {
    Save_Git_Account_Bingding,
    Get_Git_Info_List,
    Decode_Password,
    Delete_Credential
}