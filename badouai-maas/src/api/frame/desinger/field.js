/* ================设计器：字段相关接口================ */
import { request } from '@/service/request'

// 获取图片/附件地址（根据attachId）
// export const AttachUrlById = '/attach/action/attachsupport/downloadAttach.do?attachId='

// 获取字段列表
function Load_Field_Info (data) {
    return request({
        url: '/module/design/moduledesignedit/editJSON.do',
        method: 'post',
        params: data
    })
}
// 获取设计器编辑表单
function Load_Form_Data (data) {
    return request({
        url: '/jdbc/common/basecommonedit/editModule.do?mdCode=sys_module_form',
        method: 'get',
        params: data
    })
}
// 保存字段配置
function Save_Field (data) {
    return request({
        url: '/module/design/moduledesignsave/saveField.do',
        method: 'post',
        params: data
    })
}
// 同步字段到数据库
function Sync_Field (data) {
    return request({
        url: '/module/design/moduledesigncopysave/generalSyncField',
        method: 'get',
        params: data
    })
}
// 强制同步字段到数据库
function Force_Sync_Field (data) {
    return request({
        url: '/module/design/moduledesigncopysave/syncField',
        method: 'get',
        params: data
    })
}
// 保存关联关系配置
function Save_Link (data) {
    return request({
        url: '/module/design/moduledesignsave/saveLink.do',
        method: 'post',
        params: data
    })
}

// 根据关联表查找对应的关联表字段
function Load_Field_For_Table (data) {
    return request({
        url: '/module/design/moduledesignedit/loadTableField.do',
        method: 'post',
        params: data
    })
}

// 保存环节事件（流程）
function Save_Flow (data) {
    return request({
        url: '/module/design/moduledesignsave/saveFlow.do',
        method: 'post',
        params: data
    })
}

// 请求环节处理事件路由名称下拉框
function Get_Route_Array (data) {
    return request({
        url: '/module/design/moduledesignedit/getRouteArray.do',
        method: 'get',
        params: data
    })
}

// 删除字段
function Delete_Field (data) {
    return request({
        url: '/module/design/moduledesigndelete/deleteFieldById.do',
        method: 'get',
        params: data
    })
}

// 复制模型接口
function Copy_Module (data) {
    return request({
        url: '/module/design/moduledesigncopysave/copymodule.do',
        method: 'get',
        params: data
    })
}

// 保存按钮权限
function Save_Button_Promission (data) {
    let moduleId = data.moduleId
    delete data.moduleId
    return request({
        url: `/module/design/moduledesignsave/saveModuleButton/${moduleId}`,
        method: 'post',
        params: data
    })
}

// 模型按钮发布为资源接口
function Publish_Btn_To_Resource (data) {
    let {mdCode, resourceId} = data
    delete data.mdCode
    delete data.resourceId
    return request({
        url: `/system/security/resourcewithmodule/publishBtnToResource/${mdCode}/${resourceId}`,
        method: 'post',
        params: data
    })
}

// 模型生成代码接口
let Create_Code_Url = `${process.env.VUE_APP_BASE_API}/module/design/moduledesigngeneratecode/generateCode.do`

export {
    // 获取字段列表
    Load_Field_Info,
    // 获取设计器编辑表单
    Load_Form_Data,
    // 保存字段配置
    Save_Field,
    // 同步字段到数据库
    Sync_Field,
    // 强制同步字段到数据库
    Force_Sync_Field,
    // 保存关联关系配置
    Save_Link,
    // 根据关联表查找对应的关联表字段
    Load_Field_For_Table,
    // 保存环节事件（流程）
    Save_Flow,
    // 请求环节处理事件路由名称下拉框
    Get_Route_Array,
    // 删除字段
    Delete_Field,
    // 复制模型接口
    Copy_Module,
    // 模型生成代码接口
    Create_Code_Url,
    // 保存按钮权限
    Save_Button_Promission,
    // 模型按钮发布为资源接口
    Publish_Btn_To_Resource,
}