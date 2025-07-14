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
    ]
    // paramsBeforeRequest: function (params) {
    //     // this:指向src/components/frame/Common/MList/index.vue页面作用域
    //     // 判断用户是否为管理员 是管理员则显示所有数据，不是管理员显示自己的数据
    //     // if (this.$store.getters.userInfo.roleCode == 'admin') {
    //     //     console.log(params)
    //     //    return params
    //     // }
    //     // if(this.$store.getters.userInfo.roleCode !== 'YWGLY'){
    //     //     return params
    //     // }
    //       // this:指向src/components/frame/Common/MList/index.vue页面作用域
    //       let arrTemp = []
    //       // 字符串转数组
    //       arrTemp = JSON.parse(params.searchParam)
    //       // 推入当前当前用户
    //       if (!arrTemp.some(item => item.name === 'creator')) {
    //           arrTemp.push({ "name": "creator", "value": this.$store.getters.userInfo.id, "type": "text-query", "tagName": "" })
    //       }
    //       // 数组转字符串
    //       params.searchParam = JSON.stringify(arrTemp)
    //     return params
    // },
}