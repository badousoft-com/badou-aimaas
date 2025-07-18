export default {
    importUrl: '/project/maas/problemdata/problemdataimport/baseImportForResult',
    buttons: [
        { id: 'export', name: '导出', icon: 'export', type: 'primary', isHide: true },
        {
            id: 'upStatus', name: '启/停用', icon: 'edit', type: 'primary',
            click: function (btnObj) {
                // /module/stander/edit/maas_train_file/2a3bb4ad73e74e8598d8b27a34eea06bca806bf7493042d6971dbfe49f7ec034
                let selection = this.getSelection()
                if (selection.length == 0) {
                    this.$message.warning("请至少选择一行数据")
                    return
                }
                let listPageRef = this.listPageRef()
                btnObj.loading = true
                this.post('/project/maas/problemdata/problemdatavideosave/updateStatus', selection, {
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).then((res) => {
                    if (res?.hasOk) {
                        // 更新列表页面数据
                        listPageRef.init()
                        this.$message.success('变更启/停状态成功!')
                    } else {
                        this.$message.error("变更失败!请联系管理员!")
                    }
                }).finally(() => {
                    btnObj.loading = false
                })
            }
        },
        {
            id: 'edit', name: '修改', icon: 'edit', type: 'warning',
            click: function () {
                // /module/stander/edit/maas_train_file/2a3bb4ad73e74e8598d8b27a34eea06bca806bf7493042d6971dbfe49f7ec034
                let selection = this.getSelection()
                if (selection.length != 1) {
                    this.$message.error("请选择一行数据")
                    return
                }
                this.pushPage({
                    path: `/module/stander/edit/${this.mdCode}_edit/${selection[0].id}`,
                    title: '编辑页面'
                })
            }
        },
    ],
    renderField: {
        // 字段键名
        upStatusDesc: {
            /**
             * @param {Object} h 渲染函数参数对象
             * @param {Object} context 对象（包含props,children,slots,scopedSlots,parent,listeners,injection）
             * @param {Object} row 所在行对象
             * @param {Object} column 所在列对象
             * @param {*} cellValue 当前字段值
             * @param {Number} index 该行下角标
             * @param {Object} fieldObj 字段对象
             * @param {Object} scope 表格项对象
             */
            render: function (h, context, row, column, cellValue, index, fieldObj, scope) {
                let upStatus = row.upStatus
                if (upStatus == null || upStatus == 0 ) {
                    return (<span>未启用</span>)
                } else if (upStatus == 1) {
                    return (<span style="color:green">启用</span>)
                }else{
                    return (<span style="color:red">未识别</span>)
                }
            }
        }
    },
}
