export default {
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
        }
    },
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        {
            id: 'aimaasScope', name: '模型市场', icon: 'shop', type: 'primary',isHide:true, click: function (btnObj) {
                this.pushPage({
                    path: `/aiMaasScope`,
                    title: '模型市场'
                })
            }
        },
        {
            id: 'startmodel', name: '启动模型', icon: 'start', type: 'primary', 
            loading: false,
            click: function (btnObj) {
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
                this.$delete(selection[0], 'createTime')
                this.$delete(selection[0], 'updateTime')
                this.$message.warning('开始启动模型...')
                btnObj.loading = true // 重置按钮加载状态
                this.post('/project/maas/tuningmodeln/tuningmodelnedit/startModel', selection[0],
                    {
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    }).then((res) => {
                        if (res?.hasOk) {
                            this.$message.success('启动模型成功!')
                            this.pushPage({
                                path: `/module/stander/list/maas_model_app/placeholder`,
                                title: '模型运行管理'
                            })
                        } else {
                            this.$message.error(res.message)
                        }
                        btnObj.loading = false
                    }).finally(() => {
                        btnObj.loading = false
                    })
            }
        },

        {
            id: 'putShelves',
            name: "上架微调模型",
            icon: 'upload',
            type: 'primary',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                if (selection[0].source != 1) {
                    this.$message.warning('只有微调模型支持上下架操作')
                    return
                }
                let formId = 'putShelves'
                this.$dialog.init({
                    id: formId,
                    title: '填写上架信息',
                    // 高度根据内容自适应
                    isAutoFix: true,
                    dataList: [
                        {
                            type: 'textarea', label: '发布版本', name: 'pubVersion', value: '', placeholder: '请输入', rules: [
                                { required: true, message: '请输入', trigger: 'blur' },
                            ]
                        },
                        {
                            type: 'textarea', label: '发布描述', name: 'pubMsg', value: '', placeholder: '请输入', rules: [
                                { required: true, message: '请输入', trigger: 'blur' },
                            ]
                        }
                    ],
                    handlerList: [
                        {
                            name: '取消',
                            icon: 'cancel',
                            type: 'danger',
                            click: function () {
                                this.$dialog.close()
                            }
                        }, {
                            name: '保存',
                            icon: 'save',
                            type: 'primary',
                            loading: false,
                            click: function (btnObj) {
                                btnObj.loading = true
                                // getDialogConObj:全局封装，2为指定自定义表单，返回该对象作用域
                                let formObj = this.getDialogConObj(formId, 2)
                                formObj.validateForm().then(res => {
                                    this.post(`${this.BASEURL}/project/maas/tuningmodeln/tuningmodelnedit/coverTunModel`, {
                                        id: selection[0].tunModelId,
                                        type: 1,
                                        pubVersion: res.pubVersion,
                                        pubMsg: res.pubMsg
                                    }).then(res => {
                                        if (res?.hasOk) {
                                            this.$message.success('上架成功!')
                                            // 更新列表页面数据
                                            listPageRef.init()
                                        } else {
                                            this.$message.error(res.message)
                                        }
                                    })
                                }).finally(() => {
                                    btnObj.loading = false
                                })

                            }
                        }
                    ],
                })
            }
        },
        {
            id: 'remoShelves',
            name: "下架微调模型",
            type: 'danger',
            icon: 'download',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                if (selection[0].source != 1) {
                    this.$message.warning('只有微调模型支持上下架操作')
                    return
                }
                this.$confirm("下架后相关正在运行的应用同时会被关闭!", "提升确认", {
                    iconClass: "el-icon-question",//自定义图标样式
                    confirmButtonText: "确认下架",//确认按钮文字更换
                    cancelButtonText: "取消",//取消按钮文字更换
                    showClose: true,//是否显示右上角关闭按钮
                    type: "warning",//提示类型  success/info/warning/error
                }).then(() => {
                    this.post(`${this.BASEURL}/project/maas/tuningmodeln/tuningmodelnedit/coverTunModel`, {
                        id: selection[0].tunModelId,
                        type: 0
                    }).then(res => {
                        if (res?.hasOk) {
                            this.$message.success('下架成功!')
                            // 更新列表页面数据
                            listPageRef.init()
                        } else {
                            this.$message.error(res.message)
                        }
                    })
                }).catch(() => {
                    //取消操作
                });
            }
        },

    ],
    dataUrl: function () {
        return `${this.BASEURL}/project/maas/modelwarehouse/modelwarehouselist/listJSON?mdCode=maas_model_warehouse_main`
    },
}
