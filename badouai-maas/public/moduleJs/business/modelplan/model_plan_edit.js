export default {
    // saveUrl: function (mdCode) {
    //     // this:指向moduleEdit/index.vue页面所在作用域
    //     return `${this.BASEURL}/project/maas/trainplan/trainplansave/saveIncludeFile?mdCode=${mdCode}`
    // },
    buttons: [
        {
            id: 'save',
            name: '保存',
            icon: 'save',
            type: 'primary',
            loading: false,
            click: function (btnObj) {
                let mdCode = this.mdCode
                //确认操作
                // this: bd-module-edit组件作用域
                let formRef = this.getFormRef()
                // 表单校验
                formRef.validateForm().then((res) => {
                    // // 设置按钮状态-加载中
                    btnObj.loading = true
                    // 获取表单保存接口地址
                    let url = `${this.BASEURL}/project/maas/trainplan/trainplansave/saveIncludeFile?mdCode=${mdCode}`
                    // 提交接口
                    this.post(url, res).then((res) => {
                        if (res?.hasOk) {
                            if (this.$route.path.includes("add")) {
                                //如果是新增页面 保存成功跳转到编辑页面
                                const id = res.bean.id || res.data.id;
                                this.pushPage({
                                    path: `/module/stander/edit/maas_fine_tuning_plan/${id}`,
                                    title: '微调方案编辑'
                                })
                            } else {
                            this.$message.success('保存成功')
                            this.pushPage({
                                path: `/module/stander/list/${mdCode}/placeholder`,
                                title: '微调方案编辑'
                            })
                            }
                        } else {
                            this.$message.error(`保存失败！${res.message}`)
                        }
                    }).finally(() => {
                        // 设置按钮状态
                        btnObj.loading = false
                    })
                })
                // this.$confirm("确定后会启动微调任务!", "提示", {
                //     iconClass: "el-icon-question",//自定义图标样式
                //     confirmButtonText: "确定",//确认按钮文字更换
                //     cancelButtonText: "取消",//取消按钮文字更换
                //     showClose: true,//是否显示右上角关闭按钮
                //     type: "warning",//提示类型  success/info/warning/error
                // }).then(() => {

                // }).catch(() => {
                //     //取消操作
                // });
            }
        },
        {
            id: 'back',
            name: "返回",
            icon: "back",
            type: 'danger',
            click: function (btnObj) {
                let mdCode = this.mdCode
                // 获取列表页面所在作用域
                this.pushPage({
                    path: `/module/stander/list/${mdCode}/placeholder`,
                    title: '微调方案编辑'
                })
            }
        }
    ],
    valueChange: {
        /**
         * 字段键为height的值变更事件
         * @param {Object} formScope 表单所在页面作用域
         * @param {String} fieldName 字段键名
         * @param {*} value 字段值
         * @param {Object} fieldObj 字段对象
         * @param ...更多参数 有组件内自定义传回
         */
        modelId: function (formScope, fieldName, value, fieldObj) {
            // 获取当前字段对象
            let tunFrame = this.fieldList.find(i => i.name === 'tunFrame')
            let modelId = this.fieldList.find(i => i.name === "modelId")
            let _this = this
            // 只能查询与自己模型同一种类型的。如llama3与另外个llama3
            let rewardId = this.fieldList.find(i => i.name === 'rewardId')
            let modelAllName = this.fieldList.find(i => i.name === 'modelAllName')
            let modeData = this.fieldList.find(i => i.name === 'mode')
            // 获取当前字段对象
            if (modelAllName.value) {
                // this.$message.success('选择本次方案微调' + modelAllName.value + "模型")
                rewardId.initDialogParams = function () {
                    return {
                        defaultParamsObj: { "modelName": modelAllName.value }
                    }
                }
            }

            if (tunFrame && modelId) {
                let url = `${_this.BASEURL}/project/maas/trainplan/trainplansave/getPreGpuCache`
                this.post(url, {
                    tunFrame: tunFrame.value,
                    modelId: modelId.value,
                    mode: modeData.value
                }).then((res) => {
                    let preGpucache = _this.fieldList.find(i => i.name === 'preGpucache')
                    // btnObj.loading = false
                    if (res == 0) {
                        preGpucache.value = null
                        // _this.$message.error('未配置对应模型框架的预估GPU显存配置!请联系管理员!')
                        return
                    }
                    preGpucache.value = res
                }).finally(() => {
                })
            }
        },
        /**
        * 字段键为select的值变更事件
        * @param {Object} formScope 表单所在页面作用域
        * @param {String} fieldName 字段键名
        * @param {*} value 字段值
        * @param {Object} fieldObj 字段对象
        * @param {Object} currentOption 选中的数据项
        * @param {Array} options 所有可选options数据
        * @param {Object} 其余的配置项数据
        *     @attr {String} oldValue 旧值
        */
        tunFrame: function (formScope, fieldName, value, fieldObj, currentOption, options) {
            //20241220开发需求 微调参数模型标题增加显示微调框架类型 llamafactory xtuner swift
            this.$parent.$parent.$children[4].$children[0].title = '133'
            this.$parent.$parent.$children[4].$children[0].$children[0].module.moduleName = '123'
            this.$parent.$parent.$children[4].$children[0].$children[0].module.name = '123'
            this.$parent.$parent.$children[4].$children[0].$children[0].$attrs.title = '123'
            this.$parent.$parent.$children[4].$children[0].$children[0]._$attrs = '123'
            // this.$parent.$parent.$children[4].$children[0].$children[0].title =  name+"("+currentOption.text+")";
            // 获取当前字段对象
            let tunFrame = this.fieldList.find(i => i.name === "tunFrame")
            let modelId = this.fieldList.find(i => i.name === "modelId")
            let modeData = this.fieldList.find(i => i.name === "mode")
            let _this = this
            if (tunFrame && modelId) {
                let url = `${_this.BASEURL}/project/maas/trainplan/trainplansave/getPreGpuCache`
                this.post(url, {
                    tunFrame: tunFrame.value,
                    modelId: modelId.value,
                    mode: modeData.value
                }).then((res) => {
                    let preGpucache = _this.fieldList.find(i => i.name === 'preGpucache')
                    // btnObj.loading = false
                    if (res == 0) {
                        preGpucache.value = null
                        // _this.$message.error('未配置对应模型框架的预估GPU显存配置!请联系管理员!')
                        return
                    }
                    preGpucache.value = res
                }).finally(() => {
                })
            }
        },
        mode: function (formScope, fieldName, value, fieldObj, currentOption, options) {
            // 获取当前字段对象
            let tunFrame = this.fieldList.find(i => i.name === "tunFrame")
            let modelId = this.fieldList.find(i => i.name === "modelId")
            let modeData = this.fieldList.find(i => i.name === "mode")
            let _this = this
            if (tunFrame && modelId) {
                let url = `${_this.BASEURL}/project/maas/trainplan/trainplansave/getPreGpuCache`
                this.post(url, {
                    tunFrame: tunFrame.value,
                    modelId: modelId.value,
                    mode: modeData.value
                }).then((res) => {
                    let preGpucache = _this.fieldList.find(i => i.name === 'preGpucache')
                    // btnObj.loading = false
                    if (res == 0) {
                        preGpucache.value = null
                        // _this.$message.error('未配置对应模型框架的预估GPU显存配置!请联系管理员!')
                        return
                    }
                    preGpucache.value = res
                }).finally(() => {
                })
            }
        },
        // moreCardFlag: function (formScope, fieldName, value, fieldObj, currentOption, options) {
        //     //默认隐藏多卡配置 只有开启的时候 才执行
        //     if ("1" == value) {
        //         let gpuFrame = this.fieldList.find(i => i.name === "gpuFrame")
        //         gpuFrame.type = "select"
        //         let serverGpuMode = this.fieldList.find(i => i.name === "serverGpuMode")
        //         serverGpuMode.type = "radio"
        //         let gpuCount = this.fieldList.find(i => i.name === "gpuCount")
        //         gpuCount.type = "text"
        //     } else {
        //         let gpuFrame = this.fieldList.find(i => i.name === "gpuFrame")
        //         gpuFrame.type = "hidden"
        //         let serverGpuMode = this.fieldList.find(i => i.name === "serverGpuMode")
        //         serverGpuMode.type = "hidden"
        //         let gpuCount = this.fieldList.find(i => i.name === "gpuCount")
        //         gpuCount.value = 1
        //         gpuCount.type = "hidden"
        //     }

        // },
        serverGpuMode: function (formScope, fieldName, value, fieldObj, currentOption, options) {
            //多机多卡
            if ("2" == value) {
                let gpuCount = this.fieldList.find(i => i.name === "gpuCount")
                gpuCount.type = "select"
                gpuCount.value = null
            } else {
                //单机多卡
                let gpuCount = this.fieldList.find(i => i.name === "gpuCount")
                gpuCount.type = "text"
            }

        }
    },
    beforeRender: function () {
        let rewardId = this.fieldList.find(i => i.name === "rewardId")
        rewardId.type = 'hidden'
        let gpuCount = this.fieldList.find(i => i.name === "gpuCount")
        let gpuFrame = this.fieldList.find(i => i.name === "gpuFrame")
        let serverGpuMode = this.fieldList.find(i => i.name === "serverGpuMode")
        //默认隐藏多卡配置 只有开启的时候 才执行
        if (gpuCount.value > 1) {
            gpuFrame.type = "select"
            //单机
            if ("0" == serverGpuMode.value){
                gpuCount.type = "text"
            }else {
                gpuCount.type = "select"
            }
            serverGpuMode = "radio"
        } else {
            gpuCount.value = 1
            // gpuFrame.type = "hidden"
            // gpuCount.type = "hidden"
            // serverGpuMode.type = "hidden"
        }
    }
}
