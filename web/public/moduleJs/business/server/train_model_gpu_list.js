export default {
    renderField: {
        // 字段键名
        doStatusDesc: {
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
                let doStatus = row.doStatus
                if (doStatus == 0) {
                    return (<span>等待开始</span>)
                } else if (doStatus == 1) {
                    return (<span style="color:blue">微调中</span>)
                } else if (doStatus == 2) {
                    return (<span style="color:green">微调成功</span>)
                } else if (doStatus == 3) {
                    return (<span style="color:red">微调失败</span>)
                } else if (doStatus == 4) {
                    return (<span style="color:#ffa200">作废</span>)
                } else if (doStatus == 5) {
                    return (<span style="color:#8a7afb">启动应用中</span>)
                } else if (doStatus == 6) {
                    return (<span style="color:#0373ce">评价中</span>)
                }
                return (
                    // 以下使用可参考vue JSX语法：https://cn.vuejs.org/v2/guide/render-function.html#JSX
                    // 1. return 后面使用的是(), 注意不是{}
                    // 2. return()中填写的是元素标签：如<span>，不要写成字符串的标签如'<span>'
                    // 3. return()中的元素确保存在且仅有一个根级标签，该标签下可以多个子标签
                    // 4. 变量使用单花括号表示，{value},注意与vue模版标签{{}}不同
                    // 5. 标签使用事件时，格式为： on-事件类型={() => { /** 需要处理的逻辑代码 */ }}
                    // 6. 这里可使用的组件一般是全局组件，如果一定要使用局部组件，只能将局部组件注册为全局组件，有刚好的解决方案请联系

                    // 场景1：封装组件：事件
                    // <bd-button icon="edit" type="primary" on-click={this.editFun}></bd-button>
                    // 场景2：封装组件：事件与阻止冒泡
                    // <bd-button icon="edit" type="primary" nativeOn-click={(e) => {
                    //     this.editFun()
                    //     e.stopPropagation() // 阻止事件冒泡，避免表格行被选中
                    // }}></bd-button>
                    // 场景3：html原生标签：事件
                    // <button vOn:click={this.editFun}>ok</button>
                    // 场景4：html原生标签：事件与阻止冒泡
                    // <button vOn:click_stop_prevent={this.editFun}>ok</button>
                    <span>
                        <loading-progress
                            progress="progress"
                            indeterminate="indeterminate"
                            counter-clockwise="counterClockwise"
                            hide-background="hideBackground"
                            size="20"
                            rotate
                            fillDuration="2"
                            rotationDuration="1"></loading-progress>
                        执行中
                        <div class="view">
                            {/* <loading-progress
				progress="progress"
				indeterminate="indeterminate"
				counter-clockwise="counterClockwise"
				hide-background="hideBackground"
				size="64"
				rotate
				fillDuration="2"
				rotationDuration="1"
			/>

			<loading-progress
				progress="progress"
				indeterminate="indeterminate"
				counter-clockwise="counterClockwise"
				hide-background="hideBackground"
				shape="semicircle"
				size="64"
			/>
			
			<loading-progress
				progress="progress"
				indeterminate="indeterminate"
				counter-clockwise="counterClockwise"
				hide-background="hideBackground"
				shape="line"
				size="200"
				width="200"
				height="6"
			/>

			<loading-progress
				progress="progress"
				indeterminate="indeterminate"
				counter-clockwise="counterClockwise"
				hide-background="hideBackground"
				shape="square"
				size="64"
				fill-duration="2"
			/>

			<loading-progress
				progress="progress"
				indeterminate="indeterminate"
				counter-clockwise="counterClockwise"
				hide-background="hideBackground"
				shape="M10 80 C 40 10, 65 10, 95 80 S 150 150, 180 80"
				size="180"
				fill-duration="2"
			/> */}


                        </div>
                    </span>
                )
            }
        },
        // 字段键名
        threadMsg: {
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
                return `<span style='color: #007BFF;'>点击查看</span>`
            },        // 点击事件（若没有可以不写，去除整块click）
            click: function (row, column, cellValue, index, fieldObj) {
                let btnList = [
                    {
                        name: '取消',
                        type: 'danger',
                        icon: 'cancel',
                        click: function () {
                            // 关闭弹窗表单
                            this.$dialog.close()
                        }
                    }
                ]
                this.$dialog.init({
                    title: '线程信息',
                    type: 'list',
                    // width: 1200,
                    // height: 300,
                    isSelection: false,
                    id: '123',
                    fieldList: [
                        { name: 'uid', label: '进程所属用户ID', },
                        { name: 'pid', label: '进程ID', },
                        { name: 'c', label: '进程的CPU占用率', },
                        { name: 'stime', label: '进程启动时间', },
                        { name: 'time', label: '进程使用的CPU时间', },
                        { name: 'cmd', label: '启动进程的命令', },
                        { name: 'ppid', label: '父进程ID', },
                        { name: 'tty', label: '进程所属的终端', },
                    ],
                    handlerList: btnList,
                    data: JSON.parse(cellValue)
                })
            }
        },
    },
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `${this.BASEURL}/project/maas/tuningmodeln/tuningmodelncardlist/listJSON?mdCode=maas_fine_tuning_modeln_gpu`
    },
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'add', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'edit', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'delete', name: '导出', icon: 'export', type: 'primary', isHide: true },
        {
            id: 'view-eva',
            name: "查看评价结果",
            icon: "view",
            type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                // 定义弹窗唯一标识
                let signId = 'EVA_VIEW'
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
                    }
                ]
                this.$dialog.init({
                    // 弹窗内容类型
                    type: 'standerEditCode',
                    // 弹窗唯一标识，注意唯一标识与上面出现的函数getModelListObj的参数值保持一致
                    id: signId,
                    // 弹窗标题
                    title: '查看微调评价详情',
                    // 模型编码c
                    mdCode: 'maas_fine_tuning_evaluation_instan',
                    // 根据内容自适应高度
                    // isAutoFix: true,
                    width: '1300px',
                    height: '100%',
                    // 详情数据id
                    detailId: selection[0].evaluatioInstanId,
                    // 弹窗中按钮组
                    handlerList: btnList,
                    isView: true
                })
                // const { href } = this.$router.resolve({
                //     path: `/tuningmonitoring`,
                //     query: {
                //         id: selection[0].id
                //     }
                // });
                // window.open(href, '_blank');
                // this.pushPage({
                //     path: '/tunmodelevaresult', title: '发布详情', query: {
                //         id: selection[0].id
                //     }
                // })
            }
        },
        {
            id: 'putShelves',
            name: "上架微调模型",
            type: 'primary',
            icon: 'upload',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                if (selection[0].shelvesStatus == 1) {
                    this.$message.warning('已上架!请勿重复上架!')
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
                                        id: selection[0].id,
                                        type: 1,
                                        pubVersion: res.pubVersion,
                                        pubMsg: res.pubMsg
                                    }).then(res => {
                                        if (res?.hasOk) {
                                            this.$dialog.close()
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
                this.post(`${this.BASEURL}/project/maas/tuningmodeln/tuningmodelnedit/coverTunModel`, {
                    id: selection[0].id,
                    type: 0
                }).then(res => {
                    if (res?.hasOk) {
                        this.$message.success('下架成功!')
                        // 更新列表页面数据
                        listPageRef.init()
                    } else {
                        this.$message.warning(res.message)
                    }
                })
            }
        },
        {
            id: 'starttomodel',
            name: "启动微调模型",
            type: 'primary',
            isHide: true,
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
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
            }
        },
        {
            id: 'close',
            name: "作废",
            icon: "close",
            type: 'danger',
            click: function (btnObj) {
                let listPageRef = this.listPageRef()
                let selection = this.getSelection()
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
        },
        {
            id: 'tenorboard-log',
            name: "启动TenorBoard组件",
            icon: "view",
            type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                if (selection[0].doStatus != 1) {
                    this.$message.warning('服务未在运行状态')
                    return
                }
                //20241105修改原本的服务 日志分析功能改成TensorBoard服务
                btnObj.loading = true
                this.$message.info('开始初始化日志服务!请稍等!')
                this.post('/project/maas/tuningmodeln/tuningmodelnlist/buildOrGetTenorBoard', {
                    id: selection[0].id
                },
                ).then((res) => {
                    if (res?.hasOk) {
                        this.$dialog.close()
                        this.$message.success('初始化日志服务完成!正在打开监控页面')
                        this.$route.query.url = btoa(res.message)
                        this.$pageDialog.init({
                            title: 'tensorboard页面',
                            pageUrl: 'business/tensorboard/index.vue',
                            outScope: this,
                            // query: { url: btoa(res.message) },
                            handlerList: [
                                // { id: 'save', name: '获取', icon: 'save', type: 'primary', click: function (itemObj) {
                                //     this // 只想弹窗组件中的作用域
                                //     this.outScope // 若使用dialog时传入属性scope:this,则此处可获取使用弹窗的页面作用域
                                //     this.pageScope // 挂载页面所在的作用域
                                //     alert(`输入框值为：${this.pageScope.input}`)
                                // }}
                            ],
                            // isAutoFix:true // 根据内容自适应
                        })
                        // this.pushPage({
                        //     path: `/tensorboardview`,
                        //     title: 'tensorboard页面',
                        //     query: { url: btoa(res.message) }
                        // })
                    } else {
                        this.$message.error(res.message)
                    }
                }).finally(() => {
                    btnObj.loading = false
                })
            }
        },
        {
            id: 'analysis-log',
            name: "日志分析情况",
            icon: "view",
            type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                const { href } = this.$router.resolve({
                    path: `/trainplanlogstatus`,
                    query: {
                        id: selection[0].id
                    }
                });
                window.open(href, '_blank');
                // this.pushPage({ path: '/releaseDetail', title: '发布详情', query: { id: row.pubId, appId: row.id } })     
                // this.pushPage({
                //     path: '/', title: '', query: {
                //         id: selection[0].id
                //     }
                // })
            }
        },
        {
            id: 'view-log',
            name: "查看日志",
            icon: "view",
            type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                const { href } = this.$router.resolve({
                    path: `/tuningmonitoring`,
                    query: {
                        id: selection[0].id
                    }
                });
                window.open(href, '_blank');
                // this.pushPage({ path: '/releaseDetail', title: '发布详情', query: { id: row.pubId, appId: row.id } })     
                // this.pushPage({
                //     path: '/', title: '发布详情', query: {
                //         id: selection[0].id
                //     }
                // })
            }
        },
        {
            id: 'runModel',
            name: "启动微调模型",
            icon: "view",
            type: 'primary',
            isHide: true,
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                // 定义弹窗唯一标识
                let signId = 'skm0001'
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
                                this.post('/project/maas/modelapp/modelapplist/loadApiMsg', res).then((res) => {
                                    if (res?.hasOk) {
                                        this.$message.success(`保存成功！${res?.message}`)
                                    } else {
                                        this.$message.error(`保存失败！${res?.message}`)
                                    }
                                }).finally(() => {
                                    // 设置按钮状态
                                    btnObj.loading = false
                                })
                                btnObj.loading = false // 重置按钮加载状态
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
                    title: '配置启动参数',
                    // 模型编码c
                    mdCode: 'model_start',
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

    ]
}