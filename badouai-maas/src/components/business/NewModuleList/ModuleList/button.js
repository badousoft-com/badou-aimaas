import { ExportData, ImportData, Get_List_History_Title } from '@/service/module.js'

// id: 事件id
// name: 按钮名称
// icon： 图标名称
// click： 点击事件【考虑：不使用handle,可能还会有其他事件，例如hover等，拓展性考虑使用click】
let defaultButtons = [
    {
        id: 'add',
        click: function (btnObj) {
            // this: bd-module-list组件作用域
            this.pushPage({
                path: `/module/stander/edit/${this.mdCode}/add`,
                title: Get_List_History_Title.call(this, btnObj)
            })
        }
    }, {
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
                title: Get_List_History_Title.call(this, btnObj, selection[0])
            })
        }
    }, {
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
    }, {
        id: 'export',
        click: function (btnObj) {
            ExportData.call(this, btnObj)
        }
    }, {
        id: 'delete',
        click: function (btnObj) {
            // this: bd-module-list组件作用域
            // 获取列表页面所在作用域
            let listPageRef = this.listPageRef()
            // 获取选中列表数据
            let selection = this.getSelection()
            if (selection.length === 0) {
                this.$message.warning('至少选择一行！')
                return
            }
            this.$confirm('确定删除吗？', '删除', {
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
                        // TODO:需求待确认
                        this.$emit('deleteAfter', selection)
                    } else {
                        let defaultTip = '删除失败'
                        this.$message.error(res?.message ? `${defaultTip}:${res.message}` : defaultTip)
                    }
                })
            })
        }
    }, {
        id: 'import',
        name: '导入',
        icon: 'import',
        type: 'primary',
        click: function (btnObj) {
            ImportData.call(this, btnObj)
        }
    }
]
export default defaultButtons