
export default {
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `/project/maas/modelapp/modelapplistgpu/listJSON?mdCode=maas_model_app_gpu`
    },
    renderField: {
        // 字段键名
        threadMsg: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<span style='color: #007BFF;'>点击查看</span>`
            },        // 点击事件（若没有可以不写，去除整块click）
            click: function (row, column, cellValue, index, fieldObj) {
                let btnList = [
                    {
                        name: '取消',
                        type: 'danger',
                        icon: 'cancel',
                        click: function () {
                            // 关闭弹窗表单
                            this.$dialog.close()
                        }
                    }
                ]
                this.$dialog.init({
                    title: '线程信息',
                    type: 'list',
                    // width: 1200,
                    // height: 300,
                    isSelection: false,
                    id: '123',
                    fieldList: [
                        { name: 'uid', label: '进程所属用户ID',},
                        { name: 'pid', label: '进程ID',},
                        { name: 'c', label: '进程的CPU占用率',},
                        { name: 'stime', label: '进程启动时间',},
                        { name: 'time', label: '进程使用的CPU时间',},
                        { name: 'cmd', label: '启动进程的命令',},
                        { name: 'ppid', label: '父进程ID',},
                        { name: 'tty', label: '进程所属的终端',},
                    ],
                    handlerList: btnList,
                    data: JSON.parse(cellValue)
                })
            }
        },
        statusDesc: {
            render: function (h, context, row, column, cellValue, index, fieldObj, scope) {
                let that = this
                if (row.status == 0) {
                    return (
                        <span>
                            <bd-icon name="time-d" style="color:black"></bd-icon>
                            <span style="color:black">未启动</span>
                        </span>
                    )
                }
                if (row.status == 1) {
                    return (
                        <span>
                            <bd-icon name="start" style="color:lightgreen"></bd-icon>
                            <span style="color:lightgreen">启动中</span>
                        </span>
                    )
                }
                if (row.status == 2) {
                    return (
                        <span>
                            <bd-icon name="right" style="color:green"></bd-icon>
                            <span style="color:green">运行中</span>
                        </span>
                    )
                }
                if (row.status == 3) {
                    return (
                        <span>
                            <bd-icon name="multi" style="color:red"></bd-icon>
                            <span style="color:red">启动失败</span>
                        </span>
                    )
                }

                return (
                    <span>
                        <bd-icon name="time-d" style="color:black"></bd-icon>
                        <span style="color:black">未启动</span>
                    </span>
                )
                if (that.status == 3) {
                    return (
                        <bd-icon name="multi" style="color:red"><span>cellValue</span></bd-icon>
                    )
                }
                return cellValue;
                return (
                    <el-input vModel={row.value} class="primaryC" on-change={(e) => {
                        this.post('/project/maas/tuningplanparams/tuningplanparamsedit/updateValue', {
                            id: row.id,
                            newValue: e
                        }).then((res) => {
                            if (res?.hasOk) {

                            } else {
                                console.log("保存数值失败!")
                            }
                        }).finally(() => {

                        })
                    }}></el-input>
                )
            },
        }
    },
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        {
            id: 'import', name: '重启模型', icon: 'import', type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                btnObj.loading = true
                this.post('/project/maas/modelapp/modelappedit/restartPod', {
                    id: selection[0].id
                }).then((res) => {
                    if (res?.hasOk) {
                        this.$message.success(res.message)
                    } else {
                        this.$message.error(`重启失败${res.message}`)
                    }
                }).finally(() => {
                    // 设置按钮状态
                    btnObj.loading = false
                })
            }
        },
        { id: 'add', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'edit', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'delete', name: '导入', icon: 'import', type: 'primary', isHide: true },
        {
            id: 'logs',
            name: '查看日志',
            icon: 'logManage',
            type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                let that = this
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                const { href } = this.$router.resolve({
                    path: `/modelapplog`,
                    query: {
                        id: selection[0].id,
                        name: selection[0].modelName,
                        reload: "maas_model_app"
                    }
                });
                window.open(href, '_blank');
            }
        },
        {
            id: 'ress',
            name: '重启模型',
            icon: 'start',
            type: 'primary',
            isHide: true,
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                btnObj.loading = false
                this.post('/project/maas/modelapp/modelappedit/restartPod', {
                    id: selection[0].id
                }).then((res) => {
                    if (res?.hasOk) {
                        this.$message.success(res.message)
                    } else {
                        this.$message.error(`重启失败${res.message}`)
                    }
                }).finally(() => {
                    // 设置按钮状态
                    btnObj.loading = false
                })
            }
        },

        {
            id: 'start',
            name: '启动',
            icon: 'start',
            type: 'primary',
            isHide: true,
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                // this.post('/project/maas/modelapp/modelappsave/startApp', {
                //     id: selection[0].id
                // }).then((res) => {
                //     if (res?.hasOk) {
                //         this.$message.success(res.message)
                //     } else {
                //         this.$message.error(`启动失败失败！${res.message}`)
                //     }
                // }).finally(() => {
                //     // 设置按钮状态
                //     btnObj.loading = false
                // })               
            }
        },
        {
            id: 'stop',
            name: '停止模型',
            icon: 'login',
            type: 'danger',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length !== 1) {
                    this.$message({
                        type: 'warning',
                        message: '请选择一行！'
                    })
                    return
                }
                btnObj.loading = true // 重置按钮加载状态
                this.post('/project/maas/modelapp/modelappsave/stopApp', {
                    id: selection[0].id
                }).then((res) => {
                    if (res?.hasOk) {
                        listPageRef.init()
                        this.$message.success('停止成功!')
                    } else {
                        this.$message.error(res.message)
                    }
                }).finally(() => {
                    btnObj.loading = false
                })
            }
        },
        {
            id: 'edit',
            name: '编辑',
            icon: 'edit',
            type: 'danger',
            isHide: true,
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
            }
        },
        {
            id: 'zf',
            name: '停止',
            icon: 'cancel-fill',
            type: 'danger',
            isHide: true,
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                let listPageRef = this.listPageRef()
                this.post('/project/maas/modelapp/modelappsave/stopApp', {
                    id: selection[0].id
                }).then((res) => {
                    if (res?.hasOk) {
                        // 更新列表页面数据
                        listPageRef.init()
                        this.$message.success("停止成功!")
                    } else {
                        this.$message.error(`停止失败`)
                    }
                }).finally(() => {
                    // 设置按钮状态
                    btnObj.loading = false
                })
            }
        },
        {
            id: 'reflush',
            name: '刷新',
            icon: 'refresh-fill',
            type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
            }
        },
        {
            id: 'modeltalk1', name: '模型对话', icon: 'reply', type: 'primary', click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length !== 1) {
                    this.$message({
                        type: 'warning',
                        message: '请选择一行！'
                    })
                    return
                }
                this.$message.warning('开始初始化')
                btnObj.loading = true // 重置按钮加载状态
                this.post('/project/maas/modelapp/modelappedit/linkAppTalk', {
                    id: selection[0].id
                }).then((res) => {
                    if (res?.hasOk) {
                        window.open(res.message, '_blank');
                    } else {
                        this.$message.error('启动失败!请联系管理员！')
                    }
                }).finally(() => {
                    btnObj.loading = false
                })
            }
        },
        {
            id: 'modeltalk',
            name: '模型对话',
            icon: 'reply',
            type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                if (selection[0].status != 2) {
                    this.$message.warning('该模型应用未启动成功!')
                    return
                }
                //必须是启动完成的模型 暂不做 
                this.pushPage({
                    path: `/planmodelchat`,
                    title: '模型对话',
                    query: {
                        id: selection[0].id,
                        modelName: selection[0].name,
                        modelId: selection[0].modelId
                    }
                })
            }
        }
    ],
    /**
     * @param {Array} module 模型对象数据
     */
    afterModuleJSON: function (module) {
        let fieldList = module.fieldList
        let execGpuCard = fieldList.find(o => o.name === 'execGpuCard')
        execGpuCard.isHide = true
        return module
    },
}