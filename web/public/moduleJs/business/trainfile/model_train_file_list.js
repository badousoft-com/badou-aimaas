export default {
    importUrl: '/project/maas/problemdata/problemdataimport/baseImportForResult',
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        {
            id: 'upStatus', name: '启/停用', icon: 'edit', type: 'primary',
            click: function (btnObj) {
                // /module/stander/edit/maas_train_file/2a3bb4ad73e74e8598d8b27a34eea06bca806bf7493042d6971dbfe49f7ec034
                let selection = this.getSelection()
                if (selection.length == 0) {
                    this.$message.warning("请至少选择一行数据")
                    return
                }
                let listPageRef = this.listPageRef()
                btnObj.loading = true
                this.post('/project/maas/trainfile/trainfilesave/updateStatus', selection, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then((res) => {
                    if (res?.hasOk) {
                        // 更新列表页面数据
                        listPageRef.init()
                        this.$message.success('变更启/停状态成功!')
                    } else {
                        this.$message.error("变更失败!请联系管理员!")
                    }
                }).finally(() => {
                    btnObj.loading = false
                })
            }
        },
        {
            id: 'edit', name: '导入', icon: 'edit', type: 'warning',
            click: function () {
                // /module/stander/edit/maas_train_file/2a3bb4ad73e74e8598d8b27a34eea06bca806bf7493042d6971dbfe49f7ec034
                let selection = this.getSelection()
                if (selection.length != 1) {
                    this.$message.error("请选择一行数据")
                    return
                }
                this.pushPage({
                    path: `/module/stander/edit/${this.mdCode}_edit/${selection[0].id}`,
                    title: '编辑页面'
                })
            }
        },
        {
            id: 'fileImport', name: '导入文件数据', icon: 'import', type: 'primary',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()
                let selection = this.getSelection()
                let _this = this
                // 定义弹窗唯一标识
                let signId = 'helloModelForm'
                let mdCode = this.mdCode
                // 定义弹窗所需按钮
                let btnList = [
                    {
                        name: '取消',
                        type: 'danger',
                        icon: 'cancel',
                        click: function () {
                            // 关闭弹窗表单
                            this.$dialog.close()
                        }
                    }, {
                        name: '确认开始!(异步生成需要时间)',
                        icon: 'save',
                        type: 'primary',
                        loading: false,
                        click: function (btnObj) {
                            // getDialogConObj:全局封装，4为指定模型表单，返回该页面作用域
                            let formObj = this.getDialogConObj(signId, 4)
                            formObj.validateForm().then(res => {
                                if ("0" == res.type) {
                                    if (selection.length == 0) {
                                        this.$message({
                                            type: 'warning',
                                            message: '请至少选择一行训练集文件！'
                                        })
                                        return
                                    }
                                }
                                res.ids = selection.map((obj) => { return obj.id }).join(",");

                                btnObj.loading = true // 重置按钮加载状态
                                let url = `${_this.BASEURL}/project/maas/trainfile/trainfilesave/createTrainFileData?mdCode=${mdCode}`
                                // 提交接口
                                this.postFile(url, res, (isSuccess, res) => {
                                    btnObj.loading = false
                                    if (isSuccess && res?.hasOk) {
                                        // 更新列表页面数据
                                        listPageRef.init()
                                        this.$message.success('提交生成任务成功!')
                                    } else {
                                        this.$message.error(`保存失败！${res?.message}`)
                                    }
                                    this.$dialog.close()
                                }, true)
                            })
                        }
                    }
                ]
                this.$dialog.init({
                    // 弹窗内容类型
                    type: 'standerEditCode',
                    // 弹窗唯一标识，注意唯一标识与上面出现的函数getModelListObj的参数值保持一致
                    id: signId,
                    // 弹窗标题
                    title: '请选择导入类型',
                    // 模型编码c
                    mdCode: 'maas_train_importtype',
                    // 根据内容自适应高度
                    // isAutoFix: true,
                    width: '1200px',
                    height: '440px',
                    // 详情数据id
                    // detailId: '36dbdb8b765a4684a6b718df729a81b1c66529c19c1049db8092a9294dd75103',
                    // 弹窗中按钮组
                    handlerList: btnList,
                })
            }
        },
    ],
    renderField: {
        // 字段键名
        upStatusDesc: {
            /**
             * @param {Object} h 渲染函数参数对象
             * @param {Object} context 对象（包含props,children,slots,scopedSlots,parent,listeners,injection）
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前字段值
             * @param {Number} index 该行下角标
             * @param {Object} fieldObj 字段对象
             * @param {Object} scope 表格项对象
             */
            render: function (h, context, row, column, cellValue, index, fieldObj, scope) {
                let upStatus = row.upStatus
                if (upStatus == null || upStatus == 0 ) {
                    return (<span>未启用</span>)
                } else if (upStatus == 1) {
                    return (<span style="color:green">启用</span>)
                }else{
                    return (<span style="color:red">未识别</span>)
                }
            }
        }
    },
}