import { Get_Env, Get_Server_Confs } from '@/api/business/application/quickRelease'
import { Code_Ontime_Release, Pause_Schedule, Restart_Schedule } from '@/api/business/application/ontimeRelease'
import { S_Storage } from '@/utils/storage'
import { Get_Last_Application_Schedule_Config } from '@/api/business/application/ontimeRelease'

/**
 * （源码发布）把表单数据处理成请求体需要的格式
 * @param {Object} formObj 快速发布表单数据
 * @returns 处理好的请求体格式的数据
 */
async function code_publish (formObj) {
    let projectAppEntity = {}
    let projectRouterEntity = {}
    let projectPubEntity = {}
    let {
        appCode, // 应用编码
        appName, // 应用名字
        appType, // 应用类型
        appId, // 应用主键
        buildBranch, // 发布分支
        codeAddress, // 代码仓库
        // codeSource, // 仓库来源
        defaultPort, // 是否采用默认的发布端口
        envId, // 环境主键
        envResourceId, // 资源组件id
        path, // 部署路径
        port, // 发布端口
        pubDesc, // 发布描述
        pubVersion, // 发布版本
        restart, // 是否为重启
        projectName,  // 项目名字
        projectId, // 项目主键
        temp, // 发布公告 TODO:
        // pubWay, // 发布方法
        // pubSource, // 发布来源
        cron
    } = formObj // 解构对象
    // 处理不选择下拉直接提交 将资源名字转成ID
    let tempEnvId = S_Storage.getItem('RESOURCENAME-' + envResourceId)

    if (tempEnvId !== envResourceId && tempEnvId !== null) {
        envResourceId = tempEnvId
    }

    // 请求环境参数
    let envName = ''
    let envCode = ''
    // let envRes = await Get_Env({ projectId: projectId })
    let arrTemp = []
    arrTemp.push({ 'name': 'projectId', 'value': projectId, 'type': 'exact-match', 'tagName': '' })
    let envRes = await Get_Env({ searchParam: JSON.stringify(arrTemp) })
    let envObj = envRes.Rows.find(i => i.id === envId)
    envName = envObj.envName
    envCode = envObj.envCode

    // TODO: 发布公告
    if (envName === '生产上线环境') {
        // 如果发布环境是生产上线环境，则接口携带发布公告信息
        console.log(temp)
    }

    // 根据环境id请求所属服务器配置
    let pubServerId = ''
    let pubServerName = ''
    let serverConfigRes = await Get_Server_Confs({ envId: envId, projectId: projectId })
    if (serverConfigRes.Total !== 0) {
        pubServerId = serverConfigRes.Rows[0].id
        pubServerName = serverConfigRes.Rows[0].code
    } else {
        this.$message.error('该环境未绑定服务器资源，发布失败')
        return
    }

    // 如果使用默认发布端口，强制改变port为默认值
    if (defaultPort === '1') {
        appType === '0' ? port = '8000' : port = '8080'
    }

    // projectAppEntity对象赋值
    projectAppEntity.id = appId
    projectAppEntity.projectId = projectId
    projectAppEntity.projectName = projectName
    projectAppEntity.appCode = appCode
    projectAppEntity.appName = appName
    projectAppEntity.appType = appType
    projectAppEntity.codeAddress = codeAddress
    projectAppEntity.codeSource = '0'

    // projectRouterEntity对象赋值
    projectRouterEntity.projectId = projectId
    projectRouterEntity.appName = appName
    projectRouterEntity.appId = appId
    projectRouterEntity.appCode = appCode
    projectRouterEntity.path = path
    projectRouterEntity.envId = envId
    projectRouterEntity.envResourceId = envResourceId
    projectRouterEntity.port = port

    // projectPubEntity对象赋值
    projectPubEntity.projectId = projectId
    projectPubEntity.appName = appName
    projectPubEntity.appId = appId
    projectPubEntity.appType = appType
    projectPubEntity.appCode = appCode
    projectPubEntity.envCode = envCode
    projectPubEntity.envName = envName
    projectPubEntity.envId = envId
    projectPubEntity.pubServerId = pubServerId
    projectPubEntity.pubServerName = pubServerName
    projectPubEntity.pubVersion = pubVersion
    projectPubEntity.pubDesc = pubDesc
    projectPubEntity.buildBranch = buildBranch
    projectPubEntity.envResourceId = envResourceId
    projectPubEntity.envResourceName = S_Storage.getItem('RESOURCE-' + envResourceId)

    // 把数据处理成请求格式
    let data = { projectAppEntity, projectRouterEntity, projectPubEntity, defaultPort: defaultPort, restart: '1', cron: cron }

    // 返回一个处理好的请求体对象
    return data
}

/**
 * 定时发布弹窗
 * @param {String} appId 应用ID
 * @param {Number} appType 应用类型
 * @param {String} mdCode 模型编码
 */
