export default {
    afterListJSON: function (data) {
        let nav = this.$parent.$parent.$parent.$parent.$parent.$refs.nav
        if (nav) {
            nav.$refs.tabs[1].innerText = '样本集(' + this.$data.total + "条)"
        }
        return data
    },
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `${this.BASEURL}/project/maas/traindataproblem/traindataproblemlist/listJSON?mdCode=maas_problem_data_new_link_list`
    },
    buttons: [
        {
            id: 'add', name: '关联新样本数据集', icon: 'add', type: 'primary',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()
                let nowPageRef = this.$parent.$parent.$parent.$parent.$parent.$refs.tabs.$children[3].$children[0].$refs.list.$children[0].$refs.list
                this.$dialog.init({
                    type: 'standerListCode',
                    id: this.$route.params.id,
                    title: '关联样本数据集',
                    mdCode: 'maas_problem_data_new_link',
                    //传值
                    customSetting: {
                        paramsBeforeRequest: function (params) {
                            let searchParam = params.searchParam ? JSON.parse(params.searchParam) : []
                            searchParam.push({ "name": "trainFileId", "value": this.$route.params.id, "type": "exact-match", "tagName": "" })
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
                                let aimRef = this.getDialogConObj(this.$route.params.id, 3)
                                let selection = aimRef.selection
                                selection.forEach((item, index) => {
                                    delete item.createTime
                                    delete item.updateTime
                                })

                                this.post('/project/maas/traindataproblem/traindataproblemsave/linkOneProblem', {
                                    trainFileId: this.$route.params.id,
                                    problemDataEntityList: selection
                                }, {
                                    headers: {
                                        'Content-Type': 'application/json'
                                    }
                                }
                                ).then((res) => {
                                    if (res?.hasOk) {
                                        this.$dialog.close()
                                        this.$message.success('关联成功!')
                                        // 更新列表页面数据
                                        listPageRef.init()
                                        //刷新样本语料(**条)
                                        nowPageRef.init()
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
        {
            id: 'delete', name: '解除关联',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()
                let selection = this.getSelection()
                let nowPageRef = this.$parent.$parent.$parent.$parent.$parent.$refs.tabs.$children[3].$children[0].$refs.list.$children[0].$refs.list
                this.post('/project/maas/traindataproblem/traindataproblemsave/deleteLink', {
                    trainFileId: this.$route.params.id,
                    problem: selection[0].problemId,
                    id: selection[0].id,
                }).then((res) => {
                    if (res?.hasOk) {
                        this.$dialog.close()
                        this.$message.success('解除成功!')
                        // 更新列表页面数据
                        listPageRef.init()
                        //刷新样本语料(**条)
                        nowPageRef.init()
                    } else {
                        this.$message.error(res.message)
                    }
                }).finally(() => {
                    btnObj.loading = false
                })
            }
        }

    ]
}