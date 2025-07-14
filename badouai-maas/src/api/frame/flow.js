/* ================流程相关接口================ */
import { request } from '@/service/request'
// 引入举例
// import { } from '@/api/frame/flow'

// 保存流程下环节排序
function Save_Node_Priority (data) {
    return request({
        url: '/flow/busi/flow/saveNodePriority.do',
        method: 'post',
        params: data
    })
}

// 保存环节下路由排序
function Save_Route_Priority (data) {
    return request({
        url: '/flow/busi/flow/saveRoutePriority.do',
        method: 'post',
        params: data
    })
}

// 保存环节表单接口
function Save_Node_Url (mdCode) {
    return `/engine/flow/action/nodenew/saveNode.do?mdCode=${mdCode}`
}

// 发起流程审核接口【暂时废弃】
function Verify_Flow_Url (mdCode) {
    return `/jdbc/common/basecommonsave/saveFlow.do?mdCode=${mdCode}`
}

// 根据环节id获取跳转的视图URL地址
function Get_View_Url (nodeId) {
    return request({
        url: `/engine/flow/action/viewdictionary/getViewUrlByNodeId/${nodeId}`,
        method: 'post',
        params: {}
    })
}

// 获取路由按钮数据
function Get_Routes (data) {
    return request({
        url: `/bpmsflowinstance/getRoutes`,
        method: 'post',
        params: data
    })
}

// 获取流程跟踪数据接口
function Get_Trace_List (id) {
    return request({
        url: `/trace/proccesstracelist/getTraceList/${id}`,
        method: 'post',
        params: {}
    })
}

// 发起审核前获取意见与下一环节处理人数据
function Start_Option_And_Dealer (data) {
    return request({
        url: `/process/gtasks/submit/beforeStart`,
        method: 'post',
        params: data
    })
}

// 发起下一环节审核前获取意见与下一环节处理人数据
function Next_Option_And_Dealer (data) {
    return request({
        url: `/process/gtasks/submit/beforeNext`,
        method: 'post',
        params: data
    })
}

// 发起审核接口
function Start_Submit (data) {
    return request({
        url: `/process/gtasks/submit/start`,
        method: 'post',
        params: data
    })
}

// 发起下一环节审核接口
function Next_Submit (data) {
    return request({
        url: `/process/gtasks/submit/next`,
        method: 'post',
        params: data
    })
}

// 保存常用意见接口
function Save_Common_Opinion (data) {
    return request({
        url: `/process/myopinion/personopinionsave/save.do`,
        method: 'post',
        params: data
    })
}

// 删除常用意见接口
function Del_Common_Opinion (data) {
    return request({
        url: `/process/myopinion/personopiniondelete/delete.do`,
        method: 'post',
        params: data
    })
}

// 流程环节转办-更换环节处理人
function Transfer (data) {
    return request({
        url: `/instance/flow/flowinstancemonitor/transfer`,
        method: 'post',
        params: data
    })
}

// 根据流程实例id获取对应的环节实例列表数据
function Get_Node_Instance_List (data) {
    return request({
        url: `/instance/node/nodeinstanceselectlist/listJSON`,
        method: 'post',
        params: data
    })
}

// 流程环节跳转接口
function Save_Node_Jump (data) {
    return request({
        url: `/instance/flow/flowinstancemonitor/jump`,
        method: 'post',
        params: data
    })
}

// 流程重处理
function Flow_ReHandle (data) {
    return request({
        url: `/instance/flow/flowinstancemonitor/rehandle`,
        method: 'post',
        params: data
    })
}
// 传阅
function Spread_To_Others (data) {
    return request({
        url: `/engine/flow/action/spread/spread`,
        method: 'post',
        params: data
    })
}

// 待阅转已阅
function Spread_Status_Change (data) {
    return request({
        url: `/engine/flow/action/spread/batchChangeSpread`,
        method: 'post',
        params: data
    })
}

// 处理待办前校验待办是否已经被处理接口
function Workitem_Valid (wid) {
    return request({
        url: `/process/gtasks/submit/valid/${wid}`,
        method: 'post',
        params: {}
    })
}

// 催办接口
function Urge_By_BoId (data) {
    return request({
        url: `/process/gtasks/worklist/urgeByBoId`,
        method: 'post',
        params: data
    })
}

export {
    // 保存流程下环节排序
    Save_Node_Priority,
    // 保存环节下路由排序
    Save_Route_Priority,
    // 保存环节表单接口
    Save_Node_Url,
    // 发起流程审核接口【暂时废弃】
    Verify_Flow_Url,
    // 根据环节id获取跳转的视图URL地址
    Get_View_Url,
    // 获取路由按钮数据
    Get_Routes,
    // 获取流程跟踪数据接口
    Get_Trace_List,
    // 发起审核前获取意见与下一环节处理人数据
    Start_Option_And_Dealer,
    // 发起下一环节审核前获取意见与下一环节处理人数据
    Next_Option_And_Dealer,
    // 发起审核接口
    Start_Submit,
    // 发起下一环节审核接口
    Next_Submit,
    // 保存常用意见接口
    Save_Common_Opinion,
    // 删除常用意见接口
    Del_Common_Opinion,
    // 流程环节转办-更换环节处理人
    Transfer,
    // 根据流程实例id获取对应的环节实例列表数据
    Get_Node_Instance_List,
    // 流程环节跳转接口
    Save_Node_Jump,
    // 流程重处理
    Flow_ReHandle,
    // 传阅
    Spread_To_Others,
    // 待阅转已阅
    Spread_Status_Change,
    // 处理待办前校验待办是否已经被处理接口
    Workitem_Valid,
    // 催办接口
    Urge_By_BoId
}
