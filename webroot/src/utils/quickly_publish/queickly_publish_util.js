import { Get_Env, Get_Server_Confs } from '@/api/business/application/quickRelease'
import { Code_Quick_Release, Product_Quick_Release } from '@/api/business/application/quickRelease'
import { S_Storage } from '../storage'

/**
 * （源码发布）把表单数据处理成请求体需要的格式
 * @param {Object} formObj 快速发布表单数据
 * @returns 处理好的请求体格式的数据
 */
async function code_publish (formObj) {
    let projectAppEntity = {}
    let projectRouterEntity = {}
    let projectPubEntity = {}
    let jenkinsCommandDto = {}
    let {
        appCode, // 应用编码
        appName, // 应用名字
        appType, // 应用类型
        id, // 应用主键
        buildBranch, // 发布分支
        codeAddress, // 代码仓库
        codeSource, // 仓库来源
        defalutPort, // 是否采用默认的发布端口
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
        host, // 域名
        rewriteStatus,  // 是否重写Jenkins命令
        jenkinsCommand,    // Jenkins自定义构建命令
        // pubWay, // 发布方法
        // pubSource, // 发布来源

    } = formObj // 解构对象
    // 处理不选择下拉直接提交 将资源名字转成ID
    let tempEnvId = S_Storage.getItem('RESOURCENAME-' + envResourceId)

    if (tempEnvId !== envResourceId && tempEnvId !== null) {
        envResourceId = tempEnvId
    }

    // 请求环境参数
    let envName = ''
    let envCode = ''
    let arrTemp = []
    arrTemp.push({ 'name': 'projectId', 'value': projectId, 'type': 'exact-match', 'tagName': '' })
    let envRes = await Get_Env({ searchParam: JSON.stringify(arrTemp) })
    let envObj = envRes.Rows.find(i => i.id === envId || i.id === 'envId')
    envName = envObj?.envName
    envCode = envObj?.envCode

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
    if (defalutPort === '1') {
        appType === '0' ? port = '8000' : port = '8080'
    }

    // projectAppEntity对象赋值
    projectAppEntity.id = id
    projectAppEntity.projectId = projectId
    projectAppEntity.projectName = projectName
    projectAppEntity.appCode = appCode
    projectAppEntity.appName = appName
    projectAppEntity.appType = appType
    projectAppEntity.codeAddress = codeAddress
    projectAppEntity.codeSource = codeSource

    // projectRouterEntity对象赋值
    projectRouterEntity.projectId = projectId
    projectRouterEntity.appName = appName
    projectRouterEntity.appId = id
    projectRouterEntity.appCode = appCode
    projectRouterEntity.path = path
    projectRouterEntity.envId = envId
    projectRouterEntity.envResourceId = envResourceId
    projectRouterEntity.port = port
    projectRouterEntity.hostId = host

    // projectPubEntity对象赋值
    projectPubEntity.projectId = projectId
    projectPubEntity.appName = appName
    projectPubEntity.appId = id
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
    
    jenkinsCommandDto.rewriteStatus = rewriteStatus
    jenkinsCommandDto.jenkinsCommand = jenkinsCommand

    // console.log(projectAppEntity)
    // console.log(projectRouterEntity)
    // console.log(projectPubEntity)

    // 把数据处理成请求格式
    let data = { projectAppEntity, projectRouterEntity, projectPubEntity, defalutPort: defalutPort, jenkinsCommandDto: jenkinsCommandDto, restart: restart }
    // console.log(JSON.stringify(data))

    // 返回一个处理好的请求体对象
    return data
}

/**
 * （制品发布）把表单数据处理成请求体需要的格式
 * @param {Object} formObj 快速发布表单数据
 * @returns 处理好的请求体格式的数据
 */
