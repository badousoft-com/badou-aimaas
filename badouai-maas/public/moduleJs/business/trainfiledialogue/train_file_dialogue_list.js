export default {
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `${this.BASEURL}/project/maas/trainfiledialogue/trainfiledialoguelist/listJSON?mdCode=${this.mdCode}`
    },
    /**
     * @param {Array} module 模型对象数据
     */
    afterModuleJSON: function (module) {
        let dataFormat = this.$route.query.dataFormat
        let fieldList = module.fieldList
        //指令监督 只有回答的内容 屏蔽掉其他的
        if (dataFormat == '1') {
            let chosena = fieldList.find(o => o.name === 'chosena')
            chosena.isHide = true
            let rejecteda = fieldList.find(o => o.name === 'rejecteda')
            rejecteda.isHide = true
            let ktoTag = fieldList.find(o => o.name === 'ktoTagDesc')
            ktoTag.isHide = true
            // 控制搜索框
            module.searchCondition = module.searchCondition.filter(i => i.id != 'chosena' && i.id != 'rejecteda' && i.id != 'ktoTag');
        } else if (dataFormat == '0') {
            //预训练 只有内容 屏蔽掉其他的
            let question = fieldList.find(o => o.name === 'question')
            question.display = '训练内容(content)'
            let feedback = fieldList.find(o => o.name === 'feedback')
            feedback.isHide = true
            let input = fieldList.find(o => o.name === 'input')
            input.isHide = true
            let chosena = fieldList.find(o => o.name === 'chosena')
            chosena.isHide = true
            let rejecteda = fieldList.find(o => o.name === 'rejecteda')
            rejecteda.isHide = true
            let system = fieldList.find(o => o.name === 'system')
            system.isHide = true
            let ktoTag = fieldList.find(o => o.name === 'ktoTagDesc')
            ktoTag.isHide = true
            // 控制搜索框
            module.searchCondition = module.searchCondition.filter(i => i.id == 'question' || i.id == 'createTime');
        } else if (dataFormat == '50') {
            //偏好 只有问题和劣质优质回答 屏蔽掉其他的
            let feedback = fieldList.find(o => o.name === 'feedback')
            feedback.isHide = true
            let input = fieldList.find(o => o.name === 'input')
            input.isHide = true
            let system = fieldList.find(o => o.name === 'system')
            system.isHide = true
            let ktoTag = fieldList.find(o => o.name === 'ktoTagDesc')
            ktoTag.isHide = true
            // 控制搜索框
            module.searchCondition = module.searchCondition.filter(i => i.id != 'feedback' && i.id != 'input' && i.id != 'system' && i.id != 'ktoTag');
        } else if (dataFormat == '53') {
            //KTO 只有问题、回复和KTO 屏蔽掉其他的
            let chosena = fieldList.find(o => o.name === 'chosena')
            chosena.isHide = true
            let rejecteda = fieldList.find(o => o.name === 'rejecteda')
            rejecteda.isHide = true
            let system = fieldList.find(o => o.name === 'system')
            system.isHide = true
            // 控制搜索框
            module.searchCondition = module.searchCondition.filter(i => i.id != 'chosena' && i.id != 'rejecteda' && i.id != 'system');
        }

        // 执行你的数据变更
        // do something change the module data
        // 注意：修改完之后必须将模型数据返回
        return module
    },
    afterListJSON: function (data) {
        this.$parent.$parent.$parent.$parent.$parent.$refs.nav.$refs.tabs[2].innerText = '样本语料(' + this.$data.total + "条)"
        return data
    },
    buttons: [
        {
            id: 'updatesystem',
            name: '更新系统提示词',
            icon: 'edit',
            type: 'primary',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()

                let formId = 'updatesystem'
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length <= 0) {
                    this.$message.warning('请至少选择一行数据！')
                    return
                }
                this.$dialog.init({
                    id: formId,
                    title: '批量更新系统提示词',
                    // 高度根据内容自适应
                    isAutoFix: true,
                    dataList: [
                        { type: 'textarea', label: '新系统提示词', name: 'newSystem', value: '', placeholder: '请输入新提示词',require:true},
                        // { type: 'text', label: '占比变宽标签', name: 'largePer', value: '12', placeholder: '请输入', columnPer: 16 }
                    ],
                    handlerList: [
                        {
                            name: '取消',
                            icon: 'cancel',
                            type: 'danger',
                            click: function () {
                                this.$confirm('取消将不保存表单数据, 是否继续?', '提示', {
                                    confirmButtonText: '确定',
                                    cancelButtonText: '取消',
                                    type: 'warning'
                                }).then(() => {
                                    // 关闭弹窗表单
                                    this.$dialog.close()
                                }).catch(() => {
                                    // 取消关闭        
                                })
                            }
                        }, {
                            name: '保存',
                            icon: 'save',
                            type: 'primary',
                            loading: false,
                            click: function (btnObj) {
                                let ids = []
                                for (const i of selection) {
                                    ids.push(i.id)
                                }
                                btnObj.loading = true
                                let formObj = this.getDialogConObj(formId, 2)
                                formObj.validateForm().then(res => {
                                    this.post('/project/maas/trainfiledialogue/trainfiledialoguesave/batchUpdateTag', {
                                        ids: ids,
                                        newSystemRole: res.newSystem
                                    }, {
                                        headers: {
                                            'Content-Type': 'application/json'
                                        }
                                    }).then((res) => {
                                        if (res?.hasOk) {
                                            listPageRef.init()
                                            this.$message.success('修改成功!')
                                            this.$dialog.close()
                                        } else {
                                            this.$message.error('修改失败!')
                                        }
                                    }).finally(() => {

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
            id: 'edit',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()
                let dataFormat = this.$parent.$parent.$parent.$parent.$children[1].$children[0].fieldList.find(o => o.name === 'dataFormat')
                this.$route.query.dataFormat = dataFormat.value
                // 定义弹窗唯一标识
                let signId = 'edit-file-dialogue'
                let _this = this
                let mdCode = "maas_train_file_dialogue_nohis";
                if (dataFormat.value == '0') {
                    mdCode = "maas_train_file_dialogue";
                }
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
                        name: '保存',
                        icon: 'save',
                        type: 'primary',
                        loading: false,
                        click: function (btnObj) {
                            // getDialogConObj:全局封装，4为指定模型表单，返回该页面作用域
                            let formObj = this.getDialogConObj(signId, 4)
                            formObj.validateForm().then(res => {
                                btnObj.loading = true // 重置按钮加载状态
                                this.post(`/jdbc/common/basecommonsave/saveIncludeFile.do?mdCode=${_this.mdCode}`, res).then((res) => {
                                    if (res?.hasOk) {
                                        listPageRef.init()
                                        this.$message.success('修改成功!')
                                        this.$dialog.close()
                                    } else {
                                        this.$message.error(`修改失败${res?.message}`)
                                    }
                                }).finally(() => {
                                    btnObj.loading = false
                                })
                            })
                        }
                    }
                ]
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                this.$dialog.init({
                    // 弹窗内容类型
                    type: 'standerEditCode',
                    // 弹窗唯一标识，注意唯一标识与上面出现的函数getModelListObj的参数值保持一致
                    id: signId,
                    // 弹窗标题
                    title: '样本语料修改',
                    // 模型编码c
                    mdCode: mdCode,
                    // 根据内容自适应高度
                    // isAutoFix: true,
                    width: '1300px',
                    // 详情数据id
                    detailId: selection[0].id,
                    // 弹窗中按钮组
                    handlerList: btnList,
                })
            }
        }
    ]
}