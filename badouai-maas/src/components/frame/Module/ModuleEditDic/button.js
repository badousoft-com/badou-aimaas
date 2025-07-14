import { Save_Dic_URL } from '@/api/frame/common'

// id: 事件id
// name: 按钮名称
// icon： 图标名称
// click： 点击事件【考虑：不使用handle,可能还会有其他事件，例如hover等，拓展性考虑使用click】
let defaultButtons = [
    {
        id: 'back',
        name: '返回',
        icon: 'back',
        type: 'danger',
        click: function () {
            this.$router.go(-1)
        }
    },
    {
        id: 'save',
        name: '保存',
        icon: 'save',
        type: 'primary',
        loading: false,
        click: function (btnObj) {
            // 获取moduleEdit组件作用域
            let moduleEditScope = this.$refs.moduleEditDic
            // 获取表单组件作用域
            let formRef = moduleEditScope.getFormRef()
            // 数据字典表单（上部分）校验
            // res：表单数据
            formRef.validateForm().then((res) => {
                // 设置按钮为加载状态
                btnObj.loading = true
                // 数据字典子项列表数据（下部分）校验
                // childData： 表单数据
                this.$refs.dicItemTable.validateForm().then(childData => {
                    // 获取数据保存地址
                    let url = Save_Dic_URL(this.mdCode)
                    // 将【数据字典子项数据】-更新到-【数据字段表单】
                    res.child = JSON.stringify(childData)
                    // 提交接口
                    this.post(url, res).then((res) => {
                        if (res.hasOk) {
                            this.$message.success('保存成功')
                            this.$router.go(-1)
                        } else {
                            this.$message.error(`保存失败！${res.message}`)
                        }
                    }).finally(() => {
                        // 更新按钮加载状态
                        btnObj.loading = false
                    })
                }).catch (() => {
                    // 更新按钮加载状态
                    btnObj.loading = false
                })
            })
        }
    }
]
export default defaultButtons