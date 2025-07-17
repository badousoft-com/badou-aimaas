export default {
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
                    let url = `${this.BASEURL}/cfg/syscfgsafesave/save.do?mdCode=${this.mdCode}`
                    this.post(url, res).then((res) => {
                        if (res?.hasOk) {
                            this.$message.success('保存成功')
                        } else {
                            this.$message.error(`保存失败！${res?.message}`)
                        }
                    }).finally(() => {
                        // 设置按钮状态
                        btnObj.loading = false
                    })
                })
            }
        }, {
            id: "close",
            isHide: true
        }
    ],
    dataUrl: function () {
        return `/cfg/syscfgsafeedit/editJSONFront.do`
    },
    valuePath: '',
}