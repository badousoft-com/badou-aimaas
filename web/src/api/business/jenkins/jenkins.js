import { request } from '@/service/request'

// 保存数据
function Save_Jenkins_Config (data) {
    return request({
        url: `/project/jenkins/jenkinsmanageredit/save`,
        method: 'post',
        params: data
    })
}

// Jenkins命令审批通过
function Confirm_Jenkins_Command (data) {
    return request({
        url: `/project/jenkins/jenkinscommandedit/confirmJenkinsCommand`,
        method: 'post',
        params: data
    })
}

// Jenkins命令审批不通过
function Refuse_Jenkins_Command (data) {
    return request({
        url: `/project/jenkins/jenkinscommandedit/refuseJenkinsCommand`,
        method: 'post',
        params: data
    })
}

// 获取 Jenkins 自定义配置
function Get_Custom_Jenkins_Command (data) {
    return request({
        url: `/project/jenkins/jenkinscommandedit/getCustomCommand`,
        method: 'post',
        params: data
    })
}

export {
    Save_Jenkins_Config,
    Confirm_Jenkins_Command,
    Refuse_Jenkins_Command,
    Get_Custom_Jenkins_Command
}