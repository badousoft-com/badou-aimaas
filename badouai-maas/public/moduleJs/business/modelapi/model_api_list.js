
export default {
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `/project/maas/modelapp/modelapplist/listJSON?mdCode=maas_model_app`
    },
    renderField: {
        statusDesc: {
            render: function (h, context, row, column, cellValue, index, fieldObj, scope) {
                let that = this
                if (row.status == 0) {
                    return (
                        <span>
                            <bd-icon name="time-d" style="color:black"></bd-icon>
                            <span style="color:black">未启动</span>
                        </span>
                    )
                }
                if (row.status == 1) {
                    return (
                        <span>
                            <bd-icon name="start" style="color:lightgreen"></bd-icon>
                            <span style="color:lightgreen">启动中</span>
                        </span>
                    )
                }
                if (row.status == 2) {
                    return (
                        <span>
                            <bd-icon name="right" style="color:green"></bd-icon>
                            <span style="color:green">运行中</span>
                        </span>
                    )
                }
                if (row.status == 3) {
                    return (
                        <span>
                            <bd-icon name="multi" style="color:red"></bd-icon>
                            <span style="color:red">启动失败</span>
                        </span>
                    )
                }

                return (
                    <span>
                        <bd-icon name="time-d" style="color:black"></bd-icon>
                        <span style="color:black">未启动</span>
                    </span>
                )
                if (that.status == 3) {
                    return (
                        <bd-icon name="multi" style="color:red"><span>cellValue</span></bd-icon>
                    )
                }
                return cellValue;
                return (
                    <el-input vModel={row.value} class="primaryC" on-change={(e) => {
                        this.post('/project/maas/tuningplanparams/tuningplanparamsedit/updateValue', {
                            id: row.id,
                            newValue: e
                        }).then((res) => {
                            if (res?.hasOk) {

                            } else {
                                console.log("保存数值失败!")
                            }
                        }).finally(() => {

                        })
                    }}></el-input>
                )
            },
        }
    },
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'add', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'edit', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'delete', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'view', name: '导入', icon: 'import', type: 'primary', isHide: false },
        {
            id: 'apilog',
            name: 'api日志',
            icon: 'logManage',
            type: 'primary',
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                const { href } = this.$router.resolve({
                    path: `/modelapplog`,
                    query: {
                        id: selection[0].id,
                        reload: 'maas_model_app_api'
                    }
                });
                window.open(href, '_blank');
            }
        },
        {
            id: 'api',
            name: '显示API信息',
            icon: 'edit',
            type: 'danger',
            loading: false,
            click: function (btnObj) {
                let selection = this.getSelection()
                // 是否选择了一行数据
                if (selection.length !== 1) {
                    this.$message.warning('请选择一行数据！')
                    return
                }
                btnObj.loading = true
                this.$message.warning('开始检查,请稍等。检查通过则可以使用')
                let url = '/project/maas/modelapp/modelapplist/loadApiMsg'
                let copyText = ''
                let _this = this
                // 提交接口
                this.post(url, {
                    id: selection[0].id,
                    content: '你好'
                }).then((res) => {
                    btnObj.loading = false
                    if (res?.hasOk) {
                        let row = res.bean
                        copyText = "请求地址: " + row.address + "\n请求方法: " + row.method + "\n请求内容格式: " + res.bean.contentType
                        copyText = copyText + `\n${row.requestTitle}: ${row.paramsSample}`
                        copyText = copyText + `\n返回结果例子: +${row.responseSample}`
                        this.$pageDialog.init({
                            title: '对接模型接口',
                            pageUrl: 'business/apipages/index.vue',
                            outScope: this,
                            data: res.bean,
                            handlerList: [
                                {
                                    id: 'save', name: '复制到剪切板', icon: 'save', type: 'primary', click: function (itemObj) {
                                        this.$copyText(copyText).then(function (e) {
                                            _this.$message.success('复制成功!')
                                        }, function (e) {
                                            _this.$message.error('复制失败!')
                                        })
                                    }
                                }
                            ],
                            // isAutoFix: true // 根据内容自适应
                            height: '800px',
                            with: '1400px',
                        })
                        this.$message.success('校验通过！')
                    } else {
                        this.$message.error(`保存失败！${res.message}`)
                    }
                }).finally(() => {
                    // 设置按钮状态
                    btnObj.loading = false
                })
            }
        }
    ]
}