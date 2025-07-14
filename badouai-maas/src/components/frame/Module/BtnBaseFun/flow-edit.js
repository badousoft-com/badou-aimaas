import { Event_Expand } from '@/service/event-expand'
import GlobalConst from '@/service/global-const'
import { Is_Obj_Different } from '@/utils/object'
import { S_Storage } from '@/utils/storage'
import { Flow_Verify } from '@/views/module/flow/part/flow-service.js'
import { Save_Module_Form } from '@/api/frame/common.js'
/**
 * 流程发起的保存事件
 * @param {Object} btnObj 按钮对象
 */
async function Save (btnObj) {
    // this:指向按钮所在页面所在的作用域
    return new Promise(async (resolve, reject) => {
        let _this = this.$refs['moduleEditCode'].getModuleEditRef()
        // 获取表单作用域
        let formRef = _this.getFormRef()
        // 定义业务ID
        let boId = null
        // 定义通用参数，用于注入事件中使用
        let _eventExpandParams = [formRef]
        // 注入模型js中事件节点
        await Event_Expand.call(_this, 'beforeValidate', arguments[1], ..._eventExpandParams)
        // 定义主表单保存方法
        let _saveForm = () => {
            return new Promise((resolve) => {
                // 校验主表单
                formRef.validateForm().then(async (res) => {
                    // 设置按钮状态-加载中
                    btnObj.loading = true
                    // 注入模型js中事件节点
                    await Event_Expand.call(_this, 'before', arguments[1], res, ..._eventExpandParams)
                    // 提交接口
                    Save_Module_Form(res, _this._saveUrl).then(saveRes => {
                        if (saveRes?.hasOk) {
                            this.$message.success('保存成功')
                            resolve({ status: true, res: saveRes })
                        } else {
                            this.$message.error(`保存失败！${saveRes?.message || ''}`)
                            resolve({ status: false })
                        }
                    }).catch(() => {
                        btnObj.loading = false
                    })
                }).catch(err => {
                    // 设置按钮状态-取消加载
                    btnObj.loading = false
                    console.error(err)
                    resolve({ status: false })
                })
            })
        }
        /**
         * 根据主表保存后的接口响应数据获取业务id(BoId)
         * @param {Boolean} saveMainFormStatus 保存主表的结果状态
         * @param {Object} res 保存接口返回的数据
         * @param {Function} resolve
         * @returns {String} 业务id(BoId)
         */
        let _getBoId = function (saveMainFormStatus, res, resolve) {
            // 定义失败的处理函数
            let _resolveFail = () => resolve({
                status: false,
                boId: null
            })
            // 主表保存失败的情况
            if (!saveMainFormStatus) {
                _resolveFail()
                return
            }
            // 获取业务ID
            let _boId = res?.bean?.id
            if (!_boId) {
                this.$message.error('保存数据异常，请联系管理员')
                console.error('检查表单保存接口的响应数据，看下是不是没有返回id，boId期望是有值的')
                _resolveFail()
            }
            // 返回真正的业务ID
            return _boId
        }
        /**
         * 重新刷新表单数据
         * @param {String} flowOpinion 流程意见
         */
        let _reloadForm = function (flowOpinion) {
            this.isShowForm = false
            setTimeout(() => {
                this.isShowForm = true
                // 获取保存接口返回详情
                resolve({
                    status: true,
                    boId,
                    // 流程意见：优先使用传入的flowOpinion，若没有传入则使用页面上的this.flowOpinion
                    opinion: { opinion: flowOpinion || this.flowOpinion }
                })
            })
        }
        // 判断是否为表单编辑状态/新增状态
        if (!this.detailId) {
            // 这里跟常规的(基础表单/关联表单)场景不太一致
            //     1. (新增)非关联：保存后则更新路由id，刷新表单，不需要跳转页面
            //     2. (新增)关联：保存后也是更新路由id,刷新表单，不需要跳转页面
            // 不存在关联模块，表单保存成功后直接返回上一页
            
            _saveForm.call(_this).then(async ({status, res}) => {
                boId = _getBoId.call(this, status, res, resolve)
                if (!boId) {
                    // 设置按钮状态-取消加载
                    btnObj.loading = false
                    return
                }
                // 注入模型js中事件节点
                await Event_Expand.call(_this, 'after', arguments[1], ..._eventExpandParams)
                // 获取当前页面地址路径
                let _fullPath = `${this.$route.fullPath}`
                // 通过正则，返回结果为数组即表示能匹配到，即当前路由为新增表单模块，需要替换更新id
                // 定义正则，判断路径上是否含有/add/的新增表单标识
                //     1. 以/add结尾
                //     2. 含/add/
                if (_fullPath.match(new RegExp(`/${GlobalConst.form.newId}(/|$)`))) {
                    /**
                     * 流程意见这块的处理：主要面向新增的编辑表单时显示意见，提交审核时也显示意见的这种场景
                     *      新增表单保存时，路径会从add变更为实际的表单数据id，页面会刷新，所以需要先保存流程意见，以便刷新后意见能够回显
                     */
                    let flowOpinion = this.flowOpinion
                    // 存储流程意见，以便保存完后重定向页面时能够重新赋值
                    if (GlobalConst.openOpinionInFlowVerify && flowOpinion) {
                        // 这里只管存即可，对应页面刷新后会有取的逻辑
                        S_Storage.setObj('flowOpinion', { [`flow_${boId}`]: flowOpinion })
                    }
                    // 判断是否需要维持当前路由
                    if (btnObj.notReplaceRouter) {
                        // 获取保存接口返回详情
                        resolve({
                            status: true,
                            boId,
                            // 流程意见：优先使用传入的flowOpinion，若没有传入则使用页面上的this.flowOpinion
                            opinion: { opinion: flowOpinion || this.flowOpinion }
                        })
                    } else {
                        // 替换当前路由
                        this.$router.replace({
                            // 为避免替换到其他变量名一致的值，加上/更精确定位替换的内容
                            path: _fullPath.replace(`/${GlobalConst.form.newId}/`, `/${boId}/`)
                        }).then(() => {
                            // 重新加载表单-避免类似附件始终是新增的状态
                            _reloadForm.call(this, flowOpinion)
                        })
                    }
                } else {
                    // 重新加载表单-避免类似附件始终是新增的状态
                    _reloadForm.call(this)
                }
            }).finally(_ => {
                // 设置按钮状态-取消加载
                btnObj.loading = false
            })
        } else {
            // 存在id的情况下
            // 判断是否存在关联模块
            if (!(_this._showLinkData && _this._showLinkData.length > 0)) {
                // 不存在关联
                _saveForm.call(_this).then(async ({status, res}) => {
                    boId = _getBoId.call(this, status, res, resolve)
                    if (!boId) {
                        // 设置按钮状态-取消加载
                        btnObj.loading = false
                        return
                    }
                    // 注入模型js中事件节点
                    await Event_Expand.call(_this, 'after', arguments[1], ..._eventExpandParams)
                    // 重新加载表单-避免类似附件始终是新增的状态
                    _reloadForm.call(this)
                }).finally(_ => {
                    // 设置按钮状态-取消加载
                    btnObj.loading = false
                })
            } else {
                // 存在关联
                formRef.validateForm().then(res => {
                    // 定义获取主表变量
                    let _mainForm = res
                    // 定义关联表单的结果集-承诺列表
                    let _linkFormPromiseList = []
                    // 获取关联表单-数据，触发关联表-表单校验
                    _this.allLinkEditForm.forEach(i => {
                        // 添加获取表单数据
                        _linkFormPromiseList.push(i.formScope.validateForm())
                    })
                    // 等待所有关联表-表单校验结束
                    Promise.allSettled(_linkFormPromiseList).then(async (linkFormArr) => {
                        // 找出校验失败的第一个关联表单。这里只找出首个，设置页面滚动到视图可见校验报错的信息处，多个的话没法同时管理
                        let _failIndex = linkFormArr.findIndex(i => i.status !== 'fulfilled')
                        // 若存在校验失败的关联表单
                        if (~_failIndex) {
                            let { tabName, tabValue, formScope } = _this.allLinkEditForm[_failIndex]
                            // 判断是否Tab结构下的模块出现字段校验报错，则需要先切换到对应tab下，再滚动到错误字段校验
                            if (tabName) {
                                // 设置活跃Tab为校验失败的表单所处的Tab
                                _this[tabName] = tabValue
                                // 切换tab后，滚动至表单校验报错的字段处，给用户查看
                                _this.$nextTick(() => {
                                    formScope.scrollFailFormItem()
                                })
                            } else {
                                // 滚动到错误字段校验处
                                formScope.scrollFailFormItem()
                            }
                            resolve({
                                status: false,
                                boId: null
                            })
                            // 存在表单校验错误的场景，停止逻辑
                            return
                        }
                        // 校验完毕后，获取所有关联表单数据完毕，开始提交保存

                        // 设置按钮状态-加载中
                        btnObj.loading = true
                        // 注入模型js中事件节点
                        await Event_Expand.call(_this, 'before', arguments[1], _mainForm, ..._eventExpandParams)
                        // 先提交主表数据
                        this.post(_this._saveUrl, _mainForm).then(async (mainRes) => {
                            let _status = mainRes?.hasOk
                            boId = _getBoId.call(this, _status, mainRes, resolve)
                            if (!boId) {
                                // 设置按钮状态-加载中
                                btnObj.loading = false
                                return
                            }
                            // 注入模型js中事件节点
                            await Event_Expand.call(_this, 'after', arguments[1], ..._eventExpandParams)
                            // 先判断有多少表单发生了变更
                            let _linkFormPromise = []
                            /**
                             * 定义关联表单提交的函数
                             * @param {Object} form 表单值对象数据
                             * @param {Object} scope moduleEdit/index.vue页面所在作用域
                             */
                            let _postLinkForm = (form, scope) => {
                                return new Promise(async (resolve) => {
                                    // 提交接口
                                    this.post(scope._saveUrl, form).then(async (res) => {
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
                                let { form: _form, editScope } = _this.allLinkEditForm[index]
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
                                // 只有变更的才提交，没有变更的不走接口，避免发起过多无用的关联表单保存请求
                                _hasChange && _linkFormPromise.push(_postLinkForm(_newForm, editScope))
                            })
                            // 所有的关联表单保存成功时，才算整个保存成功
                            Promise.all(_linkFormPromise).then(async _ => {
                                // 注入模型js中事件节点
                                await Event_Expand.call(_this, 'finally', arguments[1], ..._eventExpandParams)
                                this.$message.success('保存成功')
                                // 设置按钮状态-加载中
                                btnObj.loading = false
                                // 默认情况：返回上一页
                                // 重新加载表单-避免类似附件始终是新增的状态
                                _reloadForm.call(this)
                            }).catch(_ => {
                                // 设置按钮状态-加载中
                                btnObj.loading = false
                            })
                        }).catch(_ => {
                            // 设置按钮状态-加载中
                            btnObj.loading = false
                        })
                    })
                }).catch(_ => {
                    resolve({
                        status: false,
                        boId: null
                    })
                    // 主表校验失败时，需要关注字段校验报错能否被用户看到
                    // 主表没有在tab中时，报错会自动处理展示出来，这里只需要处理在tab中的主表
                    if (_this.linkData.mainTab?.length >= 1) {
                        // 此时表示主表在Tab中，设置主表所在Tab为活跃Tab显示
                        _this.activeTabName = 'mainTab_0'
                        // 滚动校验错误的字段到用户可视范围
                        this.$nextTick(() => {
                            _this.getFormRef().scrollFailFormItem()
                        })
                    }
                })
            }
        }
    })
}

async function Submit () {
    let [ btnObj, ...otherArgs] = arguments
    Flow_Verify.call(this, btnObj, null, ...otherArgs)
}

export {
    Save,
    Submit
}