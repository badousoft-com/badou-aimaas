export default {
    beforeRender: function () {
        // this: 指向moduleForm/index页面所在作用域
        // 例如：this._fieldList可以拿到所有表单字段数据，你可以进行修改
        let dataFormat = this.fieldList.find(o => o.name === 'dataFormat')
        this.$route.query.dataFormat = dataFormat.value
    },
    buttons: [
        {
            id: 'save',
            name: '保存',
            icon: 'save',
            type: 'primary',
            loading: false,
            click: function (btnObj) {
                let mdCode = this.mdCode
                // this: bd-module-edit组件作用域       
                let formRef = this.getFormRef()
                // 表单校验
                formRef.validateForm().then((res) => {
                    // // 设置按钮状态-加载中
                    btnObj.loading = true
                    // 获取表单保存接口地址
                    let url = `${this.BASEURL}/project/maas/problemdata/problemdatasave/saveIncludeFile?mdCode=${mdCode}`
                    // 提交接口
                    this.post(url, res).then((res) => {
                        if (res?.hasOk) {
                            this.$message.success('保存成功')
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
        }
    ]
}