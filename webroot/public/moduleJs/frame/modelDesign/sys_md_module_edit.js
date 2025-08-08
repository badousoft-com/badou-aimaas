import { Save_URL } from '@/api/frame/common'
import { Save_Field, Save_Link, Save_Flow, Save_Button_Promission } from '@/api/frame/desinger/field'
import { EventBus } from '@/service/event-bus'
import { Has_Value } from '@/utils'
import { Deep_Clone } from '@/utils/clone'
export default {
    buttons: [{
        id: 'back',
        name: '返回',
        icon: 'back',
        type: 'danger',
        click: function (btnObj) {
            // alert('提醒保存内容')
            this.$router.go(-1)
        }
    }, {
        id: 'save',
        name: '保存',
        icon: 'save',
        type: 'primary',
        loading: false,
        click: function (btnObj) {
            // 获取【页面配置】组件作用域
            let pageRef = this.getCurrentTabScope()
            // 当前tab的mdCode，用于判断当前保存执行哪个事件
            let tab_mdCode = pageRef.mdCode
            // 定义页面变量名称
            let busDataName = 'bus_data'
            switch (tab_mdCode) {
                case 'sys_md_module':
                    // this: bd-module-edit组件作用域
                    let formRef = this.getFormRef()
                    formRef.validateForm().then((res) => {
                        btnObj.loading = true
                        let flowId = res.flowId
                        let url = Save_URL(this.mdCode)
                        this.post(url, res).then(res => {
                            if (res.hasOk) {
                                /**
                                 * 将动态引入的当前页面作用域挂到tab下，以便外部组件访问其作用域
                                 * eventBus.$emit(channel, dataName)
                                 * @param {String} channel: 指定事件名称
                                 * @param {String} dataName: 指定数据存放在父级页面变量的名称
                                 */
                                EventBus.$emit("setFlowData", busDataName)
                                let refreshByFlowId = pageRef[busDataName]?.refreshByFlowId
                                // 新增的时候会没有 setFlowData
                                if (typeof refreshByFlowId === 'function') {
                                    refreshByFlowId(flowId)
                                }
                                this.$message.success('【模型管理数据】保存成功')
                                let resDetail = res.bean?.returnDetails
                                // 处理tab情况，同时满足以下条件时：保存表单不返回上一页，新增时子tab不展示，保存数据后才展示
                                //     1. 当前为新增表单状态
                                //     2. 接口保存后返回数据存在值
                                //     3. 存在关联tab
                                if (!this.detailId &&
                                    resDetail &&
                                    this.childTab &&
                                    this.childTab.constructor === Array &&
                                    this.childTab.length > 0) {
                                    // 获取[id：详情数据id]，[mdCode:模型编码]
                                    let { id, data: { mdCode } } = resDetail || { data: {} }
                                    if (mdCode && id) {
                                        // 替换当前路由，使用保存后最新的详情id进行渲染
                                        this.$router.replace({
                                            name: 'standerEdit',
                                            params: { mdCode, id }
                                        })
                                    } else {
                                        // 默认情况：返回上一页
                                        this.$router.go(-1)
                                    }
                                }
                            } else {
                                this.$message.error(`保存失败！${res.message}`)
                            }
                            btnObj.loading = false
                            if (typeof btnObj.after === 'function') {
                                btnObj.after.call(this)
                            }
                            this.customSetting.isChangeInfo[pageRef.mdCode] = false
                        }).catch(() => {
                            btnObj.loading = false
                        })
                    })
                    break
                // 模型字段配置
                case 'sys_module_field':
                    let fieldList = this.getCurrentTabScope()?.$refs.list?.tableData
                    // 开启按钮加载状态
                    btnObj.loading = true
                    Save_Field({
                        moduleId: this.detailId,
                        fields: fieldList && JSON.stringify(fieldList),
                        isAll: false, // 2021-04-12修改，修复保存字段配置时会清空页面配置bug
                        saveForm: false // 2021-04-12添加，修复保存字段配置时会清空页面配置bug
                    }).then(res => {
                        if (res?.hasOk) {
                            this.$message.success('保存成功')                            
                            // ============= 加载完成后：刷新页面配置字段 ==========
                            let pageSettingName = 'pageSetting'
                            EventBus.$emit("setPageSetting", pageSettingName)
                            // 获取指定变量
                            let loadFieldFn = pageRef?.[pageSettingName]?.loadField
                            if (typeof loadFieldFn === 'function') {
                                // 刷新页面配置的字段
                                loadFieldFn()
                            }
                        } else {
                            this.$message.error('保存失败')
                        }
                    }).finally(() => {
                        // 关闭按钮加载状态
                        btnObj.loading = false
                        // 刷新列表数据，确保每条数据id更新
                        this.getCurrentTabScope()?.$refs?.list?.$refs?.moduleList?.listPageRef().init()
                    })
                    break
                // 当前【页面配置】模块
                case 'sys_module_form':
                    if (!pageRef) return
                    // 发布事件$emit:参数说明如下
                    /**
                     * 将动态引入的当前页面作用域挂到tab下，以便外部组件访问其作用域
                     * eventBus.$emit(channel, dataName)
                     * @param {String} channel: 指定事件名称
                     * @param {String} dataName: 指定数据存放在父级页面变量的名称
                     */
                    let pageSettingName = 'pageSetting'
                    pageRef.eventBus.$emit("setPageSetting", pageSettingName)
                    // 获取字段数组数据
                    let result = Deep_Clone(pageRef[pageSettingName]?.fieldsList)
                    // 获取【页面配置】页面所在作用域
                    let _pageSettingRef = pageRef.extendScope[`mountId${pageRef.index}`]
                    // 执行方法，转化字段数据中的附件字段数据
                    _pageSettingRef && _pageSettingRef.transformSingleAttach().save(result)
                    let params = {
                        moduleId: pageRef.mainId,
                        fields: JSON.stringify(result),
                        isAll: false
                    }
                    btnObj.loading = true
                    Save_Field(params).then(res => {
                        if (res.hasOk) {
                            this.$message.success('【页面配置数据】保存成功')
                        } else {
                            this.$message.error(`保存失败！${res.message || ''}`)
                        }
                        btnObj.loading = false
                        if (typeof btnObj.after === 'function') {
                            btnObj.after.call(this)
                        }
                        this.customSetting.isChangeInfo[pageRef.mdCode] = false
                    }).catch(err => {
                        btnObj.loading = false
                        this.$message.error(`保存失败！`)
                    })
                    break
                case 'sys_module_link':
                    if (!pageRef) return
                    // 发布事件$emit:参数说明如下
                    /**
                     * 将动态引入的当前页面作用域挂到tab下，以便外部组件访问其作用域
                     * eventBus.$emit(channel, dataName)
                     * @param {String} channel: 指定事件名称
                     * @param {String} dataName: 指定数据存放在父级页面变量的名称
                     */
                    let linkName = 'linkData'
                    EventBus.$emit("setLinkData", linkName)
                    // 获取一次性验证所有表单的函数
                    let checkLinkForm = pageRef[linkName]?.checkFormAll
                    btnObj.loading = true
                    checkLinkForm().then(res => {
                        let link_params = {
                            moduleId: pageRef.mainId,
                            links: JSON.stringify(res),
                        }
                        Save_Link(link_params).then(res => {
                            if (res.hasOk) {
                                this.$message.success('【关联关系数据】保存成功')
                            } else {
                                this.$message.error(`保存失败！${res.message || ''}`)
                            }
                            btnObj.loading = false
                            if (typeof btnObj.after === 'function') {
                                btnObj.after.call(this)
                            }
                            this.customSetting.isChangeInfo[pageRef.mdCode] = false
                        }).catch(err => {
                            btnObj.loading = false
                            this.$message.error(`保存失败！`)
                        })
                    }).catch(() => {
                        btnObj.loading = false
                        this.$message.error(`请先填写完整信息！`)
                    })
                    break
                case 'sys_module_flow':
                    if (!pageRef) return
                    /**
                     * 将动态引入的当前页面作用域挂到tab下，以便外部组件访问其作用域
                     * eventBus.$emit(channel, dataName)
                     * @param {String} channel: 指定事件名称
                     * @param {String} dataName: 指定数据存放在父级页面变量的名称
                     */
                    let flowName = 'flowData'
                    EventBus.$emit("setFlowData", flowName)
                    // 获取一次性验证所有表单的函数
                    let checkFlowForm = pageRef[flowName]?.checkFormAll
                    btnObj.loading = true
                    checkFlowForm().then(res => {
                        let flow_params = {
                            moduleId: pageRef.mainId,
                            flows: JSON.stringify(res),
                        }
                        Save_Flow(flow_params).then(save_res => {
                            if (save_res.hasOk) {
                                this.$message.success('【环节处理事件】保存成功')
                            } else {
                                this.$message.error(`保存失败！${res.message || ''}`)
                            }
                            btnObj.loading = false
                            if (typeof btnObj.after === 'function') {
                                btnObj.after.call(this)
                            }
                            this.customSetting.isChangeInfo[pageRef.mdCode] = false
                        }).catch(err => {
                            btnObj.loading = false
                            this.$message.error(`保存失败！`)
                        })
                    }).catch(() => {
                        btnObj.loading = false
                        this.$message.error(`请先填写完整信息！`)
                    })
                    break
                // 按钮权限tab
                case 'sys_btn_promission':
                    if (!pageRef) return
                    let btnPowerName = 'btnPowerData'
                    EventBus.$emit("setBtnPowerData", btnPowerName)
                    // 获取一次性验证所有表单的函数
                    let checkBtnPowerForm = pageRef[btnPowerName]?.checkForm
                    btnObj.loading = true
                    checkBtnPowerForm().then(res => {
                        // 将值里边的null转化为空字符串
                        let buttonArray = res.map(i => {
                            let itemValue = {}
                            Object.keys(i).forEach(key => {
                                itemValue[key] = Has_Value(i[key]) ? i[key] : ''
                            })
                            return itemValue
                        })
                        let params = {
                            moduleId: pageRef.mainId,
                            buttonArray: JSON.stringify(buttonArray)
                        }
                        Save_Button_Promission(params).then(saveRes => {
                            btnObj.loading = false
                            if (saveRes.hasOk) {
                                this.$message.success('【按钮权限控制配置】保存成功')
                                // 重新请求数据
                                pageRef[btnPowerName].loadData()
                            } else {
                                this.$message.error(`保存失败！${res.message || ''}`)
                            }
                            btnObj.loading = false
                            if (typeof btnObj.after === 'function') {
                                btnObj.after.call(this)
                            }
                            this.customSetting.isChangeInfo[pageRef.mdCode] = false
                        }).catch(err => {
                            btnObj.loading = false
                            this.$message.error(`保存失败！`)
                        })
                    }).catch(() => {
                        btnObj.loading = false
                        this.$message.error(`请先填写完整信息！`)
                    })
                    break
                default:
                    // do something default
            } 
        }
    }
    // 先注释掉
    // {
    //     id: 'sync',
    //     name: '同步表结构',
    //     icon: 'refresh',
    //     type: 'primary',
    //     loading: false,
    //     click: function (btnObj) {

    //     }
    // }, 
   ],
    afterModuleJSON: function (module) {
        let childTab = module.childTab
        let exeTab = [{
                showTab: 1,
                showType: 4,
                moduleName: '页面配置',
                module: 'sys_module_form',
                pageUrl: 'common/modelDesigner/PageSetting.vue'
            }, {
                showTab: 1,
                showType: 4,
                moduleName: '关联关系',
                module: 'sys_module_link',
                pageUrl: 'common/modelDesigner/Relation.vue'
            }, {
                showTab: 1,
                showType: 4,
                moduleName: '环节处理事件',
                module: 'sys_module_flow',
                pageUrl: 'common/modelDesigner/FlowHandle.vue'
            }, {
                showTab: 1,
                showType: 4,
                moduleName: '按钮权限控制',
                module: 'sys_btn_promission',
                pageUrl: 'common/modelDesigner/ButtonPermission.vue'
            }
        ]
        childTab.push(...exeTab)
        return module
    },
    
    /**
     * 字段自定义change事件
     * @param {*} fielaName 字段名
     * @param {*} value change后对应值
     */
    fieldChange: function (fielaName, value) {
        this.customSetting.isChangeInfo[this.mdCode] = true
    },
    /**
     * @description: 有父子关系的tab切换前执行的事件（必要时需要使用同步）
     * @param {*} activeName：前往tab的name
     * @param {*} oldActiveName：即将离开tab的name
     * @return {*}：false 不切换
     * @return {*}：true 允许切换
     */ 
    beforeTabLeave: function (activeName, oldActiveName) {
        return new Promise((resolve, reject) => {
            // 定义模块域
            let pageRef = null
            // 判断是否主表所在Tab
            if (oldActiveName === 'mainTab_0') {
                // 是则返回主表域
                pageRef = this.$refs[this.refName]
            } else {
                pageRef = this.$refs[this.childRefName + oldActiveName.replace('mainTab_', '')]?.[0]
            }
            let isChanged = Boolean(this.customSetting?.isChangeInfo?.[pageRef.mdCode])
            if (isChanged) {
                this.$confirm(`是否需要保存【${this.title}】模块数据`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    distinguishCancelAndClose: true,
                }).then(() => {
                    let btnObj = this.btnList[0]
                    btnObj.after = function () {
                        this.activeTabName = activeName
                        this.customSetting.isChangeInfo[pageRef.mdCode] = false
                    }
                    btnObj.click.call(this, btnObj)
                    reject(false)
                }).catch(action => {
                    if (action === 'cancel')  {
                        resolve(true)
                    } else {
                        reject(false)
                    }
                })
            } else {
                resolve(true)
            }
        })
    },
    // tab 是否存在正在修改，但未保存的状态，key为当前tab的mdCode
    isChangeInfo: {
        sys_md_module: false, // 模型管理
        sys_module_field: false, // 字段配置
        sys_module_form: false, // 页面配置
        sys_module_link: false, // 关联关系
        sys_module_flow: false, // 环节处理事件
    }
}