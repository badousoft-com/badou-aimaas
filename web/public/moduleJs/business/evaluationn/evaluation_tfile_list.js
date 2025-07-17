
export default {
    buttons: [
        {
            id: 'add', name: '配置评价训练集', icon: 'add', type: 'primary',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()
                let formId = 'selectSft9990'
                this.$dialog.init({
                    type: 'standerListCode',
                    id: formId,
                    title: '配置评价训练集(监督指令格式)',
                    mdCode: 'maas_train_file_select',
                    //传值
                    customSetting: {
                        paramsBeforeRequest: function (params) {
                            let searchParam = params.searchParam ? JSON.parse(params.searchParam) : []
                            searchParam.push({ "name": "dataFormat", "value": 0, "type": "exact-match", "tagName": "" })
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
                            name: '关联',
                            type: 'primary',
                            icon: 'save',
                            handler: function () {
                                // getDialogConObj:全局封装，3为指定模型列表，返回该页面作用域
                                let aimRef = this.getDialogConObj(formId, 3)
                                let selection = aimRef.selection
                                // 是否选择了一行数据
                                if (selection.length == 0) {
                                    this.$message.warning('请选择一行数据！')
                                    return
                                }
                                // selection.forEach((item, index) => {
                                //     delete item.createTime
                                //     delete item.updateTime
                                // })

                                this.post('/project/maas/trainfiledialogueevaluation/trainfiledialogueevaluationsave/correlation.do?mdCode=maas_train_file_dialogue_evaluation', {
                                    evaluationId: this.$route.params.id,
                                    trainFileId: selection.map(e =>e.id).join(",")
                                }).then((res) => {
                                    if (res?.hasOk) {
                                        this.$dialog.close()
                                        this.$message.success('关联成功!')
                                        // 更新列表页面数据
                                        listPageRef.init()
                                    } else {
                                        this.$message.error(res.message)
                                    }
                                }).finally(() => {
                                    btnObj.loading = false
                                })
                            }
                        }
                    ],
                    fullHeight: true
                })
            }
        },
        { id: 'edit', isHide: true },

    ]
}
