export default  {
    buttons: [
        {
            id: 'save',
            name: '保存',
            icon: 'save',
            type: 'primary',
            loading: false,
            click: function (btnObj) {
                // this: bd-module-edit组件作用域       
                let formRef = this.getFormRef()
                // 表单校验
                formRef.validateForm().then((res) => {
                    // 设置按钮状态-加载中
                    btnObj.loading = true
                    let url = `${this.BASEURL}/jdbc/common/basecommonsave/saveIncludeFile.do?mdCode=${this.mdCode}`
                    this.post(url, res).then((res) => {
                        if (res?.hasOk) {
                            // this.$message.success('保存成功')
                            this.$confirm('是否立即重新登录，使配置生效', '配置生效', {
                                confirmButtonText: '确定重新登录',
                                cancelButtonText: '取消',
                                type: 'success'
                            }).then(async () => {
                                await this.$store.dispatch('user/logout')
                                this.pushPage({
                                    path: `/login`,
                                    title: '登录页'
                                })
                                // 用户主动注销的时候,不携带当前页面路径,因为可能存在用户是要切换身份,应该要回到首页,而且不确定原有页面地址参数数据是否还有效
                                // this.$router.push(`/login?redirect=${this.$route.fullPath}`)
                            })
                        } else {
                            this.$message.error(`保存失败！${res?.message}`)
                        }
                    }).finally(() => {
                        // 设置按钮状态
                        btnObj.loading = false
                    })
                }).catch(() => {
                    btnObj.loading = false
                })
            }
        }, { 
            id: "close",
            isHide: true
        }
    ],
    dataUrl: function () {
        return `/cfg/basedefineedit/editJSONFront.do`
    },
    valuePath: '',
}