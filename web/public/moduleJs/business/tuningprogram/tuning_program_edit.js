
export default {
    buttons: [
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
        },
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
                    let url = `${this.BASEURL}/project/maas/tuningprogramn/tuningprogramnsave/saveIncludeFile?mdCode=${mdCode}`
                    // 提交接口
                    this.post(url, res).then((res) => {
                        if (res?.hasOk) {
                            this.$message.success('保存成功')
                            //如果是新增
                            if (this.$route.path.indexOf("add") != -1) {
                                this.pushPage({
                                    path: `/module/stander/edit/${mdCode}/` + res.bean.id,
                                    title: "微调计划编辑页面"
                                })
                                return
                            }
                            this.$router.go(-1)
                        } else {
                            this.$message.error(`保存失败！${res.message}`)
                        }
                    }).finally(() => {
                        // 设置按钮状态
                        btnObj.loading = false
                    })

                })
            }
        },
        {
            id: 'build',
            name: '启动',
            icon: 'timedTaskManage',
            type: 'danger',
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
                    let url = `${this.BASEURL}/project/maas/tuningprogramn/tuningprogramntasksave/saveIncludeFile?mdCode=${mdCode}`
                    // 提交接口
                    this.post(url, res).then((res) => {
                        if (res?.hasOk) {
                            this.$message.success('启动成功!')
                            this.pushPage({
                                path: `/module/stander/list/maas_fine_tuning_modeln/placeholderr`,
                                title: "微调监控列表"
                            })

                        } else {
                            this.$message.error(`保存失败！${res.message}`)
                        }
                    }).finally(() => {
                        // 设置按钮状态
                        btnObj.loading = false
                    })
                    return
                    // this.$confirm("未设置执行时间立即执行.已设置按具体时间执行", "提示", {
                    //     iconClass: "el-icon-question",//自定义图标样式
                    //     confirmButtonText: "确定",//确认按钮文字更换
                    //     cancelButtonText: "取消",//取消按钮文字更换
                    //     showClose: true,//是否显示右上角关闭按钮
                    //     type: "warning",//提示类型  success/info/warning/error
                    // }).then(() => {

                    // }).catch(() => {
                    //     //取消操作
                    // });

                })
            }
        }
    ]
}