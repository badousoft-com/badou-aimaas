import router from '@/router'
import { Get_Routes } from '@/api/frame/flow'
import { Start_Submit, Next_Submit } from '@/api/frame/flow'
import { Get_View_Url, Start_Option_And_Dealer, Next_Option_And_Dealer } from '@/api/frame/flow'
import { Variable_Replaced_String } from '@/utils/index.js'
import GlobalConst from '@/service/global-const'
import { Is_Function, Is_Object } from '@/utils/data-type'
import { Event_Expand } from '@/service/event-expand'
// 获取路由按钮
function Get_Flow_Routes ({ flowId = null, worklistId = null }) {
    return new Promise((resolve, reject) => {
        Get_Routes({
            flowId,
            worklistId
        }).then(res => {
            if (!(res && res.hasOk)) return
            let _routes = res.bean?.routes
            if (_routes && _routes.length > 0) {
                // 将路由按钮对象组装成按钮组件需要的属性
                let _addResult = _routes.map(i => {
                    return {
                        name: i.btnName,
                        icon: 'nextStep',
                        type: 'primary',
                        loading: false,
                        click: null,
                        ...i,
                        id: i.code || i.routeId,
                    }
                })
                resolve(_addResult)
            } else {
                resolve([])
            }
        }).catch(err => {
            resolve([])
        })
    })
}

// 获取下一步按钮对象
function Get_Next_Btn () {
    return {
        id: 'nextStep',
        name: '下一步',
        icon: 'nextStep',
        type: 'warning',
        click: function (btnObj) {
            btnObj.loading = true
            this.$forceUpdate()
            let nextFn = (params = {}) => {
                Flow_Verify.call(this, btnObj, ({ boId, obj }) => {
                    this.$pageDialog.init({
                        title: '下一步', // 弹窗标题
                        pageUrl: 'module/flow/part/FlowRouterTabs/index', // 弹窗显示的页面路径，会拼接src/views
                        // config: _config,
                        worklistId: this.worklistId, // 是否为发起环节
                        boId,
                        outScope: this, // 默认这样写即可，不要问
                        mainScope: this.pageScope,
                        flowBtnList: this.flowBtnList,
                        elseAttr: { opinion: params },
                        ...this.$attrs,
                        handlerList: [
                            {
                                id: 'save', name: '提交', icon: 'save', type: 'primary', loading: false, click: function (itemObj) {
                                    this.pageScope.submit(itemObj)
                                }
                            }
                        ]
                    })
                    setTimeout(() => {
                        btnObj.loading = false
                        this.$forceUpdate()
                    }, 300)
                })
            }
            let getOpinion = this.pageScope?.$refs?.flowOpinion?.submit
            if (typeof getOpinion === 'function') {
                getOpinion().then(res => {
                    nextFn(res)
                }).catch(() => {
                    btnObj.loading = false
                    this.$forceUpdate()
                })
            } else {
                nextFn()
            }
        }
    }
}

// 获取关闭按钮对象
function Get_Close_Btn () {
    return {
        id: 'back',
        name: '返回',
        icon: 'back',
        type: 'danger',
        priority: 0,
        click: function () {
            router.go(-1)
        }
    }
}

/**
 * 流程审核
 * @param {Object} btnObj 按钮对象
 */
function Flow_Verify (btnObj, fn) {
    if (!Is_Function(fn)) fn = Send_Flow_Verify
    // 审核内容为可编辑时
    if (!this.isView) {
        // 获取保存函数
        let _saveFun = this.flowRouteBtnRef?._saveFun
        if (!_saveFun) {
            let wrongTip = 'flow-route-btn组件需要传入 saveForm 属性'
            console.error(wrongTip)
            this.$message.warning(wrongTip)
            return
        }
        // 保存节点触发事件
        let saveEnv = {}
        if (Is_Object(arguments[2])) {
            Object.keys(arguments[2]).forEach(key => {
                saveEnv[key.replace('Save', '')] = arguments[2][key]
            })
        }
        // // 开启按钮加载动画
        btnObj.loading = true
        _saveFun.call(this, btnObj, saveEnv).then(({status, boId, opinion}) => {
            if (status) {
                if (!boId) {
                    this.$message.warning(`传入的boId值无效`)
                    btnObj.loading = false
                    this.$forceUpdate()
                    return
                }
                // 执行流程审核逻辑
                fn.call(this, { boId, btnObj, opinion }, arguments[2])
            } else {
                btnObj.loading = false
                this.$forceUpdate()
            }
        }).catch(() => {
            // 开启按钮加载动画
            btnObj.loading = false
        })
    } else {
        // 执行流程审核逻辑
        // 审核内容为查看状态时，不可编辑，不需要走上面的保存表单逻辑
        fn.call(this, { boId: this.detailId, btnObj }, arguments[2])
    }
}

