
export default {
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'add', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'edit', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'delete', name: '导入', icon: 'import', type: 'primary', isHide: true },
        {
            id: 'doTunPlan', name: '管理微调任务', icon: 'provinces-d', type: 'primary',
            click: function (btnObj) {
                let id = 'doTunPlan'
                this.$dialog.init({
                    type: 'standerListCode',
                    id: id,
                    title: '管理微调任务',
                    mdCode: 'maas_fine_tuning_modeln_gpu',
                    //传值
                    customSetting: {
                        paramsBeforeRequest: function (params) {
                            let searchParam = params.searchParam ? JSON.parse(params.searchParam) : []
                            searchParam.push({ "name": "serverId", "value": this.$route.params.id, "type": "exact-match", "tagName": "" })
                            searchParam.push({"name":"doStatus","value":"1","type":"exact-match","tagName":""})
                            params.searchParam = JSON.stringify(searchParam)
                            return params
                        }
                    },
                    handlerList: [
                        {
                            name: '取消',
                            type: 'danger',
                            icon: 'cancel',
                            handler: function () {
                                // 关闭弹窗表单
                                this.$dialog.close()
                            }
                        }, {
                            name: '停止',
                            type: 'danger',
                            icon: 'stop',
                            handler: function () {
                                // getDialogConObj:全局封装，3为指定模型列表，返回该页面作用域
                                let aimRef = this.getDialogConObj(id, 3)
                                let listPageRef = this.$refs.doTunPlan.$children[0].$children[0].listPageRef()
                                let selection = aimRef.selection
                                // 是否选择了一行数据
                                if (selection.length !== 1) {
                                    this.$message.warning('请选择一行数据！')
                                    return
                                }
                                this.$confirm("执行中的任务会被停止!所有相关的所有服务都会停止!请确认", "提示", {
                                    iconClass: "el-icon-question",//自定义图标样式
                                    confirmButtonText: "确定",//确认按钮文字更换
                                    cancelButtonText: "取消",//取消按钮文字更换
                                    showClose: true,//是否显示右上角关闭按钮
                                    type: "warning",//提示类型  success/info/warning/error
                                }).then(() => {
                                    this.post(`${this.BASEURL}/project/maas/tuningmodeln/tuningmodelnedit/closeTask`, {
                                        id: selection[0].id
                                    }).then(res => {
                                        if (res?.hasOk) {
                                            this.$message.success('作废成功!')
                                            // 更新列表页面数据
                                            listPageRef.init()
                                        } else {
                                            this.$message.warning(res.message)
                                        }
                                    })
                                }).catch(() => {
                                    //取消操作
                                });
                            }
                        }
                    ],
                    fullHeight: true
                })
            }
        },
        { id: 'doModelApp', name: '管理模型任务', icon: 'import', type: 'primary',  
        click: function (btnObj) {
            let id = 'doModelApp'
            this.$dialog.init({
                type: 'standerListCode',
                id: id,
                title: '管理模型任务',
                mdCode: 'maas_model_app_gpu',
                //传值
                customSetting: {
                    paramsBeforeRequest: function (params) {
                        let searchParam = params.searchParam ? JSON.parse(params.searchParam) : []
                        searchParam.push({ "name": "serverId", "value": this.$route.params.id, "type": "exact-match", "tagName": "" })
                        // searchParam.push({"name":"status","value":2,"type":"exact-match","tagName":""})
                        params.searchParam = JSON.stringify(searchParam)
                        return params
                    }
                },
                handlerList: [
                    {
                        name: '取消',
                        type: 'danger',
                        icon: 'cancel',
                        handler: function () {
                            // 关闭弹窗表单
                            this.$dialog.close()
                        }
                    }, {
                        name: '停止',
                        type: 'danger',
                        icon: 'stop',
                        handler: function () {
                            // getDialogConObj:全局封装，3为指定模型列表，返回该页面作用域
                            let aimRef = this.getDialogConObj(id, 3)
                            let selection = aimRef.selection
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
                    }
                ],
                fullHeight: true
            })
        }
        }
    ],
    paramsBeforeRequest: function (params) {
        params.serverId = this.$route.params.id
        // this:指向src/components/frame/Common/MList/index.vue页面作用域
        return params
    },
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `${this.BASEURL}/project/server/servergpulist/getServerGpu`
    },
    renderField: {
        // 字段键名
        usedGraphicsMemory: {
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
                return `<span>${cellValue}/${row.maxGraphicsMemory}MiB</span>`
            }
        },
        // 字段键名
        fanSpeedPer: {
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
                return `<span>${cellValue}%</span>`
            }
        },
        // 字段键名
        usageRate: {
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
                return `<span>${cellValue}%/100%</span>`
            }
        },
        // 字段键名
        temperature: {
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
                return `<span>${cellValue}C</span>`
            }
        },
        // 字段键名
        performanceStatus: {
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
                return `<span>P${cellValue}</span>`
            }
        },
        // 字段键名
        currentPowerDissipation: {
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
                return `<span>${cellValue}W/${row.maxPowerDissipation}W</span>`
            }
        },
        // 字段键名
        status: {
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
                return `<span>${row.usedGraphicsMemory > 3 ? '在用' : '空闲'}</span>`
            }
        }
    }
}