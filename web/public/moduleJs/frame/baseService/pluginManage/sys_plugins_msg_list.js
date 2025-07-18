// 系统管理 - 八斗插件 - 插件管理
export default {
    // 前端写法
    buttons: [
        { id: 'add', isHide: true },
        { id: 'delete', isHide: true },
        { id: 'edit', isHide: true },
        {
            id: 'enabled',
            name: '启用',
            icon: 'nextStep',
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
                if (selection.length > 1) {
                   this.$message.warning('仅允许选择一个插件启动！')
                   return
                }
                this.$confirm('您确定启动选定的记录行吗?', '启动', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        ids: selection.map(i => i.id).join(','),
                        fieldValue:{status:1}
                    }
                    let url = `${this.BASEURL}plugins/install/pluginsinstallsave/saveBatchOfField.do?mdCode=${this.mdCode}&id=${selection.id}`
                    this.post(url, params).then(res => {
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
            id: 'disabled',
            name: '禁用',
            icon: 'pause',
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
                if (selection.length > 1) {
                   this.$message.warning('仅允许选择一个插件启动！')
                   return
                }
                this.$confirm('您确定启动选定的记录行吗?', '启动', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        ids: selection.map(i => i.id).join(','),
                        fieldValue: { status: 0 } //禁用
                    }
                    let url = `${this.BASEURL}plugins/install/pluginsinstallsave/saveBatchOfField.do?mdCode=${this.mdCode}&id=${selection.id}`
                    this.post(url, params).then(res => {
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
            id: 'logs',
            name: '日志',
            icon: 'listFile',
            type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                if (selection.length === 0) {
                      this.$message.warning('请选择一条数据')
                      return
                }
                this.pushPage({
                    path: `/module/stander/list/sys_plugins_logs/placeholder`,
                    query: {
                        roleId: selection[0].id
                    },
                    title: `查看【${selection[0].name}】日志`
                })
            }
        }
    ]
}
