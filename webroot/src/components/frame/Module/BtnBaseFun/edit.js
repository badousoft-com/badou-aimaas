import { Event_Expand } from '@/service/event-expand'
import GlobalConst from '@/service/global-const'
import { Is_Obj_Different } from '@/utils/object'
import { Save_Module_Form } from '@/api/frame/common.js'

/**
 * 保存
 * @param {Object} btnObj 按钮对象
 */
async function Save (btnObj) {
    // this: bd-module-edit组件作用域       
    let formRef = this.getFormRef()
    // 定义通用参数，用于注入事件中使用
    let _eventExpandParams = [formRef]
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'beforeValidate', arguments[1], ..._eventExpandParams)
    // 定义主表单保存方法
    let _saveForm = () => {
        return new Promise((resolve) => {
            // 校验主表单
            formRef.validateForm().then(async (res) => {
                // 设置按钮状态-加载中
                btnObj.loading = true
                // 注入模型js中事件节点
                await Event_Expand.call(this, 'before', arguments[1], res, ..._eventExpandParams)
                // 提交接口
                Save_Module_Form(res, this._saveUrl).then(saveRes => {
                    if (saveRes?.hasOk) {
                        this.$message.success('保存成功')
                        // 按钮取消加载状态
                        btnObj.loading = false
                        resolve({ status: true, res: saveRes })
                    } else {
                        this.$message.error(`保存失败！${saveRes?.message || ''}`)
                        /// 按钮取消加载状态
                        btnObj.loading = false
                        resolve({ status: false, res: saveRes })
                    }
                }).catch(() => {
                    btnObj.loading = false
                })
            }).catch(_ => {
                console.error(_)
                resolve({ status: false })
            })
        })
    }
    // 判断是否存在关联模块
    if (!(this._showLinkData && this._showLinkData.length > 0)) {
        // 不存在关联模块，表单保存成功后直接返回上一页
        _saveForm.call(this).then(async ({status, res}) => {
            // 注入模型js中事件节点
            if (status) {
                await Event_Expand.call(this, 'after', arguments[1], ..._eventExpandParams, res)
                // 默认情况：返回上一页
                this.$router.go(-1)
            }
        })
    } else {
        // 存在关联模块
        // 1. 新增-直接保存-保存后刷新使用最新id更新路由
        // 2. 编辑-校验主表-校验关联表单-提交主表-提交关联表单

        // 判断是否为表单编辑状态/新增状态
        if (!this.detailId) {
            // 当前为新增表单，保存后需要刷新路由地址的id为保存后的数据id
            _saveForm.call(this).then(async ({ status, res}) => {
                if (!status) return
                // 注入模型js中事件节点
                await Event_Expand.call(this, 'after', arguments[1], ..._eventExpandParams, res)
                // 获取保存接口返回详情
                let resDetail = res.bean?.returnDetails
                if (!resDetail) return
                // 处理tab情况，同时满足以下条件时：保存表单不返回上一页，新增时子tab不展示，保存数据后才展示
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
            })
        } else {
            // 当前为关联表单-编辑状态，除了需要保存主表外，还需要保存其他的关联表单
            // 获取主表-数据，触发主表校验
            this.getFormRef().validateForm().then(res => {
                // 定义获取主表变量
                let _mainForm = res
                // 定义关联表单的结果集-承诺列表
                let _linkFormPromiseList = []
                // 获取关联表单-数据，触发关联表-表单校验
                this.allLinkEditForm.forEach(i => {
                    // 添加获取表单数据
                    _linkFormPromiseList.push(i.formScope.validateForm())
                })
                // 等待所有关联表-表单校验结束
                Promise.allSettled(_linkFormPromiseList).then(async (linkFormArr) => {
                    // 找出校验失败的第一个关联表单。这里只找出首个，设置页面滚动到视图可见校验报错的信息处，多个的话没法同时管理
                    let _failIndex = linkFormArr.findIndex(i => i.status !== 'fulfilled')
                    // 若存在校验失败的关联表单
                    if (~_failIndex) {
                        let { tabName, tabValue, formScope } = this.allLinkEditForm[_failIndex]
                        // 判断是否Tab结构下的模块出现字段校验报错，则需要先切换到对应tab下，再滚动到错误字段校验
                        if (tabName) {
                            // 设置活跃Tab为校验失败的表单所处的Tab
                            this[tabName] = tabValue
                            // 切换tab后，滚动至表单校验报错的字段处，给用户查看
                            this.$nextTick(() => {
                                formScope.scrollFailFormItem()
                            })
                        } else {
                            // 滚动到错误字段校验处
                            formScope.scrollFailFormItem()
                        }
                        // 存在表单校验错误的场景，停止逻辑
                        return
                    }
                    // 设置按钮状态-加载中
                    btnObj.loading = true
                    // 注入模型js中事件节点
                    await Event_Expand.call(this, 'before', arguments[1], _mainForm, ..._eventExpandParams)
                    // 校验完毕后，获取所有关联表单数据完毕，开始提交保存
                    // 先提交主表数据
                    this.post(this._saveUrl, _mainForm).then(async (mainRes) => {
                        let _status = mainRes?.hasOk
                        if (_status) {
                            // 注入模型js中事件节点
                            await Event_Expand.call(this, 'after', arguments[1], ..._eventExpandParams, mainRes)
                            // 先判断有多少表单发生了变更
                            let _linkFormPromise = []
                            /**
                             * 定义关联表单提交的函数
                             * @param {Object} form 表单值对象数据
                             * @param {Object} scope moduleEdit/index.vue页面所在作用域
                             */
                            let _postLinkForm = (form, scope) => {
                                return new Promise(async (resolve) => {
                                    let _this = scope
                                    // 提交接口
                                    this.post(_this._saveUrl, form).then(async res => {
                                        if (res?.hasOk) {
                                            resolve({ status: true, res })
                                        } else {
                                            this.$message.error(`保存失败！${res?.message || ''}`)
                                            resolve({ status: false })
                                        }
                                    })
                                })
                            }
                            // linkFormArr为使用Promise.allSettled返回的数据，其数据格式为
                            //     [{status: 'fulfilled', value: {}] 或 // 失败时
                            //     [{status: 'rejected', reason: ''}] // 成功时
                            //     真正的值会放在value中，所以后续逻辑使用时会使用value
                            linkFormArr.forEach((i, index) => {
                                // 获取初始的表单值对象数据
                                let { form: _form, editScope, formScope } = this.allLinkEditForm[index]
                                // 获取最新的表单值对象数据
                                let _newForm = i.value
                                // 判断表单是否有变更
                                let _hasChange = Is_Obj_Different(_form, _newForm, {
                                    // 表单中存在数组值的一般为附件，这里核心判断附件是否变化
                                    arrayCompareFun: (oldVal, newVal) => {
                                        // 附件长度前后不一致，则直接判断已变更
                                        if (newVal.length !== oldVal.length) return true
                                        // 若最新表单中含有新选择的附件(存在属性raw值且有值),则表示已变更
                                        if (newVal.some(j => j.raw)) return true
                                    }
                                })
                                // 关联表单是否有自定义的保存方法
                                let _hasScopeSave = formScope?.scopeSave && typeof formScope?.scopeSave === 'function'
                                if (_hasScopeSave) {
                                    _linkFormPromise.push(formScope.scopeSave())
                                } else {
                                    // 只有变更的才提交，没有变更的不走接口，避免发起过多无用的关联表单保存请求
                                    _hasChange && _linkFormPromise.push(_postLinkForm(_newForm, editScope))
                                }
                            })
                            // 所有的关联表单保存成功时，才算整个保存成功
                            Promise.all(_linkFormPromise).then(async _ => {
                                // 注入模型js中事件节点
                                await Event_Expand.call(this, 'finally', arguments[1], ..._eventExpandParams)
                                this.$message.success('保存成功')
                                // 默认情况：返回上一页
                                this.$router.go(-1)
                            }).finally(_ => {
                                // 取消按钮状态
                                btnObj.loading = false
                            })
                        } else {
                            // 取消按钮状态
                            btnObj.loading = false
                            this.$message.error(`保存失败！${res?.message || ''}`)
                        }
                    }).catch(() => {
                        // 取消按钮状态
                        btnObj.loading = false
                    })
                })
            }).catch(_ => {
                // 主表校验失败时，需要关注字段校验报错能否被用户看到
                // 主表没有在tab中时，报错会自动处理展示出来，这里只需要处理在tab中的主表
                if (this.linkData.mainTab?.length >= 1) {
                    // 此时表示主表在Tab中，设置主表所在Tab为活跃Tab显示
                    this.activeTabName = `mainTab_0`
                    // 滚动校验错误的字段到用户可视范围
                    this.$nextTick(() => {
                        this.getFormRef().scrollFailFormItem()
                    })
                }
            })
        }
    }
}

