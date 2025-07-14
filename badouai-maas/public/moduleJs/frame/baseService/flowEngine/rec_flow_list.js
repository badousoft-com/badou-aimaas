/**
 * 系统管理 - 流程引擎 - 流程文档
 */
export default {
    // 前端写法
    buttons: [
        { id: 'add', isHide: true },
        { id: 'edit', isHide: true },
        { id: "view", isHide: true },
        { id: "export", isHide: true },
        { id: "import", isHide: true },
        {
            id: "monitor",
            name: "监控",
            icon: "watch",
            type: 'primary',
            click: function (btnObj) {
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要监控的实例行!')
                    return
                }
                if (selection.length > 1) {
                    this.$message.warning('请选中一条!')
                    return
                }
                let { id: fiId, boId, title } = selection[0]
                this.pushPage({
                    path: `/module/stander/list/NODE_INSTANCE/placeholder?fiId=${fiId}&boId=${boId}`,
                    title: `${btnObj.name}【${title}】流程`
                })
            }
        }, {
            id: "reset",
            name: "重置未归档",
            icon: "reset",
            type: 'warning',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要恢复的记录行（流程文档实例）!')
                    return
                }
                // 获取符合结果的数据
                let _result = selection.every(i => {
                    return i.status !== 0 &&
                           i.status !== 1 &&
                           i.status !== 3
                })
                if (!_result) {
                    this.$message.warning('您选择的记录行中包含未归档数据，未归档数据不需要[重置未归档]操作!')
                    return
                }
                let title = selection.map(i => i.title).join(',')
                this.$confirm('您确定重置选定的记录行（流程文档实例）吗? \n' + title , '重置', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        ids: selection.map(i => i.id).join(',')
                    }
                    this.post(`${this.BASEURL}/instance/flow/flowinstancemonitor/resume.do`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('重置成功')
                        } else {
                            this.$message.error('重置失败')
                        }
                    })
                })
            }
        }, {
            id: "pigeonhole",
            name: "归档",
            icon: "placeFile",
            type: 'primary',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要归档的记录行（流程文档实例）!')
                    return
                }
                // 获取符合结果的数据
                let _result = selection.every(i => i.status !== 2)
                if (!_result) {
                    this.$message.warning('您选择的记录行中包含已归档数据，已归档数据不需要[归档]操作!')
                    return
                }
                let title = selection.map(i => i.title).join(',')
                this.$confirm('您确定归档选定的记录行（流程文档实例）吗? \n' + title , '归档', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        ids: selection.map(i => i.id).join(',')
                    }
                    this.post(`${this.BASEURL}/instance/flow/flowinstancemonitor/archive.do`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('归档成功')
                        } else {
                            this.$message.error('归档失败')
                        }
                    })
                })
            }
        }, {
            id: "delete",
            name: "删除",
            icon: "delete",
            type: 'danger',
            isHide: true,
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
        }
    ],
    // 设置双击数据行执行该行的审核逻辑
    dblClick: function (btnList) {
        // this:指向moduleList/index页面所在作用域
        let btnItem = btnList.find(i => i.id === 'monitor')
        btnItem.click.call(this, btnItem)
    },
}