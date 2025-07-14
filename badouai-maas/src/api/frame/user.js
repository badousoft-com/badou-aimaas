/* ================用户数据权限相关接口================ */
import { request } from '@/service/request'

// 登录
function Login (data) {
    // data为参数对象
    return request({
        url: '/system/security/logon/logon',
        method: 'post',
        params: data
    })
}

// 注册
function Register (data) {
    // data为参数对象
    return request({
        url: '/system/security/userregister/create.do',
        method: 'post',
        params: data
    })
}

// 获取用户基本信息
function Get_Info (data) {
    return request({
        url: '/system/security/logon/userInfo',
        method: 'post',
        params: data
    })
}
// 更新用户基本信息
function Update_Info (data) {
    return request({
        url: '/org/employee/employeesave/updateMyInfo.do',
        method: 'post',
        params: data
    })
}
// 更新用户密码
function Update_Pwd (data) {
    return request({
        url: '/system/security/logon/updatePassword.do',
        method: 'post',
        params: data
    })
}
// 获取用户历史头像
function Get_User_History_Avatar_List (data) {
    return request({
        url: '/org/employee/employeeedit/getHistoricalHeadImg.do',
        method: 'post',
        params: data
    })
}

// 获取用户权限菜单
function Get_User_Routes () {
    return request({
        url: '/system/security/resource/userResource',
        method: 'post',
        params: {}
    })
}

// 保存带有角色选择的菜单保存
function Save_Resource_And_Role (data) {
    return request({
        url: '/system/security/resourcewithmodule/saveResourceAndRole',
        // url: '/system/security/resourcetree/saveResourceAndRole',
        method: 'post',
        params: data
    })
}

function Logout () {
    return request({
        url: '/system/security/logout/logout',
        method: 'post'
    })
}

// 切换用户角色
function Change_User_Role (roleCode) {
    return request({
        url: `/system/security/logon/switchRole/${roleCode}`,
        method: 'get',
        params: {}
    })
}
// 获取用户常用意见
function Get_User_Opinions () {
    return request({
        url: '/process/gtasks/submit/userOpinions',
        method: 'get'
    })
}

// 免登录获取token
function SSO_Get_Token (data) {
    return request({
        url: '/system/security/logon/logonLargeScreen',
        method: 'post',
        params: data
    })
}

export {
    // 登录
    Login,
    // 注册
    Register,
    // 获取用户基本信息
    Get_Info,
    // 更新用户基本信息
    Update_Info,
    // 更新用户密码
    Update_Pwd,
    // 获取用户历史头像
    Get_User_History_Avatar_List,
    // 获取用户权限菜单
    Get_User_Routes,
    // 保存带有角色选择的菜单保存
    Save_Resource_And_Role,
    Logout,
    // 切换用户角色
    Change_User_Role,
    // 获取用户常用意见
    Get_User_Opinions,
    // 免登陆根据用户id获取token
    SSO_Get_Token,
}
