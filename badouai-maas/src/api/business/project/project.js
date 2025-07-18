import { request } from '@/service/request'

// 获取当前的项目数据
function Get_Project () {
    return request({
        url: '/project/project/projectmanagerlist/getCurrentUserProjects',
        method: 'get',
    })
}

// 保存拖拽后项目数据
function Save_Project_Order (data) {
    return request({
        url: '/project/project/projectorderedit/saveProjectOrder',
        method: 'post',
        params: data
    })
}

// 获取项目总览数据
function Get_Project_Total (data) {
    return request({
        url: '/project/project/projectmanagerlist/projectOverview',
        method: 'get',
        params: data
    })
}

// 获取当前项目的环境数据
function Get_Project_Env_Config (projectId) {
    let data = {}
    let arrTemp = []
    // 推入当前选中项目
    arrTemp.push({ 'name': 'projectId', 'value': projectId, 'type': 'text-query', 'tagName': '' })
    // 数组转字符串
    data.searchParam = JSON.stringify(arrTemp)
    return request({
        url: '/jdbc/common/basecommonlist/listJSON.do?mdCode=fbpt_project_env',
        method: 'get',
        params: data
    })
}

// 获取当前项目的环境数据
function Get_ENV_RESOURCE_DETAIL (id) {
    let data = {}
    // 数组转字符串
    return request({
        url: '/jdbc/common/basecommonedit/editJSON.do?mdCode=fbpt_env_resource_apply&isView=true&id=' + id,
        method: 'get',
        params: data
    })
}

// 首次登录修改密码
function Save_First_Logon (data) {
    return request({
        url: '/project/mail/mailedit/firstLogon',
        method: 'post',
        params: data
    })
}

export {
    Get_Project,
    Get_Project_Total,
    Get_Project_Env_Config,
    Get_ENV_RESOURCE_DETAIL,
    Save_Project_Order,
    Save_First_Logon,
}
