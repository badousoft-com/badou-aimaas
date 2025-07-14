export default {
    importUrl: '/project/maas/problemdata/problemdataimport/baseImportForProblemResult',
    renderField: {
        //将字段name展示为 : 适应行业-样本集名称
        name: {
            formatter(row, column, cellValue, index, fieldObj) {
                const industryDesc = row.applicableIndustryDesc;
                const value = cellValue || '-';
                if (!industryDesc && value === '-') {
                    return '';
                }
                return `<span>${industryDesc ? industryDesc + ' - ' : ''}${value}</span>`;
            }
        }
    },
    paramsBeforeRequest: function (params) {
        let arrTemp = []
        // 字符串转数组
        arrTemp = JSON.parse(params.searchParam)
        // 推入当前当前用户
        if (!arrTemp.some(item => item.name === 'creator')) {
            arrTemp.push({ "name": "creator", "value": this.$store.getters.userInfo.id, "type": "text-query", "tagName": "" })
        }
        // 数组转字符串
        params.searchParam = JSON.stringify(arrTemp)
        return params
    },
    buttons: [
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
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        {
            id: 'import', name: '导入', icon: 'import', type: 'primary',

        },
        {
            id: 'datacenter', name: '训练集广场', icon: 'import', type: 'primary', click: function (btnObj) {
                this.pushPage({
                    path: `/myProject/maas_problem_data_new_center/placeholder`,
                    title: '样本数据广场'
                })
            }
        },
        {
            id: 'coverTrainData', name: '转成训练集', icon: 'import', type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                let currentIds = [selection[0].id]
                for (const i of selection) {
                    currentIds.push(i.id)
                }
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
                        name: '确认',
                        icon: 'save',
                        type: 'primary',
                        loading: false,
                        click: function (btnObj) {
                            // getDialogConObj:全局封装，4为指定模型表单，返回该页面作用域
                            let formObj = this.getDialogConObj(signId, 4)
                            formObj.validateForm().then(res => {
                                let signListId = 'signListId'
                                //如果是导入到新文件 直接导入就结束
                                if (res.type == 1) {
                                    btnObj.loading = true
                                    this.post('/project/maas/problemdata/problemdatasave/exportTrainFile', {
                                        ids: currentIds,
                                        type: res.type,
                                        fileName: res.fileName,
                                        exportRange: res.exportRange
                                    },
                                        {
                                            headers: {
                                                'Content-Type': 'application/json'
                                            }
                                        }
                                    ).then((res) => {
                                        if (res?.hasOk) {
                                            this.$dialog.close()
                                            this.$message.success('导入成功!')
                                            this.pushPage({
                                                path: `/module/stander/list/maas_train_file/placeholder`,
                                                title: '训练集文件'
                                            })
                                        } else {
                                            this.$message.error(res.message)
                                        }
                                    }).finally(() => {
                                        btnObj.loading = false
                                    })
                                }
                                if (res.type == 0) {
                                    //如果类型是旧文件.则打开训练集文件列表让客户选择
                                    this.$dialog.close()
                                    this.$dialog.init({
                                        type: 'standerListCode',
                                        id: signListId,
                                        title: '请选择导入的训练集!',
                                        mdCode: 'maas_problem_data_selectlist',
                                        //传值
                                        // customSetting: {
                                        //     paramsBeforeRequest: function (params) {
                                        //         // this:指向src/components/frame/Common/MList/index.vue页面作用域
                                        //         let searchParam = params.searchParam ? JSON.parse(params.searchParam) : []
                                        //         if(res.name){
                                        //             searchParam.push({ "name": "name", "value": res.name, "type": "text-query", "tagName": "" })
                                        //         }
                                        //         if(res.applicableIndustry){
                                        //             searchParam.push({ "name": "applicableIndustry", "value": res.applicableIndustry, "type": "exact-match", "tagName": "" })
                                        //         }
                                        //         if(res.applicableSubject){
                                        //             searchParam.push({ "name": "applicableSubject", "value": res.applicableSubject, "type": "exact-match", "tagName": "" })
                                        //         }
                                        //         params.searchParam = JSON.stringify(searchParam)
                                        //         return params
                                        //     }
                                        // },
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
                                                name: '确认导入',
                                                type: 'primary',
                                                icon: 'save',
                                                loading: false,
                                                handler: function (btnObj) {
                                                    // getDialogConObj:全局封装，3为指定模型列表，返回该页面作用域
                                                    let aimRef = this.getDialogConObj(signListId, 3)
                                                    let selection = aimRef.selection
                                                    // 是否选择了一行数据
                                                    if (selection.length == 0) {
                                                        this.$message.warning('请选择一行数据！')
                                                        return
                                                    }
                                                    for (const i of selection) {
                                                        this.$delete(i, 'createTime')
                                                        this.$delete(i, 'updateTime')
                                                    }
                                                    btnObj.loading = true
                                                    this.post('/project/maas/problemdata/problemdatasave/exportTrainFile', {
                                                        ids: currentIds,
                                                        type: res.type,
                                                        valueList: selection,
                                                        fileName: res.fileName,
                                                        exportRange: res.exportRange
                                                    },
                                                        {
                                                            headers: {
                                                                'Content-Type': 'application/json'
                                                            }
                                                        }
                                                    ).then((res) => {
                                                        if (res?.hasOk) {
                                                            this.$dialog.close()
                                                            this.$message.success('导入成功!')
                                                            this.pushPage({
                                                                path: `/module/stander/list/maas_train_file/placeholder`,
                                                                title: '训练集文件'
                                                            })
                                                        } else {
                                                            this.$message.error("导入失败:" + res.message)
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
                    title: '配置转换规则',
                    // 模型编码c
                    mdCode: 'maas_problem_data_select',
                    customSetting: {
                        beforeRender: function () {
                        }
                    
                    },
                    // 根据内容自适应高度
                    // isAutoFix: true,
                    width: '500px',
                    height: '300px',
                    // 详情数据id
                    // detailId: '36dbdb8b765a4684a6b718df729a81b1c66529c19c1049db8092a9294dd75103',
                    // 弹窗中按钮组
                    handlerList: btnList,
                    // 在弹窗渲染前对字段“文件名字(仅限新文件)”进行赋值
                    customSetting: {
                        beforeRender: function () {
                            let fileNameField = this.fieldList.find(i => i.name === 'fileName')
                            if (fileNameField) {
                                fileNameField.value = selection[0].name
                            }
                        }
                    }
                })
            }
        },
        {
            id: 'match-import', name: '训练集匹配导入', icon: 'import', type: 'primary', click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                let currentId = this.getSelection()[0].id
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
                        name: '开始匹配',
                        icon: 'save',
                        type: 'primary',
                        loading: false,
                        click: function (btnObj) {
                            // getDialogConObj:全局封装，4为指定模型表单，返回该页面作用域
                            let formObj = this.getDialogConObj(signId, 4)
                            formObj.validateForm().then(res => {
                                // console.log(`筛选参数:${JSON.stringify(res)}`)
                                // btnObj.loading = true // 重置按钮加载状态
                                // alert(JSON.stringify(res))
                                // btnObj.loading = false // 重置按钮加载状态
                                let signListId = 'signListId'
                                this.$dialog.close()
                                //没问题就打开匹配到的结果
                                this.$dialog.init({
                                    type: 'standerListCode',
                                    id: signListId,
                                    title: '请选择导入的训练集!',
                                    mdCode: 'maas_train_file_select',
                                    //传值
                                    customSetting: {
                                        paramsBeforeRequest: function (params) {
                                            // this:指向src/components/frame/Common/MList/index.vue页面作用域
                                            let searchParam = params.searchParam ? JSON.parse(params.searchParam) : []
                                            if (res.name) {
                                                searchParam.push({ "name": "name", "value": res.name, "type": "text-query", "tagName": "" })
                                            }
                                            if (res.applicableIndustry) {
                                                searchParam.push({ "name": "applicableIndustry", "value": res.applicableIndustry, "type": "exact-match", "tagName": "" })
                                            }
                                            if (res.applicableSubject) {
                                                searchParam.push({ "name": "applicableSubject", "value": res.applicableSubject, "type": "exact-match", "tagName": "" })
                                            }
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
                                            name: '确认导入到训练集',
                                            type: 'primary',
                                            icon: 'save',
                                            loading: false,
                                            handler: function (btnObj) {
                                                // getDialogConObj:全局封装，3为指定模型列表，返回该页面作用域
                                                let aimRef = this.getDialogConObj(signListId, 3)
                                                let selection = aimRef.selection
                                                // 是否选择了一行数据
                                                if (selection.length !== 1) {
                                                    this.$message.warning('请选择一行数据！')
                                                    return
                                                }
                                                btnObj.loading = true
                                                this.post('/project/maas/trainfile/trainfilesave/importToQuestion', {
                                                    id: currentId,
                                                    trainFileEntitys: selection
                                                }, {
                                                    headers: {
                                                        'Content-Type': 'application/json'
                                                    }
                                                }).then((res) => {
                                                    if (res?.hasOk) {
                                                        this.$dialog.close()
                                                        this.$message.success('导入成功!')
                                                    } else {
                                                        console.log("保存数值失败!")
                                                    }
                                                }).finally(() => {
                                                    btnObj.loading = false
                                                })
                                            }
                                        }
                                    ],
                                    fullHeight: true
                                })
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
                    title: '选择训练集匹配规则',
                    // 模型编码c
                    mdCode: 'maas_train_file_select',
                    // 根据内容自适应高度
                    // isAutoFix: true,
                    width: '800px',
                    // 详情数据id
                    // detailId: '36dbdb8b765a4684a6b718df729a81b1c66529c19c1049db8092a9294dd75103',
                    // 弹窗中按钮组
                    handlerList: btnList,
                })
            }
        },
        {
            id: 'create-train', name: '智能推荐生成', icon: 'export', type: 'primary', isHide: true, click: function (btnObj) {
                //     let selection = this.getSelection()
                //     // 是否选择了一行数据
                //     if (selection.length !== 1) {
                //         this.$message.warning('请选择一行数据！')
                //         return
                //     }
                //     // 定义弹窗唯一标识
                //     let signId = 'select-train'
                //     // 定义弹窗所需按钮
                //     let btnList = [
                //         {
                //             name: '取消',
                //             type: 'danger',
                //             icon: 'cancel',
                //             click: function () {
                //                 // 关闭弹窗表单
                //                 this.$dialog.close()
                //             }
                //         }, {
                //             name: '确认智能生成',
                //             icon: 'save',
                //             type: 'primary',
                //             loading: false,
                //             click: function (btnObj) {
                //                 // getDialogConObj:全局封装，4为指定模型表单，返回该页面作用域
                //                 let formObj = this.getDialogConObj(signId, 4)
                //                 formObj.validateForm().then(res => {
                //                     btnObj.loading = true
                //                     //发起生成结果请求
                //                     this.post('/project/apihelper/apihelper/createQustionByTip', {
                //                         assistantId: res.assistantId,
                //                         assistantName: res.assistantName,
                //                         callWord: res.callWord,
                //                         referenceRange: res.referenceRange,
                //                         id: selection[0].id
                //                     }).then((res) => {
                //                         if (res?.hasOk) {
                //                             this.$message.success("生成成功!")
                //                         } else {
                //                             this.$message.error(`生成失败`)
                //                         }
                //                     }).finally(() => {
                //                         // 设置按钮状态
                //                         btnObj.loading = false
                //                     })
                //                 })
                //             }
                //         }
                //     ]
                //     this.$dialog.init({
                //         // 弹窗内容类型
                //         type: 'standerEditCode',
                //         // 弹窗唯一标识，注意唯一标识与上面出现的函数getModelListObj的参数值保持一致
                //         id: signId,
                //         // 弹窗标题
                //         title: '配置智能推荐规则',
                //         // 模型编码c
                //         mdCode: 'maas_problem_data_create',
                //         // 根据内容自适应高度
                //         // isAutoFix: true,
                //         width: '1500px',
                //         // 详情数据id
                //         // detailId: '36dbdb8b765a4684a6b718df729a81b1c66529c19c1049db8092a9294dd75103',
                //         // 弹窗中按钮组
                //         handlerList: btnList,
                //     })
            }
        },
        {
            id: 'coverSft', loading: false, name: '转成指令样本集', icon: 'edit', type: 'primary',
            click: function (btnObj) {
                // /module/stander/edit/maas_train_file/2a3bb4ad73e74e8598d8b27a34eea06bca806bf7493042d6971dbfe49f7ec034
                let selection = this.getSelection()
                if (selection.length != 1) {
                    this.$message.error("请选择一行数据")
                    return
                }
                let listPageRef = this.listPageRef()
                btnObj.loading = true
                this.post('/project/maas/problemdata/problemdatasave/coverSft', {
                    id: selection[0].id
                }).then((res) => {
                    if (res?.hasOk) {
                        // 更新列表页面数据
                        listPageRef.init()
                        this.$message.success('生成成功!')
                    } else {
                        this.$message.error("生成失败!" + res.message)
                    }
                }).finally(() => {
                    btnObj.loading = false
                })
            }
        },

    ]
    // /* 执行自定义表格行的双击事件
    // * @param {Array} btnList 按钮数组
    // */
    // dblClick: function (btnList) {
    //     let selection = this.getSelection()

    //     this.pushPage({
    //         path: `/module/stander/list/maas_model_filelist/placeholder`,
    //         title: '路径文件列表',
    //         query: { id: selection[0].id }
    //     })
    //     // let btnItem = btnList.find(i => i.id === 'view')
    //     // btnItem && btnItem.click.call(this, btnItem)
    // },
}
