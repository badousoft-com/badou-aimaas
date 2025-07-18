import { request } from '@/service/request'

// 通过 id 获取定时任务对象
function Get_App_Schedule (data) {
    return request({
        url: '/project/project/projectappschedulelist/getAppSchedule',
        method: 'get',
        params: data
    })
}

// 定时任务审批通过
function Approval_App_Schedule (data) {
    return request({
        url: '/project/project/projectappscheduleedit/approvalSchedule',
        method: 'post',
        params: data
    })
}

// 定时任务审批失败
function Revoke_App_Schedule (data) {
    return request({
        url: '/project/project/projectappscheduleedit/failSchedule',
        method: 'post',
        params: data
    })
}

export {
    Get_App_Schedule,
    Approval_App_Schedule,
    Revoke_App_Schedule
}