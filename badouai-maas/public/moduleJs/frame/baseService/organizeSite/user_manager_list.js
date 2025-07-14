import { Set_Panel_Dialog } from '@/service/base-service'
export default {
    /**
     * 树的配置
     */
    treeAjaxConfig: function (nodeParentId, data, node) {
        if (nodeParentId) {
            return {
                url: `${this.BASEURL}/org/department/departmenttree/ptree.do`,
                params: { pid: nodeParentId }
            }
        }
        return {
            url: `${this.BASEURL}/org/department/departmenttree/tree.do`,
            params: {}
        }
    },
    dataUrl: function () {
        return `${this.BASEURL}/org/employee/employeelist/listJSON.do`
    },
    /**
     * 获取数据列表请求参数
     * @param {*} params 已有参数对象
     * @param {*} data 节点数据
     * @param {*} node  节点对象
     * @param {*} nodeComponent 节点实例
     */
    defaultParams: function (params, data, node, nodeComponent) {
        return {
            ...(params || {}),
            departmentId: data.id
        }
    },
    // 按钮
    buttons: [
        {
            id: "freeze",
            name: "冻结",
            icon : "freeze",
            type: 'danger',
            click : function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要冻结的员工!')
                    return
                }
                this.$prompt('', '员工冻结意见', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    inputPlaceholder: '员工冻结必须填写冻结意见...',
                    inputValidator: value => {
                        if (value === null || value.trim() === '') {
                            return '请输入冻结意见'
                        }
                    },
                }).then(result => {
                    let params = {
                        opinion: result.value.trim(),
                        ids: selection.map(i => i.id).join(',')
                    };
                    this.post(`${this.BASEURL}/project/project/employeefreeze/freeze`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('冻结成功')
                        } else {
                            this.$message.error(res?.message)
                        }
                    })
                })
            }
        }, {
            id: "unfreeze",
            name: "解冻",
            icon : "unfreeze",
            type: 'primary',
            click : function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要解冻的员工!')
                    return
                }
                // flgActive为1的表示已解冻，此处为找出冻结项
                let hasFreezeItem = selection.find(i => i.flgActive !== 1)
                if (!hasFreezeItem) {
                    this.$message.success('解冻成功!')
                    // 存在冻结项，进行处理
                } else {
                    let params = {
                        ids: selection.map(i => i.id).join(',')
                    };
                    this.post(`${this.BASEURL}/org/employee/employeesave/unFreeze.do`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('解冻成功')
                        } else {
                            this.$message.error('解冻失败')
                        }
                    })
                }
            }
        }, {
            id: "transfer",
            name: "调动",
            icon : "move",
            type: 'primary',
            click: function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要调动的员工!')
                    return
                }
                this.addressBook({
                    type: '2',
                    selectType: 0,
                    singleChoose: true
                }).then(data => {
                    let params = {
                        ids: selection.map(i => i.id).join(','),
                        target: data[0].value
                    }
                    this.post(`${this.BASEURL}/org/employee/employeesave/move.do`, params).then(res => {
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
            id: "resetErrorLogon",
            name: "重置非法登录次数",
            icon : "reset",
            type: 'warning',
            click : function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要重置的员工!')
                    return
                }
                // flgPwdError表示非法登录数
                let hasPwdErrorItem = selection.find(i => i.flgPwdError !== 0)
                if (!hasPwdErrorItem) {
                    this.$message.success('重置成功！')
                    // 存在冻结项，进行处理
                } else {
                    let params = {
                        ids: selection.map(i => i.id).join(',')
                    };
                    this.post(`${this.BASEURL}/org/employee/employeesave/resetFlgPwdError.do`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('重置成功')
                        } else {
                            this.$message.error('重置失败')
                        }
                    })
                }
            }
        }, {
            id: "panelSetting",
            name: "面板配置",
            icon : "setting",
            type: 'primary',
            click: function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择要配置面板的用户!')
                    return
                } else if (selection.length !== 1) {
                    this.$message.warning('一次只能配置一个用户的面板')
                    return
                }
                // 执行弹窗配置面板事件
                Set_Panel_Dialog.call(this, selection, { type: '20' })
            }
        }, {
            id: 'delete',
            name: '删除',
            icon: 'delete',
            type: 'danger',
            isHide: true, // 2021-09-27暂时隐藏该功能，后端说删除员工时可能会导致业务数据炸掉
            click : function (btnObj) {
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要删除的员工!')
                    return
                }
                this.$confirm('确定删除吗？', '删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        ids: selection.map(i => i.id).join(',')
                    }
                    this.post(`${this.BASEURL}/org/employee/employeedelete/deleteUser`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('删除成功')
                        } else {
                            this.$message.error('删除失败，失败原因：' + res?.message)
                        }
                    })
                })
            }
        }
    ]
}