/**
 * @description: 打开流程审核弹窗
 * @param {Object} config：调用弹窗填写意见与确认下一环节处理人使用的参数
 * @param {Object} opinionInfo：审批意见（参数实例：{ opinion: '流程审批意见' }）
 * @param {Function} submitFn：流程提交方法
 * @param {Object} hookEvents：各个节点的事件钩子函数集合
 */
async function handlerVerifyDialog ({btnObj, config, opinionInfo, submitFn}, hookEvents) {
    // 获取流程按钮组件作用域
    let that = this.flowRouteBtnRef
    // 弹窗配置
    let dialogConfig = {
        title: btnObj.name || '流程提交对话框', // 弹窗标题
        config,
        isCreateNode: !that.worklistId, // 是否为发起环节
        pageUrl: 'module/flow/part/SubmitFlow', // 弹窗显示的页面路径，会拼接src/views
        outScope: that, // 默认这样写即可，不要问
        opinion: opinionInfo,
    }
    // 执行打开弹窗前的钩子，此过程更改传入弹窗组件的参数
    await Event_Expand.call(this, 'beforeDialog', hookEvents, dialogConfig)
    // 弹窗填写审核意见与选择下一环节处理人
    this.$pageDialog.init({
        ...dialogConfig,
        handlerList: [ // 操作的按钮列表
            {
                id: 'save', name: '提交', icon: 'save', type: 'primary', loading: false, click: async function (itemObj) {
                    // 执行弹窗表单校验前的钩子函数
                    await Event_Expand.call(this, 'beforeDialogValidate', hookEvents)
                    // 检验弹窗表单并返回表单值
                    this.pageScope.$refs.submitFlow.validateForm().then(async (submitRes) => {
                        let submitParams = { ...submitRes, ...config }
                        // 执行审批前的钩子，此过程可更改审批提交参数
                        await Event_Expand.call(this, 'beforeSubmit', hookEvents, submitParams)
                        // 设置按钮状态-加载中
                        itemObj.loading = true
                        // 发起流程
                        submitFn(submitParams).then(async res => {
                            if (res && res.hasOk) {
                                let successTip = '操作成功'
                                if (!that.worklistId) {
                                    successTip += '，已发起审核'
                                } else {
                                    if (submitRes && submitRes.handler) {
                                        successTip += '，已提交下一环节审核'
                                    }
                                }
                                // 执行提交审批后的钩子
                                await Event_Expand.call(this, 'afterSubmit', hookEvents, res)
                                this.$message.success(successTip)
                                this.$pageDialog.close()
                                this.outScope.$router.go(-1)
                            } else {
                                this.$message.error(`操作失败！${res?.message || ''}`)
                            }
                            // 设置按钮状态
                            itemObj.loading = false
                        })
                    })
                }
            }
        ]
    })
}

// 获取下一环节审批人与提交审核（不弹出审批弹窗）
function getHandlerAndSubmit ({btnObj, config, opinionInfo, submitFn}, hookEvents) {
    // 获取流程按钮组件作用域
    let that = this.flowRouteBtnRef
    // 根据是否为发起环节确定使用的接口
    let handlerRequest = !that.worklistId ? Start_Option_And_Dealer : Next_Option_And_Dealer
    // 获取默认环节处理人
    btnObj.loading = true
    handlerRequest(config || {}).then(async res => {
        if (!res || !res.hasOk || !res.bean) {
            this.$message.error(res.message || '获取处理人失败！请联系系统管理员')
            btnObj.loading = false
            return
        }
        if (res && res.hasOk) {
            // 获取下一环节处理人字段对象
            let handlers = res?.bean?.routeSelect?.handlers || []
            if (!handlers.length) {
                this.$message.error(res.message || '此流程没有配置默认处理人！请联系系统管理员处理')
                btnObj.loading = false
                return
            }
            let handlerStr = handlers.map(i => i.id).join(',')
            let params = {
                ...config,
                handler: handlerStr,  // 下一环节处理人数据
                opinion: opinionInfo?.opinion || '',  // 流程意见
            }
            // 获取是否需要抄送的状态
            let { needSpread, spreadHandlerList } = res.bean
            // 传阅人数据
            if (needSpread) {
                params.spreadIds = (spreadHandlerList?.handlers || []).map(i => i.id).join(',')
            }
            // 执行审批前的钩子，此过程可更改审批提交参数
            await Event_Expand.call(this, 'beforeSubmit', hookEvents, params)
            // 提交审批
            submitFn(params).then(async resp => {
                if (!resp?.hasOk) {
                    btnObj.loading = false
                    this.$message.warning(resp.message || '操作失败！请联系系统管理员')
                    return
                }
                // 执行提交审批后的钩子
                await Event_Expand.call(this, 'afterSubmit', hookEvents, resp)
                this.$message.success(`${btnObj.name}成功`)
                this.$router.go(-1)
            })
        } else {
            btnObj.loading = false
        }
    })
}