async function product_publish (formObj) {
    let projectAppEntity = {}
    let projectRouterEntity = {}
    let projectPubEntity = {}
    let {
        appCode, // 应用编码
        appName, // 应用名字
        appType, // 应用类型
        id, // 应用主键
        pubImage, // 发布镜像
        codeAddress, // 代码仓库
        codeSource, // 仓库来源
        defalutPort, // 是否采用默认的发布端口
        envId, // 环境主键
        envResourceId, // 资源主键
        envResourceName, // 资源名称
        path, // 部署路径
        port, // 发布端口
        pubDesc, // 发布说明
        pubVersion, // 发布版本
        pubAccount, // 镜像仓库账号
        pubPassword, // 镜像仓库密码
        restart, // 是否为重启
        projectName,  // 项目名字
        projectId, // 项目主键
        temp, // 发布公告 TODO:
        // pubWay, // 发布方法
        // pubSource, // 发布来源

    } = formObj // 解构对象

    // 请求环境参数
    let envName = ''
    let envCode = ''
    let envRes = await Get_Env({ projectId: projectId })
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
    // console.log(serverConfigRes)
    if (serverConfigRes.Total !== 0) {
        pubServerId = serverConfigRes.Rows[0].id
        pubServerName = serverConfigRes.Rows[0].code
    } else {
        this.$message.error('该环境未绑定服务器资源，发布失败')
        return
    }

    // 如果使用默认发布端口，强制改变port为默认值
    if (defalutPort === '1') {
        appType === '0' ? port = '8000' : port = '8080'
    }

    // projectAppEntity对象赋值
    projectAppEntity.id = id
    projectAppEntity.projectId = projectId
    projectAppEntity.projectName = projectName
    projectAppEntity.appCode = appCode
    projectAppEntity.appName = appName
    projectAppEntity.appType = appType
    projectAppEntity.codeAddress = codeAddress
    projectAppEntity.codeSource = codeSource

    // projectRouterEntity对象赋值
    projectRouterEntity.projectId = projectId
    projectRouterEntity.appName = appName
    projectRouterEntity.appId = id
    projectRouterEntity.appCode = appCode
    projectRouterEntity.path = path
    projectRouterEntity.envId = envId
    projectRouterEntity.envResourceId = envResourceId
    projectRouterEntity.port = port

    // projectPubEntity对象赋值
    projectPubEntity.projectId = projectId
    projectPubEntity.appName = appName
    projectPubEntity.appId = id
    projectPubEntity.appType = appType
    projectPubEntity.appCode = appCode
    projectPubEntity.envCode = envCode
    projectPubEntity.envName = envName
    projectPubEntity.envId = envId
    projectPubEntity.pubServerId = pubServerId
    projectPubEntity.pubServerName = pubServerName
    projectPubEntity.pubVersion = pubVersion
    projectPubEntity.pubDesc = pubDesc
    projectPubEntity.pubImage = pubImage
    projectPubEntity.pubAccount = pubAccount
    projectPubEntity.pubPassword = pubPassword
    projectPubEntity.buildBranch = 'none'
    projectRouterEntity.envResourceId = envResourceId
    projectPubEntity.envResourceName = envResourceName
    // console.log(projectAppEntity)
    // console.log(projectRouterEntity)
    // console.log(projectPubEntity)

    // 把数据处理成请求格式
    let data = { projectAppEntity, projectRouterEntity, projectPubEntity, defalutPort: defalutPort, restart: restart }
    // console.log(JSON.stringify(data))

    // 返回一个处理好的请求体对象
    return data
}

/**
 * 快速发布弹窗
 * @param {String} appId 应用ID
 * @param {Number} appType 应用类型
 * @param {String} mdCode 模型编码
 */