async function ontime_publish_dialog (appId, appType, mdCode) {
    let lastConfig = await Get_Last_Application_Schedule_Config({ appId: appId})
    let pauseSchedule = lastConfig.pauseSchedule === 0
    let restartSchedule = lastConfig.restartSchedule === 0

    this.$dialog.init({
        // 弹窗内容类型
        type: 'standerEditCode',
        // 弹窗唯一标识，注意唯一标识与上面出现的函数getModelListObj的参数值保持一致
        id: mdCode,
        // 弹窗标题
        title: '代码仓库发布',
        // 模型编码
        mdCode: mdCode,
        // 根据内容自适应高度
        // isAutoFix: true,
        width: '800px',
        height: '740px',
        // 详情数据id
        detailId: appId,
        // 弹窗中按钮组
        handlerList: [
            {
                name: '取消',
                type: 'danger',
                icon: 'cancel',
                click: function () {
                    // 关闭弹窗表单
                    this.$dialog.close()
                }
            },
            {
                name: '定时发布',
                type: 'primary',
                icon: 'todoT',
                click: function (_btnObj) {
                    // // modelForm 对象就是一个 vue 组件
                    let modelForm = this.getDialogConObj(mdCode, 4) // 获取模型表单页面作用域，id为dialog属性id的值
                    modelForm.validateForm().then(formObj => {
                        this.$confirm('确定为当前应用设置定时任务吗？', '定时发布任务', {
                            confirmButtonText: '确定',
                            cancelButtonText: '取消',
                            type: 'warning'
                        }).then(async (res) => {
                            let requestData = {} // 接收处理好的请求体对象
                            let postRes = {} // 接收请求返回的数据
                            // 检查是否与上次发布的资源一致
                            let lastResource =  S_Storage.getItem('LASTRESOURCE-' + formObj.id)     // 'dch20230306175220'
                            let nowResource =  S_Storage.getItem('RESOURCE-' + formObj.envResourceId)   // null
                            let lastEnvName =  S_Storage.getItem('LASTENV-' + formObj.id)   // '测试环境'

                            if (nowResource == null) {
                                nowResource = formObj.envResourceId
                            }
                            let lastEnvId = S_Storage.getItem('ENVNAME' + lastEnvName)
                            if (lastResource !== nowResource && lastResource !== '' && lastEnvId === formObj.envId) {
                                this.$confirm('检查发现与上一次定时发布资源不一致，若定时任务申请通过，会导致清空本应用之前的资源，是否确定', '资源使用变更确定', {
                                    confirmButtonText: '确定',
                                    cancelButtonText: '取消',
                                    type: 'warning'
                                }).then(async () => {
                                    _btnObj.loading = true  // 校验成功，准备请求，设置加载状态
                                    if (mdCode === 'CODE_QUICK_RELEASE') {  // 源码发布
                                        // 将数据处理成请求体需要的格式
                                        requestData = await code_publish.call(this, formObj)
                                        // 源码定时发布请求
                                        postRes = await Code_Ontime_Release(requestData)
                                    }
                                    // else 语句添加制品发布
                                })
                                if (postRes.hasOk) {
                                    this.$message.success('定时任务设置成功')
                                } else {
                                    this.$message.error(postRes.message)
                                }
                                _btnObj.loading = false // 重置按钮加载状态，实际场景中应该在保存接口结束后
                                this.$dialog.close()    // 关闭弹窗表单
                            } else {
                                _btnObj.loading = true // 检验成功，准备请求，设置加载状态
                                if (mdCode === 'CODE_ONTIME_RELEASE') {
                                    // 将数据处理成请求体需要的格式
                                    requestData = await code_publish.call(this, formObj)
                                    // 源码定时发布请求
                                    postRes = await Code_Ontime_Release(requestData)
                                }
                                // else 语句添加制品发布
                                if (postRes.hasOk) {
                                    this.$dialog.close()
                                    this.$message.success('定时任务设置成功，请等待管理员审批')
                                    this.$dialog.close()    // 关闭弹窗表单
                                } else {
                                    this.$message.error(postRes.message)
                                }
                                _btnObj.loading = false // 重置按钮加载状态，实际场景中应该在保存接口结束后
                            }
                        })
                    })
                }
            },
            {
                name: '暂停任务',
                icon: 'pause',
                type: 'info',
                disabled: pauseSchedule,
                click: async function (_btnObj) {
                    let postRes = await Pause_Schedule.call(this, { id: appId })
                    if (postRes) {
                        this.$dialog.close()
                        this.$message.success('定时任务取消成功')
                    } else {
                        this.$message.error(postRes.message)
                    }
                }
            },
            {
                name: '重启任务',
                icon: 'reset-fill',
                type: 'warning',
                disabled: restartSchedule,
                click: async function (_btnObj) {
                    let postRes = await Restart_Schedule.call(this, { id: appId })
                    if (postRes) {
                        this.$dialog.close()
                        this.$message.success('定时任务重启成功')
                    } else {
                        this.$message.error(postRes.message)
                    }
                }
            }
        ],
        isView: false
    })
}

export {
    ontime_publish_dialog
}