/**
 * 保存并新增
 * @param {Object} btnObj 按钮对象
 */
async function SaveAndNew (btnObj) {
    // 获取保存按钮的保存方法
    let _saveFun = this.btnList.find(i => i.id === 'save')?.click
    if (!_saveFun) {
        this.$alert('不存在【保存】按钮，无法启用【保存后新增】按钮', { type: 'warning' })
        return
    }
    _saveFun.call(this, btnObj, {
        afterSave: function (resolve) {
            // 如果是在新增页面点击了【保存后新增】,由于不能自己跳自己，需要借助Refresh.vue页面作为跳板，触发跳转生效
            if (this.$route.params?.id === GlobalConst.form.newId) {
                this.$router.replace('/refresh')
            } else {
                // 替换当前路由，使用保存后最新的详情id进行渲染
                let {
                    path,
                    query,
                    params
                } = this.$route
                if ('id' in params) {
                    // 使用点击后的数据id替换现有的表单id
                    path = path.replace(params.id, GlobalConst.form.newId)
                }
                // 刷新路由
                this.$router.replace({
                    path,
                    query
                })
            }
        }
    })
}

/**
 * 返回
 * @param {Object} btnObj 按钮对象
 */
 async function Back (btnObj) {
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'before', arguments[1])
    this.$router.go(-1)
    // 注入模型js中事件节点
    await Event_Expand.call(this, 'after', arguments[1])
}

// import { Save, Back } from '@/components/frame/Module/BtnBaseFun/edit'
export {
    Save, // 保存
    SaveAndNew, // 保存后新增
    Back, // 返回
}
