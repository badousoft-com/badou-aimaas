let defaultButtons = [
    {
        id: 'add',
        name: '保存',
        icon: 'save',
        type: 'primary',
        loading: false,
        click: function (btnObj) {
            // 设置按钮状态-加载中
            btnObj.loading = true
            let params = {
                roleId: this.defaultParamsObj.roleId,
                mids: this.getCheckedNodeKeys()
            }
            this.post(`${this.BASEURL}/auth/assign/roleresourceassignmenu/save.do`, params).then(res => {
                if (res?.hasOk) {
                    // this.reset()
                    this.$message.success('操作成功')
                } else {
                    this.$message.success('操作失败')
                }
            }).finally(() => {
                // 设置按钮加载状态
                btnObj.loading = false
            })
        }
    }
]
export default defaultButtons
