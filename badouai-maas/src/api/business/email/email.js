import { request } from '@/service/request'

// 获取邮件相关事件
function Get_Mail_Event (data) {
    return request({
        url: '/jdbc/common/basecommonlist/listJSON.do?mdCode=email_event',
        method: 'get',
        params: data
    })
}

// 获取项目成员
function Get_Project_Member (data) {
    return request({
        url: '/project/project/projectmemberlist/getProjectMemberListForMail',
        method: 'post',
        params: data
    })
}

// 保存邮件配置
function Save_Email_Config (data) {
    return request({
        url: '/project/mail/emailconfigedit/save',
        method: 'post',
        params: data
    })
}

// 启用配置
function Start_Email_Config (data) {
    return request({
        url: '/project/mail/emailconfigedit/start',
        method: 'post',
        params: data
    })
}

// 停用配置
function Stop_Email_Config (data) {
    return request({
        url: '/project/mail/emailconfigedit/stop',
        method: 'post',
        params: data
    })
}

// 获取邮件配置
function Get_Last_Email_Config (data) {
    return request({
        url: '/project/mail/emailconfigedit/getLastEmailConfig',
        method: 'post',
        params: data
    })
}

// 点击资源变更获取项目成员
function Get_All_System_Managers (data) {
    return request({
        url: '/project/mail/emailedit/getAllSystemManager',
        method: 'post',
        params: data
    })
}

export {
    Get_Mail_Event,
    Get_Project_Member,
    Save_Email_Config,
    Start_Email_Config,
    Stop_Email_Config,
    Get_Last_Email_Config,
    Get_All_System_Managers
}