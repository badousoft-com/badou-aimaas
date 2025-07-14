export default {
    // 前端写法
    buttons: [
        {
            id: "delete",
            name: "删除",
            icon: "delete",
            type: 'danger',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取列表页面所在作用域
                let listPageRef = this.listPageRef()
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length === 0) {
                    this.$message.warning('请先选择需要删除的记录行（流程文档实例）!')
                    return
                }
                this.$confirm('您确定删除选定的记录行吗?', '删除', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let params = {
                        mdCode: this.mdCode,
                        ids: selection.map(i => i.id).join(',')
                    }
                    this.post(`${this.BASEURL}/jdbc/common/basecommondelete/delete.do`, params).then(res => {
                        if (res?.hasOk) {
                            // 更新列表页面数据
                            listPageRef.init()
                            this.$message.success('删除成功')
                        } else {
                            this.$message.error('删除失败')
                        }
                    })
                })
            }
        }
    ]
}