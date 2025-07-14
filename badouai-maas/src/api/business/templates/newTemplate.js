const { request } = require('@/service/request')
// 历史中间件类型(弹窗)-从数据字典获取
function getType () {
    return request({
        url: 'common/commoninterface/listDicJSON',
        params: { dicCode: 'COMPOENTTYPE' }
    })
}
// 保存
function saveTemplateFormFile (params = {}) {
    let url = `/project/templatemodule/templateinfosave/save`
    return new Promise((resolve, reject) => {
        this.postFile(url, params, (isSuccess, res) => {
            if (isSuccess && res?.hasOk) {
                this.$message.success('保存成功')
                resolve(res)
            } else {
                this.$message.error(`保存失败！${res?.message}`)
                resolve(res)
            }
        }, true)
    })
}
// 模板发布
function publicTemplate (params) {
    const url = '/project/templateproject/templateprojectsave/publishTemplateProject'
    return request({
        url,
        params,
        method: 'post'
    },
    {
        withCredentials: true,
        headers: {
            'Content-Type': 'application/json'
        }
    }
    )
}

function environmentalQuery (params) {
    const url = '/project/templateproject/templateprojectapplist/getDefaultTemplateConfig'
    return request({
        url,
        params,
        method: 'get'
    },
    )
}

function ProjectPreset (params) {
    const url = '/project/project/projectmanagerlist/getDlcItemList'
    params = {
        codes: 'TEMPLATEPRESET,APPDEPLOYTYPE'
    }
    return request({
        url,
        params,
        method: 'get'
    },
    )
}

function baseImage (data) {
    return request({
        url: `/jdbc/common/basecommonlist/listJSON.do?mdCode=fbpt_base_image`,
        method: 'post',
        params: data
    })
}

function baseImageVersion (data) {
    return request({
        url: `/jdbc/common/basecommonlist/listJSON.do?mdCode=fbpt_base_image_version`,
        method: 'post',
        params: data
    })
}

function getModuleAppEnvQuery (data) {
    return request({
        url: `/jdbc/common/basecommonlist/listJSON.do?mdCode=fbpt_app_config_parament`,
        method: 'post',
        params: data
    })
}

function getResourcesComponent (data) {
    return request({
        url: `/jdbc/common/basecommonlist/listJSON.do?mdCode=fbpt_template_middleware_info`,
        method: 'post',
        params: data
    })
}

function editModuleData (data) {
    return request({
        url: `/project/templatemodule/templateinfoedit/getTemplateAllInfo`,
        method: 'post',
        params: data
    })
}

// 修改更新
function editTemplateFormFile (params = {}) {
    let url = `/project/templatemodule/templateinfoedit/update`
    return new Promise((resolve, reject) => {
        this.postFile(url, params, (isSuccess, res) => {
            if (isSuccess && res?.hasOk) {
                this.$message.success('保存成功')
                resolve(res)
            } else {
                this.$message.error(`保存失败！${res?.message}`)
                resolve(res)
            }
        }, true)
    })
}

// 提交审批
function applyTemplate(data) {
    return request({
        url: `/project/templatemodule/templateinfoedit/applyTemplate`,
        method: 'post',
        params: data
    })
}

// 模板测试
function checkTemplate(data) {
    return request({
        url: `/project/templatemodule/templateinfoedit/checkTemplateBeforeShelf`,
        method: 'post',
        params: data
    })
}

// 审批通过
function approvalTemplate(data) {
    return request({
        url: `/project/templatemodule/templateinfoedit/confirmTemplate`,
        method: 'post',
        params: data
    })
}

// 审批撤销
function refuseTemplate(data) {
    return request({
        url: `/project/templatemodule/templateinfoedit/refuseTemplate`,
        method: 'post',
        params: data
    })
}

// 获取上次测试结果
function getLastTemplateCheckResult (data) {
    return request({
        url: `/project/templatemodule/templateinfoedit/getLastTemplateCheckResult`,
        method: 'get',
        params: data
    })
}

export {
    getType,
    saveTemplateFormFile,
    publicTemplate,
    environmentalQuery,
    ProjectPreset,
    baseImage,
    baseImageVersion,
    getModuleAppEnvQuery,
    getResourcesComponent,
    editModuleData,
    editTemplateFormFile,
    applyTemplate,
    checkTemplate,
    approvalTemplate,
    refuseTemplate,
    getLastTemplateCheckResult
}
