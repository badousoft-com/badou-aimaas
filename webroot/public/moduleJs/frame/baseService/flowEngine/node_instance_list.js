/**
 * 菜单：系统管理-流程引擎-流程文档，选择一条数据后，点击【监控】按钮，可进入对应页面
 */
import GlobalConst from '@/service/global-const'
import { Transfer, Save_Node_Jump, Flow_ReHandle } from '@/api/frame/flow'
// 定义临时变量：用于获取参数
let tempFiId = null
let tempBoId= null
// 定义转办-按钮对象
//     点击【转办（显示）】，就会显示【转办（隐藏）】与【转办】
//     点击【转办（隐藏）】，就会显示【转办（显示）】
let transferObj = {
    fun: 'transfer',
    show: 'transferShowCheck',
    hide: 'transferHideCheck'
}
// 定义转办按钮对应关系，两组为互斥关系
let transferBtn = {
    // 【转办（隐藏）】与【转办】捆绑为一组
    hide: [transferObj.hide, transferObj.fun],
    // 【转办（显示）】自己为一组
    show: [transferObj.show]
}

/**
 * 过滤隐藏按钮
 * @param {Array} btnList 按钮数组
 * @param {String} id 按钮id
 * @param {Boolean} status 需要设置按钮状态，传入true则显示
 */
function showBtn (btnList, id, status = true) {
    btnList.find(i => i.id === id).isHide = !status
}

/**
 * 根据传入的环节处理人列表，判断当前环节是否为待办状态
 * @param {Array} item 
 * @returns {Boolean} true:表示传入的处理人列表中含有待办状态
 */
function isWaitDoStatus (item) {
    return item &&
           item.constructor === Array &&
           item.length > 0 &&
        //    只有一个人含有status为1，表示当前该环节为待办状态（status:1-待办  2-已办）
           item.some(j => j.status === 1)
}

