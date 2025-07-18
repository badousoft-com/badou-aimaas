export default {
    buttons: [
        { id: 'edit', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        {
            id: 'add', name: '关联训练集文件', icon: 'synchro', type: 'primary',
            click: function () {
                let listPageRef = this.listPageRef()
                //获取父表的类型
                let tunTypeObj = this.$parent.$parent.$parent.$parent.$children[1].$children[0]._fieldList.find(i => i.name === "doWay")
                let rlhfWay = this.$parent.$parent.$parent.$parent.$children[1].$children[0]._fieldList.find(i => i.name === "rlhfWay")
                
                let parentId = this.$parent.$parent.$parent.$parent.$children[1].$children[0]._fieldList.find(i => i.name === "id").value
                let id = 'planlinkselectlist'
                let mdCode = "maas_train_file_select"
                if ( "4" == tunTypeObj.value ){
                    mdCode = 'maas_train_file_select_multi'
                }

                this.$dialog.init({
                    type: 'standerListCode',
                    id: id,
                    title: '微调-训练集文件选择',
                    mdCode: mdCode,
                    //传值
                    customSetting: {
                        paramsBeforeRequest: function (params) {
                            let value = tunTypeObj.value
                            //如果是偏好类型并且偏好类型详情有值 则进入偏好数据集选择模式 数字+50
                            if(tunTypeObj.value == '2' && rlhfWay.value){
                                //如果偏好详情类型是2 代表是DPO DPO的数据集是和偏好对齐一样的 共享 所以设置成0
                                if (rlhfWay.value == '2'){
                                    value = '0';   
                                }
                                value = parseInt(rlhfWay.value, 10) + 50;
                            }
                            if ( mdCode == 'maas_train_file_select'){
                                let searchParam = params.searchParam ? JSON.parse(params.searchParam) : []
                                searchParam.push({ "name": "dataFormat", "value": value, "type": "other-query", "tagName": "" })
                                searchParam.push({ "name": "upStatus", "value": 1, "type": "other-query", "tagName": "" })
                                params.searchParam = JSON.stringify(searchParam)
                            } else if ( mdCode == 'maas_train_file_select_multi'){
                                let searchParam = params.searchParam ? JSON.parse(params.searchParam) : []
                                searchParam.push({ "name": "upStatus", "value": 1, "type": "other-query", "tagName": "" })
                                params.searchParam = JSON.stringify(searchParam)
                            }
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
                            name: '确认',
                            type: 'primary',
                            icon: 'save',
                            handler: function () {
                                // getDialogConObj:全局封装，3为指定模型列表，返回该页面作用域
                                let aimRef = this.getDialogConObj(id, 3)
                                let selection = aimRef.selection
                                if(selection.length != 1){
                                    this.$message.error("请选择一条数据!")
                                    return
                                }
                                if(!selection[0].name){
                                    this.$message.error("该训练集未设置名字.不允许绑定!")
                                    return
                                }
                                this.post('/jdbc/common/basecommonsave/saveIncludeFile.do?mdCode=maas_fine_tuning_plan_link', {
                                    id: "",
                                    trainDataId: selection[0].id,
                                    trainDataName: selection[0].name,
                                    planLinkId: parentId
                                }).then((res) => {
                                    if (res?.hasOk) {
                                        // 更新列表页面数据
                                        listPageRef.init()
                                        this.$message.success("绑定成功!")
                                        this.$dialog.close()
                                    } else {
                                        this.$message.error(`绑定失败`)
                                    }
                                }).finally(() => {
                                })
                            }
                        }
                    ],
                    fullHeight: true
                })
            }
        },

    ]
}