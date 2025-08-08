export default {
    dataUrl: function () {
        // this:指向moduleList/index.vue所在页面作用域
        return `${this.BASEURL}/project/maas/modeltrain/traindatasourcelist/listJson`
    },
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        { id: 'import', name: '导入', icon: 'import', type: 'primary', isHide: true },
        {
            id: 'edit',
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
                    path: `/module/stander/edit/${this.mdCode}/${selection[0].id}`,
                    title: '修改'
                })
            }
        }
        
    ]
}