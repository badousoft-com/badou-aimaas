import { request } from '@/service/request'

// 添加项目成员
function Add_Project_Member (data) {
    return request(
        {
            url: '/project/project/projectmanageredit/addProjectMember',
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

// 项目转让
function Transfer_Project (data) {
    return request(
        {
            url: '/project/project/projectmanageredit/transferProject',
            method: 'post',
            params: data
        })
}

// 管理员设置项目所有者
function Manager_Transfer_Project (data) {
    return request({
        url: '/project/project/projectmanageredit/managerTransferProject',
        method: 'post',
        params: data
    })
}

// 获取当前项目的所有成员
function Get_Project_Member (data) {
    return request(
        {
            url: '/jdbc/common/basecommonlist/listJSON.do?mdCode=fbpt_project_member',
            method: 'post',
            params: data
        }
    )
}

// 获取所有公开状态的样本数据集
function Get_Pub_problem (data) {
    return request(
        {
            url: '/jdbc/common/basecommonlist/listJSON.do?mdCode=maas_problem_data_new',
            method: 'post',
            params: data
        }
    )
}


// 删除项目成员
function Delete_Project_Member (data) {
    return request(
        {
            url: '/project/project/projectmanageredit/deleteProjectMember',
            method: 'post',
            params: data
        },
        {
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json'
            }
        }
    )
}

// 离开项目
function Leave_project (data) {
    return request(
        {
            url: '/project/project/projectmanageredit/moveProject',
            method: 'post',
            params: data
        },
        {
            withCredentials: true,
            headers: {
                'Content-Type': 'application/json'
            }
        }
    )
}

// 项目置顶
function Top_Project (data) {
    return request({
        url: '/project/project/projectorderedit/topProject',
        method: 'post',
        params: data
    })
}

// 项目归档
function File_Away_Project (data) {
    return request({
        url: '/project/project/projectmanageredit/fileawayProject',
        method: 'post',
        params: data
    })
}

// 项目删除
function Delete_Project (data) {
    return request({
        url: '/project/project/projectmanageredit/deleteProject',
        method: 'post',
        params: data
    })
}

export {
    Add_Project_Member,
    Transfer_Project,
    Get_Project_Member,
    Delete_Project_Member,
    Leave_project,
    Top_Project,
    File_Away_Project,
    Delete_Project,
    Manager_Transfer_Project,
    Get_Pub_problem
}