
export default {
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `/project/maas/modelapp/modelapplist/listJSON?mdCode=maas_model_app`
    },
    renderField: {
        // 字段键名
        contentLength: {
            /**
             * 格式化展示
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前表格项值
             * @param {Number} index 所在行下角标
             * @param {Object} fieldObj 字段所在列的配置数据
             */
            formatter: function (row, column, cellValue, index, fieldObj) {
                if (!cellValue) {
                    return ""
                }
                cellValue = cellValue + "KB"
                // this: 作用域指代核心列表[mList/index]所在页面作用域
                return `<span>${cellValue}</span>`
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
                            <bd-icon name="start" style="color:orange"></bd-icon>
                            <span style="color:orange">启动中</span>
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
                if (row.status == 4) {
                    return (
                        <span>
                            <bd-icon name="multi" style="color:orange"></bd-icon>
                            <span style="color:orange">手动关闭</span>
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
            id: 'restart', name: '重启模型', icon: 'import', type: 'primary',
            loading: false,
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                btnObj.loading = true
                let listPageRef = this.listPageRef()
                this.post('/project/maas/modelapp/modelappedit/restartPod', {
                    id: selection[0].id
                }).then((res) => {
                    if (res?.hasOk) {
                        listPageRef.init()
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
            loading: false,
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
            id: 'reflush',
            name: '刷新',
            icon: 'refresh-fill',
            type: 'primary',
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
        // {
        //     id: 'modeltalk', name: '模型对话', icon: 'reply', type: 'primary', loading: false, click: function (btnObj) {
        //         // this: bd-module-list组件作用域
        //         // 获取选中列表数据
        //         let selection = this.getSelection()
        //         if (selection.length !== 1) {
        //             this.$message({
        //                 type: 'warning',
        //                 message: '请选择一行！'
        //             })
        //             return
        //         }
        //         this.$message.warning('开始初始化')
        //         btnObj.loading = true // 重置按钮加载状态
        //         this.post('/project/maas/modelapp/modelappedit/linkAppTalk', {
        //             id: selection[0].id
        //         }).then((res) => {
        //             if (res?.hasOk) {
        //                 window.open(res.message, '_blank');
        //             } else {
        //                 this.$message.error("初始化失败." + res.message)
        //             }
        //         }).finally(() => {
        //             btnObj.loading = false
        //         })
        //     }
        // },
        {
            id: 'modeltalk',
            name: '模型对话',
            icon: 'reply',
            type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                // if (selection.length !== 1) {
                //     this.$message.warning('请选择一行数据！')
                //     return
                // }
                // if (selection[0].status != 2) {
                //     this.$message.warning('该模型应用未启动成功!')
                //     return
                // }
                //必须是启动完成的模型 暂不做 
                this.pushPage({
                    path: `/aichat`,
                    title: '模型对话',
                    query: {
                        // id: selection[0].id,
                        // modelName: selection[0].name,
                        // modelId: selection[0].modelId
                    }
                })
            }
        },
        {
            id: 'api',
            name: '显示API信息',
            icon: 'edit',
            type: 'danger',
            loading: false,
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                btnObj.loading = true
                this.$message.warning('开始检查,请稍等。检查通过则可以使用')
                let url = '/project/maas/modelapp/modelapplist/loadApiMsg'
                let copyText = ''
                let _this = this
                // 提交接口
                this.post(url, {
                    id: selection[0].id,
                    content: '你好'
                }).then((res) => {
                    btnObj.loading = false
                    if (res?.hasOk) {
                        let row = res.bean
                        copyText = "请求地址: " + row.address + "\n请求方法: " + row.method + "\n请求内容格式: " + res.bean.contentType
                        copyText = copyText + `\n${row.requestTitle}: ${row.paramsSample}`
                        copyText = copyText + `\n返回结果例子: ${row.responseSample}`
                        this.$pageDialog.init({
                            title: '对接模型接口',
                            pageUrl: 'business/apipages/index.vue',
                            outScope: this,
                            data: res.bean,
                            handlerList: [
                                {
                                    id: 'save', name: '复制到剪切板', icon: 'save', type: 'primary', click: function (itemObj) {
                                        this.$copyText(copyText).then(function (e) {
                                            _this.$message.success('复制成功!')
                                        }, function (e) {
                                            _this.$message.error('复制失败!')
                                        })
                                    }
                                }
                            ],
                            // isAutoFix: true // 根据内容自适应
                            height: '800px',
                            with: '2000px',
                        })
                        this.$message.success('校验通过！')
                    } else {
                        this.$message.error(`保存失败！${res.message}`)
                    }
                }).finally(() => {
                    // 设置按钮状态
                    btnObj.loading = false
                })
            }
        }
    ],

}