// 获取当前所处环节在列表数据中的下角标
function findCurrentNodeIndex (list) {
    return list.findIndex(i => isWaitDoStatus(i?.actualHandler))
}
export default {
    buttons: [
        { id: "add", isHide: true },
        { id: "edit", isHide: true },
        { id: "view", isHide: true },
        { id: "delete", isHide: true },
        { id: "export", isHide: true },
        { id: "import", isHide: true },
        {
            id: transferObj.show,
            name: "转办(显示复选框)",
            icon: "transfer",
            type: 'primary',
            isHide: false,
            click: function (btnObj) {
                // 获取当前流程中待办环节所在的下角标
                let waitDoIndex = findCurrentNodeIndex(this.data)
                // 设置checkbox显示，可进行处理人选择
                this.data[waitDoIndex].showHandlerCheckBox = true
                // 设置操作按钮的对应隐藏展示
                transferBtn.hide.forEach(i => {
                    showBtn(this.btnList, i)
                })
                transferBtn.show.forEach(i => {
                    showBtn(this.btnList, i, false)
                })
            }
        }, {
            id: transferObj.fun,
            name: "转办",
            icon: "transfer",
            type: 'primary',
            isHide: true,
            click: function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取当前流程中待办环节所在的下角标
                let waitDoIndex = findCurrentNodeIndex(this.data)
                // 获取当前选中的处理人
                let chooseHandler = this.data[waitDoIndex].chooseHandler
                if (chooseHandler.length === 0) {
                    this.$message.warning('请选择需要转办的待办处理人')
                    return
                }
                // 调用用户地址本选择转办给谁
                this.addressBook({
                    title: `请选择转办给的人员`,
                    addressTypes: '2-1-20-2', // 新添加这个属性，从这个进行走通，尽量不影响原有属性
                }).then(data => {
                    // 获取环节名称
                    let { nodeName } = this.data[waitDoIndex]
                    // 获取旧的环节处理人
                    let oldHandler = chooseHandler.map(i => i.actualHandlerName).join(GlobalConst.separator)
                    // 获取新的环节处理人
                    let newHandler = data.map(i => i.name).join(GlobalConst.separator)
                    this.$prompt(`填写您的意见`, `确定将【${nodeName}】环节处理人由[${oldHandler}]修改为[${newHandler}]`, {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                    }).then(({ value }) => {
                        let params = {
                            target: data.map(i => i.value).join(GlobalConst.separator),
                            wids: chooseHandler.map(i => i.wid).join(GlobalConst.separator),
                            opinion: value // 意见框
                        }
                        // 执行转办的逻辑
                        Transfer(params).then(res => {
                            if (res?.hasOk) {
                                // 更新列表页面数据
                                listPageRef.init().then(res => {
                                    // 重新点击按钮转办，让当前继续可进行转办（处理人可选状态）
                                    let btnItem = this.btnList.find(i => i.id === transferObj.show)
                                    btnItem.click.call(this, btnItem)
                                })
                                this.$message.success('操作成功')
                            } else {
                                this.$message.error('操作失败')
                            }
                        })
                    }).catch((err) => {
                        // 取消输入时进入当前逻辑 或当前逻辑错误
                        console.error(err)
                    })
                })
            }
        }, {
            id: transferObj.hide,
            name: "转办(隐藏复选框)",
            icon: "transfer",
            type: 'success',
            isHide: true,
            click: function (btnObj) {
                // 获取当前流程中待办环节所在的下角标
                let waitDoIndex = findCurrentNodeIndex(this.data)
                // 隐藏处理人的选择框
                this.data[waitDoIndex].showHandlerCheckBox = false
                // 设置操作按钮的对应隐藏展示
                transferBtn.hide.forEach(i => {
                    showBtn(this.btnList, i, false)
                })
                transferBtn.show.forEach(i => {
                    showBtn(this.btnList, i)
                })
            }
        }, {
            id: 'jump',
            name: "跳转",
            icon: "allot",
            type: 'primary',
            click: function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取当前流程中待办环节所在的下角标
                let waitDoIndex = findCurrentNodeIndex(this.data)
                this.$pageDialog.init({
                    title: '请选择需要跳转到的环节', // 弹窗标题
                    pageUrl: '/module/flow/part/NodeSelect', // 弹窗显示的页面路径，会拼接src/views
                    outScope: this, // 默认这样写即可，不要问
                    fiId: tempFiId,
                    nodeIndex: waitDoIndex, // 待办所处环节下角标
                    height: '500px',
                    handlerList: [ // 操作的按钮列表
                        { id: 'save', name: '确定', icon: 'save', type: 'primary', click: function (itemObj) {
                            // this // 只想弹窗组件中的作用域
                            // this.outScope // 若使用dialog时传入属性outScope:this,则此处可获取使用弹窗的页面作用域
                            // this.pageScope // 挂载页面所在的作用域

                            // 获取跳转的环节数据
                            let selection = this.pageScope.getSelection()
                            if (selection.length === 0) {
                                this.$message.warning('请选择一个跳转的环节')
                                return
                            }
                            if (selection.length > 1) {
                                this.$message.warning('请只选择一个跳转的环节')
                                return
                            }
                            // 执行跳转接口
                            Save_Node_Jump({
                                fiId: tempFiId,
                                targetNodeId: selection[0].id
                            }).then(res => {
                                if (res?.hasOk) {
                                    // 更新列表页面数据
                                    listPageRef.init()
                                    this.$message.success('操作成功')
                                    this.$pageDialog.close() // 关闭弹窗
                                } else {
                                    this.$message.error('操作失败')
                                }
                            })
                        }}
                    ],
                })
            }
        }, {
            id: 'reDo',
            name: "重处理",
            icon: "synchro",
            type: 'warning',
            click: function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                this.$confirm(`您确定将当前流程文档实例重处理吗？` , '重处理', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    // 执行重处理逻辑
                    Flow_ReHandle({
                        fiId: tempFiId
                    }).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('操作成功')
                        } else {
                            this.$message.error('操作失败')
                        }
                    })
                })
            }
        }, {
            id: 'processTrack',
            name: "流程跟踪",
            icon: "carryOut",
            type: 'primary',
            click: function (btnObj) {
                this.$pageDialog.init({
                    title: '流程跟踪意见', // 弹窗标题
                    pageUrl: '/module/flow/part/FlowTrace', // 弹窗显示的页面路径，会拼接src/views
                    outScope: this, // 默认这样写即可，不要问
                    height: '500px',
                    boId: tempBoId,
                    noHeader: true, // 去除流程跟踪组件中的卡片头
                    fullHeight: true, // 设置列表内容自适应铺满
                })
            }
        }, {
            id: 'refresh',
            name: "刷新",
            icon: "refresh",
            type: 'success',
            click: function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                listPageRef.init()
            }
        }, {
            id: 'back',
            name: "返回",
            icon: "back",
            type: 'danger',
            click: function (btnObj) {
                // 获取列表页面所在作用域
                this.$router.go(-1)
            }
        }
    ],
    isSelection: false, // 目前按钮都是操作整个流程，不操作每个环节（每条数据），所以不需要开启选择框
    isUsePagination: false, // 关闭分页
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `${this.BASEURL}/instance/node/nodeinstancelist/listJSON`
    },
    paramsBeforeRequest: function (params) {
        // 获取参数对象
        let defaultSearchParam = params &&
                                 params.defaultSearchParam &&
                                 JSON.parse(params.defaultSearchParam)
        // 定义参数键名
        let _name = 'fiId'
        if (defaultSearchParam &&
            defaultSearchParam.constructor === Array) {
            // 构造需要请求的参数
            tempFiId = defaultSearchParam.find(i => i.name === _name)?.value
            // 提取boId的值，赋值给全局变量，不需要参与请求
            tempBoId = defaultSearchParam.find(i => i.name === 'boId')?.value
            params[_name] = tempFiId
        }
        // 默认返回参数
        return params
    },
    /**
     * @param {Array} data 列表数据
     */
    afterListJSON: function (data) {
        // 处理人字段数据拼装，从actualHandler字段获取值给handler
        if (data && data.constructor === Array) {
            data.forEach(i => {
                // 将实际处理人字段赋值给处理人字段
                i.handler = i.actualHandler
                // 构造字段：用于存储在转办时选择的处理人数据，转办时，处理人变更为复选框模式
                i.chooseHandler = [],
                // 构造字段：设置展示处理人复选框为隐藏状态
                i.showHandlerCheckBox = false
            })
        }
        return data
    },
    renderField: {
        handler: {
            /**
             * @param {Object} h 渲染函数参数对象
             * @param {Object} context 对象（包含props,children,slots,scopedSlots,parent,listeners,injection）
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前字段值
             * @param {Number} index 该行下角标
             * @param {Object} fieldObj 字段对象
             * @param {Object} scope 表格项对象
             */
            render: function (h, context, row, column, cellValue, index, fieldObj, scope) {
                if (!(cellValue && cellValue.length > 0)) return (<span>{GlobalConst.view.value}</span>)
                // 根据处理人可否选中编辑状态
                if (row.showHandlerCheckBox) {
                    // 可选时处理为checkbox模式
                    return (
                        <el-checkbox-group vModel={row.chooseHandler}>
                        {
                            cellValue.map(i => {
                                return <el-checkbox label={i} class="marR-10 warningC">{i.actualHandlerName}</el-checkbox>
                            })
                        }
                        </el-checkbox-group>
                    )
                } else {
                    // 获取当前环节是否为待办状态
                    let _isWaitDoStatus = isWaitDoStatus(cellValue)
                    return (
                        <span>
                            {
                                cellValue.map(i => {
                                    // 定义主题对象
                                    let theme = {
                                        color: _isWaitDoStatus ? 'warningC' : 'successC',
                                        icon: _isWaitDoStatus ? 'clock' : 'selected-fill',
                                    }
                                    return (
                                        <span>
                                            <bd-icon name={theme.icon} class={theme.color}></bd-icon>
                                            <span class='marR-10 warningC' class={'marR-10' + theme.color}>{i.actualHandlerName}</span>
                                        </span>
                                    )
                                })
                            }
                        </span>
                    )
                }
            }
        }
    }
}