export default {
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        {
            id: 'create-train', name: '智能推荐生成', isEdit: true, icon: 'export', type: 'primary', click: function (btnObj) {
                let listPageRef = this.listPageRef()
                // 定义弹窗唯一标识
                let signId = 'select-train'
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
                        name: '确认智能生成',
                        icon: 'save',
                        type: 'primary',
                        loading: false,
                        click: function (btnObj) {
                            // getDialogConObj:全局封装，4为指定模型表单，返回该页面作用域
                            let formObj = this.getDialogConObj(signId, 4)
                            formObj.validateForm().then(res => {
                                btnObj.loading = true
                                let url = `${this.BASEURL}/project/apihelper/apihelper/createQustionByTip`
                                // 提交接口

                                this.postFile(url, {
                                    assistantId: res.assistantId,
                                    assistantName: res.assistantName,
                                    callWord: res.callWord,
                                    knowledgeContent: res.knowledgeContent,
                                    knowledgeFiles: res.knowledgeFile,
                                    id: this.$route.params.id
                                }, (isSuccess, res) => {
                                    btnObj.loading = false
                                    if (isSuccess && res?.hasOk) {
                                        this.$dialog.close()
                                        // 更新列表页面数据
                                        listPageRef.init()
                                        this.$message.success('智能推荐生成成功!')
                                    } else {
                                        this.$message.error(`智能推荐生成失败!！${res?.message}`)
                                    }
                                }, true)
                                // //发起生成结果请求
                                // this.post('/project/apihelper/apihelper/createQustionByTip', {
                                //     assistantId: res.assistantId,
                                //     assistantName: res.assistantName,
                                //     callWord: res.callWord,
                                //     referenceRange: res.referenceRange,
                                //     id: this.$route.params.id
                                // }).then((res) => {
                                //     if (res?.hasOk) {
                                //         this.$message.success("生成成功!")
                                //         this.$dialog.close()
                                //         // 更新列表页面数据
                                //         listPageRef.init()
                                //     } else {
                                //         this.$message.error(`生成失败`)
                                //     }
                                // }).finally(() => {
                                //     // 设置按钮状态
                                //     btnObj.loading = false
                                // })
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
                    title: '配置智能推荐规则',
                    // 模型编码c
                    mdCode: 'maas_problem_data_create',
                    // 根据内容自适应高度
                    // isAutoFix: true,
                    width: '1500px',
                    // 详情数据id
                    // detailId: '36dbdb8b765a4684a6b718df729a81b1c66529c19c1049db8092a9294dd75103',
                    // 弹窗中按钮组
                    handlerList: btnList,
                })
            }
        },

    ],
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
            module.searchCondition = module.searchCondition.filter(i => i.id != 'feedback' && i.id != 'input' && i.id != 'system'  && i.id != 'ktoTag');
        } else if (dataFormat == '53') {
            //偏好 只有问题和劣质优质回答 屏蔽掉其他的
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
}