// 执行流程审核逻辑
function Send_Flow_Verify ({boId, btnObj, opinion}) {
    // 获取流程按钮组件作用域
    let that = this.flowRouteBtnRef
    // 调用弹窗填写意见与确认下一环节处理人使用的参数
    let _config = !that.worklistId ? {
        boId, // 业务实例id
        routeId: btnObj.routeId, // 路由id
    } : {
        worklistId: that.worklistId, // 待办id
        routeId: btnObj.routeId, // 路由id
    }

    // 流程提交接口
    let submitRequest = !that.worklistId ? Start_Submit : Next_Submit
    // 流程审核方法
    let verifyFn = String(btnObj[GlobalConst.flow.isNeedDialogKey]) === '0' ? getHandlerAndSubmit : handlerVerifyDialog
    let nextFn = (params = {}) => {
        setTimeout(() => {
            verifyFn.call(this, {
                btnObj,  // 按钮对象
                config: _config,  // 调用弹窗填写意见与确认下一环节处理人使用的参数
                opinionInfo: params,  // 审批意见（参数实例：{ opinion: '流程审批意见' }）
                submitFn: submitRequest, // 流程提交接口api
            }, arguments[1])
        }, 0)
    }
    let getOpinion = this.$refs?.flowOpinion?.submit
    if (typeof getOpinion === 'function') {
        getOpinion().then(res => {
            nextFn(res)
        }).catch(() => {
        })
    } else {
        nextFn(opinion)
    }
}

/**
 * 处理待办与已办数据事件
 * @param {Object} btnObj 按钮对象
 * @returns
 */
function Deal_Work_Event (btnObj) {
    // 获取选中列表数据
    let selection = this.getSelection()
    if (!selection) return
    if (selection.length === 0) {
        this.$message.warning('请先选择需要处理的数据!')
        return
    }
    if (selection.length > 1) {
        this.$message.warning('请选择一条数据进行处理!')
        return
    }
    let _flowItem = selection[0]
    let {
        nodeId, // 获取环节id：nodeId
        // 状态码值，如下
        //      1-待办
        //      2-已办
        //      4-待办:超时未处理
        //      8-待办:超时已处理
        //      16-未阅
        //      32-已阅
        status,
    } = _flowItem
    // 获取跳转的视图地址接口
    Get_View_Url(nodeId).then(res => {
        if (!(res && res.hasOk)) return
        let {
            spreadUrl, // 待办URL
            doneUrl, // 已办URL
            viewUrl, // 查阅URL
            // previewUrl // 预览URL eslint检测：变量未使用
        } = res.bean || {}
        // 定义即将跳转的页面地址
        let _pageUrl = null
        // 跳转title前缀
        let preTitle = '查看'
        // 根据状态码更新即将跳转的视图地址
        switch (status) {
            case 1: // 待办
                _pageUrl = viewUrl
                preTitle = '审核'
                break
            case 2: // 已办
                _pageUrl = doneUrl
                break
            case 16: // 待阅
            case 32: // 已阅
                // 这里说明下待阅与已阅都会使用同一个页面地址，对应的区别是通过state进行判断处理
                //     进入待阅页面时，会请求接口将待阅类型数据转成已阅
                //     进行已阅页面时，不需要做什么处理
                _pageUrl = spreadUrl
                break
            // previewUrl: 预览地址咱不明确
            default:
                _pageUrl = null
        }
        if (_pageUrl) {
            // 将点击的数据项通过session进行保存传递
            // 不使用query是因为参数众多，不确定传谁，全部传地址的话过长
            // 不使用param是因为刷新之后参数就会丢失
            // S_Storage.setObj('flowItem', _flowItem)
            // this.$nextTick(() => {
            //     this.$router.push({
            //         // 将待办URL中的变量转成对应数据
            //         path: Variable_Replaced_String(_pageUrl, _flowItem)
            //     })
            // })
            this.pushPage({
                // 将待办URL中的变量转成对应数据
                path: Variable_Replaced_String(_pageUrl, _flowItem),
                title: `${preTitle}流程`,
                storage: {
                    flowItem: _flowItem
                }
            })
        } else {
            this.$message.warning(`使用的视图地址无效,无法跳转`)
        }
    })
}

export {
    Get_Flow_Routes,
    Get_Next_Btn,
    Get_Close_Btn,
    Flow_Verify,
    Deal_Work_Event,
}