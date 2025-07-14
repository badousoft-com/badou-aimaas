import { request } from '@/service/request'

// 新增资源
function Add_Resource (data) {
    return request(
        {
            url: '/project/project/envresourceedit/addResource',
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

// 资源变更
function Change_Resource (data) {
    return request(
        {
            url: '/project/project/envresourceedit/changeResource',
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

// 通过资源审批
function Confirm_Resource (data) {
    return request({
        url: '/project/project/envresourceedit/confirmResource',
        method: 'post',
        params: data
    })
}

// 拒绝资源审批
function Refuse_Resource (data) {
    return request({
        url: '/project/project/envresourceedit/refuseResource',
        method: 'post',
        params: data
    })
}

// 获取资源下应用实际占用的资源
function GET_NAMESPACE_ALL_APP_STATUS (data) {
    return request({
        url: '/project/project/envresourcelist/getNameSpaceAllAppStatus',
        method: 'post',
        params: data
    })
}

// 获取资源详细状态
function GET_NAMESPACE_RESOURCE_DETAIL_STATUS (data) {
    return request({
        url: '/project/project/envresourcelist/getNameSpaceResoureDetailStatus',
        method: 'post',
        params: data
    })
}

// 新增资源
function GET_ONE_PROJECT (data) {
    return request({
        url: '/project/project/projectmanagerlist/getOneProject',
        method: 'GET',
        params: data
    })
}

// 一键申请资源
function One_Click_Apply_Resource (data) {
    return request({
        url: '/project/project/envresourceedit/oneClickApplyResource',
        method: 'POST',
        params: data
    })
}

export {
    Add_Resource,
    Change_Resource,
    Confirm_Resource,
    Refuse_Resource,
    GET_NAMESPACE_ALL_APP_STATUS,
    GET_NAMESPACE_RESOURCE_DETAIL_STATUS,
    GET_ONE_PROJECT,
    One_Click_Apply_Resource
}