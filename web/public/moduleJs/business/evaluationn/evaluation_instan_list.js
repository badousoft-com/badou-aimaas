
export default {
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'add', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'edit', name: '导入', icon: 'import', type: 'primary', isHide: true },
        { id: 'delete', name: '导入', icon: 'import', type: 'primary', isHide: true },
        {
            id: 'view',
            click: function (btnObj) {
                // this: bd-module-list组件作用域
                // 获取选中列表数据
                let selection = this.getSelection()
                if (selection.length !== 1) {
                    this.$message({
                        type: 'warning',
                        message: '请选择一行！'
                    })
                    return
                }
                this.pushPage({
                    path: `/module/view/view/${this.mdCode}/${selection[0].id}`,
                    title: Get_List_History_Title.call(this, btnObj, selection[0])
                })
            }
        }
    ]
}

// 获取列表按钮跳转的历史记录title，需要call(this)
function Get_List_History_Title (btnObj, row) {
    return btnObj.name + this.name
}