function publish_dialog (appId, appType, mdCode) {
    let appTypeDesc = appType === 0 ? '前端' : '后端'
    let btnMsg = '申请' + appTypeDesc + '资源'
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
                name: btnMsg,
                type: 'primary',
                icon: 'placeFile',
                click: function () {
                    this.pushPage({
                        path: `/module/stander/edit/fbpt_env_resource/add`,
                        title: '新增',
                        query: {
                            type: appType
                        }
                    })
                }
            },
            {
                name: '下载Dockerfile模板',
                type: 'primary',
                icon: 'download',
                click: function () {
                    if (appType === 0) {
                        window.open(`${this.BASEURL}/project/project/projectapplist/downloadAttach?attachId=8a74805685862a560185862d275d0000`, '_blank')
                    } else if (appType === 1) {
                        window.open(`${this.BASEURL}/project/project/projectapplist/downloadAttach?attachId=8a74805685862a560185862ecf880001`, '_blank')
                    } else {
                        this.$message.error('无效的应用类型')
                    }
                }
            },
            {
                name: '发布应用',
                icon: 'save',
                type: 'primary',
                loading: false,
                click: function (_btnObj) {
                    let modelForm = this.getDialogConObj(mdCode, 4) // 获取模型表单页面作用域，id为dialog属性id的值
                    modelForm.validateForm().then(formObj => {
                        this.$confirm('确定发布吗？', '发布', {
                            confirmButtonText: '确定',
                            cancelButtonText: '取消',
                            type: 'warning'
                        }).then(async () => {
                            // console.log(res)
                            let requestData = {} // 接收处理好的请求体对象
                            let postRes = {} // 接收请求返回的数据
                            // 检查是否与上次发布的资源一致
                            let lastResource =  S_Storage.getItem('LASTRESOURCE-' + formObj.id)
                            let nowResource =  S_Storage.getItem('RESOURCE-' + formObj.envResourceId)
                            let lastEnvName =  S_Storage.getItem('LASTENV-' + formObj.id)

                            if (nowResource == null) {
                                nowResource = formObj.envResourceId
                            }
                            let lastEnvId = S_Storage.getItem('ENVNAME' + lastEnvName)
                            if (lastResource !== nowResource && lastResource !== '' && lastEnvId === formObj.envId) {
                                this.$confirm(`检查发现与上一次发布资源不一致.继续发布会导致清空本应用之前的资源,是否确定`, '资源使用变更确定', {
                                    confirmButtonText: '确定',
                                    cancelButtonText: '取消',
                                    type: 'warning'
                                }).then(async () => {
                                    _btnObj.loading = true // 检验成功，准备请求，设置加载状态
                                    if (mdCode === 'CODE_QUICK_RELEASE') {
                                        // 源码发布
                                        // 将数据处理成请求体需要的格式
                                        requestData = await code_publish.call(this, formObj)
                                        // 源码快速发布请求
                                        postRes = await Code_Quick_Release(requestData)
                                    } else if (mdCode === 'PRODUCT_QUICK_RELEASE') {
                                        // 源码发布
                                        // 将数据处理成请求体需要的格式
                                        requestData = await product_publish(formObj)
                                        // 源码快速发布请求
                                        postRes = await Product_Quick_Release(requestData)
                                    }
                                    if (postRes.hasOk) {
                                        // 完成快速发布自动跳转发布详细页面
                                        this.pushPage({
                                            path: '/releaseDetail',
                                            title: '发布详情',
                                            query: { id: postRes.message, appId: requestData.projectRouterEntity.appId }
                                        })
                                        this.$message.success('发布成功')
                                    } else {
                                        this.$message.error(postRes.message)
                                    }
                                    _btnObj.loading = false // 重置按钮加载状态，实际场景中应该在保存接口结束后
                                    // 关闭弹窗表单
                                    this.$dialog.close()
                                })
                            } else {
                                _btnObj.loading = true // 检验成功，准备请求，设置加载状态
                                if (mdCode === 'CODE_QUICK_RELEASE') {
                                    // 源码发布
                                    // 将数据处理成请求体需要的格式
                                    requestData = await code_publish.call(this, formObj)
                                    // 源码快速发布请求
                                    postRes = await Code_Quick_Release(requestData)
                                } else if (mdCode === 'PRODUCT_QUICK_RELEASE') {
                                    // 源码发布
                                    // 将数据处理成请求体需要的格式
                                    requestData = await product_publish(formObj)
                                    // 源码快速发布请求
                                    postRes = await Product_Quick_Release(requestData)
                                }
                                if (postRes.hasOk) {
                                    // 完成快速发布自动跳转发布详细页面
                                    this.pushPage({
                                        path: '/releaseDetail',
                                        title: '发布详情',
                                        query: { id: postRes.message, appId: requestData.projectRouterEntity.appId }
                                    })
                                    this.$message.success('发布成功')
                                } else {
                                    this.$message.error(postRes.message)
                                }
                                _btnObj.loading = false // 重置按钮加载状态，实际场景中应该在保存接口结束后
                                // 关闭弹窗表单
                                this.$dialog.close()
                            }
                        })
                    })
                }
            }
        ],
        isView: false
    })
}

export {
    code_publish,
    publish_dialog
}