import { request } from '@/service/request'

// 获取项目的应用列表
function GetProjectAppList (data) {
    return request({
        url: '/project/project/projectapplist/listJSON?mdCode=APPLICATION_MANAGE',
        method: 'post',
        params: data
    })
}

// 启动发布计划
function StartPubPlan (data) {
    return request({
        url: '/project/pubplan/pubplanlist/startPubPlan',
        method: 'post',
        params: data
    })
}

export {
    GetProjectAppList,
    StartPubPlan
}