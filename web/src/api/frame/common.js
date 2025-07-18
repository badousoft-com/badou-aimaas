/* ================通用接口================ */
import { request } from '@/service/request'

// import {  } from '@/api/frame/common'
// 获取模型列表
function Get_Common_List (data, method) {
    return request({
        url: '/jdbc/common/basecommonlist/listJSON.do',
        method: method || 'get',
        params: data
    })
}

// 返回模型保存接口地址
function Save_URL (data) {
    return `${process.env.VUE_APP_BASE_API}/jdbc/common/basecommonsave/saveIncludeFile.do?mdCode=${data}`
}

// 保存模型数据
function Save_Module_Form (data, url) {
    let mdCode = data.mdCode
    delete data.mdCode
    return request({
        url: url || `/jdbc/common/basecommonsave/saveIncludeFile.do?mdCode=${mdCode}`,
        method: 'post',
        params: data
    })
}

// 删除
function Common_Delete (data) {
    return request({
        url: '/jdbc/common/basecommondelete/delete.do',
        method: 'post',
        params: data
    })
}

// 返回保存树型-表单数据的地址
function Save_Tree_URL (data) {
    return `${process.env.VUE_APP_BASE_API}/jdbc/common/basecommontreesave/saveIncludeFile.do?mdCode=${data}`
}

// 返回保存数据字典-表单数据的地址
function Save_Dic_URL (data) {
    return `${process.env.VUE_APP_BASE_API}/jdbc/common/basecommonsave/saveDic.do?mdCode=${data}`
}

// 返回模型列表数据请求地址
function Module_List_URL (data) {
    return `${process.env.VUE_APP_BASE_API}/jdbc/common/basecommonlist/listJSON.do?mdCode=${data}`
}

// 返回模型-树型列表数据请求地址
function Tree_List_URL (mdCode) {
    return `${process.env.VUE_APP_BASE_API}/jdbc/common/basecommontreelist/listJSON.do?mdCode=${mdCode}&addParent=false`
}

// 获取模型树型-节点接口地址
let Tree_Node_List_URL = `${process.env.VUE_APP_BASE_API}/jdbc/common/basecommontree/tree.do`

// 获取模型编辑值详情数据
function Get_Module_EditJSON (data) {
    return request({
        url: '/jdbc/common/basecommonedit/editJSON.do',
        method: 'get',
        params: data
    })
}

// 验证表单字段值的唯一性
function Valid_Unique (data) {
    return request({
        url: '/jdbc/common/basecommonedit/validUnique.do',
        method: 'get',
        params: data
    })
}

// 返回下载导入模板请求地址
function Download_Import_Url (mdCode) {
    return `${process.env.VUE_APP_BASE_API}/jdbc/common/basemoduleimport/templateDown?mdCode=${mdCode}`
}

// 返回导入模型请求地址
function Import_Url (mdCode) {
    return `${process.env.VUE_APP_BASE_API}/jdbc/common/basecommonimport/baseImportForResult?mdCode=${mdCode}`
}

// 返回导出模型请求地址
function Export_Url (mdCode) {
    return `${process.env.VUE_APP_BASE_API}/jdbc/common/basecommonlist/export.do?mdCode=${mdCode}`
}

export {
    // 获取模型列表
    Get_Common_List,
    // 返回模型保存接口地址
    Save_URL,
    // 保存模型表单数据
    Save_Module_Form,
    // 删除
    Common_Delete,
    Common_Delete as Delete_Url, // TODO 待删除
    // 返回保存树型-表单数据的地址
    Save_Tree_URL,
    // 返回保存数据字典-表单数据的地址
    Save_Dic_URL,
    // 返回模型列表数据请求地址
    Module_List_URL,
    // 返回模型-树型列表数据请求地址
    Tree_List_URL,
    // 获取模型树型-节点接口地址
    Tree_Node_List_URL,
    // 获取模型编辑值详情数据
    Get_Module_EditJSON,
    // 验证表单字段值的唯一性
    Valid_Unique,
    // 返回下载导入模板请求地址
    Download_Import_Url,
    // 返回导入模型请求地址
    Import_Url,
    // 返回导出模型请求地址
    Export_Url
}