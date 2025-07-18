/**
 * 待办维护
 */
import { Transfer } from '@/api/frame/flow'
import GlobalConst from '@/service/global-const'
export default {
    // 前端写法
    buttons: [
        { id: "add", isHide: true },
        { id: "edit", isHide: true },
        { id: "view", isHide: true },
        { id: "delete", isHide: true },
        { id: "export", isHide: true },
        { id: "import", isHide: true },
        {
            id: 'transfor',
            name: "转办",
            icon: "transfer",
            type: 'primary',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                // 获取选中数据长度
                let _length = selection.length
                if (_length === 0) {
                    this.$message.warning('请先选择需要转办的记录!')
                    return
                }
                for (let i = 0; i < _length; i++) {
                    if (selection[i].status === 2 || selection[i].status === 12) {
                        this.$message.warning('您选择的记录行中包含已办（已阅）记录，已办（已阅）不能进行转办操作!')
                        return
                    }
                }
                this.addressBook({
                    type: '2',
                    selectType: 20,
                }).then(data => {
                    let _data = data && data.map(i => i.name).join(GlobalConst.separator)
                    this.$prompt(`填写您的意见`, `确定转办至【${_data}】吗?`, {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                    }).then(({ value }) => {
                        let params = {
                            target: data.map(i => i.value).join(GlobalConst.separator),
                            wids: selection.map(i => i.id).join(GlobalConst.separator),
                            opinion: value
                        }
                        // 2021-07-19之前使用接口：workmaintain/workmaintainsave/transfer.do
                        // 2021-07-19变更为以下接口，与流程文档的转办使用同一个接口：/instance/flow/flowinstancemonitor/transfer
                        // 执行转办的逻辑
                        Transfer(params).then(res => {
                            if (res?.hasOk) {
                                // 更新列表页面数据
                                listPageRef.init()
                                this.$message.success('操作成功')
                            } else {
                                this.$message.error('操作失败')
                            }
                        })
                    }).catch((err) => {
                        // 取消输入时进入当前逻辑 或 存在逻辑异常
                        console.error(err)
                    })
                })
            }
        }, {
            id: "transforToDone",
            name: "转为已处理",
            icon: "exchange",
            type: 'primary',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要操作的记录行!')
                    return
                }
                for (let i = 0; i < selection.length; i++) {
                    if (selection[i].status === 2 || selection[i].status === 12) {
                        this.$message.warning('您选择的记录行中包含已办（已阅）记录，已办（已阅）不需要再进行转为已处理操作!')
                        return
                    }
                }
                this.$confirm('您确定将选定的记录行转为已处理吗?' , '转为已处理', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        status: 2,
                        ids: selection.map(i => i.id).join(',')
                    }
                    this.post(`${this.BASEURL}/workmaintain/workmaintainsave/trunk.do`, params).then(res => {
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
            id: "transforToUndo",
            name: "转为未处理",
            icon: "exchange",
            type: 'primary',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要操作的记录行!')
                    return
                }
                for (let i = 0; i < selection.length; i++) {
                    if (selection[i].status === 1 || selection[i].status === 11) {
                        this.$message.warning('您选择的记录行中包含待办（待阅）记录，待办（待阅）不需要再进行转为未处理操作!')
                        return
                    }
                }
                this.$confirm('您确定将选定的记录行转为未处理吗?' , '转为未处理', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        status: 1,
                        ids: selection.map(i => i.id).join(',')
                    }
                    this.post(`${this.BASEURL}/workmaintain/workmaintainsave/trunk.do`, params).then(res => {
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
            id: "newDelete",
            name: "删除",
            icon: "delete",
            type: 'danger',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要删除的记录行（流程文档实例）!')
                    return
                }
                this.$confirm('您确定删除选定的记录行（流程文档实例）吗?' , '删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        mdCode: this.mdCode,
                        ids: selection.map(i => i.id).join(',')
                    }
                    this.post(`${this.BASEURL}/jdbc/common/basecommondelete/delete.do`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('删除成功')
                        } else {
                            this.$message.error('删除失败')
                        }
                    })
                })
            }
        }, {
            id: "refresh",
            name: "刷新",
            icon: "refresh",
            type: 'primary',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 更新列表页面数据
                listPageRef.init()
            }
        }
    ]
}