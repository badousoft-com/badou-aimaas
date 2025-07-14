export default {
    // 前端写法
    buttons: [
        { id: "delete", isHide: true },
        {
            id: "start",
            name: "启动",
            icon: "nextStep",
            type: 'primary',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('至少选择一行！')
                    return
                }
                this.$confirm('您确定启动选定的记录行吗?', '启动', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        ids: selection.map(i => i.id).join(',')
                    }
                    this.post(`${this.BASEURL}/quartz/qwtemplatesave/batchStart.do`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('启动成功')
                        } else {
                            this.$message.error('启动失败')
                        }
                    })
                })
            }
        }, {
            id: "stop",
            name: "停止",
            icon: "pause",
            type: 'danger',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('至少选择一行！')
                    return
                }
                this.$confirm('您确定停止选定的记录行吗?', '停止', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        ids: selection.map(i => i.id).join(',')
                    }
                    this.post(`${this.BASEURL}/quartz/qwtemplatesave/batchStop.do`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('停止成功')
                        } else {
                            this.$message.error('停止失败')
                        }
                    })
                })
            }
        }, {
            id: "execute",
            name: "立即执行",
            icon: "right",
            type: 'primary',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请选择一行！')
                    return
                }
                if (selection.length > 1) {
                    this.$message.warning('最多选择一行！')
                    return
                }
                this.$confirm('您确定立即执行选定的记录行吗?', '执行', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        id: selection[0].id
                    }
                    this.post(`${this.BASEURL}/quartz/qwtemplatesave/startQwJobIgnoredTime.do`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('执行成功')
                        } else {
                            this.$message.error('执行失败')
                        }
                    })
                })
            }
        }
    ],
    
}